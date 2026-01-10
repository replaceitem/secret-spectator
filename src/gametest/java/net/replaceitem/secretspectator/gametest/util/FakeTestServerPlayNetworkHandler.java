package net.replaceitem.secretspectator.gametest.util;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class FakeTestServerPlayNetworkHandler extends ServerGamePacketListenerImpl {
    public FakeTestServerPlayNetworkHandler(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie clientData) {
        super(server, connection, player, clientData);
    }

    @Override
    public void send(Packet<?> packet) {
        if(packet instanceof ClientboundPlayerInfoUpdatePacket playerListS2CPacket) {
            ((FakeTestPlayer) this.player).onPlayerListPacket(playerListS2CPacket);
        }
    }
}
