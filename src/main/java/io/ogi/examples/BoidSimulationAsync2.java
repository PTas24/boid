package io.ogi.examples;

import io.ogi.examples.model.Boid;
import io.ogi.examples.model.BoidModel;
import io.ogi.examples.model.BoidPositions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BoidSimulationAsync2 implements Runnable {

//    private static final Logger LOGGER = Logger.getLogger(BoidSimulationAsync2.class.getName());
//    private final MessageQueue messageQueue = MessageQueue.instance();
//    private BoidPositions boidPositions = null;
//    private static BoidPositions boidPositionsStatic;
//    private final BoidSimulationConfig boidSimulationConfig;
//    private static BoidSimulationConfig boidSimulationConfigStatic;
//    private BoidModel boidModel;
//
//    public BoidSimulationAsync2(BoidSimulationConfig boidSimulationConfig) {
//        this.boidSimulationConfig = boidSimulationConfig;
//        this.boidPositions = new BoidPositions();
//        this.boidModel = boidSimulationConfig.getBoidModel();
//        initializeBoids();
//    }
//
//    public void initializeBoids() {
//        boidModel = boidSimulationConfig.getBoidModel();
//        LOGGER.info(() -> "model: " + boidModel);
//        boidPositions.setBoids(Stream.generate(() -> new Boid(boidModel)).limit(boidModel.getNumOfBoids()).collect(toList()));
//        boidPositionsStatic = boidPositions;
//        boidSimulationConfigStatic = boidSimulationConfig;
//    }
//
//    void setBoidPositions(List<Boid> boids) {
//        boidPositions.setBoids(boids);
//    }
//
//    public BoidPositions getBoidPositions() {
//        return boidPositions;
//    }
//
//    public BoidModel getBoidModel() {
//        return boidModel;
//    }
//
//    void moveTheBoids() {
//        List<Boid> boidList = boidPositions.getBoids()
//                .stream()
//                .map(b -> move(b, boidPositions.getBoids()))
//                .collect(toList());
//        boidPositions.setBoids(boidList);
//
//    }
//
//    Boid move(Boid actualBoid, List<Boid> boids) {
//        Boid movedBoid = new Boid(actualBoid.getX(), actualBoid.getY(), actualBoid.getDx(), actualBoid.getDy());
//
//        double vx1 = BoidTransformation.flyTowardsCenterDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getCohesionRange()),
//                boidModel.getCohesionFactor());
//        double vy1 = BoidTransformation.flyTowardsCenterDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getCohesionRange()),
//                boidModel.getCohesionFactor());
//
//        double vx2 = BoidTransformation.keepdistanceDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getSeparationRange()),
//                boidModel.getSeparationFactor());
//        double vy2 =  BoidTransformation.keepDistanceDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getSeparationRange()),
//                boidModel.getSeparationFactor());
//
//        double vx3 =  BoidTransformation.matchVelocityDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getAlignmentRange()),
//                boidModel.getAlignmentFactor());
//        double vy3 = BoidTransformation.matchVelocityDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getAlignmentRange()),
//                boidModel.getAlignmentFactor());
//
//        movedBoid.setDx(movedBoid.getDx() + vx1 + vx2 + vx3);
//        movedBoid.setDy(movedBoid.getDy() + vy1 + vy2 + vy3);
//        double speed = Math.sqrt(movedBoid.getDx() * movedBoid.getDx() + movedBoid.getDy() * movedBoid.getDy());
//        if (speed > boidModel.getSpeedLimit()) {
//            movedBoid.setDx(BoidTransformation.limitSpeedDx(actualBoid, speed, boidModel.getSpeedLimit()));
//            movedBoid.setDy(BoidTransformation.limitSpeedDy(actualBoid, speed, boidModel.getSpeedLimit()));
//        }
//
//        keepWithinBounds(movedBoid);
//
//        movedBoid.setX((int)Math.round(movedBoid.getX() + movedBoid.getDx()));
//        movedBoid.setY((int)Math.round(movedBoid.getY() + movedBoid.getDy()));
//        return movedBoid;
//    }
//
//    private void keepWithinBounds(Boid movedBoid) {
//        if (movedBoid.getX() < boidModel.getCanvasMargin()) {
//            movedBoid.setDx(movedBoid.getDx() + boidModel.getSpeedAdjust());
//        }
//        if (movedBoid.getX() >  (boidModel.getCanvasWidth() - boidModel.getCanvasMargin())) {
//            movedBoid.setDx(movedBoid.getDx() - boidModel.getSpeedAdjust());
//        }
//        if (movedBoid.getY() < boidModel.getCanvasMargin()) {
//            movedBoid.setDy(movedBoid.getDy() + boidModel.getSpeedAdjust());
//        }
//        if (movedBoid.getY() >  (boidModel.getCanvasHeight() - boidModel.getCanvasMargin())) {
//            movedBoid.setDy(movedBoid.getDy() - boidModel.getSpeedAdjust());
//        }
//    }
//
//    private void drawTheBoids() {
//        messageQueue.push(boidPositions);
//    }
//
    @Override
    public void run() {
//        moveTheBoidAsync();
//        drawTheBoids();
    }
//
//    public static double flyTowardsCenter(int position, double center, double cohesionFactor) {
//        return (center - position) * cohesionFactor;
//    }
//
//    public static double keepDistance(int position, List<Integer> otherPositions, double separationFactor) {
//        int move = otherPositions.stream()
//                .mapToInt(other -> (position - other))
//                .sum();
//        return move * separationFactor;
//    }
//
//    public static double matchVelocity(double averageVelocity, double alignmentFactor) {
//        return averageVelocity * alignmentFactor;
//    }
//
//    public static double calculateNewVelocity(double currentVelocity, double v1, double v2, double v3) {
//        return currentVelocity + v1 + v2 + v3;
//    }
//
//    public static double calculateNewSpeed(double vx, double vy) {
//        return Math.sqrt(vx * vx + vy * vy);
//    }
//
//    public static double limitSpeed(double velocity, double speed, int speedLimit) {
//        return (velocity / speed) * speedLimit;
//    }
//
//    public static double keepWithinXbounds(int position, double velocity, int canvasMargin, int canvasWidth, int speedAdjust) {
//        if (position < canvasMargin) {
//            return velocity + speedAdjust;
//        }
//        if (position > (canvasWidth - canvasMargin)) {
//            return velocity - speedAdjust;
//        }
//        return velocity;
//    }
//
//    public static double keepWithinYbounds(int position, double velocity, int canvasMargin, int canvasHeight, int speedAdjust) {
//        if (position < canvasMargin) {
//            return velocity + speedAdjust;
//        }
//        if (position > (canvasHeight - canvasMargin)) {
//            return velocity - speedAdjust;
//        }
//        return velocity;
//    }
//    public static double keepWithinBounds(int position, double velocity, int canvasMargin, int canvasLimit, int speedAdjust) {
//        if (position < canvasMargin) {
//            return velocity + speedAdjust;
//        }
//        if (position > (canvasLimit - canvasMargin)) {
//            return velocity - speedAdjust;
//        }
//        return velocity;
//    }
//
//    public static int calculateMove(int position, int velocity) {
//        return (int)Math.round((double)position + velocity);
//    }
//
//    public static Stream<CompletableFuture<Boid>> moveBoidsStream(List<Boid> boids, BoidModel boidModel) {
//        if (boids.isEmpty()) {
//            return Stream.empty();
//        }
//        double centerX = boids.stream().mapToDouble(Boid::getX).sum() / boids.size();
//        double centerY = boids.stream().mapToDouble(Boid::getY).sum() / boids.size();
//        List<Integer> otherXPositions = boids.stream().map(Boid::getX).collect(Collectors.toList());
//        List<Integer> otherYPositions = boids.stream().map(Boid::getY).collect(Collectors.toList());
//        double averageXVelocity = boids.stream().mapToDouble(Boid::getDx).sum() / boids.size();
//        double averageYVelocity = boids.stream().mapToDouble(Boid::getDy).sum() / boids.size();
//
//        boids.forEach(boid -> {
//                    CompletableFuture<Double> velocityX1 = CompletableFuture.supplyAsync(() ->
//                        flyTowardsCenter(boid.getX(), centerX, boidModel.getCohesionFactor())
//                    );
//                    CompletableFuture<Double> velocityY1 = CompletableFuture.supplyAsync(() ->
//                         flyTowardsCenter(boid.getY(), centerY, boidModel.getCohesionFactor())
//                    );
//                    CompletableFuture<Double> velocityX2 = CompletableFuture.supplyAsync(() ->
//                        keepDistance(boid.getX(), otherXPositions, boidModel.getSeparationFactor())
//                    );
//                    CompletableFuture<Double> velocityY2 = CompletableFuture.supplyAsync(() ->
//                         keepDistance(boid.getY(), otherYPositions, boidModel.getSeparationFactor())
//                    );
//                    CompletableFuture<Double> velocityX3 = CompletableFuture.supplyAsync(() ->
//                         matchVelocity(averageXVelocity, boidModel.getAlignmentFactor())
//                    );
//                    CompletableFuture<Double> velocityY3 = CompletableFuture.supplyAsync(() ->
//                         matchVelocity(averageYVelocity, boidModel.getAlignmentFactor())
//                    );
//                    CompletableFuture<Double> combinedXVelocity = velocityX1
//                            .thenCombine(velocityX2, Double::sum)
//                            .thenCombine(velocityX3, Double::sum);
//                    CompletableFuture<Double> combinedYVelocity = velocityY1
//                            .thenCombine(velocityY2, Double::sum)
//                            .thenCombine(velocityY3, Double::sum);
//
//        }
//        );
//
////        return boids.stream()
////                .map(boid -> CompletableFuture.supplyAsync(() -> flyTowardsCenter(boid.getX(), centerX, boidModel.getCohesionFactor())));
//
//
//        Boid movedBoid = new Boid(actualBoid.getX(), actualBoid.getY(), actualBoid.getDx(), actualBoid.getDy());
//
//        double vx1 = BoidTransformation.flyTowardsCenterDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getCohesionRange()),
//                boidModel.getCohesionFactor());
//        double vy1 = BoidTransformation.flyTowardsCenterDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getCohesionRange()),
//                boidModel.getCohesionFactor());
//
//        double vx2 = BoidTransformation.keepdistanceDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getSeparationRange()),
//                boidModel.getSeparationFactor());
//        double vy2 =  BoidTransformation.keepDistanceDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getSeparationRange()),
//                boidModel.getSeparationFactor());
//
//        double vx3 =  BoidTransformation.matchVelocityDx(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getAlignmentRange()),
//                boidModel.getAlignmentFactor());
//        double vy3 = BoidTransformation.matchVelocityDy(
//                actualBoid,
//                BoidTransformation.getNeighbors(actualBoid, boids, boidModel.getAlignmentRange()),
//                boidModel.getAlignmentFactor());
//
//        movedBoid.setDx(movedBoid.getDx() + vx1 + vx2 + vx3);
//        movedBoid.setDy(movedBoid.getDy() + vy1 + vy2 + vy3);
//        double speed = Math.sqrt(movedBoid.getDx() * movedBoid.getDx() + movedBoid.getDy() * movedBoid.getDy());
//        if (speed > boidModel.getSpeedLimit()) {
//            movedBoid.setDx(BoidTransformation.limitSpeedDx(actualBoid, speed, boidModel.getSpeedLimit()));
//            movedBoid.setDy(BoidTransformation.limitSpeedDy(actualBoid, speed, boidModel.getSpeedLimit()));
//        }
//
////        keepWithinBounds(movedBoid);
//
//        movedBoid.setX((int)Math.round(movedBoid.getX() + movedBoid.getDx()));
//        movedBoid.setY((int)Math.round(movedBoid.getY() + movedBoid.getDy()));
//        return movedBoid;
//
//    }
//
//    CompletableFuture<Boid> moveOneBoid(Boid boid, List<Boid> boids, BoidModel boidModel) {
//        return CompletableFuture.supplyAsync(() -> {
//                double centerX = boids.stream().mapToDouble(Boid::getX).sum() / boids.size();
//                double centerY = boids.stream().mapToDouble(Boid::getY).sum() / boids.size();
//                List<Integer> otherXPositions = boids.stream().map(Boid::getX).collect(Collectors.toList());
//                List<Integer> otherYPositions = boids.stream().map(Boid::getY).collect(Collectors.toList());
//                double averageXVelocity = boids.stream().mapToDouble(Boid::getDx).sum() / boids.size();
//                double averageYVelocity = boids.stream().mapToDouble(Boid::getDy).sum() / boids.size();
//                double velocityX1 = flyTowardsCenter(boid.getX(), centerX, boidModel.getCohesionFactor());
//                double velocityX2 = keepDistance(boid.getX(), otherXPositions, boidModel.getSeparationFactor());
//                double velocityX3 = matchVelocity(averageXVelocity, boidModel.getAlignmentFactor());
//                double velocityY1 = flyTowardsCenter(boid.getY(), centerY, boidModel.getCohesionFactor());
//                double velocityY2 = keepDistance(boid.getY(), otherYPositions, boidModel.getSeparationFactor());
//                double velocityY3 = matchVelocity(averageYVelocity, boidModel.getAlignmentFactor());
//                double xVelocity = velocityX1 + velocityX2 + velocityX3;
//                double yVelocity = velocityY1 + velocityY2 + velocityY3;
//                double speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
//                if (speed > boidModel.getSpeedLimit()) {
//                    xVelocity =  (xVelocity / speed) * boidModel.getSpeedLimit();
//                    yVelocity =  (yVelocity / speed) * boidModel.getSpeedLimit();
//                }
//                xVelocity = keepWithinBounds(boid.getX(), xVelocity, boidModel.getCanvasMargin(), boidModel.getCanvasWidth(), boidModel.getSpeedAdjust());
//                yVelocity = keepWithinBounds(boid.getY(), yVelocity, boidModel.getCanvasMargin(), boidModel.getCanvasHeight(), boidModel.getSpeedAdjust());
//                return new Boid((int)Math.round(boid.getX() + xVelocity),(int)Math.round(boid.getY() + yVelocity), xVelocity, yVelocity);
//            })
//            .exceptionally(ex -> new Boid(boid.getX(),boid.getY(), boid.getDx(), boid.getDy()));
//    }
//
//
//
//    CompletableFuture<Double> flyTowardsCenterDx = CompletableFuture.supplyAsync(() ->
//        BoidTransformation.flyTowardsCenterDx(boidPositions.getBoids().get(0),
//                BoidTransformation.getNeighbors(boidPositions.getBoids().get(0), boidPositions.getBoids(), boidModel.getCohesionRange()),
//                boidModel.getCohesionFactor()));
//
//
//    public void moveTheBoidAsync() {
//
//        List<CompletableFuture<Boid>> newBoidFutures = boidPositions.getBoids().stream()
//                .map(boid -> moveOneBoid(boid, boidPositions.getBoids(), boidModel))
//                .collect(toList());
//
//        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
//                newBoidFutures.toArray(new CompletableFuture[newBoidFutures.size()])
//        );
//
//       // When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
//        CompletableFuture<List<Boid>> allBoidFuture = allFutures.thenApply(v ->
//            newBoidFutures.stream()
//                    .map(CompletableFuture::join)
//                    .collect(Collectors.toList())
//        );
//
//        try {
//            boidPositions.setBoids(allBoidFuture.get());
//        } catch (InterruptedException | ExecutionException ignored) {
//            //
//        }
//
//
//
//        Map<String, Double> velocityParts = new HashMap<>();
//
////        Stream<CompletableFuture<Boid>> priceFuturesStream = boidPositions.getBoids().stream()
////                .map(shop -> CompletableFuture
////                        .supplyAsync(() -> moveOneBoid(boidPositions.getBoids(), boidPositions.getBoids().get(0), boidSimulationConfig.getBoidModel()))
////                        .thenCombine(
////                                CompletableFuture.supplyAsync(() -> ExchangeService.getRate(Money.EUR, Money.USD)),
////                                (price, rate) -> price * rate)
////                        .thenApply(price -> shop.getName() + " price is " + price));
////
////
////        CompletableFuture<Boid> futurePriceInUSD =
////                CompletableFuture.supplyAsync(() -> moveOneBoid(boidPositions.getBoids(), boidPositions.getBoids().get(0), boidSimulationConfig.getBoidModel()))
////                        .thenCombine(
////                                CompletableFuture.supplyAsync(
////                                        () -> ExchangeService.getRate(Money.EUR, Money.USD)),
////                                (price, rate) -> price * rate
////                        ).thenApply(price -> shop.getName() + " price is " + price);
//
////        ExecutorService executorService = Executors.newFixedThreadPool(10);
////        CompletableFuture<Boid> move1 = new CompletableFuture<>();
////        executorService.submit(() -> move1.complete(moveOneBoid(boidPositions.getBoids(), boidPositions.getBoids().get(0), boidSimulationConfig.getBoidModel())));
//
//
//    }
}
