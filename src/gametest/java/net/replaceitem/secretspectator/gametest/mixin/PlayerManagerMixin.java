package net.replaceitem.secretspectator.gametest.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.replaceitem.secretspectator.gametest.util.FakeTestPlayer;
import net.replaceitem.secretspectator.gametest.util.FakeTestServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    @WrapOperation(method="placeNewPlayer", at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)Lnet/minecraft/server/network/ServerGamePacketListenerImpl;"))
    private ServerGamePacketListenerImpl replaceNetworkHandler(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie clientData, Operation<ServerGamePacketListenerImpl> original) {
        if(player instanceof FakeTestPlayer) {
            return new FakeTestServerPlayNetworkHandler(server, connection, player, clientData);
        }
        return original.call(server, connection, player, clientData);
    }
}
