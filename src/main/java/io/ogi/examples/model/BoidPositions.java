package io.ogi.examples.model;

import java.util.List;

public class BoidPositions {

    private List<Boid> boids;

    public BoidPositions() {
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public void setBoids(List<Boid> boids) {
        this.boids = boids;
    }

    @Override
    public String toString() {
        return "BoidPosition{" +
                "boids=" + getBoids() +
                '}';
    }
}
