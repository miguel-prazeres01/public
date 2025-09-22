package infrastructure.seat.rest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;

@ClientEndpoint
public class NotificationClientEndpoint {
    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @OnMessage
    public void onMessage(String message) {
        messages.add(message);
    }

    public String getNextMessage() throws InterruptedException {
        // Wait up to 10 seconds for a message
        return messages.poll(10, java.util.concurrent.TimeUnit.SECONDS);
    }
}
