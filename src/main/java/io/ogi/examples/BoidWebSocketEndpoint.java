package io.ogi.examples;

import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;

import javax.websocket.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class BoidWebSocketEndpoint extends Endpoint {

  public interface SimulationMode {
    void applySimulation();
  }

  private static final Logger LOGGER = Logger.getLogger(BoidWebSocketEndpoint.class.getName());
  private static final String START_SYNC = "start simulation:sync";
  private static final String START_ASYNC = "start simulation:async";
  private static final String START_REACTIVE = "start simulation:reactive";
  private static final String STOP = "stop simulation";
  private ScheduledExecutorService scheduledExecutorService;
  private ExecutorService executor;

  private final BoidSimulation boidSimulation;
  private final BoidSimulationAsync boidSimulationAsync;
  private final BoidSimulationReactive boidSimulationReactive;
  private final Map<String, SimulationMode> messageMap =
      Map.of(
          START_SYNC, this::syncronSimulation,
          START_ASYNC, this::asyncronSimulation,
          START_REACTIVE, this::reactiveSimulation,
          STOP, this::stopSimulation);

  public BoidWebSocketEndpoint(
      BoidSimulation boidSimulation, BoidSimulationAsync boidSimulationAsync) {
    this.boidSimulation = boidSimulation;
    this.boidSimulationAsync = boidSimulationAsync;
    this.boidSimulationReactive = new BoidSimulationReactive(new BoidSimulationConfig(null));
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    LOGGER.info("Opening session " + session.getId());
    session.addMessageHandler(String.class, this::startSimulation);

    boidSimulation.initializeBoids();
    boidSimulationAsync.initializeBoids();
    executor =
        ThreadPoolSupplier.builder()
            .threadNamePrefix("boid-message-queue-taker-thread")
            .corePoolSize(1)
            .daemon(true)
            .build()
            .get();
    MessageQueueTaker messageQueueTaker = new MessageQueueTaker(session);
    executor.execute(messageQueueTaker);
    //        executor.submit(boidSimulationAsync::reactiveBoidRun);
  }

  private void startSimulation(String message) {
    messageMap.get(message).applySimulation();
    LOGGER.info(() -> "Received message:" + message);
  }

  private void syncronSimulation() {
    LOGGER.info("start message sync");
    scheduledExecutorService =
        ScheduledThreadPoolSupplier.builder()
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

  private void asyncronSimulation() {
    LOGGER.info("start message async");
    scheduledExecutorService =
        ScheduledThreadPoolSupplier.builder()
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

  private void reactiveSimulation() {
    LOGGER.info("start message reactive");
    scheduledExecutorService =
        ScheduledThreadPoolSupplier.builder()
            .threadNamePrefix("boid-simulation-async-thread")
            .corePoolSize(1)
            .daemon(true)
            .build()
            .get();

    boidSimulationReactive.initializeBoids();
    scheduledExecutorService.scheduleAtFixedRate(
        boidSimulationReactive::startSim,
        5,
        boidSimulationReactive.getBoidModel().getSimulationSpeed(),
        TimeUnit.MILLISECONDS);
  }

  private void stopSimulation() {
    LOGGER.info("stop message");
    scheduledExecutorService.shutdownNow();
  }

  @Override
  public void onClose(final Session session, final CloseReason closeReason) {
    super.onClose(session, closeReason);

    LOGGER.info("Closing session " + session.getId());
    executor.shutdownNow();
    scheduledExecutorService.shutdownNow();
  }
}
