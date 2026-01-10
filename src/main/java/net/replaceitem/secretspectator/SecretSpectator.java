package net.replaceitem.secretspectator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.GameType;
import net.replaceitem.secretspectator.mixin.PlayerListS2CPacketAccessor;

import java.util.List;
import java.util.function.Function;

public class SecretSpectator implements ModInitializer {
    @Override
    public void onInitialize() {
        
    }
    
    public static ClientboundPlayerInfoUpdatePacket copyPacketWithModifiedEntries(ClientboundPlayerInfoUpdatePacket packet, Function<ClientboundPlayerInfoUpdatePacket.Entry, ClientboundPlayerInfoUpdatePacket.Entry> mapper) {
        ClientboundPlayerInfoUpdatePacket newPacket = new ClientboundPlayerInfoUpdatePacket(packet.actions(), List.of());
        ((PlayerListS2CPacketAccessor) newPacket).setEntries(packet.entries().stream().map(mapper).toList());
        return newPacket;
    }
    
    public static ClientboundPlayerInfoUpdatePacket.Entry cloneEntryWithGamemode(ClientboundPlayerInfoUpdatePacket.Entry entry, GameType newGameMode) {
        return new ClientboundPlayerInfoUpdatePacket.Entry(entry.profileId(), entry.profile(), entry.listed(), entry.latency(), newGameMode, entry.displayName(), entry.showHat(), entry.listOrder(), entry.chatSession());
    }
    
    
    public static boolean canSeeOtherSpectators(ServerPlayer player) {
        return player.isSpectator() || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }
    
    public static boolean canPlayerSeeThatOtherIsSpectator(ServerPlayer player, ServerPlayer other) {
        return player.equals(other) || canSeeOtherSpectators(player);
    }
}
