package net.replaceitem.secretspectator.gametest.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.replaceitem.secretspectator.gametest.mixin.ServerPlayerInteractionManagerAccessor;
import org.jetbrains.annotations.Nullable;

public class FakeTestPlayer extends ServerPlayerEntity {
    private final VirtualPlayerList playerList = new VirtualPlayerList();
    private final GameMode gameMode;
    
    public FakeTestPlayer(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, GameMode gameMode) {
        super(server, world, profile, clientOptions);
        this.gameMode = gameMode;
    }
    
    public void onPlayerListPacket(PlayerListS2CPacket playerListS2CPacket) {
        playerList.onPacket(playerListS2CPacket);
    }

    public VirtualPlayerList getPlayerList() {
        return playerList;
    }

    @Override
    public void readGameModeNbt(@Nullable NbtCompound nbt) {
        ((ServerPlayerInteractionManagerAccessor) this.interactionManager).callSetGameMode(gameMode, null);
    }
}
