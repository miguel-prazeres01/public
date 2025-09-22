package domain.websocket;

import jakarta.websocket.*;

public interface NotificationWebSocket {
    void onOpen(Session session);

    void onClose(Session session);

    void onMessage(String message, Session session);

    void sendNotification(String sessionId, String message);

}
