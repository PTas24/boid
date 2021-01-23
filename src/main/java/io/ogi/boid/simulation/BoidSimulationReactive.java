package io.ogi.boid.simulation;

import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.ogi.boid.MessageQueue;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.corealgorithm.BoidMoves;
import io.ogi.boid.model.*;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BoidSimulationReactive {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationReactive.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private BoidModel boidModel;
  private Messaging moveBoidsMessaging;
  private Messaging drawMessaging;
  Channel<BoidAndNeighbours> moveBoidChannel;
  Channel<BoidPositions> drawBoidChannel;
  Channel<BoidPositions> nextMoveBoidChannel;
  private final List<Boid> nextPositions;
  private final BoidMoves boidMoves = new BoidMoves();

  public BoidSimulationReactive(BoidSimulationConfig boidSimulationConfig) {
    moveBoidChannel = Channel.create("moveBoidChannel");
    drawBoidChannel = Channel.create("drawBoidChannel");
    nextMoveBoidChannel = Channel.create("nextMoveBoidChannel");

    this.boidPositions = new BoidPositions();
    this.boidModel = boidSimulationConfig.getBoidModel();
    this.nextPositions = new ArrayList<>();
    boidModel = boidSimulationConfig.getBoidModel();
    initializeBoids();
    initializeMessaging();
  }

  public void initializeBoids() {
    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(toList()));
  }

  public void initializeMessaging() {

    moveBoidsMessaging =
        Messaging.builder()
            .publisher(
                moveBoidChannel,
                ReactiveStreams.fromIterable(boidPositions.getBoids())
                    .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
                    .map(BoidAndNeighboursMessage::new))
            .subscriber(
                moveBoidChannel,
                ReactiveStreams.<Message<BoidAndNeighbours>>builder()
                    .map(Message::getPayload)
                    .map(
                        boidAndNeighbours ->
                            boidMoves.moveOneBoidSync(
                                boidAndNeighbours.getCurrentBoid(),
                                boidAndNeighbours.getBoids(),
                                boidModel))
                    .forEach(nextPositions::add))
            .build();

    drawMessaging =
        Messaging.builder()
            .publisher(
                drawBoidChannel,
                ReactiveStreams.of(new BoidPositions(nextPositions))
                    .map((Function<BoidPositions, BoidMessage<BoidPositions>>) BoidMessage::new))
            .listener(drawBoidChannel, messageQueue::push)
            .build();
  }

  public void startSim() {
    moveBoidsMessaging.start();
    drawMessaging.start();
    boidPositions.setBoids(nextPositions);
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
