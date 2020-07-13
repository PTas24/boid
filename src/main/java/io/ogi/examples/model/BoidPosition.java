package io.ogi.examples.model;

import io.ogi.examples.Boid;

import java.util.List;

public class BoidPosition {

    public List<Boid> boids;

    public BoidPosition() {
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public void setBoids(List<Boid> boids) {
        this.boids = boids;
    }
}
