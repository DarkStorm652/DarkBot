package org.darkstorm.minecraft.darkbot.world;

public class PlayerInfo {
    private final String playerUUID;
    private final String playerName;

    public PlayerInfo(String playerUUID, String playerName)
    {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }
}
