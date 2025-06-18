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

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @WrapOperation(method = "changeGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    private void sendPackets(PlayerManager playerManager, Packet<?> packet, Operation<Void> original) {
        if(this.player.isSpectator()) {
            for (ServerPlayerEntity other : playerManager.getPlayerList()) {
                if(SecretSpectator.canPlayerSeeThatOtherIsSpectator(other, this.player)) {
                    // let other players know who should see us being in spectator
                    other.networkHandler.sendPacket(packet);
                }
            }
            // send all current spectators to us
            List<ServerPlayerEntity> visibleOtherSpectators = playerManager.getPlayerList().stream()
                    .filter(other -> !other.equals(this.player) && other.isSpectator() && SecretSpectator.canPlayerSeeThatOtherIsSpectator(this.player, other))
                    .toList();
            if(!visibleOtherSpectators.isEmpty()) {
                this.player.networkHandler.sendPacket(new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_GAME_MODE), visibleOtherSpectators));
            }
        } else {
            // we let all know we went in survival
            original.call(Objects.requireNonNull(this.player.getServer()).getPlayerManager(), packet);
            // other spectators tell us they're in survival
            if(!SecretSpectator.canSeeOtherSpectators(this.player)) {
                List<ServerPlayerEntity> pretendSurvivalPlayers = playerManager.getPlayerList().stream().filter(other -> !other.equals(this.player) && other.isSpectator()).toList();
                if(!pretendSurvivalPlayers.isEmpty()) {
                    PlayerListS2CPacket backToSurvivalPacket = SecretSpectator.copyPacketWithModifiedEntries(
                            new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_GAME_MODE), pretendSurvivalPlayers),
                            entry -> SecretSpectator.cloneEntryWithGamemode(entry, GameMode.SURVIVAL)
                    );
                    this.player.networkHandler.sendPacket(backToSurvivalPacket);
                }
            }
        }
    }
}
