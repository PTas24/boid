
package io.ogi.boid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;

import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonb.JsonbSupport;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.StaticContentSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.tyrus.TyrusSupport;
import io.ogi.boid.boidconfig.BoidSimulationConfig;
import io.ogi.boid.encoder.BoidPositionTextEncoder;

import javax.websocket.Encoder;
import javax.websocket.server.ServerEndpointConfig;

/**
 * The application main class.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * @param args command line arguments.
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     * @throws IOException if there are problems reading logging properties
     */
    static WebServer startServer() throws IOException {
        setupLogging();

        Config config = Config.create();

        WebServer server = WebServer.builder(createRouting(config))
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
                .addMediaSupport(JsonbSupport.create())
                .build();

        server.start()
                .thenAccept(ws -> {
                    System.out.println(
                            "WEB server is up! http://localhost:" + ws.port() + "/boid");
                    System.out.println(
                            "http://localhost:" + ws.port() + "/index.html");
                    ws.whenShutdown().thenRun(()
                            -> System.out.println("WEB server is DOWN. Good bye!"));
                })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });

        return server;
    }

    private static Routing createRouting(Config config) {
        List<Class<? extends Encoder>> encoders =
                Collections.singletonList(BoidPositionTextEncoder.class);

        MetricsSupport metrics = MetricsSupport.create();
        BoidSimulationConfig boidSimulationConfig = new BoidSimulationConfig(config);
        BoidService boidService = new BoidService(boidSimulationConfig);

        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();

        return Routing.builder()
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/boid", boidService)
                .register("/websocket", TyrusSupport.builder().register(
                        ServerEndpointConfig.Builder.create(BoidWebSocketEndpoint.class, "/boid-positions")
                                .encoders(encoders)
                                .configurator(new ServerEndpointConfig.Configurator() {
                                    @Override
                                    @SuppressWarnings("unchecked")
                                    public <T> T getEndpointInstance(Class<T> endpointClass) {
                                        return (T) new BoidWebSocketEndpoint(boidSimulationConfig);
                                    }
                                })
                                .build()).build())
                .register("/", StaticContentSupport.builder("/WEB").welcomeFileName("index.html"))
                .build();
    }


    private static void setupLogging() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }
}
