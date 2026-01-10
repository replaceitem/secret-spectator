package net.replaceitem.secretspectator.gametest.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.replaceitem.secretspectator.gametest.mixin.ServerPlayerInteractionManagerAccessor;

public class FakeTestPlayer extends ServerPlayer {
    private final VirtualPlayerList playerList = new VirtualPlayerList();
    private final GameType targetGameMode;
    
    public FakeTestPlayer(MinecraftServer server, ServerLevel world, GameProfile profile, ClientInformation clientOptions, GameType targetGameMode) {
        super(server, world, profile, clientOptions);
        this.targetGameMode = targetGameMode;
    }
    
    public void onPlayerListPacket(ClientboundPlayerInfoUpdatePacket playerListS2CPacket) {
        playerList.onPacket(playerListS2CPacket);
    }

    public VirtualPlayerList getPlayerList() {
        return playerList;
    }
    
    public void loadInitialData() {
        this.setGameMode(this.targetGameMode);
    }
}
