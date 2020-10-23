package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;
import io.ogi.examples.model.BoidTempState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ogi.examples.BoidTransformation2.*;
import static java.util.stream.Collectors.toList;

public class BoidSimulationAsync implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(BoidSimulationAsync.class.getName());
    private final MessageQueue messageQueue = MessageQueue.instance();
    private BoidPositions boidPositions;
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
        boidPositions.setBoids(Stream.generate(() -> new Boid(boidModel)).limit(boidModel.getNumOfBoids()).collect(toList()));
    }

    public BoidModel getBoidModel() {
        return boidModel;
    }

    private void drawTheBoids() {
        messageQueue.push(boidPositions);
    }

    @Override
    public void run() {
        try {
            moveTheBoidAsync(new BoidTempState(boidPositions.getBoids()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        drawTheBoids();
    }

    CompletableFuture<Boid> moveOneBoid(Boid boid, List<Boid> boids, BoidModel boidModel, BoidTempState boidTempState) {
        return CompletableFuture.supplyAsync(() -> {
                double velocityX1 = flyTowardsCenter(boid.getX(), boidTempState.getCenterX(), boidModel.getCohesionFactor());
//                double velocityX1 = 0;
                double velocityX2 = keepDistance(boid.getX(), boidTempState.getOtherXPositions(), boidModel.getSeparationFactor());
//                double velocityX2 = 0;
//                double velocityX3 = matchVelocity(boidTempState.getAverageXVelocity(), boidModel.getAlignmentFactor());
                double velocityX3 = 0;
                double velocityY1 = flyTowardsCenter(boid.getY(), boidTempState.getCenterY(), boidModel.getCohesionFactor());
//                double velocityY1 = 0;
                double velocityY2 = keepDistance(boid.getY(), boidTempState.getOtherYPositions(), boidModel.getSeparationFactor());
//                double velocityY2 = 0;
//                double velocityY3 = matchVelocity(boidTempState.getAverageYVelocity(), boidModel.getAlignmentFactor());
                double velocityY3 = 0;
                double xVelocity = boid.getDx() + velocityX1 + velocityX2 + velocityX3;
                double yVelocity = boid.getDy() + velocityY1 + velocityY2 + velocityY3;
                double speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
                if (speed > boidModel.getSpeedLimit()) {
                    xVelocity =  (boid.getDx() / speed) * boidModel.getSpeedLimit();
                    yVelocity =  (boid.getDy() / speed) * boidModel.getSpeedLimit();
                }
                xVelocity = keepWithinBounds(boid.getX(), xVelocity, boidModel.getCanvasMargin(), boidModel.getCanvasWidth(), boidModel.getSpeedAdjust());
                yVelocity = keepWithinBounds(boid.getY(), yVelocity, boidModel.getCanvasMargin(), boidModel.getCanvasHeight(), boidModel.getSpeedAdjust());
                return new Boid((int)Math.round(boid.getX() + xVelocity),(int)Math.round(boid.getY() + yVelocity), xVelocity, yVelocity);
            })
            .exceptionally(ex -> new Boid(boid.getX(),boid.getY(), boid.getDx(), boid.getDy()));
    }

    public void moveTheBoidAsync(BoidTempState boidTempState) throws InterruptedException, ExecutionException {
        List<CompletableFuture<Boid>> newBoidFutures = boidPositions.getBoids().stream()
                .map(boid -> moveOneBoid(boid, boidPositions.getBoids(), boidModel, boidTempState))
                .collect(toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                newBoidFutures.toArray(new CompletableFuture[newBoidFutures.size()])
        );

       // When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
        CompletableFuture<List<Boid>> allBoidFuture = allFutures.thenApply(v ->
            newBoidFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
        );

        boidPositions.setBoids(allBoidFuture.get());
        System.out.println("x: " + boidPositions.getBoids().get(0).getX());
        System.out.println("y: " + boidPositions.getBoids().get(0).getY());

    }
}
