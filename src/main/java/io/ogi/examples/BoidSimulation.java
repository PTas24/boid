package io.ogi.examples;

import io.helidon.config.Config;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPosition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoidSimulation implements Runnable {

    private final MessageQueue messageQueue = MessageQueue.instance();

    BoidPosition boidPosition;

    public BoidSimulation(BoidSimulationConfig boidSimulationConfig) {
        this.boidPosition = new BoidPosition();
        initializeBoids(boidSimulationConfig.getBoidModel());
//        boids.forEach(System.out::println);
    }

    private void initializeBoids(BoidModel boidModel) {
        boidPosition.boids = Stream.generate(() -> new Boid(boidModel)).limit(boidModel.numOfBoids).collect(Collectors.toList());
    }

    private void moveTheBoids() {
        List<Boid> boidList = boidPosition.getBoids()
                .stream()
                .peek(b -> b.move(boidPosition.getBoids()))
                .collect(Collectors.toList());
        boidPosition.setBoids(boidList);
//        for (Boid b : boidPosition.boids) {
//            b.move(boidPosition.boids);
//        }
    }

    private void drawTheBoids() {
        messageQueue.push(boidPosition);
    }

    private void updateTheBoids() {
        List<Boid> boidList = boidPosition.getBoids()
                .stream()
                .peek(b -> {b.x += b.dx; b.y += b.dy;})
                .collect(Collectors.toList());
        boidPosition.setBoids(boidList);
    }

    @Override
    public void run() {
        moveTheBoids();
//        System.out.println("after move:" + boidPosition.getBoids().get(0));
        drawTheBoids();
        updateTheBoids();
//        System.out.println("after update:" + boidPosition.getBoids().get(0));

    }
}
