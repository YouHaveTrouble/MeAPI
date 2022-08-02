package endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.dv8tion.jda.api.OnlineStatus;
import org.json.JSONObject;
import steam.SteamOnlineStatus;

import java.io.IOException;
import java.io.OutputStream;

public class SteamHandler implements HttpHandler {

    public static String gameName;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        JSONObject object = new JSONObject();
        object.put("in_game", OnlineHandler.steamStatus == SteamOnlineStatus.IN_GAME);
        object.put("game", gameName == null ? JSONObject.NULL : gameName);
        String htmlResponse = object.toString();
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
