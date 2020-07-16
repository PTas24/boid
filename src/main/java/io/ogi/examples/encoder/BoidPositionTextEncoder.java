package io.ogi.examples.encoder;

import io.ogi.examples.model.BoidPositions;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class BoidPositionTextEncoder  implements Encoder.Text<BoidPositions> {

    Jsonb jsonb = JsonbBuilder.create();

    @Override
    public String encode(BoidPositions boidPositions) throws EncodeException {
        return jsonb.toJson(boidPositions);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
