package io.ogi.examples;

import io.ogi.examples.model.BoidPosition;

import javax.websocket.*;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoidWebSocketEndpoint extends Endpoint {

    private static final Logger LOGGER = Logger.getLogger(BoidWebSocketEndpoint.class.getName());
    ExecutorService executor = Executors.newFixedThreadPool(1);
    private final MessageQueue messageQueue = MessageQueue.instance();
    Session session;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;

        System.out.println("BoidWebSocketEndpoint onOpen");
        Runnable messageQueueTaker = () -> {
            while(!Thread.interrupted()) {
                if (!messageQueue.isEmpty()) {
                    try {
                        session.getBasicRemote().sendObject(messageQueue.pop());
                    } catch (IOException | EncodeException e) {
                        LOGGER.log(Level.SEVERE, "Message sending failed", e);
                    }
                }
            }
        };

        executor.execute(messageQueueTaker);
    }

    @Override
    public void onClose(final Session session, final CloseReason closeReason) {
        super.onClose(session, closeReason);
        LOGGER.info("Closing session " + session.getId());
        executor.shutdown();
    }


}
