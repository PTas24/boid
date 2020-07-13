package io.ogi.examples;

import io.helidon.config.Config;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPosition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoidSimulation {
//    public static final int CANVAS_WIDTH = 200;
//    public static final int CANVAS_HEIGHT = 200;
//    public static final int CANVAS_MARGIN = 10;
//    public static final int SPEED_ADJUST = 1;
//    public static final int NUM_OF_BOIDS = 200;
//
//    public static final int COHESION_RANGE = 40;
//    public static final int SEPARATION_RANGE = 10;
//    public static final int ALIGNMENT_RANGE = 40;
//
//    public static final double COHESION_FACTOR = 0.005;
//    public static final double SEPARATION_FACTOR = 0.05;
//    public static final double ALIGNMENT_FACTOR = 0.05;
//
//    public static final int SPEED_LIMIT = 15;

    private final MessageQueue messageQueue = MessageQueue.instance();

//    List<Boid> boids;
    BoidPosition boidPosition;
    BoidModel boidModel;

    public BoidSimulation(Config config) {
        this.boidModel = new BoidModel();
        this.boidPosition = new BoidPosition();
        initializeParameters(config);
        initializeBoids();
//        boids.forEach(System.out::println);
    }

    private void initializeParameters(Config config) {

        Config canvasConfig = config.get("canvas");
        Config rangeConfig = config.get("range");
        Config factorConfig = config.get("factor");

        boidModel.canvasWidth = canvasConfig.get("width").asInt().orElse(200);
        boidModel.canvasHeight = canvasConfig.get("height").asInt().orElse(200);
        boidModel.canvasMargin = canvasConfig.get("margin").asInt().orElse(10);
        boidModel.speedAdjust = config.get("speedAdjust").asInt().orElse(1);
        boidModel.numOfBoids = config.get("numOfBoids").asInt().orElse(200);
        boidModel.cohesionRange = rangeConfig.get("cohesion").asInt().orElse(40);
        boidModel.separationRange = rangeConfig.get("separation").asInt().orElse(10);
        boidModel.alignmentRange = rangeConfig.get("alignment").asInt().orElse(40);
        boidModel.cohesionFactor =   factorConfig.get("cohesion").asDouble().orElse(0.005);
        boidModel.separationFactor = factorConfig.get("separation").asDouble().orElse(0.05);
        boidModel.alignmentFactor =  factorConfig.get("alignment").asDouble().orElse(0.05);
        boidModel.speedLimit = config.get("speedLimit").asInt().orElse(15);
    }


    private void initializeBoids() {
        boidPosition.boids = Stream.generate(() -> new Boid(boidModel)).limit(boidModel.numOfBoids).collect(Collectors.toList());
    }

    public void startBoidSimulation() {
        moveTheBoids();
        drawTheBoids();
    }

    private void moveTheBoids() {
        for (Boid b : boidPosition.boids) {
            b.move(boidPosition.boids);
        }
    }

    private void drawTheBoids() {
        messageQueue.push(boidPosition);
    }


    public CompletionStage<BoidModel> getParams() {
        return CompletableFuture.completedFuture(boidModel);
    }

    public CompletionStage<BoidModel> modifyBoidSimulation(BoidModel newBoidModel) {
        System.out.println(newBoidModel);
        boidModel = newBoidModel;
        return CompletableFuture.completedFuture(boidModel);
    }

    public BoidModel getParams1() {
        return boidModel;
    }
}
