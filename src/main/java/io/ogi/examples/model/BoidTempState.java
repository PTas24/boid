package io.ogi.examples.model;

import java.util.List;
import java.util.stream.Collectors;

public class BoidTempState {
    final double centerX;
    final double centerY;
    final List<Integer> otherXPositions;
    final List<Integer> otherYPositions;
    final double averageXVelocity;
    final double averageYVelocity;

    public BoidTempState(List<Boid> boids) {
        centerX = boids.stream().mapToDouble(Boid::getX).sum() / boids.size();
        centerY = boids.stream().mapToDouble(Boid::getY).sum() / boids.size();
        otherXPositions = boids.stream().map(Boid::getX).collect(Collectors.toList());
        otherYPositions = boids.stream().map(Boid::getY).collect(Collectors.toList());
        averageXVelocity = boids.stream().mapToDouble(Boid::getDx).sum() / boids.size();
        averageYVelocity = boids.stream().mapToDouble(Boid::getDy).sum() / boids.size();
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public List<Integer> getOtherXPositions() {
        return otherXPositions;
    }

    public List<Integer> getOtherYPositions() {
        return otherYPositions;
    }

    public double getAverageXVelocity() {
        return averageXVelocity;
    }

    public double getAverageYVelocity() {
        return averageYVelocity;
    }
}
