package endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.dv8tion.jda.api.OnlineStatus;
import org.json.JSONObject;
import steam.SteamOnlineStatus;

import java.io.IOException;
import java.io.OutputStream;

public class OnlineHandler implements HttpHandler {

    public static OnlineStatus discordStatus;
    public static SteamOnlineStatus steamStatus;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        JSONObject object = new JSONObject();
        object.put("discord", discordStatus == null ? JSONObject.NULL : discordStatus);
        object.put("steam", steamStatus == null ? JSONObject.NULL : steamStatus);
        String htmlResponse = object.toString();
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
