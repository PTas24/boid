package io.ogi.examples;

import io.helidon.config.Config;
import io.helidon.webserver.*;
import io.ogi.examples.model.BoidModel;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import java.util.Collections;
import java.util.logging.Logger;

public class BoidService  implements Service {

    private final BoidSimulation simulation;
    private static final Logger LOGGER = Logger.getLogger(BoidService.class.getName());
    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    public BoidService(Config config) {
        simulation = new BoidSimulation(config);
        simulation.startBoidSimulation();
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::getCurrentBoidParams);
        rules.post("/", Handler.create(BoidModel.class, this::startWithNewBoidParams));
    }

    private void getCurrentBoidParams(ServerRequest serverRequest, ServerResponse serverResponse) {
        LOGGER.fine("getCurrentBoidParams");
        simulation.getParams()
                .thenAccept(serverResponse::send)
                .exceptionally(serverResponse::send);
    }

    private void startWithNewBoidParams(ServerRequest serverRequest, ServerResponse serverResponse, BoidModel boidModel) {
        LOGGER.fine("startWithNewBoidParams");

        this.simulation.modifyBoidSimulation(boidModel)
                .thenAccept(r -> serverResponse.status(201).send())
                .exceptionally(serverResponse::send);
    }

}
