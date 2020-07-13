package io.ogi.examples.model;

public class BoidModel {
    public int canvasWidth;
    public int canvasHeight;
    public int canvasMargin;
    public int speedAdjust;
    public int numOfBoids;
    public int cohesionRange;
    public int separationRange;
    public int alignmentRange;
    public double cohesionFactor;
    public double separationFactor;
    public double alignmentFactor;
    public int speedLimit;

    @Override
    public String toString() {
        return "BoidModel{" +
                "canvasWidth=" + canvasWidth +
                ", canvasHeight=" + canvasHeight +
                ", canvasMargin=" + canvasMargin +
                ", speedAdjust=" + speedAdjust +
                ", numOfBoids=" + numOfBoids +
                ", cohesionRange=" + cohesionRange +
                ", separationRange=" + separationRange +
                ", alignmentRange=" + alignmentRange +
                ", cohesionFactor=" + cohesionFactor +
                ", separationFactor=" + separationFactor +
                ", alignmentFactor=" + alignmentFactor +
                ", speedLimit=" + speedLimit +
                '}';
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getCanvasMargin() {
        return canvasMargin;
    }

    public void setCanvasMargin(int canvasMargin) {
        this.canvasMargin = canvasMargin;
    }

    public int getSpeedAdjust() {
        return speedAdjust;
    }

    public void setSpeedAdjust(int speedAdjust) {
        this.speedAdjust = speedAdjust;
    }

    public int getNumOfBoids() {
        return numOfBoids;
    }

    public void setNumOfBoids(int numOfBoids) {
        this.numOfBoids = numOfBoids;
    }

    public int getCohesionRange() {
        return cohesionRange;
    }

    public void setCohesionRange(int cohesionRange) {
        this.cohesionRange = cohesionRange;
    }

    public int getSeparationRange() {
        return separationRange;
    }

    public void setSeparationRange(int separationRange) {
        this.separationRange = separationRange;
    }

    public int getAlignmentRange() {
        return alignmentRange;
    }

    public void setAlignmentRange(int alignmentRange) {
        this.alignmentRange = alignmentRange;
    }

    public double getCohesionFactor() {
        return cohesionFactor;
    }

    public void setCohesionFactor(double cohesionFactor) {
        this.cohesionFactor = cohesionFactor;
    }

    public double getSeparationFactor() {
        return separationFactor;
    }

    public void setSeparationFactor(double separationFactor) {
        this.separationFactor = separationFactor;
    }

    public double getAlignmentFactor() {
        return alignmentFactor;
    }

    public void setAlignmentFactor(double alignmentFactor) {
        this.alignmentFactor = alignmentFactor;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }
}
