package io.ogi.examples;

import io.helidon.config.Config;
import io.ogi.examples.model.Boid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class BoidSimulationTest {

    Config config;
    BoidSimulationConfig boidSimulationConfig;
    BoidSimulation boidSimulation;

    @BeforeEach
    void setUp() {
        config = io.helidon.config.Config.create();
        boidSimulationConfig = new BoidSimulationConfig(config);
        boidSimulation = new BoidSimulation(boidSimulationConfig);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void moveTest() {
        Boid actualBoid = new Boid(2,2,3,4);
        List<Boid> boids = List.of(
                new Boid(2,2,3,4),
                new Boid(-1,3,4,3),
                new Boid(3,2,2,1),
                new Boid(1, -3, -3,4),
                new Boid(0, 2, -3,-4),
                new Boid(4,1,-4,3)
                );
        boidSimulation.setBoidPositions(boids);
        boidSimulation.getBoidPositions().getBoids().forEach(System.out::println);
        boidSimulation.moveTheBoids();
        System.out.println(boidSimulation.getBoidPositions());
    }
}