package io.ogi.examples;

import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;
import io.ogi.examples.model.BoidPosition;

import javax.websocket.*;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoidWebSocketEndpoint extends Endpoint {

    private static final Logger LOGGER = Logger.getLogger(BoidWebSocketEndpoint.class.getName());
    private static ScheduledExecutorService scheduledExecutorService;
    private static ExecutorService executor;

    private final MessageQueue messageQueue = MessageQueue.instance();
    private BoidSimulation boidSimulation;
    private Session session;

//    static {
//        scheduledExecutorService = ScheduledThreadPoolSupplier.builder()
//                .threadNamePrefix("boid-simulation-thread")
//                .corePoolSize(1)
//                .daemon(false)
//                .build()
//                .get();
//        executor = ThreadPoolSupplier.builder()
//                .threadNamePrefix("boid-message-queue-taker-thread")
//                .corePoolSize(1)
//                .daemon(true)
//                .build()
//                .get();
//    }

    public BoidWebSocketEndpoint(BoidSimulation boidSimulation) {
        this.boidSimulation = boidSimulation;
        //System.out.println("stated: " + boidSimulation.boidPosition.toString());
        //System.out.println("ttt");
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        scheduledExecutorService = ScheduledThreadPoolSupplier.builder()
                .threadNamePrefix("boid-simulation-thread")
                .corePoolSize(1)
                .daemon(false)
                .build()
                .get();
        executor = ThreadPoolSupplier.builder()
                .threadNamePrefix("boid-message-queue-taker-thread")
                .corePoolSize(1)
                .daemon(true)
                .build()
                .get();

        System.out.println("BoidWebSocketEndpoint onOpen");
        Runnable messageQueueTaker = () -> {
            while(session.isOpen()) {
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
        scheduledExecutorService.scheduleAtFixedRate(boidSimulation, 10,10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClose(final Session session, final CloseReason closeReason) {
        super.onClose(session, closeReason);

        LOGGER.info("Closing session " + session.getId());
        executor.shutdown();
        scheduledExecutorService.shutdown();
    }


}
