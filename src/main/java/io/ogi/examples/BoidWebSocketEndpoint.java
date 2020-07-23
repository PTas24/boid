package io.ogi.examples;

import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;

import javax.websocket.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class BoidWebSocketEndpoint extends Endpoint {

    private static final Logger LOGGER = Logger.getLogger(BoidWebSocketEndpoint.class.getName());
    private static final String START = "start simulation";
    private static final String STOP = "stop simulation";
    private static ScheduledExecutorService scheduledExecutorService;
    private static ExecutorService executor;

    private final BoidSimulation boidSimulation;

    public BoidWebSocketEndpoint(BoidSimulation boidSimulation) {
        this.boidSimulation = boidSimulation;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        LOGGER.info("Opening session " + session.getId());
        MessageQueueTaker messageQueueTaker = new MessageQueueTaker(session);
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                if (message.equalsIgnoreCase(START)) {
                    LOGGER.info("start message");
                    scheduledExecutorService = ScheduledThreadPoolSupplier.builder()
                            .threadNamePrefix("boid-simulation-thread")
                            .corePoolSize(1)
                            .daemon(false)
                            .build()
                            .get();

                    boidSimulation.initializeBoids();
                    scheduledExecutorService.scheduleAtFixedRate(boidSimulation, 5, boidSimulation.getBoidModel().getSimulationSpeed(), TimeUnit.MILLISECONDS);
                }
                if (message.equalsIgnoreCase(STOP)) {
                    LOGGER.info("stop message");
                    scheduledExecutorService.shutdownNow();
                }
                System.out.println("Received message:"+ message + ":");
            }
        });

        executor = ThreadPoolSupplier.builder()
                .threadNamePrefix("boid-message-queue-taker-thread")
                .corePoolSize(1)
                .daemon(false)
                .build()
                .get();

        boidSimulation.initializeBoids();
        executor.execute(messageQueueTaker);
    }

    @Override
    public void onClose(final Session session, final CloseReason closeReason) {
        super.onClose(session, closeReason);

        LOGGER.info("Closing session " + session.getId());
        executor.shutdownNow();
        scheduledExecutorService.shutdownNow();
    }
}
