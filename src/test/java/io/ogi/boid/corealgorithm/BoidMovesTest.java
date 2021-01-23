package io.ogi.boid.corealgorithm;

import io.ogi.boid.model.Boid;
import io.ogi.boid.model.BoidModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class BoidMovesTest {

  static BoidModel boidModel;
  static Executor executor;

  @BeforeAll
  static void setup() {
    boidModel = new BoidModel();
    boidModel.setCanvasWidth(200);
    boidModel.setCanvasHeight(200);
    boidModel.setCanvasMargin(10);
    boidModel.setSpeedAdjust(1);
    boidModel.setNumOfBoids(200);
    boidModel.setCohesionRange(75);
    boidModel.setSeparationRange(20);
    boidModel.setAlignmentRange(75);
    boidModel.setCohesionFactor(0.005);
    boidModel.setSeparationFactor(0.05);
    boidModel.setAlignmentFactor(0.05);
    boidModel.setSpeedLimit(15);
    boidModel.setSimulationSpeed(5);
    boidModel.setInitialMaxSpeed(5);

    executor =
        Executors.newFixedThreadPool(
            Math.min(boidModel.getNumOfBoids(), 100),
            (Runnable r) -> {
              Thread t = new Thread(r);
              t.setDaemon(true);
              return t;
            });
  }

  @Test
  void moveOneBoid() throws ExecutionException, InterruptedException {
    Boid boid = new Boid(10,10,2,2);
    List<Boid> neighbors = List.of(
        new Boid(12,12,2,1),
        new Boid(8,8,-2,1),
        new Boid(10,12,2,1),
        new Boid(10,8,1,-1)
    );

    CompletableFuture<Boid> nextBoidState = new BoidMoves().moveOneBoid(boid, neighbors, boidModel, executor);
    Assertions.assertEquals(new Boid(12,12,2.0375, 2.025), nextBoidState.get());
  }

  @Test
  void moveOneBoidSyncTest() {
    Boid boid = new Boid(10,10,2,2);
    List<Boid> neighbors = List.of(
        new Boid(12,12,2,1),
        new Boid(8,8,-2,1),
        new Boid(10,12,2,1),
        new Boid(10,8,1,-1)
    );

    Boid nextBoidState = new BoidMoves().moveOneBoidSync(boid, neighbors, boidModel);
    Assertions.assertEquals(new Boid(12,12,2.0375, 2.025), nextBoidState);
  }

  @Test
  void moveOneBoidSyncTest_BoidsMovingOpposite() {
    Boid boid = new Boid(10,10,2,2);
    List<Boid> neighbors = List.of(
        new Boid(12,12,-20,-1),
        new Boid(8,8,-20,-1),
        new Boid(10,12,-20,-1),
        new Boid(10,8,-10,-1)
    );

    Boid nextBoidState = new BoidMoves().moveOneBoidSync(boid, neighbors, boidModel);
    Assertions.assertEquals(new Boid(11,12,1.125, 1.95), nextBoidState);
  }

  @Test
  void moveOneBoidTest_BoidsMovingOpposite() throws ExecutionException, InterruptedException {
    Boid boid = new Boid(10,10,2,2);
    List<Boid> neighbors = List.of(
        new Boid(12,12,-20,-1),
        new Boid(8,8,-20,-1),
        new Boid(10,12,-20,-1),
        new Boid(10,8,-10,-1)
    );

    CompletableFuture<Boid> nextBoidState = new BoidMoves().moveOneBoid(boid, neighbors, boidModel, executor);
    Assertions.assertEquals(new Boid(11,12,1.125, 1.95), nextBoidState.get());
  }
}