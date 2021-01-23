package io.ogi.boid;

import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.simulation.BoidSimulation;
import io.ogi.boid.simulation.BoidSimulationAsync;
import io.ogi.boid.simulation.BoidSimulationReactive;

import javax.websocket.*;
import java.util.concurrent.*;
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
    executor =
        ThreadPoolSupplier.builder()
            .threadNamePrefix("boid-message-queue-taker-thread")
            .corePoolSize(1)
            .daemon(true)
            .build()
            .get();
    scheduledExecutorService =
        ScheduledThreadPoolSupplier.builder()
            .threadNamePrefix("boid-simulation-async-thread")
            .corePoolSize(1)
            .daemon(true)
            .build()
            .get();

    session.addMessageHandler(
        String.class,
        message -> {
          switch (message) {
            case START_SYNC:
              syncronSimulation();
              break;
            case START_ASYNC:
              asyncSimulation();
              break;
            case START_REACTIVE:
              reactiveSimulation();
              break;
            case STOP:
              stopSimulation();
              break;
            default:
              throw new IllegalStateException("Unexpected value: " + message);
          }
        });

    MessageQueueTaker messageQueueTaker = new MessageQueueTaker(session);
    executor.execute(messageQueueTaker);
  }

  private void syncronSimulation() {
    LOGGER.info("start message sync");
    boidSimulation.initializeBoids();
    scheduledExecutorService.scheduleAtFixedRate(
        boidSimulation::startSim,
        5,
        boidSimulation.getBoidModel().getSimulationSpeed(),
        TimeUnit.MILLISECONDS);
  }

  private void asyncSimulation() {
    LOGGER.info("start message async");
    boidSimulationAsync.initializeBoids();
    scheduledExecutorService.scheduleAtFixedRate(
        boidSimulationAsync::startSim,
        5,
        boidSimulationAsync.getBoidModel().getSimulationSpeed(),
        TimeUnit.MILLISECONDS);
  }

  private void reactiveSimulation() {
    LOGGER.info("start message reactive");
    boidSimulationReactive.initializeBoids();
    boidSimulationReactive.initializeMessaging();
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
