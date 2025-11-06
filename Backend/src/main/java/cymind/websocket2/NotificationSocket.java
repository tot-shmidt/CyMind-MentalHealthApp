package cymind.websocket2;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import cymind.dto.article.ArticleNotificationDTO;
import cymind.model.Article;
import cymind.model.ResourceNotification;
import cymind.repository.ResourceNotificationRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/notifications/{userId}")
public class NotificationSocket {
    private static final Logger logger = LoggerFactory.getLogger(NotificationSocket.class);
    // Jackson ObjectMapper for JSON conversion
    private static ObjectMapper objectMapper;
    // cannot autowire static directly (instead we do it by the below method
    private static ResourceNotificationRepository resourceNotifRepo;

    @Autowired
    public void setObjectMapper(ObjectMapper mapper) {
        NotificationSocket.objectMapper = mapper;
    }

    @Autowired
    public void setResourceNotificationRepo(ResourceNotificationRepository resRepo) {
        resourceNotifRepo = resRepo;
    }

    // Store all socket session and their corresponding username.
    private static Map<Session, Long> sessionUseridMap = new Hashtable<>();
    private static Map<Long, Session> useridSessionMap = new Hashtable<>();



    @OnOpen
    // userId must be a student`s one, as this feature is made for students to get notifications.
    public void onOpen(Session session, @PathParam("userId") Long userId) throws IOException {
        logger.info("Connection opened for User ID: {}", userId);

        // Store connecting user information
        sessionUseridMap.put(session, userId);
        useridSessionMap.put(userId, session);

        // Send history if it exists
        sendRecentHistory(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        logger.info("Received Message from client: {}", message);

        for (Session s : sessionUseridMap.keySet()) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(message);
            }
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException { // --- FIXED SIGNATURE ---
        // We get the userId from our map, not from the path
        Long userId = sessionUseridMap.get(session);

        if (userId != null) {
            logger.info("Connection closed for User ID: {}", userId);
            sessionUseridMap.remove(session);
            useridSessionMap.remove(userId);
        } else {
            logger.warn("Closed a session that had no associated user ID.");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket Error for session {}: {}", session.getId(), throwable.getMessage());
        // Cleaning up session.
        Long userId = sessionUseridMap.get(session);
        if (userId != null) {
            useridSessionMap.remove(userId);
            sessionUseridMap.remove(session);
        }
    }

    /**
     * Called by ArticleService to notify all users of a new article.
     */
    public static void broadcastNewArticle(Article article) {
        ResourceNotification entity = new ResourceNotification(
                "New Article: " + article.getArticleName(), article
        );
        resourceNotifRepo.save(entity);

        // Create DTO for live broadcast
        ArticleNotificationDTO dto = new ArticleNotificationDTO(
                article.getArticleName(),
                "New Article just added!",
                article.getId()
        );

        // Broadcast to all connected users
        broadcastToAll(dto);
    }

    /**
     * Helper method to send a DTO to all connected sessions.
     */
    private static void broadcastToAll(ArticleNotificationDTO dto) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(dto);

            sessionUseridMap.keySet().forEach(session -> {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(jsonMessage);
                }
            });
        } catch (IOException e) {
            logger.error("Error serializing or broadcasting JSON: {}", e.getMessage());
        }
    }

    /**
     * Sends recent notification history to a single user who just connected.
     */
    private void sendRecentHistory(Session session) {
        List<ResourceNotification> history = resourceNotifRepo.findTop2ByOrderByTimestampDesc();

        // Loop backwards to send oldest first
        for (int i = history.size() - 1; i >= 0; i--) {
            ResourceNotification notif = history.get(i);

            String articleName = "Article";
            Long articleId = null;

            // Get real data from the related article
            if (notif.getRelatedArticle() != null) {
                articleName = notif.getRelatedArticle().getArticleName();
                articleId = notif.getRelatedArticle().getId();
            }

            // Populate the DTO
            ArticleNotificationDTO dto = new ArticleNotificationDTO(
                    articleName,
                    notif.getMessage(),
                    articleId
            );

            try {
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(dto));
            } catch (IOException e) {
                logger.error("Error sending history message to user: {}", e.getMessage());
            }
        }
    }

    /**
     * Send notificationDTO to a specific user, if are online
     */
    public static void sendNotificationToUser(Long userId, Object notificationDTO) {
        Session session = useridSessionMap.get(userId);

        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(notificationDTO);

                session.getAsyncRemote().sendText(jsonMessage);
                logger.info("Sent notification to user {}: {}", userId, jsonMessage);

            } catch (IOException e) {
                logger.error("Error serializing or sending notification to user {}: {}", userId, e.getMessage());
            }
        } else {
            logger.info("User {} is not online. Live notification not sent.", userId);
        }
    }
}
