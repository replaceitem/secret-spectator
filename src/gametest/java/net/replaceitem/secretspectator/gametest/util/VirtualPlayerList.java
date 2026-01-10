package net.replaceitem.secretspectator.gametest.util;

import java.util.*;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class VirtualPlayerList {
    private final Map<UUID, VirtualPlayerListEntry> playerListEntries = new HashMap<>();
    
    public Optional<GameType> getGameMode(ServerPlayer player) {
        return Optional.ofNullable(playerListEntries.get(player.getUUID())).map(virtualPlayerListEntry -> virtualPlayerListEntry.gameMode);
    }
    
    public void onPacket(ClientboundPlayerInfoUpdatePacket packet) {
        for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.newEntries()) {
            VirtualPlayerListEntry playerListEntry = new VirtualPlayerListEntry();
            this.playerListEntries.putIfAbsent(entry.profileId(), playerListEntry);
        }

        for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
            VirtualPlayerListEntry playerListEntry = this.playerListEntries.get(entry.profileId());
            if (playerListEntry != null) {
                for (ClientboundPlayerInfoUpdatePacket.Action action : packet.actions()) {
                    this.handlePlayerListAction(action, entry, playerListEntry);
                }
            }
        }
    }

    private void handlePlayerListAction(ClientboundPlayerInfoUpdatePacket.Action action, ClientboundPlayerInfoUpdatePacket.Entry receivedEntry, VirtualPlayerListEntry currentEntry) {
        if (Objects.requireNonNull(action) == ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE) {
            currentEntry.gameMode = receivedEntry.gameMode();
        }
    }
    
    private static class VirtualPlayerListEntry {
        private GameType gameMode = GameType.DEFAULT_MODE;
    }
}
