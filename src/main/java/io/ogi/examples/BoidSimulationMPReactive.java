package io.ogi.examples;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.ogi.examples.model.*;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static io.ogi.examples.BoidTransformation.*;
import static java.util.stream.Collectors.toList;

public class BoidSimulationMPReactive implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationMPReactive.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private BoidPositions nextBoidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;
  private Messaging moveBoidsMessaging;
  private Messaging drawBoidMessaging;
  private Messaging nextMoveBoidMessaging;
  Channel<BoidAndNeighbours> moveBoidChannel;
  Channel<BoidMessage> drawBoidChannel;
  Channel<BoidMessage> nextMoveBoidChannel;
  private List<Boid> nextPositions;
  private static ScheduledExecutorService executor;
//  private Executor executor;
//  private Channel<Boid> channel1

  public BoidSimulationMPReactive(BoidSimulationConfig boidSimulationConfig) {
//    Channel<String> channel1 = Channel.create("channel1");
    moveBoidChannel = Channel.create("moveBoidChannel");
    drawBoidChannel = Channel.create("drawBoidChannel");
    nextMoveBoidChannel = Channel.create("nextMoveBoidChannel");

    this.boidSimulationConfig = boidSimulationConfig;
    this.boidPositions = new BoidPositions();
    this.boidModel = boidSimulationConfig.getBoidModel();
    this.nextPositions = new ArrayList<>();
    executor = Executors.newSingleThreadScheduledExecutor();
    initializeBoids();
  }

  public void initializeBoids() {
    boidModel = boidSimulationConfig.getBoidModel();
    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(toList()));

//    Channel<BoidAndNeighbours> boidAndNeighboursChannel = Channel.create("boids");
    moveBoidsMessaging = Messaging.builder()
        .publisher(moveBoidChannel,
            Multi.create(boidPositions.getBoids())
            .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
            .map(BoidAndNeighboursMessage::new))
        .subscriber(moveBoidChannel,
            multi -> multi
                .map(Message::getPayload)
                .map(pl ->
                  moveOneBoid(pl.getCurrentBoid(), pl.getBoids(), boidModel))
                .forEach(nextPositions::add))
        .build()
        ;
//        .start();



    drawBoidMessaging = Messaging.builder()
        .publisher(drawBoidChannel,
            Single.just(new BoidPositions(nextPositions)).map((Function<BoidPositions, BoidMessage>) BoidMessage::new))
        .subscriber(drawBoidChannel,
            multi -> multi
                .map(Message::getPayload)
                .forEach(bp -> messageQueue.push((BoidPositions) bp.getPayload())))
        .build()
    ;

    nextMoveBoidMessaging = Messaging.builder()
        .publisher(nextMoveBoidChannel,
            Single.just(new BoidPositions(nextPositions)).map((Function<BoidPositions, BoidMessage>) BoidMessage::new))
        .subscriber(drawBoidChannel,
            multi -> multi
                .map(Message::getPayload)
//                .timer(1, TimeUnit.MILLISECONDS, executor)
                .forEach(bp -> boidPositions.setBoids(((BoidPositions) bp.getPayload()).getBoids()))
        )
        .build();


//    moveBoidsMessaging = Messaging.builder()
//        .publisher(moveBoidChannel,
//            Multi.create(boidPositions.getBoids())
//                .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
//                .map(BoidAndNeighboursMessage::new))
//        .subscriber(moveBoidChannel,
//            multi -> multi
//                .map(Message::getPayload)
//                .map(pl ->
//                    moveOneBoid(pl.getCurrentBoid(), pl.getBoids(), boidModel))
//                .forEach(nextPositions::add))
//        .build()
//    ;

//    executor =
//        Executors.newFixedThreadPool(
//            Math.min(boidModel.getNumOfBoids(), 100),
//            (Runnable r) -> {
//              Thread t = new Thread(r);
//              t.setDaemon(true);
//              return t;
//            });
  }

  public BoidModel getBoidModel() {
    return boidModel;
  }

  private void drawTheBoids() {
    messageQueue.push(boidPositions);
  }

  @Override
  public void run() {
//    moveTheBoidAsync();
    drawTheBoids();
  }

  private Boid moveOneBoid(Boid boid, List<Boid> boids, BoidModel boidModel) {
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

  }

  private double getAverageValue(List<Boid> neighbours, DoubleStream doubleStream) {
    if (neighbours.isEmpty()) {
      return 0;
    }
    return doubleStream.sum() / neighbours.size();
  }

//  void moveTheBoidAsync() {
//    List<CompletableFuture<Boid>> newBoidFutures =
//        boidPositions.getBoids().stream()
//            .map(boid -> moveOneBoid(boid, boidPositions.getBoids(), boidModel))
//            .collect(toList());
//
//    List<Boid> newBoid = newBoidFutures.stream().map(CompletableFuture::join).collect(toList());
//    boidPositions.setBoids(newBoid);
//  }
}
