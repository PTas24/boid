package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;

import java.util.ArrayList;
import java.util.List;
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

    private void initializeBoids() {
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
//        List<Boid> boidList2 = new ArrayList<>();
//        for (Boid b : boidPositions.getBoids()) {
//            Boid movedBoid = move(b, boidPositions.getBoids());
//            boidList2.add(movedBoid);
//        }

        List<Boid> boidList = boidPositions.getBoids()
                .stream()
                .map(b -> move(b, boidPositions.getBoids()))
//                .peek(System.out::println)
                .collect(Collectors.toList());
        boidPositions.setBoids(boidList);
//        for (Boid b : boidPosition.boids) {
//            b.move(boidPosition.boids);
//        }
    }

    Boid move(Boid actualBoid, List<Boid> boids) {
        Boid movedBoid = new Boid(actualBoid.getX(), actualBoid.getY(), actualBoid.getDx(), actualBoid.getDy());
//        System.out.println("1 dx: " + actualBoid.getDx());
//        System.out.println("1 dy: " + actualBoid.getDy());
        double vx1 = BoidTransformation.flyTowardsCenterDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.cohesionRange),
                boidModel.cohesionFactor);
        double vy1 = BoidTransformation.flyTowardsCenterDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.cohesionRange),
                boidModel.cohesionFactor);
//        System.out.println("vx1: " + vx1);
//        System.out.println("vy1: " + vy1);
        double vx2 = BoidTransformation.keepdistanceDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.separationRange),
                boidModel.separationFactor);
        double vy2 =  BoidTransformation.keepDistanceDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.separationRange),
                boidModel.separationFactor);
//        System.out.println("vx2: " + vx2);
//        System.out.println("vy2: " + vy2);

        double vx3 =  BoidTransformation.matchVelocityDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.alignmentRange),
                boidModel.alignmentFactor);
        double vy3 = BoidTransformation.matchVelocityDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.alignmentRange),
                boidModel.alignmentFactor);
//        System.out.println("vx3: " + vx3);
//        System.out.println("vy3: " + vy3);
        movedBoid.setDx(actualBoid.getDx() + vx1 + vx2 + vx3);
        movedBoid.setDy(actualBoid.getDy() + vy1 + vy2 + vy3);
//        movedBoid.setDx(actualBoid.getDx() + vx1 + vx3);
//        movedBoid.setDy(actualBoid.getDy() + vy1 + vy3);
        double speed = Math.sqrt(movedBoid.getDx() * movedBoid.getDx() + movedBoid.getDy() * movedBoid.getDy());
        if (speed > boidModel.speedLimit) {
            movedBoid.setDx(BoidTransformation.limitSpeedDx(actualBoid, speed, boidModel.speedLimit));
            movedBoid.setDy(BoidTransformation.limitSpeedDy(actualBoid, speed, boidModel.speedLimit));
        }
//        System.out.println("5 dx: " + movedBoid.getDx());
//        System.out.println("5 dy: " + movedBoid.getDy());
        movedBoid.setX((int)Math.round(movedBoid.getX() + movedBoid.getDx()));
        movedBoid.setY((int)Math.round(movedBoid.getY() + movedBoid.getDy()));

        keepWithinBounds(movedBoid);
//        movedBoid.setDx(movedBoid.getDx() + BoidTransformation.keepWithinBoundsDx(
//                movedBoid, boidModel.canvasWidth, boidModel.canvasMargin, boidModel.speedAdjust));
//        movedBoid.setDy(movedBoid.getDy() + BoidTransformation.keepWithinBoundsDy(
//                movedBoid, boidModel.canvasHeight, boidModel.canvasMargin, boidModel.speedAdjust));
//        System.out.println("6 dx: " + movedBoid.getDx());
//        System.out.println("6 dy: " + movedBoid.getDy());
//        System.out.println("zzz");
//        movedBoid.setX((int)Math.round(movedBoid.getX() + movedBoid.getDx()));
//        movedBoid.setY((int)Math.round(movedBoid.getY() + movedBoid.getDy()));
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

//    Boid moveOld(Boid actualBoid, List<Boid> boids) {
//        Boid movedBoid = new Boid(actualBoid.getX(), actualBoid.getY(), actualBoid.getDx(), actualBoid.getDy());
////        System.out.println("1 dx: " + actualBoid.getDx());
////        System.out.println("1 dy: " + actualBoid.getDy());
//        movedBoid.setDx(movedBoid.getDx() + BoidTransformation.flyTowardsCenterDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.cohesionRange),
//                boidModel.cohesionFactor));
//        movedBoid.setDy(movedBoid.getDy() + BoidTransformation.flyTowardsCenterDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.cohesionRange),
//                boidModel.cohesionFactor));
////        System.out.println("2 dx: " + movedBoid.getDx());
////        System.out.println("2 dy: " + movedBoid.getDy());
//        movedBoid.setDx(movedBoid.getDx() + BoidTransformation.keepdistanceDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.separationRange),
//                boidModel.separationFactor));
//        movedBoid.setDy(movedBoid.getDy() + BoidTransformation.keepDistanceDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.separationRange),
//                boidModel.separationFactor));
////        System.out.println("3 dx: " + movedBoid.getDx());
////        System.out.println("3 dy: " + movedBoid.getDy());
//        movedBoid.setDx(movedBoid.getDx() + BoidTransformation.matchVelocityDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.alignmentRange),
//                boidModel.alignmentFactor));
//        movedBoid.setDy(movedBoid.getDy() + BoidTransformation.matchVelocityDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.alignmentRange),
//                boidModel.alignmentFactor));
////        System.out.println("4 dx: " + movedBoid.getDx());
////        System.out.println("4 dy: " + movedBoid.getDy());
//        double speed = Math.sqrt(movedBoid.getDx() * movedBoid.getDx() + movedBoid.getDy() * movedBoid.getDy());
//        if (speed > boidModel.speedLimit) {
//            movedBoid.setDx(BoidTransformation.limitSpeedDx(actualBoid, speed, boidModel.speedLimit));
//            movedBoid.setDy(BoidTransformation.limitSpeedDy(actualBoid, speed, boidModel.speedLimit));
//        }
////        System.out.println("5 dx: " + movedBoid.getDx());
////        System.out.println("5 dy: " + movedBoid.getDy());
//        movedBoid.setDx(movedBoid.getDx() + BoidTransformation.keepWithinBoundsDx(
//                actualBoid, boidModel.canvasWidth, boidModel.canvasMargin, boidModel.speedAdjust));
//        movedBoid.setDy(movedBoid.getDy() + BoidTransformation.keepWithinBoundsDy(
//                actualBoid, boidModel.canvasHeight, boidModel.canvasMargin, boidModel.speedAdjust));
////        System.out.println("6 dx: " + movedBoid.getDx());
////        System.out.println("6 dy: " + movedBoid.getDy());
////        System.out.println("zzz");
//        return movedBoid;
//    }


    private void drawTheBoids() {
        messageQueue.push(boidPositions);
    }

//    private void updateTheBoids() {
//        List<Boid> boidList = boidPositions.getBoids()
//                .stream()
//                .map(this::updateBoid)
//                .collect(Collectors.toList());
//        boidPositions.setBoids(boidList);
//    }
//
//    private Boid updateBoid(Boid b) {
////        return new Boid((int)Math.round(b.getX() + b.getDx()), (int)Math.round(b.getY() + b.getDy()), b.getDx(), b.getDy());
//        b.setX((int)Math.round(b.getX() + b.getDx()));
//        b.setY((int)Math.round(b.getY() + b.getDy()));
//        return b;
//    }

    @Override
    public void run() {
        moveTheBoids();
        drawTheBoids();
//        updateTheBoids();
    }
}
