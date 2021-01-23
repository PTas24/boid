package io.ogi.boid.corealgorithm;

import io.ogi.boid.model.Boid;
import io.ogi.boid.model.BoidModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static io.ogi.boid.corealgorithm.BoidTransformation.*;

public class BoidMoves {

  public CompletableFuture<Boid> moveOneBoid(Boid boid, List<Boid> boids, BoidModel boidModel, Executor executor) {
    List<Boid> cohesionNeighbours = getNeighbors(boid, boids, boidModel.getCohesionRange());
    List<Boid> separationNeighbours = getNeighbors(boid, boids, boidModel.getSeparationRange());
    List<Boid> alignmentNeighbours = getNeighbors(boid, boids, boidModel.getAlignmentRange());
    double centerX =
        getAverageValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getX));
    double centerY =
        getAverageValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getY));
    List<Integer> othersXPositions =
        separationNeighbours.stream().map(Boid::getX).collect(Collectors.toList());
    List<Integer> othersYPositions =
        separationNeighbours.stream().map(Boid::getY).collect(Collectors.toList());
    double averageXVelocity =
        getAverageValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDx));
    double averageYVelocity =
        getAverageValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDy));

    return CompletableFuture.supplyAsync(
        () -> {
          double velocityX1 =
              flyTowardsCenter(boid.getX(), centerX, boidModel.getCohesionFactor());
          double velocityX2 =
              keepDistance(boid.getX(), othersXPositions, boidModel.getSeparationFactor());
          double velocityX3 = matchVelocity(averageXVelocity, boidModel.getAlignmentFactor());
          double velocityY1 =
              flyTowardsCenter(boid.getY(), centerY, boidModel.getCohesionFactor());
          double velocityY2 =
              keepDistance(boid.getY(), othersYPositions, boidModel.getSeparationFactor());
          double velocityY3 = matchVelocity(averageYVelocity, boidModel.getAlignmentFactor());
          double xVelocity = boid.getDx() + velocityX1 + velocityX2 + velocityX3;
          double yVelocity = boid.getDy() + velocityY1 + velocityY2 + velocityY3;
          double speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
          if (speed > boidModel.getSpeedLimit()) {
            xVelocity = (boid.getDx() / speed) * boidModel.getSpeedLimit();
            yVelocity = (boid.getDy() / speed) * boidModel.getSpeedLimit();
          }
          xVelocity =
              keepWithinBounds(
                  boid.getX(),
                  xVelocity,
                  boidModel.getCanvasMargin(),
                  boidModel.getCanvasWidth(),
                  boidModel.getSpeedAdjust());
          yVelocity =
              keepWithinBounds(
                  boid.getY(),
                  yVelocity,
                  boidModel.getCanvasMargin(),
                  boidModel.getCanvasHeight(),
                  boidModel.getSpeedAdjust());
          return new Boid(
              (int) Math.round(boid.getX() + xVelocity),
              (int) Math.round(boid.getY() + yVelocity),
              xVelocity,
              yVelocity);
        },
        executor)
        .exceptionally(ex -> new Boid(boid.getX(), boid.getY(), boid.getDx(), boid.getDy()));
  }

  public Boid moveOneBoidSync(Boid boid, List<Boid> boids, BoidModel boidModel) {
    List<Boid> cohesionNeighbours = getNeighbors(boid, boids, boidModel.getCohesionRange());
    List<Boid> separationNeighbours = getNeighbors(boid, boids, boidModel.getSeparationRange());
    List<Boid> alignmentNeighbours = getNeighbors(boid, boids, boidModel.getAlignmentRange());
    double centerX =
        getAverageValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getX));
    double centerY =
        getAverageValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getY));
    List<Integer> othersXPositions =
        separationNeighbours.stream().map(Boid::getX).collect(Collectors.toList());
    List<Integer> othersYPositions =
        separationNeighbours.stream().map(Boid::getY).collect(Collectors.toList());
    double averageXVelocity =
        getAverageValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDx));
    double averageYVelocity =
        getAverageValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDy));

    double velocityX1 =
        flyTowardsCenter(boid.getX(), centerX, boidModel.getCohesionFactor());
    double velocityX2 =
        keepDistance(boid.getX(), othersXPositions, boidModel.getSeparationFactor());
    double velocityX3 = matchVelocity(averageXVelocity, boidModel.getAlignmentFactor());
    double velocityY1 =
        flyTowardsCenter(boid.getY(), centerY, boidModel.getCohesionFactor());
    double velocityY2 =
        keepDistance(boid.getY(), othersYPositions, boidModel.getSeparationFactor());
    double velocityY3 = matchVelocity(averageYVelocity, boidModel.getAlignmentFactor());
    double xVelocity = boid.getDx() + velocityX1 + velocityX2 + velocityX3;
    double yVelocity = boid.getDy() + velocityY1 + velocityY2 + velocityY3;
    double speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
    if (speed > boidModel.getSpeedLimit()) {
      xVelocity = (boid.getDx() / speed) * boidModel.getSpeedLimit();
      yVelocity = (boid.getDy() / speed) * boidModel.getSpeedLimit();
    }
    xVelocity =
        BoidTransformation.keepWithinBounds(
            boid.getX(),
            xVelocity,
            boidModel.getCanvasMargin(),
            boidModel.getCanvasWidth(),
            boidModel.getSpeedAdjust());
    yVelocity =
        BoidTransformation.keepWithinBounds(
            boid.getY(),
            yVelocity,
            boidModel.getCanvasMargin(),
            boidModel.getCanvasHeight(),
            boidModel.getSpeedAdjust());
    return new Boid(
        (int) Math.round(boid.getX() + xVelocity),
        (int) Math.round(boid.getY() + yVelocity),
        xVelocity,
        yVelocity);
  }

  private double getAverageValue(List<Boid> neighbours, DoubleStream doubleStream) {
    if (neighbours.isEmpty()) {
      return 0;
    }
    return doubleStream.sum() / neighbours.size();
  }
}
