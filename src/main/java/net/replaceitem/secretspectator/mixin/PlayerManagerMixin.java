package net.replaceitem.secretspectator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.replaceitem.secretspectator.SecretSpectator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @WrapOperation(method = "onPlayerConnect", at = @At(value = "INVOKE", ordinal = 5, target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void fakeSelfConnectPacket(ServerPlayNetworkHandler instance, Packet<?> packet, Operation<Void> original) {
        ServerPlayerEntity player = instance.getPlayer();
        PlayerListS2CPacket playerListS2CPacket = ((PlayerListS2CPacket) packet);
        if(!SecretSpectator.canSeeOtherSpectators(player)) {
            playerListS2CPacket = SecretSpectator.copyPacketWithModifiedEntries(playerListS2CPacket, entry -> SecretSpectator.cloneEntryWithGamemode(entry, GameMode.SURVIVAL));
        }
        original.call(instance, playerListS2CPacket);
    }

    @WrapOperation(method = "onPlayerConnect", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    private void fakeOtherConnectPacket(PlayerManager instance, Packet<?> packet, Operation<Void> original) {
        PlayerListS2CPacket playerListS2CPacket = (PlayerListS2CPacket) packet;
        PlayerListS2CPacket.Entry entry = playerListS2CPacket.getEntries().getFirst();
        ServerPlayerEntity player = instance.getPlayer(entry.profileId());
        if(player != null && player.isSpectator()) {
            // other player logged in as spectator
            PlayerListS2CPacket fakePacket = SecretSpectator.copyPacketWithModifiedEntries(playerListS2CPacket, entry1 -> SecretSpectator.cloneEntryWithGamemode(entry1, GameMode.SURVIVAL));
            for (ServerPlayerEntity serverPlayerEntity : instance.getPlayerList()) {
                serverPlayerEntity.networkHandler.sendPacket(SecretSpectator.canPlayerSeeThatOtherIsSpectator(serverPlayerEntity, player) ? packet : fakePacket);
            }
        } else {
            original.call(instance, packet);
        }
    }
}
