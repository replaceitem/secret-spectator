package net.replaceitem.secretspectator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import net.replaceitem.secretspectator.SecretSpectator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @WrapOperation(method = "changeGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    private void sendPackets(PlayerManager instance, Packet<?> packet, Operation<Void> original) {
        if(this.player.isSpectator()) {
            for (ServerPlayerEntity serverPlayerEntity : instance.getPlayerList()) {
                if(SecretSpectator.canPlayerSeeSpectatorOf(serverPlayerEntity, this.player)) {
                    // let other players know who should see us being in spectator
                    serverPlayerEntity.networkHandler.sendPacket(packet);
                }
                if(!serverPlayerEntity.equals(this.player) && serverPlayerEntity.isSpectator() && SecretSpectator.canPlayerSeeSpectatorOf(this.player, serverPlayerEntity)) {
                    // let us know which other spectators
                    this.player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));
                }
            }
        } else {
            // we let all know
            original.call(this.player.server.getPlayerManager(), packet);
            // other spectators tell us they're in survival
            if(!SecretSpectator.canSeeOtherSpectators(this.player)) {
                for (ServerPlayerEntity serverPlayerEntity : instance.getPlayerList()) {
                    if (this.player != serverPlayerEntity && serverPlayerEntity.isSpectator()) {
                        PlayerListS2CPacket backToSurvivalPacket = SecretSpectator.copyPacketWithModifiedEntries(
                                new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity),
                                entry -> SecretSpectator.cloneEntryWithGamemode(entry, GameMode.SURVIVAL)
                        );
                        this.player.networkHandler.sendPacket(backToSurvivalPacket);
                    }
                }
            }
        }
    }
}
