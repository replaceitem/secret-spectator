package net.replaceitem.secretspectator.gametest.util;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FakeTestServerPlayNetworkHandler extends ServerPlayNetworkHandler {
    public FakeTestServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
        super(server, connection, player, clientData);
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        if(packet instanceof PlayerListS2CPacket playerListS2CPacket) {
            ((FakeTestPlayer) this.player).onPlayerListPacket(playerListS2CPacket);
        }
    }
}
