package io.ogi.boid.simulation;

import io.helidon.common.reactive.BufferedEmittingPublisher;
import io.helidon.common.reactive.Multi;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Emitter;
import io.helidon.messaging.Messaging;
import io.ogi.boid.MessageQueue;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.corealgorithm.BoidMoves;
import io.ogi.boid.model.*;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BoidSimulationReactive2 {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationReactive2.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;
  private Messaging moveBoidsMessaging;
  private Messaging drawMessaging;
  Channel<BoidAndNeighbours> moveBoidChannel;
  Channel<List<Boid>> moveBoidChannel2;
  Channel<BoidPositions> drawBoidChannel;
  Channel<List<Boid>> drawBoidChannel2;
  Channel<BoidPositions> nextMoveBoidChannel;
  private List<Boid> nextPositions;
  private final BoidMoves boidMoves = new BoidMoves();
//  Emitter<BoidAndNeighbours> myEmitter = Emitter.create(moveBoidChannel);
//  BufferedEmittingPublisher<BoidAndNeighbours> emitter = BufferedEmittingPublisher.create();

  Channel<List<Boid>> channel0 = Channel.create("channel00");
  Channel<List<BoidAndNeighbours>> channel01 = Channel.create("channel01");
  Channel<List<Boid>> channel02 = Channel.create("channel02");
  Channel<List<Boid>> channel05 = Channel.create("channel05");

  Channel<List<Boid>> channel3 = Channel.create("channel3");

  private ScheduledExecutorService scheduledExecutorService;
//  Emitter<List<Boid>> emitter = Emitter.create(channel0);
  Emitter<List<Boid>> emitter = Emitter.<List<Boid>>builder()
    .channel(channel0)
    .channel(channel02)
    .channel(channel05)
    .channel(channel3)
    .build();

  public BoidSimulationReactive2(BoidSimulationConfig boidSimulationConfig) {
    moveBoidChannel = Channel.create("moveBoidChannel");
//    moveBoidChannel2 = Channel.create("moveBoidChannel2");
    drawBoidChannel = Channel.create("drawBoidChannel");
//    drawBoidChannel2 = Channel.create("drawBoidChannel2");
//    nextMoveBoidChannel = Channel.create("nextMoveBoidChannel");

    this.boidSimulationConfig = boidSimulationConfig;
    this.boidPositions = new BoidPositions();
    this.boidModel = boidSimulationConfig.getBoidModel();
    this.nextPositions = new ArrayList<>();
    boidModel = boidSimulationConfig.getBoidModel();
    initializeBoids();
//    initializeMessaging();
  }

  public void initializeBoids() {
    boidModel = boidSimulationConfig.getBoidModel();
    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(toList()));
//    nextPositions = boidPositions.getBoids();
  }

  public void initializeMessaging() {


    //    ReactiveStreams.generate(() -> UUID.randomUUID().toString())
    //        .flatMapCompletionStage(uuid -> CompletableFuture.supplyAsync(() -> {
    //          // do some long repetitive task
    //          return Multi.interval(1, TimeUnit.SECONDS, scheduledExecutorService)
    //              .limit(1)
    //              .map(i -> uuid)
    //              .first()
    //              .await();
    //        }))
    //        .forEach(System.out::println)
    //        .run()
    //        .toCompletableFuture()
    //        .get();

    //    Emitter<List<Boid>> emitter = Emitter.create(channel0);

    moveBoidsMessaging =
        Messaging.builder()
            .emitter(emitter)
            .processor(
                channel0,
                channel01,
                boids ->
                    boids.stream()
                        .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
                        .peek((b) -> System.out.println("current: " + b.getCurrentBoid()))
                        .collect(toList()))
            .processor(
                channel01,
                channel02,
                boidAndN ->
                    boidAndN.stream()
                        .map(
                            boidAndNeighbours ->
                                boidMoves.moveOneBoidSync(
                                    boidAndNeighbours.getCurrentBoid(),
                                    boidAndNeighbours.getBoids(),
                                    boidModel))
                        .peek((b) -> System.out.println("current boid: " + b.getX()))
                        .collect(toList()))

            //        .publisher(channel1, Multi.create(boidPositions.getBoids())
            //            .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
            //            .map(Message::of)
            //        )
            //        .processor(channel1, channel2,
            // ReactiveStreams.<Message<BoidAndNeighbours>>builder()
            //            .map(Message::getPayload)
            //            .map(
            //                boidAndNeighbours ->
            //                    boidMoves.moveOneBoidSync(
            //                        boidAndNeighbours.getCurrentBoid(),
            //                        boidAndNeighbours.getBoids(),
            //                        boidModel))
            //            .map(Message::of)
            //        )
//            .listener(
//                channel02,
//                all -> {
//                  System.out.println("itt: " + all.get(0));
//                  nextPositions.clear();
//                  nextPositions.addAll(all);
//                })
            //            .listener(channel02, boidPositions::setBoids)
            //        .listener(channel02, nextPositions::addAll)
            //        .listener(channel2, nextPositions::add)
            .listener(
                channel05,
                s ->
                    s.stream()
                        .limit(2)
                        .forEach(b -> System.out.println("Intercepted message " + b)))
            //            .subscriber(
            //                channel02,
            //                ReactiveStreams.<Message<List<Boid>>>builder()
            //                    .map(Message::getPayload)
            //                    .peek((b) -> System.out.println("next boid: " + b.get(0).getX()))
            //                    .forEach(emitter::send))

            //            sb -> emitter.send(sb.))
            .build()
    //        .start()
    ;

//    moveBoidsMessaging =
//        Messaging.builder()
//            //            .publisher(
//            //                moveBoidChannel,
//            //                ReactiveStreams.fromIterable(boidPositions.getBoids())
//            //                    .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
//            //                    .map(BoidAndNeighboursMessage::new))
//            .publisher(
//                moveBoidChannel,
//                Multi.create(boidPositions.getBoids())
//                    .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
//                    .map(Message::of))
//            .processor(
//                moveBoidChannel,
//                moveBoidChannel2,
//                message -> {
//                  try {
//                    return ReactiveStreams.<Message<BoidAndNeighbours>>builder()
//                        .peek(Message::ack)
//                        .map(Message::getPayload)
//                        .map(
//                            boidAndNeighbours ->
//                                boidMoves.moveOneBoidSync(
//                                    boidAndNeighbours.getCurrentBoid(),
//                                    boidAndNeighbours.getBoids(),
//                                    boidModel))
//                        .collect(Collectors.toList())
//                        .build()
//                        .getCompletion()
//                        .toCompletableFuture()
//                        .get();
//                  } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    return List.of();
//                  } catch (ExecutionException e) {
//                    e.printStackTrace();
//                    return List.of();
//                  }}
//                )
//            //            .processor(
//            //            moveBoidChannel2, drawBoidChannel, message ->
//            //              ReactiveStreams.<Message<List<Boid>>>builder()
//            //                  .peek(Message::ack)
//            ////                  .map(Message::getPayload)
//            //                  .map()
//            //            )
//            //            .processor(moveBoidChannel, drawBoidChannel2,
//            //                ReactiveStreams.<Message<BoidAndNeighbours,
//            // List<Boid>>>fromProcessor(moveBoidChannel)
//            //                .peek(Message::ack)
//            //                .map(Message::getPayload)
//            //                .map(BoidAndNeighbours::getCurrentBoid)
//            //                .forEach(nextPositions::add))
//            //
//            .subscriber(
//                moveBoidChannel2,
//                ReactiveStreams.<Message<List<Boid>>>builder()
//                    .peek(Message::ack)
//                    .map(Message::getPayload)
//                    .map(BoidPositions::new)
//                    .forEach(
//                        m -> {
//                          messageQueue.push(m);
//                          System.out.println(m);
//                        }))
//            .build()
//    //            .start()
//    ;

    drawMessaging =
        Messaging.builder()
            .emitter(emitter)
            .processor(channel3, drawBoidChannel, BoidPositions::new)
//            .publisher(
//                drawBoidChannel,
////                ReactiveStreams.of(new BoidPositions(nextPositions))
//                Multi.just(new BoidPositions(boidPositions.getBoids()))
//                    .map(BoidMessage::new))
            .listener(drawBoidChannel, messageQueue::push)
            .build();
  }

  public void startSimReactive() {
    System.out.println("aaaaaa: " + boidPositions.getBoids().get(0));
//    boidPositions.setBoids(nextPositions);
    moveBoidsMessaging.start();
    drawMessaging.start();
    emitter.send(boidPositions.getBoids());
//    moveBoidsMessaging.stop();
//    drawMessaging.stop();
//    moveBoidsMessaging.stop();
//    boidPositions.setBoids(nextPositions);
//    drawMessaging.stop();

  }

  public void nextSimReactive() {
    System.out.println("bbbbbb: " + boidPositions.getBoids().get(0));
    System.out.println("dddddd: " + nextPositions.size());



    emitter.send(nextPositions);

//    moveBoidsMessaging.stop();
//    moveBoidsMessaging.start();
//    moveBoidsMessaging.stop();
//    drawMessaging.stop();
//    drawMessaging.start();
//    boidPositions.setBoids(nextPositions);
//    nextPositions.clear();
    System.out.println("xxxxx: " + boidPositions.getBoids().get(0));
  }

  public void stopSimReactive() {
    moveBoidsMessaging.stop();
  }

  public BoidModel getBoidModel() {
    return boidModel;
  }

  void setBoidPositions(List<Boid> boids) {
    boidPositions.setBoids(boids);
  }

  BoidPositions getBoidPositions() {
    return boidPositions;
  }

  MessageQueue getMessageQueue() {
    return messageQueue;
  }
}
