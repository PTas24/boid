package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoidSimulation implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(BoidSimulation.class.getName());
    private final MessageQueue messageQueue = MessageQueue.instance();
    private final BoidPositions boidPositions;
    private final BoidSimulationConfig boidSimulationConfig;
    private BoidModel boidModel;

    public BoidSimulation(BoidSimulationConfig boidSimulationConfig) {
        this.boidSimulationConfig = boidSimulationConfig;
        this.boidPositions = new BoidPositions();
        this.boidModel = boidSimulationConfig.getBoidModel();
        initializeBoids();
//        boidPositions.getBoids().forEach(System.out::println);
    }

    public void initializeBoids() {
        boidModel = boidSimulationConfig.getBoidModel();
        LOGGER.info(() -> "model: " + boidModel);
        boidPositions.setBoids(Stream.generate(() -> new Boid(boidModel)).limit(boidModel.getNumOfBoids()).collect(Collectors.toList()));
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
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getCohesionRange()),
                boidModel.getCohesionFactor());
        double vy1 = BoidTransformation.flyTowardsCenterDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getCohesionRange()),
                boidModel.getCohesionFactor());

        double vx2 = BoidTransformation.keepdistanceDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getSeparationRange()),
                boidModel.getSeparationFactor());
        double vy2 =  BoidTransformation.keepDistanceDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getSeparationRange()),
                boidModel.getSeparationFactor());
//        vx2 = 0; vy2 = 0;

        double vx3 =  BoidTransformation.matchVelocityDx(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getAlignmentRange()),
                boidModel.getAlignmentFactor());
        double vy3 = BoidTransformation.matchVelocityDy(
                actualBoid,
                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getAlignmentRange()),
                boidModel.getAlignmentFactor());

        movedBoid.setDx(movedBoid.getDx() + vx1 + vx2 + vx3);
        movedBoid.setDy(movedBoid.getDy() + vy1 + vy2 + vy3);
        double speed = Math.sqrt(movedBoid.getDx() * movedBoid.getDx() + movedBoid.getDy() * movedBoid.getDy());
        if (speed > boidModel.getSpeedLimit()) {
            movedBoid.setDx(BoidTransformation.limitSpeedDx(actualBoid, speed, boidModel.getSpeedLimit()));
            movedBoid.setDy(BoidTransformation.limitSpeedDy(actualBoid, speed, boidModel.getSpeedLimit()));
        }

        keepWithinBounds(movedBoid);

        movedBoid.setX((int)Math.round(movedBoid.getX() + movedBoid.getDx()));
        movedBoid.setY((int)Math.round(movedBoid.getY() + movedBoid.getDy()));
        return movedBoid;
    }

    private void keepWithinBounds(Boid movedBoid) {
        if (movedBoid.getX() < boidModel.getCanvasMargin()) {
            movedBoid.setDx(movedBoid.getDx() + boidModel.getSpeedAdjust());
        }
        if (movedBoid.getX() >  (boidModel.getCanvasWidth() - boidModel.getCanvasMargin())) {
            movedBoid.setDx(movedBoid.getDx() - boidModel.getSpeedAdjust());
        }
        if (movedBoid.getY() < boidModel.getCanvasMargin()) {
            movedBoid.setDy(movedBoid.getDy() + boidModel.getSpeedAdjust());
        }
        if (movedBoid.getY() >  (boidModel.getCanvasHeight() - boidModel.getCanvasMargin())) {
            movedBoid.setDy(movedBoid.getDy() - boidModel.getSpeedAdjust());
        }
    }

    private void drawTheBoids() {
        messageQueue.push(boidPositions);
    }

    @Override
    public void run() {
        moveTheBoids();
        drawTheBoids();
    }
}
