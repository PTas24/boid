package io.ogi.boid;

import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.simulation.BoidSimulation;
import io.ogi.boid.simulation.BoidSimulationAsync;
import io.ogi.boid.simulation.BoidSimulationReactive;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BoidWebSocketEndpoint extends Endpoint {

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

  public BoidWebSocketEndpoint(BoidSimulationConfig boidSimulationConfig) {
    this.boidSimulation = new BoidSimulation(boidSimulationConfig);
    this.boidSimulationAsync = new BoidSimulationAsync(boidSimulationConfig);
    this.boidSimulationReactive = new BoidSimulationReactive(boidSimulationConfig);
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    LOGGER.info("Opening session " + session.getId());
    MessageQueueTaker messageQueueTaker = new MessageQueueTaker(session);
    executor =
        ThreadPoolSupplier.builder()
            .threadNamePrefix("boid-message-queue-taker-thread")
            .corePoolSize(1)
            .daemon(true)
            .build()
            .get();

    session.addMessageHandler(
        String.class,
        message -> {
          switch (message) {
            case START_SYNC:
              LOGGER.info("start message sync");
              syncronSimulation();
              break;
            case START_ASYNC:
              LOGGER.info("start message async");
              asyncSimulation();
              break;
            case START_REACTIVE:
              reactiveSimulation();
              break;
            case STOP:
              LOGGER.info("stop message");
              scheduledExecutorService.shutdownNow();
              break;
            default:
              throw new IllegalStateException("Unexpected value: " + message);
          }
        });

    executor.execute(messageQueueTaker);
  }

  private void syncronSimulation() {
    scheduledExecutorService = ScheduledThreadPoolSupplier.builder()
        .threadNamePrefix("boid-simulation-s-thread")
        .corePoolSize(1)
        .daemon(true)
        .build()
        .get();
    boidSimulation.initializeBoids();
    scheduledExecutorService.scheduleAtFixedRate(
        boidSimulation::startSimSync,
        5,
        boidSimulation.getBoidModel().getSimulationSpeed(),
        TimeUnit.MILLISECONDS);
  }

  private void asyncSimulation() {
    scheduledExecutorService = ScheduledThreadPoolSupplier.builder()
        .threadNamePrefix("boid-simulation-a-thread")
        .corePoolSize(1)
        .daemon(true)
        .build()
        .get();
    boidSimulationAsync.initializeBoids();
    scheduledExecutorService.scheduleAtFixedRate(
        boidSimulationAsync::startSimAsync,
        5,
        boidSimulationAsync.getBoidModel().getSimulationSpeed(),
        TimeUnit.MILLISECONDS);
  }

  private void reactiveSimulation() {
    LOGGER.info("start message reactive");
    scheduledExecutorService =
        ScheduledThreadPoolSupplier.builder()
            .threadNamePrefix("boid-simulation-r-thread")
            .corePoolSize(1)
            .daemon(true)
            .build()
            .get();
    boidSimulationReactive.initializeBoids();
    boidSimulationReactive.initializeMessaging();
    boidSimulationReactive.startSimReactive();
    scheduledExecutorService.scheduleAtFixedRate(
        boidSimulationReactive::startSimReactive,
        5,
        boidSimulationReactive.getBoidModel().getSimulationSpeed(),
        TimeUnit.MILLISECONDS);
  }


  @Override
  public void onClose(final Session session, final CloseReason closeReason) {
    super.onClose(session, closeReason);

    LOGGER.info("Closing session " + session.getId());
    executor.shutdownNow();
    scheduledExecutorService.shutdownNow();
  }
}
