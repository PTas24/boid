package io.ogi.examples.model;

import java.util.List;

public class BoidAndNeighbours {
  private final Boid currentBoid;
  private final List<Boid> boids;

  public BoidAndNeighbours(Boid currentBoid, List<Boid> boids) {
    this.currentBoid = currentBoid;
    this.boids = boids;
  }
  public static BoidAndNeighbours of(Boid currentBoid, List<Boid> boids) {
    return new BoidAndNeighbours(currentBoid, boids);
  }

  public Boid getCurrentBoid() {
    return currentBoid;
  }

  public List<Boid> getBoids() {
    return boids;
  }

}
