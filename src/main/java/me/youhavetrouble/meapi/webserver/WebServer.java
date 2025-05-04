package me.youhavetrouble.meapi.webserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.youhavetrouble.meapi.MeAPI;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class WebServer {

    private final HttpServer server;

    public WebServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        ThreadFactory threadFactory = task -> {
            Thread thread = Thread.ofVirtual().unstarted(task);
            thread.setName("WebServer-VT");
            return thread;
        };
        server.setExecutor(Executors.newThreadPerTaskExecutor(threadFactory));
    }

    public void registerEndpoint(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }

    public void start() {
        server.start();
        MeAPI.logger.info("Started web server on port {}", server.getAddress().getPort());
    }

    public void stop() {
        server.stop(0);
    }

}
