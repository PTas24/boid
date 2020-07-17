package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoidSimulation implements Runnable {

    private final MessageQueue messageQueue = MessageQueue.instance();
    private final BoidPositions boidPositions;
    private final BoidModel boidModel;

    public BoidSimulation(BoidSimulationConfig boidSimulationConfig) {
        this.boidPositions = new BoidPositions();
        this.boidModel = boidSimulationConfig.getBoidModel();
        initializeBoids();
//        boidPositions.getBoids().forEach(System.out::println);
    }

    public void initializeBoids() {
        boidPositions.setBoids(Stream.generate(() -> new Boid(boidModel)).limit(boidModel.numOfBoids).collect(Collectors.toList()));
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
        List<Boid> boidList = boidPositions.getBoids()
                .stream()
                .map(b -> move(b, boidPositions.getBoids()))
                .collect(Collectors.toList());
        boidPositions.setBoids(boidList);

    }

    Boid move(Boid actualBoid, List<Boid> boids) {
        Boid movedBoid = new Boid(actualBoid.getX(), actualBoid.getY(), actualBoid.getDx(), actualBoid.getDy());

        double vx1 = BoidTransformation.flyTowardsCenterDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.cohesionRange),
                boidModel.cohesionFactor);
        double vy1 = BoidTransformation.flyTowardsCenterDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.cohesionRange),
                boidModel.cohesionFactor);

        double vx2 = BoidTransformation.keepdistanceDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.separationRange),
                boidModel.separationFactor);
        double vy2 =  BoidTransformation.keepDistanceDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.separationRange),
                boidModel.separationFactor);
//        vx2 = 0; vy2 = 0;

        double vx3 =  BoidTransformation.matchVelocityDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.alignmentRange),
                boidModel.alignmentFactor);
        double vy3 = BoidTransformation.matchVelocityDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.alignmentRange),
                boidModel.alignmentFactor);

        movedBoid.setDx(movedBoid.getDx() + vx1 + vx2 + vx3);
        movedBoid.setDy(movedBoid.getDy() + vy1 + vy2 + vy3);
        double speed = Math.sqrt(movedBoid.getDx() * movedBoid.getDx() + movedBoid.getDy() * movedBoid.getDy());
        if (speed > boidModel.speedLimit) {
            movedBoid.setDx(BoidTransformation.limitSpeedDx(actualBoid, speed, boidModel.speedLimit));
            movedBoid.setDy(BoidTransformation.limitSpeedDy(actualBoid, speed, boidModel.speedLimit));
        }

//        movedBoid.setX((int)Math.round(movedBoid.getX() + movedBoid.getDx()));
//        movedBoid.setY((int)Math.round(movedBoid.getY() + movedBoid.getDy()));

        keepWithinBounds(movedBoid);

        movedBoid.setX((int)Math.round(movedBoid.getX() + movedBoid.getDx()));
        movedBoid.setY((int)Math.round(movedBoid.getY() + movedBoid.getDy()));
        return movedBoid;
    }

    private void keepWithinBounds(Boid movedBoid) {
        if (movedBoid.getX() <  boidModel.canvasMargin) {
            movedBoid.setDx(movedBoid.getDx() + boidModel.speedAdjust);
        }
        if (movedBoid.getX() >  (boidModel.canvasWidth - boidModel.canvasMargin)) {
            movedBoid.setDx(movedBoid.getDx() - boidModel.speedAdjust);
        }
        if (movedBoid.getY() <  boidModel.canvasMargin) {
            movedBoid.setDy(movedBoid.getDy() + boidModel.speedAdjust);
        }
        if (movedBoid.getY() >  (boidModel.canvasHeight - boidModel.canvasMargin)) {
            movedBoid.setDy(movedBoid.getDy() - boidModel.speedAdjust);
        }
    }

    private void drawTheBoids() {
//        if (boidPositions == null) {
//            return;
//        }
        messageQueue.push(boidPositions);
    }


    @Override
    public void run() {
        moveTheBoids();
        drawTheBoids();
    }

    public CompletableFuture<Void> initializeBoidsForNextFly(BoidModel boidModel) {
        initializeBoids();
//        System.out.println("we are here");
        return new CompletableFuture<>();
    }
}
