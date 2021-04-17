package io.ogi.boid.simulation;

import io.helidon.config.Config;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.model.Boid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class BoidSimulationReactiveTest {

  static Config config;
  static BoidSimulationConfig boidSimulationConfig;
  static BoidSimulationReactive boidSimulation;

  @BeforeAll
  static void setUp() {
    config = io.helidon.config.Config.create();
    boidSimulationConfig = new BoidSimulationConfig(config);
    boidSimulation = new BoidSimulationReactive(boidSimulationConfig);
  }

//  @Test
  void moveTest() {
    List<Boid> boids =
        List.of(
            new Boid(2, 2, 3, 4),
            new Boid(-1, 3, 4, 3),
            new Boid(3, 2, 2, 1),
            new Boid(1, -3, -3, 4),
            new Boid(0, 2, -3, -4),
            new Boid(4, 1, -4, 3));

    boidSimulation.setBoidPositions(boids);
    boidSimulation.initializeMessaging();
    boidSimulation.startSimReactive();

    List<Boid> expectedBoids =
        List.of(
            new Boid(6, 7, 3.999, 5.34),
            new Boid(3, 8, 4.380000000000001, 4.563000000000001),
            new Boid(6, 4, 3.222, 2.415),
            new Boid(-1, 1, -2.0490000000000004, 4.35),
            new Boid(-2, 0, -2.2470000000000003, -2.46),
            new Boid(2, 5, -2.43, 4.167));

    Assertions.assertEquals(expectedBoids, boidSimulation.getBoidPositions().getBoids());
    Assertions.assertEquals(expectedBoids, boidSimulation.getMessageQueue().pop().getBoids());
  }
}