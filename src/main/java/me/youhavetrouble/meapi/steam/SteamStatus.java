package me.youhavetrouble.meapi.steam;

public class SteamStatus {

    private final SteamOnlineStatus status;
    private final String game;

    protected SteamStatus(SteamOnlineStatus status, String gameName) {
        this.status = status;
        this.game = gameName;
    }

    public SteamOnlineStatus getStatus() {
        return status;
    }

    public String getGame() {
        return game;
    }
}
