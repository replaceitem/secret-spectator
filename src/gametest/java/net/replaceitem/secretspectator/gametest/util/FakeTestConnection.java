package net.replaceitem.secretspectator.gametest.util;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.state.NetworkState;

public class FakeTestConnection extends ClientConnection {
    public FakeTestConnection(NetworkSide side) {
        super(side);
    }

    @Override
    public void tryDisableAutoRead() {
        
    }

    @Override
    public void handleDisconnection() {
        
    }

    @Override
    public void setInitialPacketListener(PacketListener packetListener) {
        
    }

    @Override
    public <T extends PacketListener> void transitionInbound(NetworkState<T> state, T packetListener) {
        
    }
}
