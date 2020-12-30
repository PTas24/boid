package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BoidSimulationAsync extends BoidSimulationBase {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationAsync.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;

  public BoidSimulationAsync(BoidSimulationConfig boidSimulationConfig) {
    this.boidSimulationConfig = boidSimulationConfig;
    this.boidPositions = new BoidPositions();
    this.boidModel = boidSimulationConfig.getBoidModel();
    initializeBoids();
  }

  public void initializeBoids() {
    boidModel = boidSimulationConfig.getBoidModel();
    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(toList()));
    executor =
        Executors.newFixedThreadPool(
            Math.min(boidModel.getNumOfBoids(), 100),
            (Runnable r) -> {
              Thread t = new Thread(r);
              t.setDaemon(true);
              return t;
            });
  }

  public BoidModel getBoidModel() {
    return boidModel;
  }

  private void drawTheBoids() {
    messageQueue.push(boidPositions);
  }

  public void startSim() {
    moveTheBoidAsync();
    drawTheBoids();
  }

  void moveTheBoidAsync() {
    List<CompletableFuture<Boid>> newBoidFutures =
        boidPositions.getBoids().stream()
            .map(boid -> moveOneBoid(boid, boidPositions.getBoids(), boidModel))
            .collect(toList());

    List<Boid> newBoid = newBoidFutures.stream().map(CompletableFuture::join).collect(toList());
    boidPositions.setBoids(newBoid);
  }
}
