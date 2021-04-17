package io.ogi.boid.simulation;

import io.helidon.messaging.Channel;
import io.helidon.messaging.Emitter;
import io.helidon.messaging.Messaging;
import io.ogi.boid.MessageQueue;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.corealgorithm.BoidMoves;
import io.ogi.boid.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BoidSimulationReactive {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationReactive.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;
  private Messaging moveBoidsMessaging;
  private Messaging drawMessaging;
  Channel<BoidAndNeighbours> moveBoidChannel;
  Channel<BoidPositions> drawBoidChannel;
  private List<Boid> nextPositions;
  private final BoidMoves boidMoves = new BoidMoves();

  Channel<List<Boid>> channel0 = Channel.create("channel00");
  Channel<List<BoidAndNeighbours>> channel01 = Channel.create("channel01");
  Channel<List<Boid>> channel02 = Channel.create("channel02");
  Channel<List<Boid>> channel05 = Channel.create("channel05");

  Channel<List<Boid>> channel3 = Channel.create("channel3");

  Emitter<List<Boid>> emitter = Emitter.<List<Boid>>builder()
    .channel(channel0)
    .channel(channel02)
    .channel(channel05)
    .channel(channel3)
    .build();

  public BoidSimulationReactive(BoidSimulationConfig boidSimulationConfig) {
    moveBoidChannel = Channel.create("moveBoidChannel");
    drawBoidChannel = Channel.create("drawBoidChannel");

    this.boidSimulationConfig = boidSimulationConfig;
    this.boidPositions = new BoidPositions();
    this.boidModel = boidSimulationConfig.getBoidModel();
    this.nextPositions = new ArrayList<>();
    boidModel = boidSimulationConfig.getBoidModel();
    initializeBoids();
  }

  public void initializeBoids() {
    boidModel = boidSimulationConfig.getBoidModel();
    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(toList()));
  }

  public void initializeMessaging() {
    moveBoidsMessaging =
        Messaging.builder()
            .emitter(emitter)
            .processor(
                channel0,
                channel01,
                boids ->
                    boids.stream()
                        .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
                        .peek(b -> System.out.println("current: " + b.getCurrentBoid()))
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
                        .peek(b -> System.out.println("current boid: " + b.getX()))
                        .collect(toList()))
            .listener(
                channel05,
                s ->
                    s.stream()
                        .limit(2)
                        .forEach(b -> System.out.println("Intercepted message " + b)))
            .build();

    drawMessaging =
        Messaging.builder()
            .emitter(emitter)
            .processor(channel3, drawBoidChannel, BoidPositions::new)
            .listener(drawBoidChannel, messageQueue::push)
            .build();
  }

  public void startSimReactive() {
    LOGGER.info("startSimReactive: " + boidPositions.getBoids().get(0));
    moveBoidsMessaging.start();
    drawMessaging.start();
    emitter.send(boidPositions.getBoids());
  }

  public void nextSimReactive() {
    LOGGER.info("nextSimReactive before send: " + boidPositions.getBoids().get(0));
    LOGGER.info("nextSimReactive before send: " + nextPositions.size());
    emitter.send(nextPositions);
    LOGGER.info("nextSimReactive after send: " + boidPositions.getBoids().get(0));
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
