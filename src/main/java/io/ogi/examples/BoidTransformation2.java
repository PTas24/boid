package io.ogi.examples;

import java.util.List;

public class BoidTransformation2 {
    public static double flyTowardsCenter(int position, double center, double cohesionFactor) {
        return (center - position) * cohesionFactor;
    }

    public static double keepDistance(int position, List<Integer> otherPositions, double separationFactor) {
        int move = otherPositions.stream()
                .mapToInt(other -> (position - other))
                .sum();
        return move * separationFactor;
    }

    public static double matchVelocity(double averageVelocity, double alignmentFactor) {
        return averageVelocity * alignmentFactor;
    }

    public static double keepWithinBounds(int position, double velocity, int canvasMargin, int canvasLimit, int speedAdjust) {
        if (position < canvasMargin) {
            return velocity + speedAdjust;
        }
        if (position > (canvasLimit - canvasMargin)) {
            return velocity - speedAdjust;
        }
        return velocity;
    }
}
