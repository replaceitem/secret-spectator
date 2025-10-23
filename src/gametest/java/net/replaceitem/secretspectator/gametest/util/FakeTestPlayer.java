package net.replaceitem.secretspectator.gametest.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.world.GameMode;
import net.replaceitem.secretspectator.gametest.mixin.ServerPlayerInteractionManagerAccessor;

public class FakeTestPlayer extends ServerPlayerEntity {
    private final VirtualPlayerList playerList = new VirtualPlayerList();
    private final GameMode targetGameMode;
    
    public FakeTestPlayer(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, GameMode targetGameMode) {
        super(server, world, profile, clientOptions);
        this.targetGameMode = targetGameMode;
    }
    
    public void onPlayerListPacket(PlayerListS2CPacket playerListS2CPacket) {
        playerList.onPacket(playerListS2CPacket);
    }

    public VirtualPlayerList getPlayerList() {
        return playerList;
    }
    
    public void loadInitialData() {
        this.changeGameMode(this.targetGameMode);
    }
}
