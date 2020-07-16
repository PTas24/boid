package io.ogi.examples;

import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoidWebSocketEndpoint extends Endpoint {

    private static final Logger LOGGER = Logger.getLogger(BoidWebSocketEndpoint.class.getName());
    private static ScheduledExecutorService scheduledExecutorService;
    private static ExecutorService executor;

    private final MessageQueue messageQueue = MessageQueue.instance();
    private final BoidSimulation boidSimulation;
    //    private int counter;

    public BoidWebSocketEndpoint(BoidSimulation boidSimulation) {
        this.boidSimulation = boidSimulation;
//        counter = 0;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        LOGGER.info("Opening session " + session.getId());
//        counter = 0;
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

        Runnable messageQueueTaker = () -> {
            while(session.isOpen()) {
                if (!messageQueue.isEmpty()) {
                    try {
//                        System.out.println(messageQueue.peek());
                        session.getBasicRemote().sendObject(messageQueue.pop());
                    } catch (IOException | EncodeException e) {
                        LOGGER.log(Level.SEVERE, "Message sending failed", e);
                    }
                }
            }
        };

        executor.execute(messageQueueTaker);
        scheduledExecutorService.scheduleAtFixedRate(boidSimulation, 5,boidSimulation.getBoidModel().simulationSpeed, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClose(final Session session, final CloseReason closeReason) {
        super.onClose(session, closeReason);

        LOGGER.info("Closing session " + session.getId());
        executor.shutdown();
        scheduledExecutorService.shutdown();
    }


}
