package org.darkstorm.minecraft.darkbot.world;

public class PlayerInfo {
    private final String playerUUID;
    private String playerName;

    public PlayerInfo(String playerUUID, String playerName)
    {
        this.playerUUID = playerUUID;
        this.setPlayerName(playerName);
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
