package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.youhavetrouble.meapi.MeAPI;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class RootEndpoint implements HttpHandler {

    private final String rootHtml = new Scanner(RootEndpoint.class.getResourceAsStream("/root.html"), StandardCharsets.UTF_8).useDelimiter("\\A").next();

    @Override
    public void handle(HttpExchange httpExchange) {
        try (HttpExchange exchange = httpExchange) {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, rootHtml.length());
            exchange.getResponseBody().write(rootHtml.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            MeAPI.logger.warning("Error handling root endpoint: " + e.getMessage());
        }
    }
}
