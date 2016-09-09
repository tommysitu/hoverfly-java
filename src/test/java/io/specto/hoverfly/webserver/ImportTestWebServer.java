package io.specto.hoverfly.webserver;


import com.google.common.io.Resources;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

public class ImportTestWebServer extends AbstractHandler {

    private static Server server;

    public static URI run() throws Exception {
        final int port = findUnusedPort();
        server = new Server(port);
        server.setHandler(new ImportTestWebServer());
        server.start();
        return UriComponentsBuilder.fromUriString(String.format("http://localhost:%s", port)).build().toUri();
    }

    private static int findUnusedPort() {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Cannot find available port", e);
        }
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

        // Declare response encoding and types
        response.setContentType("application/json; charset=utf-8");

        final URL resourceUrl = Resources.getResource("test-service.json");
        final String json = Resources.toString(resourceUrl, Charset.defaultCharset());

        response.getWriter().write(json);

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }
}
