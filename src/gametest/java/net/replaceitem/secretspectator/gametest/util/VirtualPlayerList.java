package net.replaceitem.secretspectator.gametest.util;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.*;

public class VirtualPlayerList {
    private final Map<UUID, VirtualPlayerListEntry> playerListEntries = new HashMap<>();
    
    public Optional<GameMode> getGameMode(ServerPlayerEntity player) {
        return Optional.ofNullable(playerListEntries.get(player.getUuid())).map(virtualPlayerListEntry -> virtualPlayerListEntry.gameMode);
    }
    
    public void onPacket(PlayerListS2CPacket packet) {
        for (PlayerListS2CPacket.Entry entry : packet.getPlayerAdditionEntries()) {
            VirtualPlayerListEntry playerListEntry = new VirtualPlayerListEntry();
            this.playerListEntries.putIfAbsent(entry.profileId(), playerListEntry);
        }

        for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
            VirtualPlayerListEntry playerListEntry = this.playerListEntries.get(entry.profileId());
            if (playerListEntry != null) {
                for (PlayerListS2CPacket.Action action : packet.getActions()) {
                    this.handlePlayerListAction(action, entry, playerListEntry);
                }
            }
        }
    }

    private void handlePlayerListAction(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, VirtualPlayerListEntry currentEntry) {
        if (Objects.requireNonNull(action) == PlayerListS2CPacket.Action.UPDATE_GAME_MODE) {
            currentEntry.gameMode = receivedEntry.gameMode();
        }
    }
    
    private static class VirtualPlayerListEntry {
        private GameMode gameMode = GameMode.DEFAULT;
    }
}
