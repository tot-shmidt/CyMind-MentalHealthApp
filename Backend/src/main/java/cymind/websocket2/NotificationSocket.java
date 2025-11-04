package cymind.websocket2;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cymind.model.ResourceNotification;
import cymind.repository.ResourceNotificationRepository;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/notifications/{userId}")
public class NotificationSocket {

    // cannot autowire static directly (instead we do it by the below method
    private static ResourceNotificationRepository resourceNotifRepo;

    @Autowired
    public void setResourceNotificationRepo(ResourceNotificationRepository resRepo) {
        resourceNotifRepo = resRepo;
    }

    // Store all socket session and their corresponding username.
    private static Map<Session, String> sessionUseridMap = new Hashtable<>();
    private static Map<String, Session> useridSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(NotificationSocket.class);


    @OnOpen
    // userId must be a student`s one, as this feature is made for students to get notifications.
    public void onOpen(Session session, @PathParam("userId") Long userId) throws IOException {
        logger.info("~~~ Entered in onOpen ~~~");

        // Store connecting user information
        sessionUseridMap.put(session, userId.toString());
        useridSessionMap.put(userId.toString(), session);


        // here connected user has to receive notification history
        sendResNotifHistoryToUser(userId, getResNotifHistory());
    }

    // TO-DO:
    private void sendResNotifHistoryToUser(Long userId, List<ResourceNotification> resNotifHistory) {
    }

    //TO-DO:
    private List<ResourceNotification> getResNotifHistory() {
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId) throws IOException {
        logger.info("Entered into Close");

        // remove the user connection information
        sessionUseridMap.remove(session);
        useridSessionMap.remove(userId);

        // Something else?

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }
}
