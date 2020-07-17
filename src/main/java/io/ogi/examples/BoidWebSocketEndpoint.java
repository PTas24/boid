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
    private static final String START = "start simulation";
    private static final String STOP = "stop simulation";
    private static ScheduledExecutorService scheduledExecutorService;
    private static ExecutorService executor;

    private final MessageQueue messageQueue = MessageQueue.instance();
    private final BoidSimulation boidSimulation;
    private MessageQueueTaker messageQueueTaker;
    //    private int counter;

    public BoidWebSocketEndpoint(BoidSimulation boidSimulation) {
        this.boidSimulation = boidSimulation;
//        counter = 0;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        LOGGER.info("Opening session " + session.getId());
        messageQueueTaker = new MessageQueueTaker(session);
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                if (message.equalsIgnoreCase(START)) {
                    System.out.println("here");
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

                    boidSimulation.initializeBoids();
                    executor.execute(messageQueueTaker);
                    scheduledExecutorService.scheduleAtFixedRate(boidSimulation, 5,boidSimulation.getBoidModel().simulationSpeed, TimeUnit.MILLISECONDS);
                }
                if (message.equalsIgnoreCase(STOP)) {
                    System.out.println("there");
                    executor.shutdownNow();
                    scheduledExecutorService.shutdownNow();
                }
                System.out.println("Received message:"+ message + ":");
            }
        });

//        counter = 0;
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
//
//        boidSimulation.initializeBoids();
//        executor.execute(messageQueueTaker);
//        scheduledExecutorService.scheduleAtFixedRate(boidSimulation, 5,boidSimulation.getBoidModel().simulationSpeed, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClose(final Session session, final CloseReason closeReason) {
        super.onClose(session, closeReason);

        LOGGER.info("Closing session " + session.getId());
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        if (!scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }


}
