package io.ogi.boid;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageQueueTaker  implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(MessageQueueTaker.class.getName());
    private final MessageQueue messageQueue = MessageQueue.instance();
    private final Session session;

    public MessageQueueTaker(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        while(session.isOpen()) {
            if (!messageQueue.isEmpty()) {
                try {
                    //                        System.out.println(messageQueue.peek());

                    session.getBasicRemote().sendObject(messageQueue.pop());
                } catch (IOException | EncodeException | IllegalArgumentException e) {
                    LOGGER.log(Level.SEVERE, "Message sending failed", e);
                }
            }
        }
    }
}
