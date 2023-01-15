package me.youhavetrouble.meapi.endpoints;

import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.RequestMethod;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.response.HtmlResponse;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;


public class RootEndpoint implements Endpoint {

    String rootHtml = new Scanner(RootEndpoint.class.getResourceAsStream("/root.html"), StandardCharsets.UTF_8).useDelimiter("\\A").next();

    @Override
    public String path() {
        return "";
    }

    @Override
    public HttpResponse handle(@NotNull RequestMethod requestMethod, @NotNull URI uri, @NotNull Headers headers, @NotNull Map<String, String> map, @Nullable String s) {
        return HtmlResponse.create(rootHtml);
    }
}
