package net.replaceitem.secretspectator.gametest.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.*;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestContext;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.replaceitem.secretspectator.gametest.util.FakeTestConnection;
import net.replaceitem.secretspectator.gametest.util.FakeTestPlayer;
import net.replaceitem.secretspectator.gametest.util.TestContextExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(TestContext.class)
public abstract class TestContextMixin implements TestContextExtension {

    @Shadow public abstract ServerWorld getWorld();

    @Shadow public abstract Box getTestBox();

    @Override
    public FakeTestPlayer createFakeTestPlayer(FakePlayerOptions options) {
        ServerWorld world = this.getWorld();
        MinecraftServer server = world.getServer();
        GameProfile profile = Uuids.getOfflinePlayerProfile(options.getName());
        PlayerManager playerManager = server.getPlayerManager();
        FakeTestPlayer fakeTestPlayer = new FakeTestPlayer(server, world, profile, SyncedClientOptions.createDefault(), options.getGameMode());
        setOpLevel(playerManager.getOpList(), fakeTestPlayer.getPlayerConfigEntry(), options.getOpLevel());
        playerManager.onPlayerConnect(new FakeTestConnection(NetworkSide.SERVERBOUND), fakeTestPlayer, new ConnectedClientData(profile, 0, fakeTestPlayer.getClientOptions(), false));
        fakeTestPlayer.loadInitialData();
        fakeTestPlayer.refreshPositionAndAngles(this.getTestBox().getCenter(), 0, 0);
        return fakeTestPlayer;
    }

    @Unique
    private static void setOpLevel(OperatorList opList, PlayerConfigEntry profile, int level) {
        if(level == 0) {
            opList.remove(profile);
        } else {
            opList.add(new OperatorEntry(profile, level, false));
        }
    }
}
