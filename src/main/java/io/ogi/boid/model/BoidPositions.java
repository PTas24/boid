package io.ogi.boid.model;

import java.util.ArrayList;
import java.util.List;

public class BoidPositions {

    private List<Boid> boids;

    public BoidPositions() {
        boids = new ArrayList<>();
    }

  public BoidPositions(List<Boid> boids) {
    this.boids = boids;
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
