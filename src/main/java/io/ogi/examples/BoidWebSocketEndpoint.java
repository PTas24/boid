package io.ogi.examples;

import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;

import javax.websocket.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class BoidWebSocketEndpoint extends Endpoint {

    private static final Logger LOGGER = Logger.getLogger(BoidWebSocketEndpoint.class.getName());
    private static final String START_SYNC = "start simulation:sync";
    private static final String START_ASYNC = "start simulation:async";
    private static final String STOP = "stop simulation";
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executor;

    private final BoidSimulation boidSimulation;
    private final BoidSimulationAsync boidSimulationAsync;

    public BoidWebSocketEndpoint(BoidSimulation boidSimulation, BoidSimulationAsync boidSimulationAsync) {
        this.boidSimulation = boidSimulation;
        this.boidSimulationAsync = boidSimulationAsync;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        LOGGER.info("Opening session " + session.getId());
        MessageQueueTaker messageQueueTaker = new MessageQueueTaker(session);
        session.addMessageHandler(String.class, message -> {
            if (START_SYNC.equalsIgnoreCase(message)) {
                LOGGER.info("start message sync");
                scheduledExecutorService = ScheduledThreadPoolSupplier.builder()
                        .threadNamePrefix("boid-simulation-thread")
                        .corePoolSize(1)
                        .daemon(true)
                        .build()
                        .get();

                boidSimulation.initializeBoids();
                scheduledExecutorService.scheduleAtFixedRate(
                        boidSimulation,
                        5,
                        boidSimulation.getBoidModel().getSimulationSpeed(),
                        TimeUnit.MILLISECONDS);
            }
            if (START_ASYNC.equalsIgnoreCase(message)) {
                LOGGER.info("start message async");
                scheduledExecutorService = ScheduledThreadPoolSupplier.builder()
                        .threadNamePrefix("boid-simulation-async-thread")
                        .corePoolSize(1)
                        .daemon(true)
                        .build()
                        .get();

                boidSimulationAsync.initializeBoids();
                scheduledExecutorService.scheduleAtFixedRate(
                        boidSimulationAsync,
                        5,
                        boidSimulationAsync.getBoidModel().getSimulationSpeed(),
                        TimeUnit.MILLISECONDS);
            }
            if (STOP.equalsIgnoreCase(message)) {
                LOGGER.info("stop message");
                scheduledExecutorService.shutdownNow();
            }
            LOGGER.info(() -> "Received message:" + message);
        });

        executor = ThreadPoolSupplier.builder()
                .threadNamePrefix("boid-message-queue-taker-thread")
                .corePoolSize(1)
                .daemon(true)
                .build()
                .get();

        boidSimulation.initializeBoids();
        boidSimulationAsync.initializeBoids();
        executor.execute(messageQueueTaker);
//        executor.submit(boidSimulationAsync::reactiveBoidRun);
    }

    @Override
    public void onClose(final Session session, final CloseReason closeReason) {
        super.onClose(session, closeReason);

        LOGGER.info("Closing session " + session.getId());
        executor.shutdownNow();
        scheduledExecutorService.shutdownNow();
    }
}
