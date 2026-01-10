package net.replaceitem.secretspectator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameType;
import net.replaceitem.secretspectator.SecretSpectator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayer player;

    @Shadow protected ServerLevel level;

    @WrapOperation(method = "changeGameModeForPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void sendPackets(PlayerList playerManager, Packet<?> packet, Operation<Void> original) {
        if(this.player.isSpectator()) {
            var modifiedPacket = SecretSpectator.copyPacketWithModifiedEntries(
                    ((ClientboundPlayerInfoUpdatePacket) packet),
                    entry -> SecretSpectator.cloneEntryWithGamemode(entry, GameType.SURVIVAL)
            );
            for (ServerPlayer other : playerManager.getPlayers()) {
                if(SecretSpectator.canPlayerSeeThatOtherIsSpectator(other, this.player)) {
                    // let other players know who should see us being in spectator
                    other.connection.send(packet);
                } else {
                    // tell the rest we are in survival
                    other.connection.send(modifiedPacket);
                }
            }
            // send all current spectators to us
            List<ServerPlayer> visibleOtherSpectators = playerManager.getPlayers().stream()
                    .filter(other -> !other.equals(this.player) && other.isSpectator() && SecretSpectator.canPlayerSeeThatOtherIsSpectator(this.player, other))
                    .toList();
            if(!visibleOtherSpectators.isEmpty()) {
                this.player.connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), visibleOtherSpectators));
            }
        } else {
            // we let all know we went in survival
            original.call(Objects.requireNonNull(this.level.getServer()).getPlayerList(), packet);
            // other spectators tell us they're in survival
            if(!SecretSpectator.canSeeOtherSpectators(this.player)) {
                List<ServerPlayer> pretendSurvivalPlayers = playerManager.getPlayers().stream().filter(other -> !other.equals(this.player) && other.isSpectator()).toList();
                if(!pretendSurvivalPlayers.isEmpty()) {
                    ClientboundPlayerInfoUpdatePacket backToSurvivalPacket = SecretSpectator.copyPacketWithModifiedEntries(
                            new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), pretendSurvivalPlayers),
                            entry -> SecretSpectator.cloneEntryWithGamemode(entry, GameType.SURVIVAL)
                    );
                    this.player.connection.send(backToSurvivalPacket);
                }
            }
        }
    }
}
