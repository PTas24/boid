package io.ogi.examples;

import io.ogi.examples.model.BoidModel;

import javax.json.bind.annotation.JsonbTransient;
import java.util.List;
import java.util.stream.Collectors;

public class Boid {
    int x;
    int y;
    int dx;
    int dy;

    @JsonbTransient
    BoidModel boidModel;


    public Boid(BoidModel boidModel) {
        this.boidModel = boidModel;
        this.x = (int)(Math.random()*((this.boidModel.canvasWidth)+1));
        this.y = (int)(Math.random()*((this.boidModel.canvasHeight)+1));
        this.dx = (int)(Math.random()*(11))-5;
        this.dy = (int)(Math.random()*(11))-5;
    }

    public void move(List<Boid> boids) {
//        System.out.println("1 dx: " + dx);
//        System.out.println("1 dy: " + dy);
        dx += flyTowardsCenterDx(getNeighbors(boids, boidModel.cohesionRange));
        dy += flyTowardsCenterDy(getNeighbors(boids, boidModel.cohesionRange));
//        System.out.println("2 dx: " + dx);
//        System.out.println("2 dy: " + dy);
        dx += avoidOthersDx(getNeighbors(boids, boidModel.separationRange));
        dy += avoidOthersDy(getNeighbors(boids, boidModel.separationRange));
//        System.out.println("3 dx: " + dx);
//        System.out.println("3 dy: " + dy);
        dx += matchVelocityDx(getNeighbors(boids, boidModel.alignmentRange));
        dy += matchVelocityDy(getNeighbors(boids, boidModel.alignmentRange));
//        System.out.println("4 dx: " + dx);
//        System.out.println("4 dy: " + dy);
        double speed = Math.sqrt(dx * dx + dy * dy);
        if (speed > boidModel.speedLimit) {
            dx = limitSpeedDx(speed);
            dy = limitSpeedDy(speed);
        }
//        System.out.println("5 dx: " + dx);
//        System.out.println("5 dy: " + dy);
        dx += keepWithinBoundsDx();
        dy += keepWithinBoundsDy();
//        System.out.println("6 dx: " + dx);
//        System.out.println("6 dy: " + dy);
//        System.out.println("zzz");
    }

    private List<Boid> getNeighbors(List<Boid> boids, int range) {
        return boids.stream().filter(other -> closeEnough(other, range)).collect(Collectors.toList());
    }

    private boolean closeEnough(Boid other, int range) {
        double distance = Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
        return distance < range;
    }

    private double flyTowardsCenterDx(List<Boid> boids) {
        if (boids.size() == 0) {
            return 0;
        }
        int centerX = boids.stream().mapToInt(b -> b.x).sum() / boids.size();
        return (centerX - x) * boidModel.cohesionFactor;
    }

    private double flyTowardsCenterDy(List<Boid> boids) {
        if (boids.size() == 0) {
            return 0;
        }
        int centerY = boids.stream().mapToInt(b -> b.y).sum() / boids.size();
        return (centerY - y) * boidModel.cohesionFactor;
    }

    private double avoidOthersDx(List<Boid> boids) {
        if (boids.size() == 0) {
            return 0;
        }
        int moveX = boids.stream().mapToInt(other -> (x - other.x)).sum();
        return moveX * boidModel.separationFactor;
    }

    private double avoidOthersDy(List<Boid> boids) {
        if (boids.size() == 0) {
            return 0;
        }
        int moveY = boids.stream().mapToInt(other -> (y - other.y)).sum();
        return moveY * boidModel.separationFactor;
    }

    private double matchVelocityDx(List<Boid> boids) {
        if (boids.size() == 0) {
            return 0;
        }
        int averageDx = boids.stream().mapToInt(b -> b.dx).sum() / boids.size();
        return (averageDx - dx) * boidModel.alignmentFactor;
    }

    private double matchVelocityDy(List<Boid> boids) {
        if (boids.size() == 0) {
            return 0;
        }
        int averageDy = boids.stream().mapToInt(b -> b.dy).sum() / boids.size();
        return (averageDy - dy) * boidModel.alignmentFactor;
    }

    private int limitSpeedDx(double speed) {
        return (int)(dx / speed) * boidModel.speedLimit;
    }

    private int limitSpeedDy(double speed) {
        return (int)(dy / speed) * boidModel.speedLimit;
    }

    private int keepWithinBoundsDx() {
        if (x < boidModel.canvasMargin) {
            return boidModel.speedAdjust;
        }
        if (x > (boidModel.canvasWidth - boidModel.canvasMargin)) {
            return -boidModel.speedAdjust;
        }
        return 0;
    }

    private int keepWithinBoundsDy() {
        if (y < boidModel.canvasMargin) {
            return boidModel.speedAdjust;
        }
        if (y > (boidModel.canvasHeight - boidModel.canvasMargin)) {
            return -boidModel.speedAdjust;
        }
        return 0;
    }



    @Override
    public String toString() {
        return "Boid{" +
                "x=" + x +
                ", y=" + y +
                ", dx=" + dx +
                ", dy=" + dy +
                '}';
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

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
}
