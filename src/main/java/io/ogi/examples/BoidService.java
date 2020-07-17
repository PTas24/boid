package io.ogi.examples;

import io.helidon.webserver.*;
import io.ogi.examples.model.BoidModel;

import java.util.logging.Logger;

public class BoidService  implements Service {

    private final BoidSimulationConfig boidSimulationConfig;
    private static final Logger LOGGER = Logger.getLogger(BoidService.class.getName());

    public BoidService(BoidSimulationConfig boidSimulationConfig) {
        this.boidSimulationConfig = boidSimulationConfig;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::getCurrentBoidParams);
        rules.post("/", Handler.create(BoidModel.class, this::startWithNewBoidParams));
    }

    private void getCurrentBoidParams(ServerRequest serverRequest, ServerResponse serverResponse) {
        LOGGER.fine("getCurrentBoidParams");
        boidSimulationConfig.getParams()
                .thenAccept(serverResponse::send)
                .exceptionally(serverResponse::send);
    }

    private void startWithNewBoidParams(ServerRequest serverRequest, ServerResponse serverResponse, BoidModel boidModel) {
        LOGGER.fine("startWithNewBoidParams");

        this.boidSimulationConfig.modifyBoidSimulation(boidModel)
//                .thenApply()
                .thenAccept(r -> serverResponse.status(201).send())
                .exceptionally(serverResponse::send);
    }

}
