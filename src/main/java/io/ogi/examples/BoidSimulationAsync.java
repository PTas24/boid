package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static io.ogi.examples.BoidTransformation2.*;
import static java.util.stream.Collectors.toList;

public class BoidSimulationAsync implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(BoidSimulationAsync.class.getName());
    private final MessageQueue messageQueue = MessageQueue.instance();
    private final BoidPositions boidPositions;
    private final BoidSimulationConfig boidSimulationConfig;
    private BoidModel boidModel;
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
        boidPositions.setBoids(Stream.generate(() -> new Boid(boidModel)).limit(boidModel.getNumOfBoids()).collect(toList()));
        executor = Executors.newFixedThreadPool(Math.min(boidModel.getNumOfBoids(), 100), (Runnable r) -> {
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

    @Override
    public void run() {
        moveTheBoidAsync();
        drawTheBoids();
    }

    CompletableFuture<Boid> moveOneBoid(Boid boid, List<Boid> boids, BoidModel boidModel) {
        List<Boid> cohesionNeighbours = getNeighbors(boid, boids, boidModel.getCohesionRange());
        List<Boid> separationNeighbours = getNeighbors(boid, boids, boidModel.getSeparationRange());
        List<Boid> alignmentNeighbours = getNeighbors(boid, boids, boidModel.getAlignmentRange());
        double centerX = getValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getX));
        double centerY = getValue(cohesionNeighbours, cohesionNeighbours.stream().mapToDouble(Boid::getY));
        List<Integer> otherXPositions = separationNeighbours.stream().map(Boid::getX).collect(Collectors.toList());
        List<Integer> otherYPositions = separationNeighbours.stream().map(Boid::getY).collect(Collectors.toList());
        double averageXVelocity = getValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDx));
        double averageYVelocity = getValue(alignmentNeighbours, alignmentNeighbours.stream().mapToDouble(Boid::getDy));

        return CompletableFuture.supplyAsync(() -> {
                double velocityX1 = flyTowardsCenter(boid.getX(), centerX, boidModel.getCohesionFactor());
                double velocityX2 = keepDistance(boid.getX(), otherXPositions, boidModel.getSeparationFactor());
                double velocityX3 = matchVelocity(averageXVelocity, boidModel.getAlignmentFactor());
                double velocityY1 = flyTowardsCenter(boid.getY(), centerY, boidModel.getCohesionFactor());
                double velocityY2 = keepDistance(boid.getY(), otherYPositions, boidModel.getSeparationFactor());
                double velocityY3 = matchVelocity(averageYVelocity, boidModel.getAlignmentFactor());
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
            }, executor)
            .exceptionally(ex -> new Boid(boid.getX(),boid.getY(), boid.getDx(), boid.getDy()));
    }

    private double getValue(List<Boid> neighbours, DoubleStream doubleStream) {
        if (neighbours.isEmpty()) {
            return 0;
        }
        return doubleStream.sum() / neighbours.size();
    }

    public void moveTheBoidAsync() {
        List<CompletableFuture<Boid>> newBoidFutures = boidPositions.getBoids().stream()
                .map(boid -> moveOneBoid(boid, boidPositions.getBoids(), boidModel))
                .collect(toList());

        List<Boid> newBoid = newBoidFutures.stream().map(CompletableFuture::join).collect(toList());
        boidPositions.setBoids(newBoid);
    }
}
