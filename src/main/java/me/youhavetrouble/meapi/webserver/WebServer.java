package me.youhavetrouble.meapi.webserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer {

    private final HttpServer server;

    public WebServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    public void registerEndpoint(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

}
