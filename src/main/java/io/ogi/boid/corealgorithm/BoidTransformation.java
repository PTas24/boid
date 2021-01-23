package io.ogi.boid.corealgorithm;

import io.ogi.boid.model.Boid;

import java.util.List;
import java.util.stream.Collectors;

class BoidTransformation {

  private BoidTransformation() {}

  public static List<Boid> getNeighbors(Boid actualBoid, List<Boid> boids, int range) {
    return boids.stream()
        .filter(other -> !other.equals(actualBoid))
        .filter(other -> closeEnough(actualBoid, other, range))
        .collect(Collectors.toList());
  }

  private static boolean closeEnough(Boid actualBoid, Boid other, int range) {
    double distance =
        Math.sqrt(
            (double)(actualBoid.getX() - other.getX()) * (actualBoid.getX() - other.getX())
                + (actualBoid.getY() - other.getY()) * (actualBoid.getY() - other.getY()));
    return distance < range;
  }

  public static double flyTowardsCenter(int position, double center, double cohesionFactor) {
    if (center == 0) {
      return position;
    }
    return (center - position) * cohesionFactor;
  }

  public static double keepDistance(
      int position, List<Integer> otherPositions, double separationFactor) {
    int move = otherPositions.stream().mapToInt(other -> (position - other)).sum();
    return move * separationFactor;
  }

  public static double matchVelocity(double averageVelocity, double alignmentFactor) {
    if (averageVelocity == 0) {
      return 0;
    }
    return averageVelocity * alignmentFactor;
  }

  public static double keepWithinBounds(
      int position, double velocity, int canvasMargin, int canvasLimit, int speedAdjust) {
    if (position < canvasMargin) {
      return velocity + speedAdjust;
    }
    if (position > (canvasLimit - canvasMargin)) {
      return velocity - speedAdjust;
    }
    return velocity;
  }
}
