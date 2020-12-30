package io.ogi.examples;

import io.helidon.config.Config;
import io.ogi.examples.model.BoidModel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class BoidSimulationConfig {

  private static final Logger LOGGER = Logger.getLogger(BoidSimulationConfig.class.getName());

  AtomicReference<BoidModel> boidModel;

  public BoidSimulationConfig(Config config) {
    this.boidModel = new AtomicReference<>();
    initializeParameters(config);
    LOGGER.info(() -> String.valueOf(boidModel.get()));
  }

  public BoidModel getBoidModel() {
    return boidModel.get();
  }

  private void initializeParameters(Config config) {

    Config canvasConfig = config.get("app.canvas");
    Config rangeConfig = config.get("app.range");
    Config factorConfig = config.get("app.factor");
    BoidModel tmp = new BoidModel();
    tmp.setCanvasWidth(canvasConfig.get("width").asInt().orElse(200));
    tmp.setCanvasHeight(canvasConfig.get("height").asInt().orElse(200));
    tmp.setCanvasMargin(canvasConfig.get("margin").asInt().orElse(10));
    tmp.setSpeedAdjust(config.get("app.speedAdjust").asInt().orElse(1));
    tmp.setNumOfBoids(config.get("app.numOfBoids").asInt().orElse(200));
    tmp.setCohesionRange(rangeConfig.get("cohesion").asInt().orElse(75));
    tmp.setSeparationRange(rangeConfig.get("separation").asInt().orElse(20));
    tmp.setAlignmentRange(rangeConfig.get("alignment").asInt().orElse(75));
    tmp.setCohesionFactor(factorConfig.get("cohesion").asDouble().orElse(0.005));
    tmp.setSeparationFactor(factorConfig.get("separation").asDouble().orElse(0.05));
    tmp.setAlignmentFactor(factorConfig.get("alignment").asDouble().orElse(0.05));
    tmp.setSpeedLimit(config.get("app.speedLimit").asInt().orElse(15));
    tmp.setSimulationSpeed(config.get("app.simulationSpeed").asInt().orElse(5));
    tmp.setInitialMaxSpeed(config.get("app.initialMaxSpeed").asInt().orElse(5));
    boidModel.set(tmp);
    LOGGER.info(() -> "init: " + boidModel.get());
  }

  public CompletionStage<BoidModel> getParams() {
    return CompletableFuture.completedFuture(boidModel.get());
  }

  public CompletionStage<BoidModel> modifyBoidSimulation(BoidModel newBoidModel) {
    boidModel.set(newBoidModel);
    LOGGER.info(() -> String.valueOf(getBoidModel()));
    return CompletableFuture.completedFuture(boidModel.get());
  }
}
