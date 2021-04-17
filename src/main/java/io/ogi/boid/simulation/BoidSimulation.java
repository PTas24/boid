package io.ogi.boid.simulation;

import io.ogi.boid.MessageQueue;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.corealgorithm.BoidMoves;
import io.ogi.boid.model.Boid;
import io.ogi.boid.model.BoidModel;
import io.ogi.boid.model.BoidPositions;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoidSimulation {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulation.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;
  private final BoidMoves boidMoves = new BoidMoves();

  public BoidSimulation(BoidSimulationConfig boidSimulationConfig) {
    this.boidSimulationConfig = boidSimulationConfig;
    this.boidPositions = new BoidPositions();
    initializeBoids();
  }

  public void initializeBoids() {
    boidModel = boidSimulationConfig.getBoidModel();
//    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(Collectors.toList()));
  }

  void setBoidPositions(List<Boid> boids) {
    boidPositions.setBoids(boids);
  }

  BoidPositions getBoidPositions() {
    return boidPositions;
  }

  public BoidModel getBoidModel() {
    return boidModel;
  }

  void moveTheBoids() {
    List<Boid> boidList =
        boidPositions.getBoids().stream()
            .map(b -> boidMoves.moveOneBoidSync(b, boidPositions.getBoids(), boidModel))
            .collect(Collectors.toList());
    boidPositions.setBoids(boidList);
  }

  private void drawTheBoids() {
    messageQueue.push(boidPositions);
  }

  public void startSimSync() {
    moveTheBoids();
    drawTheBoids();
  }
}
