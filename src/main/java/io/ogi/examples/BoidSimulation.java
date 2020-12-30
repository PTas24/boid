package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static io.ogi.examples.BoidTransformation2.*;
import static io.ogi.examples.BoidTransformation2.keepWithinBounds;

public class BoidSimulation extends BoidSimulationBase {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulation.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;

  public BoidSimulation(BoidSimulationConfig boidSimulationConfig) {
    this.boidSimulationConfig = boidSimulationConfig;
    this.boidPositions = new BoidPositions();
    initializeBoids();
  }

  public void initializeBoids() {
    boidModel = boidSimulationConfig.getBoidModel();
    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(Collectors.toList()));
  }

  void setBoidPositions(List<Boid> boids) {
    boidPositions.setBoids(boids);
  }

  public BoidPositions getBoidPositions() {
    return boidPositions;
  }

  public BoidModel getBoidModel() {
    return boidModel;
  }

  void moveTheBoids() {
    List<Boid> boidList =
        boidPositions.getBoids().stream()
            .map(b -> moveOneBoidSync(b, boidPositions.getBoids(), boidModel))
            .collect(Collectors.toList());
    boidPositions.setBoids(boidList);
  }

//  private Boid moveOneBoidSync(Boid boid, List<Boid> boids, BoidModel boidModel) {
//    List<Boid> cohesionNeighbours = getNeighbors(boid, boids, boidModel.getCohesionRange());
//    List<Boid> separationNeighbours = getNeighbors(boid, boids, boidModel.getSeparationRange());
//    List<Boid> alignmentNeighbours = getNeighbors(boid, boids, boidModel.getAlignmentRange());
//    double centerX =
//        getAverageValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getX));
//    double centerY =
//        getAverageValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getY));
//    List<Integer> othersXPositions =
//        separationNeighbours.stream().map(Boid::getX).collect(Collectors.toList());
//    List<Integer> othersYPositions =
//        separationNeighbours.stream().map(Boid::getY).collect(Collectors.toList());
//    double averageXVelocity =
//        getAverageValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDx));
//    double averageYVelocity =
//        getAverageValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDy));
//
//    double velocityX1 =
//        flyTowardsCenter(boid.getX(), centerX, boidModel.getCohesionFactor());
//    double velocityX2 =
//        keepDistance(boid.getX(), othersXPositions, boidModel.getSeparationFactor());
//    double velocityX3 = matchVelocity(averageXVelocity, boidModel.getAlignmentFactor());
//    double velocityY1 =
//        flyTowardsCenter(boid.getY(), centerY, boidModel.getCohesionFactor());
//    double velocityY2 =
//        keepDistance(boid.getY(), othersYPositions, boidModel.getSeparationFactor());
//    double velocityY3 = matchVelocity(averageYVelocity, boidModel.getAlignmentFactor());
//    double xVelocity = boid.getDx() + velocityX1 + velocityX2 + velocityX3;
//    double yVelocity = boid.getDy() + velocityY1 + velocityY2 + velocityY3;
//    double speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
//    if (speed > boidModel.getSpeedLimit()) {
//      xVelocity = (boid.getDx() / speed) * boidModel.getSpeedLimit();
//      yVelocity = (boid.getDy() / speed) * boidModel.getSpeedLimit();
//    }
//    xVelocity =
//        BoidTransformation2.keepWithinBounds(
//            boid.getX(),
//            xVelocity,
//            boidModel.getCanvasMargin(),
//            boidModel.getCanvasWidth(),
//            boidModel.getSpeedAdjust());
//    yVelocity =
//        BoidTransformation2.keepWithinBounds(
//            boid.getY(),
//            yVelocity,
//            boidModel.getCanvasMargin(),
//            boidModel.getCanvasHeight(),
//            boidModel.getSpeedAdjust());
//    return new Boid(
//        (int) Math.round(boid.getX() + xVelocity),
//        (int) Math.round(boid.getY() + yVelocity),
//        xVelocity,
//        yVelocity);
//  }

//  private double getAverageValue(List<Boid> neighbours, DoubleStream doubleStream) {
//    if (neighbours.isEmpty()) {
//      return 0;
//    }
//    return doubleStream.sum() / neighbours.size();
//  }

  private void drawTheBoids() {
    messageQueue.push(boidPositions);
  }

  public void startSim() {
    moveTheBoids();
    drawTheBoids();
  }
}
