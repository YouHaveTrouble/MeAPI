package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.HttpHandler;

public interface Endpoint extends HttpHandler {

    public String getId();

    public void refreshData();

    public int refreshInterval();

}
