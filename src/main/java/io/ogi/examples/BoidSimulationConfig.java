package io.ogi.examples;

import io.helidon.config.Config;
import io.ogi.examples.model.BoidModel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

public class BoidSimulationConfig {

    AtomicReference<BoidModel> boidModel;

    public BoidSimulationConfig(Config config) {
        this.boidModel = new AtomicReference<>();
        initializeParameters(config);
//        System.out.println(boidModel);
    }

    public BoidModel getBoidModel() {
        return boidModel.get();
    }

    private void initializeParameters(Config config) {

        Config canvasConfig = config.get("app.canvas");
        Config rangeConfig = config.get("app.range");
        Config factorConfig = config.get("app.factor");
        BoidModel tmp = new BoidModel();
        tmp.canvasWidth = canvasConfig.get("width").asInt().orElse(200);
        tmp.canvasHeight = canvasConfig.get("height").asInt().orElse(200);
        tmp.canvasMargin = canvasConfig.get("margin").asInt().orElse(10);
        tmp.speedAdjust = config.get("app.speedAdjust").asInt().orElse(1);
        tmp.numOfBoids = config.get("app.numOfBoids").asInt().orElse(200);
        tmp.cohesionRange = rangeConfig.get("cohesion").asInt().orElse(75);
        tmp.separationRange = rangeConfig.get("separation").asInt().orElse(20);
        tmp.alignmentRange = rangeConfig.get("alignment").asInt().orElse(75);
        tmp.cohesionFactor =   factorConfig.get("cohesion").asDouble().orElse(0.005);
        tmp.separationFactor = factorConfig.get("separation").asDouble().orElse(0.05);
        tmp.alignmentFactor =  factorConfig.get("alignment").asDouble().orElse(0.05);
        tmp.speedLimit = config.get("app.speedLimit").asInt().orElse(15);
        tmp.simulationSpeed = config.get("app.simulationSpeed").asInt().orElse(5);
        tmp.initialMaxSpeed = config.get("app.initialMaxSpeed").asInt().orElse(5);
        boidModel.set(tmp);
        System.out.println("init: " + boidModel);
    }

    public CompletionStage<BoidModel> getParams() {
        return CompletableFuture.completedFuture(boidModel.get());
    }

    public CompletionStage<BoidModel> modifyBoidSimulation(BoidModel newBoidModel) {
        //System.out.println(newBoidModel);
        boidModel.set(newBoidModel);
        System.out.println(getBoidModel());
        return CompletableFuture.completedFuture(boidModel.get());
    }

}
