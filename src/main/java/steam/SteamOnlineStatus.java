package steam;

public enum SteamOnlineStatus {
    ONLINE,
    OFFLINE,
    IN_GAME;

    public static SteamOnlineStatus statusFromString(String string) {
        if (string.equalsIgnoreCase("Currently Online")) {
            return ONLINE;
        }
        if (string.equalsIgnoreCase("Currently In-Game")) {
            return IN_GAME;
        }
        return OFFLINE;
    }



}
