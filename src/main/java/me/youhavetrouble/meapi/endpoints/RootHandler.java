package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class RootHandler implements HttpHandler {

    String rootHtml = new Scanner(RootHandler.class.getResourceAsStream("/root.html"), StandardCharsets.UTF_8).useDelimiter("\\A").next();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String htmlResponse = rootHtml;
        httpExchange.getResponseHeaders().set("Content-Type", "text/html");
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }

}
