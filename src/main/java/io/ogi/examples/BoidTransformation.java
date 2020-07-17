package io.ogi.examples;

import io.ogi.examples.model.Boid;

import java.util.List;
import java.util.stream.Collectors;

public class BoidTransformation {

    public static List<Boid> getNeighbors(Boid actualBoid, List<Boid> boids, int range) {
        return boids.stream()
                .filter(other -> !other.equals(actualBoid))
                .filter(other -> closeEnough(actualBoid, other, range))
                .collect(Collectors.toList());
    }

    public static boolean closeEnough(Boid actualBoid, Boid other, int range) {
        double distance = Math.sqrt(
                (actualBoid.getX() - other.getX()) * (actualBoid.getX() - other.getX())
                        + (actualBoid.getY() - other.getY()) * (actualBoid.getY() - other.getY()));
        return distance < range;
    }

    public static double flyTowardsCenterDx(Boid actualBoid, List<Boid> boids, double cohesionFactor) {
        if (boids.size() == 0) {
            return 0;
        }
        double centerX = boids.stream().mapToDouble(Boid::getX).sum() / boids.size();
        return (centerX - actualBoid.getX()) * cohesionFactor;
    }

    public static double flyTowardsCenterDy(Boid actualBoid, List<Boid> boids, double cohesionFactor) {
        if (boids.size() == 0) {
            return 0;
        }
        double centerY = boids.stream().mapToDouble(Boid::getY).sum() / boids.size();
        return (centerY - actualBoid.getY()) * cohesionFactor;
    }

    public static double keepdistanceDx(Boid actualBoid, List<Boid> boids, double separationFactor) {
        if (boids.size() == 0) {
            return 0;
        }
        int moveX = boids.stream()
                .mapToInt(other -> (actualBoid.getX() - other.getX()))
                .sum();
        return moveX * separationFactor;
    }

    public static double keepdistanceDxSzar(Boid actualBoid, List<Boid> boids, double separationFactor) {
        if (boids.size() == 0) {
            return 0;
        }
//        double moveDx = boids.stream()
//                .mapToDouble(Boid::getX)
//                .reduce(0, (acc, next) -> acc - (next - actualBoid.getX()));
//        return moveDx;
//        return moveDx * separationFactor;
//        System.out.println("group size: " + boids.size());
        int moveX = boids.stream()
//                .peek(other -> System.out.println("actual x: " + actualBoid.getX() + " other x: " + other.getX()))
//                .mapToInt(other -> (other.getX() - actualBoid.getX()))
                .mapToInt(other -> (actualBoid.getX() - other.getX()))
//                .peek(xx -> System.out.println(" diff x: " + xx))
                .sum();
//        System.out.println("moveX: " + moveX);
        return moveX * separationFactor;
    }

    public static double keepDistanceDy(Boid actualBoid, List<Boid> boids, double separationFactor) {
        if (boids.size() == 0) {
            return 0;
        }
//        double moveDy = boids.stream()
//                .mapToDouble(Boid::getY)
//                .reduce(0, (acc, next) -> acc - (next - actualBoid.getY()));
//        return moveDy;
//        return moveDy * separationFactor;
        int moveY = boids.stream().mapToInt(other -> (actualBoid.getY() - other.getY())).sum();
        return moveY * separationFactor;
    }

    public static double matchVelocityDx(Boid actualBoid, List<Boid> boids, double alignmentFactor) {
        if (boids.size() == 0) {
            return 0;
        }
        double averageDx = boids.stream().mapToDouble(Boid::getDx).sum() / boids.size();
//        return (averageDx - actualBoid.getDx()) * alignmentFactor;
        return averageDx * alignmentFactor;
    }

    public static double matchVelocityDy(Boid actualBoid, List<Boid> boids, double alignmentFactor) {
        if (boids.size() == 0) {
            return 0;
        }
        double averageDy = boids.stream().mapToDouble(Boid::getDy).sum() / boids.size();
//        return (averageDy - actualBoid.getDy()) * alignmentFactor;
        return averageDy * alignmentFactor;
    }

    public static double limitSpeedDx(Boid actualBoid, double speed, int speedLimit) {
        return (actualBoid.getDx() / speed) * speedLimit;
    }

    public static double limitSpeedDy(Boid actualBoid, double speed, int speedLimit) {
        return (actualBoid.getDy() / speed) * speedLimit;
    }

    public static int keepWithinBoundsDx(Boid actualBoid, int canvasWidth, int canvasMargin, int speedAdjust) {
        if (actualBoid.getX() < canvasMargin) {
            System.out.println("x: " + actualBoid.getX());
            return speedAdjust;
        }
        if (actualBoid.getX() > (canvasWidth - canvasMargin)) {
            System.out.println("x: " + actualBoid.getX());
            return -speedAdjust;
        }
        return 0;
    }

    public static int keepWithinBoundsDy(Boid actualBoid, int canvasHeight, int canvasMargin, int speedAdjust) {
        if (actualBoid.getY() < canvasMargin) {
            return speedAdjust;
        }
        if (actualBoid.getY() > (canvasHeight - canvasMargin)) {
            return -speedAdjust;
        }
        return 0;
    }
}
