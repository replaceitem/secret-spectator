package net.replaceitem.secretspectator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.replaceitem.secretspectator.mixin.PlayerListS2CPacketAccessor;

import java.util.List;
import java.util.function.Function;

public class SecretSpectator implements ModInitializer {
    @Override
    public void onInitialize() {
        
    }
    
    public static PlayerListS2CPacket copyPacketWithModifiedEntries(PlayerListS2CPacket packet, Function<PlayerListS2CPacket.Entry, PlayerListS2CPacket.Entry> mapper) {
        PlayerListS2CPacket newPacket = new PlayerListS2CPacket(packet.getActions(), List.of());
        ((PlayerListS2CPacketAccessor) newPacket).setEntries(packet.getEntries().stream().map(mapper).toList());
        return newPacket;
    }
    
    public static PlayerListS2CPacket.Entry cloneEntryWithGamemode(PlayerListS2CPacket.Entry entry, GameMode newGameMode) {
        return new PlayerListS2CPacket.Entry(entry.profileId(), entry.profile(), entry.listed(), entry.latency(), newGameMode, entry.displayName(), entry.listOrder(), entry.chatSession());
    }
    
    
    public static boolean canSeeOtherSpectators(ServerPlayerEntity player) {
        return player.isSpectator() || player.hasPermissionLevel(2);
    }
    
    public static boolean canPlayerSeeSpectatorOf(ServerPlayerEntity player, ServerPlayerEntity other) {
        return player.equals(other) || canSeeOtherSpectators(player);
    }
}
