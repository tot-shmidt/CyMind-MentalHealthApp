package com.example.myapplication.chat;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.myapplication.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.myapplication.Authorization.generateAuthToken;

public class ChatManager {
    private static final String APP_API_URL = "http://coms-3090-066.class.las.iastate.edu:8080/";

    private static ChatManager instance;
    private Map<String, ChatRoom> chatRooms;
    private int currentUserId;

    // LiveData for message events
    private MutableLiveData<MessageEvent> messageEvent = new MutableLiveData<>();
    private MutableLiveData<MessageEvent> outgoingMessageEvent = new MutableLiveData<>();

    private ChatManager() {
        chatRooms = new HashMap<>();
    }

    public static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void addChatRoom(ChatRoom chatRoom) {
        chatRooms.put(chatRoom.getChatId(), chatRoom);
    }

    public ChatRoom getChatRoom(String chatId) {
        return chatRooms.get(chatId);
    }

    public List<ChatRoom> getAllChatRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    public List<ChatRoom> getChatRoomsForProfessional(int professionalId) {
        List<ChatRoom> professionalChats = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms.values()) {
            if (chatRoom.getProfessionalIds().contains(professionalId)) {
                professionalChats.add(chatRoom);
            }
        }

        return professionalChats;
    }

    public void removeChatRoom(String chatId) {
        chatRooms.remove(chatId);
    }

    public void updateLastMessage(String chatId, String message) {
        ChatRoom room = chatRooms.get(chatId);
        if (room != null) {
            room.setLastMessage(message);
        }
    }

    public void clear() {
        chatRooms.clear();
    }

    // LiveData for incoming messages from WebSocket
    public LiveData<MessageEvent> getMessageEvent() {
        return messageEvent;
    }

    public void postMessage(String chatId, String message) {
        messageEvent.postValue(new MessageEvent(chatId, message));
    }

    // LiveData for outgoing messages to WebSocket
    public LiveData<MessageEvent> getOutgoingMessageEvent() {
        return outgoingMessageEvent;
    }

    public void sendMessage(String chatId, String message) {
        outgoingMessageEvent.postValue(new MessageEvent(chatId, message));
    }

    // Simple event class for messages
    public static class MessageEvent {
        public final String chatId;
        public final String message;
        public final long timestamp;

        public MessageEvent(String chatId, String message) {
            this.chatId = chatId;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Fetches chat messages from the backend for a specific user.
     * Backend endpoint: GET SERVER_URL/chat/{userId}
     * Response format: [{"groupId": "string", "content": "string", "senderUserId": int}, ...]
     *
     * @param context Application context for Volley requests
     * @param userId The user ID to fetch chats for
     * @param callback Callback to handle the fetched messages
     */
    public void fetchChatsFromBackend(Context context, int userId, ChatFetchCallback callback) {
        String url = APP_API_URL + "chat/" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null, // GET request has no body
            response -> {
                Log.d("ChatManager", "Fetched " + response.length() + " messages from backend");
                List<ChatMessage> messages = new ArrayList<>();
                Map<String, List<ChatMessage>> messagesByGroup = new HashMap<>();

                try {
                    // Parse each message from the response array
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject msgObj = response.getJSONObject(i);

                        String groupId = msgObj.getString("groupId");
                        String content = msgObj.getString("content");
                        int senderUserId = msgObj.getInt("senderUserId");

                        // Determine if message was sent by current user
                        boolean isSentByCurrentUser = (senderUserId == currentUserId);

                        // Create ChatMessage with groupId
                        ChatMessage chatMessage = new ChatMessage(
                            groupId,
                            senderUserId,
                            content,
                            System.currentTimeMillis(), // Backend doesn't provide timestamp, using current time
                            isSentByCurrentUser
                        );

                        messages.add(chatMessage);

                        // Group messages by groupId
                        if (!messagesByGroup.containsKey(groupId)) {
                            messagesByGroup.put(groupId, new ArrayList<>());
                        }
                        messagesByGroup.get(groupId).add(chatMessage);
                    }

                    Log.d("ChatManager", "Parsed messages into " + messagesByGroup.size() + " groups");

                    if (callback != null) {
                        callback.onSuccess(messages, messagesByGroup);
                    }

                } catch (JSONException e) {
                    Log.e("ChatManager", "JSON parse error when fetching chats", e);
                    if (callback != null) {
                        callback.onError("Failed to parse chat messages: " + e.getMessage());
                    }
                }
            },
            error -> {
                Log.e("ChatManager", "Error fetching chats: " + error.toString());
                if (callback != null) {
                    callback.onError("Failed to load chats from server: " + error.getMessage());
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Callback interface for chat fetch operations
     */
    public interface ChatFetchCallback {
        void onSuccess(List<ChatMessage> allMessages, Map<String, List<ChatMessage>> messagesByGroup);
        void onError(String errorMessage);
    }

    /**
     * Fetches all chat groups the user is part of from the backend.
     * GET /chat/groups -> [{id, professionalIds, studentIds, groupName, messageIds, createdOn}, ...]
     *
     * @param context Application context for Volley requests
     * @param callback Callback to handle the fetched groups
     */
    public void fetchChatGroups(Context context, ChatGroupsCallback callback) {
        String url = APP_API_URL + "chat/groups";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                Log.d("ChatManager", "Fetched " + response.length() + " chat groups");
                List<ChatRoom> chatRooms = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        org.json.JSONObject groupObj = response.getJSONObject(i);

                        String id = groupObj.getString("id");
                        String groupName = groupObj.getString("groupName");

                        // Parse professionalIds array
                        List<Integer> professionalIds = new ArrayList<>();
                        org.json.JSONArray profIdsArray = groupObj.optJSONArray("professionalIds");
                        if (profIdsArray != null) {
                            for (int j = 0; j < profIdsArray.length(); j++) {
                                professionalIds.add(profIdsArray.getInt(j));
                            }
                        }

                        // Parse studentIds array
                        List<Integer> studentIds = new ArrayList<>();
                        org.json.JSONArray studentIdsArray = groupObj.optJSONArray("studentIds");
                        if (studentIdsArray != null) {
                            for (int j = 0; j < studentIdsArray.length(); j++) {
                                studentIds.add(studentIdsArray.getInt(j));
                            }
                        }

                        // Build WebSocket URL
                        String wsUrl = "ws://coms-3090-066.class.las.iastate.edu:8080/chat/" + id + "/" + currentUserId;

                        // Create ChatRoom with both professionalIds and studentIds
                        ChatRoom chatRoom = new ChatRoom(id, groupName, professionalIds, studentIds, wsUrl);
                        chatRooms.add(chatRoom);

                        // Add to manager
                        addChatRoom(chatRoom);
                    }

                    Log.d("ChatManager", "Successfully parsed " + chatRooms.size() + " chat groups");

                    if (callback != null) {
                        callback.onSuccess(chatRooms);
                    }

                } catch (JSONException e) {
                    Log.e("ChatManager", "Error parsing chat groups", e);
                    if (callback != null) {
                        callback.onError("Failed to parse chat groups: " + e.getMessage());
                    }
                }
            },
            error -> {
                Log.e("ChatManager", "Error fetching chat groups: " + error.toString());
                if (callback != null) {
                    callback.onError("Failed to load chat groups: " + error.getMessage());
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Callback interface for chat groups fetch operations
     */
    public interface ChatGroupsCallback {
        void onSuccess(List<ChatRoom> chatRooms);
        void onError(String errorMessage);
    }

    /**
     * Updates a chat group.
     * PUT /chat/groups/{id} {professionalIds, studentIds, groupName} -> {id, professionalIds, studentIds, groupName, messageIds, createdOn}
     *
     * @param context Application context for Volley requests
     * @param groupId The ID of the group to update
     * @param professionalIds List of professional IDs
     * @param studentIds List of student IDs
     * @param groupName The group name
     * @param callback Callback to handle the response
     */
    public void updateChatGroup(Context context, String groupId, List<Integer> professionalIds,
                                List<Integer> studentIds, String groupName, ChatGroupUpdateCallback callback) {
        String url = APP_API_URL + "chat/groups/" + groupId;

        try {
            org.json.JSONObject requestBody = new org.json.JSONObject();

            // Add professionalIds array
            org.json.JSONArray profIdsArray = new org.json.JSONArray();
            for (Integer profId : professionalIds) {
                profIdsArray.put(profId);
            }
            requestBody.put("professionalIds", profIdsArray);

            // Add studentIds array
            org.json.JSONArray studentIdsArray = new org.json.JSONArray();
            for (Integer studentId : studentIds) {
                studentIdsArray.put(studentId);
            }
            requestBody.put("studentIds", studentIdsArray);

            // Add groupName
            requestBody.put("groupName", groupName);

            com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest = new com.android.volley.toolbox.JsonObjectRequest(
                Request.Method.PUT,
                url,
                requestBody,
                response -> {
                    Log.d("ChatManager", "Group updated successfully: " + groupId);
                    if (callback != null) {
                        callback.onSuccess(response.toString());
                    }
                },
                error -> {
                    Log.e("ChatManager", "Error updating group: " + error.toString());
                    if (callback != null) {
                        callback.onError("Failed to update group: " + error.getMessage());
                    }
                }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Basic " + generateAuthToken());
                    return headers;
                }
            };

            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

        } catch (org.json.JSONException e) {
            Log.e("ChatManager", "Error building update request: " + e.getMessage());
            if (callback != null) {
                callback.onError("Error building request: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes a chat group.
     * DELETE /chat/groups/{id} -> 200
     *
     * @param context Application context for Volley requests
     * @param groupId The ID of the group to delete
     * @param callback Callback to handle the response
     */
    public void deleteChatGroup(Context context, String groupId, ChatGroupDeleteCallback callback) {
        String url = APP_API_URL + "chat/groups/" + groupId;

        com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
            Request.Method.DELETE,
            url,
            response -> {
                Log.d("ChatManager", "Group deleted successfully: " + groupId);
                // Remove from local cache
                removeChatRoom(groupId);
                if (callback != null) {
                    callback.onSuccess();
                }
            },
            error -> {
                Log.e("ChatManager", "Error deleting group: " + error.toString());
                if (callback != null) {
                    callback.onError("Failed to delete group: " + error.getMessage());
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + generateAuthToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    /**
     * Callback interface for chat group update operations
     */
    public interface ChatGroupUpdateCallback {
        void onSuccess(String response);
        void onError(String errorMessage);
    }

    /**
     * Callback interface for chat group delete operations
     */
    public interface ChatGroupDeleteCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}