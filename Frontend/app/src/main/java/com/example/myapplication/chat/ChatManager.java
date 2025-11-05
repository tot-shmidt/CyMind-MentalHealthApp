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
}