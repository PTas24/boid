package io.ogi.examples;

import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.ogi.examples.model.*;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BoidSimulationReactive extends BoidSimulationBase {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationReactive.class.getName());
  private final MessageQueue messageQueue = MessageQueue.instance();
  private final BoidPositions boidPositions;
  private final BoidSimulationConfig boidSimulationConfig;
  private BoidModel boidModel;
  private Messaging moveBoidsMessaging;
  private Messaging drawMessaging;
  Channel<BoidAndNeighbours> moveBoidChannel;
  Channel<BoidPositions> drawBoidChannel;
  Channel<BoidPositions> nextMoveBoidChannel;
  private final List<Boid> nextPositions;

  public BoidSimulationReactive(BoidSimulationConfig boidSimulationConfig) {
    moveBoidChannel = Channel.create("moveBoidChannel");
    drawBoidChannel = Channel.create("drawBoidChannel");
    nextMoveBoidChannel = Channel.create("nextMoveBoidChannel");

    this.boidSimulationConfig = boidSimulationConfig;
    this.boidPositions = new BoidPositions();
    this.boidModel = boidSimulationConfig.getBoidModel();
    this.nextPositions = new ArrayList<>();
    initializeBoids();
  }

  public void initializeBoids() {
    boidModel = boidSimulationConfig.getBoidModel();
    LOGGER.info(() -> "model: " + boidModel);
    boidPositions.setBoids(
        Stream.generate(() -> new Boid(boidModel))
            .limit(boidModel.getNumOfBoids())
            .collect(toList()));

    moveBoidsMessaging = Messaging.builder()
        .publisher(moveBoidChannel,
            ReactiveStreams.fromIterable(boidPositions.getBoids())
            .map(boid -> BoidAndNeighbours.of(boid, boidPositions.getBoids()))
            .map(BoidAndNeighboursMessage::new))
        .subscriber(moveBoidChannel,
            ReactiveStreams.<Message<BoidAndNeighbours>>builder()
                .map(Message::getPayload)
                .map(boidAndNeighbours ->
                  moveOneBoidSync(boidAndNeighbours.getCurrentBoid(), boidAndNeighbours.getBoids(), boidModel))
                .forEach(nextPositions::add))
        .build();

    drawMessaging = Messaging.builder()
        .publisher(drawBoidChannel,
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
}
