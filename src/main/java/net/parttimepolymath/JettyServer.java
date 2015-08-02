package net.parttimepolymath;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import net.parttimepolymath.api.StatusService;
import net.parttimepolymath.api.TransferService;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * this is the wrapper on the Jetty server. This is broken out of the main app so that we can use the
 * app to run a server and as a test harness.
 * 
 * @author robert
 */
public final class JettyServer {
    /**
     * the port we will run on
     */
    private final int port;

    /**
     * default constructor.
     */
    public JettyServer() {
        port = 8080;
    }

    /**
     * construct to use a specified port.
     * 
     * @param port the port to specify, assumed but not required to be a useful number.
     */
    public JettyServer(final int port) {
        this.port = port;
    }

    /**
     * start an embedded Jetty. This could use considerable more work - it would be very nice to ensure that we cleanly shut down.
     * 
     * @throws Exception if starting up or stopping the server fails.
     */
    public void start() throws Exception {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
        ResourceConfig config = new ResourceConfig(StatusService.class, TransferService.class).register(JacksonFeature.class);
        Server jettyServer = JettyHttpContainerFactory.createServer(baseUri, config);
        try {
            jettyServer.join();
            jettyServer.start();
        } finally {
            jettyServer.destroy();
        }
    }
}
