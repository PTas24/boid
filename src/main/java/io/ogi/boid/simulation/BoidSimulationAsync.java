package io.ogi.boid.simulation;

import io.ogi.boid.MessageQueue;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.corealgorithm.BoidMoves;
import io.ogi.boid.model.Boid;
import io.ogi.boid.model.BoidModel;
import io.ogi.boid.model.BoidPositions;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BoidSimulationAsync {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationAsync.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;
  private final BoidMoves boidMoves = new BoidMoves();
  private Executor executor;

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

  public void startSimAsync() {
    moveTheBoidAsync();
    drawTheBoids();
  }

  void moveTheBoidAsync() {
    List<CompletableFuture<Boid>> newBoidFutures =
        boidPositions.getBoids().stream()
            .map(boid -> boidMoves.moveOneBoid(boid, boidPositions.getBoids(), boidModel, executor))
            .collect(toList());

    List<Boid> newBoid = newBoidFutures.stream().map(CompletableFuture::join).collect(toList());
    boidPositions.setBoids(newBoid);
  }

  private void drawTheBoids() {
//    System.out.println("async draw");
    messageQueue.push(boidPositions);
  }


  void setBoidPositions(List<Boid> boids) {
    boidPositions.setBoids(boids);
  }

  BoidPositions getBoidPositions() {
    return boidPositions;
  }

}
