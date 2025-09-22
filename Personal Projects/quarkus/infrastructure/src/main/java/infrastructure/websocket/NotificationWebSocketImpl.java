package infrastructure.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import domain.websocket.NotificationWebSocket;

import java.io.IOException;

@ServerEndpoint("/notifications")
public class NotificationWebSocketImpl implements NotificationWebSocket{

    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @Inject
    JWTParser jwtParser;

    @OnOpen
    @Override
    public void onOpen(Session session) {
        try {
            String query = session.getRequestURI().getQuery(); 
            Map<String, String> params = parseQuery(query);
            String token = params.get("token");
            if (token != null){
                token = token.substring(0, token.length()-6);
            }
            System.out.println("CLEAN TOKEN: " + token);

            if (token == null) {
                session.close();
                return;
            }

            var jwt = jwtParser.parse(token);

            String sessionId = jwt.getClaim("upn");

            if (sessionId == null || sessionId.isEmpty()) {
                session.close();
                return;
            }

            // Map sessionId to this WebSocket session
            sessionMap.put(sessionId, session);
            System.out.println("WebSocket connected for sessionId: " + sessionId);

        } catch (Exception e) {
            try {
                e.printStackTrace();
                session.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @OnClose
    @Override
    public void onClose(Session session) {
        sessionMap.values().remove(session);
        System.out.println("WebSocket closed: " + session.getId());
    }

    @OnMessage
    @Override
    public void onMessage(String message, Session session) {
        // Optional: handle incoming messages
    }

    @Override
    public void sendNotification(String sessionId, String message) {
        try{
            Session session = sessionMap.get(sessionId);
            if (session != null && session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    private Map<String, String> parseQuery(String query) {
        if (query == null) return Map.of();
        return java.util.Arrays.stream(query.split("&"))
            .map(p -> p.split("="))
            .collect(Collectors.toMap(a -> a[0], a -> a[1]));
    }
}
