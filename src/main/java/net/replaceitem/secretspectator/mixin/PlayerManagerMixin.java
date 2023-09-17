package net.replaceitem.secretspectator.mixin;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.replaceitem.secretspectator.SecretSpectator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", ordinal = 8, target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void fakeSelfConnectPacket(ServerPlayNetworkHandler serverPlayNetworkHandler, Packet<?> packet) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
        PlayerListS2CPacket playerListS2CPacket = ((PlayerListS2CPacket) packet);
        if(!SecretSpectator.canSeeOtherSpectators(player)) {
            playerListS2CPacket = SecretSpectator.copyPacketWithModifiedEntries(playerListS2CPacket, entry -> SecretSpectator.cloneEntryWithGamemode(entry, GameMode.SURVIVAL));
        }
        serverPlayNetworkHandler.sendPacket(playerListS2CPacket);
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    private void fakeOtherConnectPacket(PlayerManager playerManager, Packet<?> packet) {
        PlayerListS2CPacket playerListS2CPacket = (PlayerListS2CPacket) packet;
        PlayerListS2CPacket.Entry entry = playerListS2CPacket.getEntries().get(0);
        ServerPlayerEntity player = playerManager.getPlayer(entry.profileId());
        if(player != null && player.isSpectator()) {
            // we log in as spectator
            PlayerListS2CPacket fakePacket = SecretSpectator.copyPacketWithModifiedEntries(playerListS2CPacket, entry1 -> SecretSpectator.cloneEntryWithGamemode(entry1, GameMode.SURVIVAL));
            for (ServerPlayerEntity serverPlayerEntity : playerManager.getPlayerList()) {
                serverPlayerEntity.networkHandler.sendPacket(SecretSpectator.canPlayerSeeSpectatorOf(serverPlayerEntity, player) ? packet : fakePacket);
            }
        } else {
            playerManager.sendToAll(packet);
        }
    }
}
