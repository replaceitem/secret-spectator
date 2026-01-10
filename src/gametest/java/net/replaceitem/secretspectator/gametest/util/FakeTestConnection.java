package net.replaceitem.secretspectator.gametest.util;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.PacketFlow;

public class FakeTestConnection extends Connection {
    public FakeTestConnection(PacketFlow side) {
        super(side);
    }

    @Override
    public void setReadOnly() {
        
    }

    @Override
    public void handleDisconnection() {
        
    }

    @Override
    public void setListenerForServerboundHandshake(PacketListener packetListener) {
        
    }

    @Override
    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> state, T packetListener) {
        
    }
}
