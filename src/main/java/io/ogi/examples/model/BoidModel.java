package io.ogi.examples.model;

public class BoidModel {
  private int canvasWidth;
  private int canvasHeight;
  private int canvasMargin;
  private int speedAdjust;
  private int numOfBoids;
  private int cohesionRange;
  private int separationRange;
  private int alignmentRange;
  private double cohesionFactor;
  private double separationFactor;
  private double alignmentFactor;
  private int speedLimit;
  private int simulationSpeed;
  private int initialMaxSpeed;

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

  public int getSimulationSpeed() {
    return simulationSpeed;
  }

  public void setSimulationSpeed(int simulationSpeed) {
    this.simulationSpeed = simulationSpeed;
  }

  public int getInitialMaxSpeed() {
    return initialMaxSpeed;
  }

  public void setInitialMaxSpeed(int initialMaxSpeed) {
    this.initialMaxSpeed = initialMaxSpeed;
  }

  @Override
  public String toString() {
    return "BoidModel{\n"
        + "canvasWidth="
        + canvasWidth
        + ", \ncanvasHeight="
        + canvasHeight
        + ", \ncanvasMargin="
        + canvasMargin
        + ", \nspeedAdjust="
        + speedAdjust
        + ", \nnumOfBoids="
        + numOfBoids
        + ", \ncohesionRange="
        + cohesionRange
        + ", \nseparationRange="
        + separationRange
        + ", \nalignmentRange="
        + alignmentRange
        + ", \ncohesionFactor="
        + cohesionFactor
        + ", \nseparationFactor="
        + separationFactor
        + ", \nalignmentFactor="
        + alignmentFactor
        + ", \nspeedLimit="
        + speedLimit
        + ", \nsimulationSpeed="
        + simulationSpeed
        + ", \ninitialMaxSpeed="
        + initialMaxSpeed
        + '}';
  }
}
