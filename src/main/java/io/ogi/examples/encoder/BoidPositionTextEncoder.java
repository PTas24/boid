package io.ogi.examples.encoder;

import io.ogi.examples.model.BoidPosition;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class BoidPositionTextEncoder  implements Encoder.Text<BoidPosition> {

    Jsonb jsonb = JsonbBuilder.create();

    @Override
    public String encode(BoidPosition boidPosition) throws EncodeException {
        return jsonb.toJson(boidPosition);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
