package io.ogi.boid.model;

import java.util.Objects;
import java.util.Random;

public class Boid {
  int x;
  int y;
  double dx;
  double dy;

  public Boid() {}

  public Boid(BoidModel boidModel) {
    Random r = new Random();
    this.x = r.nextInt((boidModel.getCanvasWidth()) + 1);
    this.y = r.nextInt((boidModel.getCanvasHeight()) + 1);
    this.dx =
        Math.random() * (boidModel.getInitialMaxSpeed() * 2 + 1) - boidModel.getInitialMaxSpeed();
    this.dy =
        Math.random() * (boidModel.getInitialMaxSpeed() * 2 + 1) - boidModel.getInitialMaxSpeed();
  }

  public Boid(int x, int y, double dx, double dy) {
    this.x = x;
    this.y = y;
    this.dx = dx;
    this.dy = dy;
  }

  @Override
  public String toString() {
    return "Boid{" + "x=" + x + ", y=" + y + ", dx=" + dx + ", dy=" + dy + '}';
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public double getDx() {
    return dx;
  }

  public void setDx(double dx) {
    this.dx = dx;
  }

  public double getDy() {
    return dy;
  }

  public void setDy(double dy) {
    this.dy = dy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Boid boid = (Boid) o;
    return x == boid.x
        && y == boid.y
        && Double.compare(boid.dx, dx) == 0
        && Double.compare(boid.dy, dy) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, dx, dy);
  }
}
