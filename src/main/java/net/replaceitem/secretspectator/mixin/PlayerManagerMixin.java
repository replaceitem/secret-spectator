package net.replaceitem.secretspectator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameType;
import net.replaceitem.secretspectator.SecretSpectator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    @WrapOperation(method = "placeNewPlayer", at = @At(value = "INVOKE", ordinal = 5, target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void fakeSelfConnectPacket(ServerGamePacketListenerImpl instance, Packet<?> packet, Operation<Void> original) {
        ServerPlayer player = instance.getPlayer();
        ClientboundPlayerInfoUpdatePacket playerListS2CPacket = ((ClientboundPlayerInfoUpdatePacket) packet);
        if(!SecretSpectator.canSeeOtherSpectators(player)) {
            playerListS2CPacket = SecretSpectator.copyPacketWithModifiedEntries(playerListS2CPacket, entry -> SecretSpectator.cloneEntryWithGamemode(entry, GameType.SURVIVAL));
        }
        original.call(instance, playerListS2CPacket);
    }

    @WrapOperation(method = "placeNewPlayer", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void fakeOtherConnectPacket(PlayerList instance, Packet<?> packet, Operation<Void> original) {
        ClientboundPlayerInfoUpdatePacket playerListS2CPacket = (ClientboundPlayerInfoUpdatePacket) packet;
        ClientboundPlayerInfoUpdatePacket.Entry entry = playerListS2CPacket.entries().getFirst();
        ServerPlayer player = instance.getPlayer(entry.profileId());
        if(player != null && player.isSpectator()) {
            // other player logged in as spectator
            ClientboundPlayerInfoUpdatePacket fakePacket = SecretSpectator.copyPacketWithModifiedEntries(playerListS2CPacket, entry1 -> SecretSpectator.cloneEntryWithGamemode(entry1, GameType.SURVIVAL));
            for (ServerPlayer serverPlayerEntity : instance.getPlayers()) {
                serverPlayerEntity.connection.send(SecretSpectator.canPlayerSeeThatOtherIsSpectator(serverPlayerEntity, player) ? packet : fakePacket);
            }
        } else {
            original.call(instance, packet);
        }
    }
}
