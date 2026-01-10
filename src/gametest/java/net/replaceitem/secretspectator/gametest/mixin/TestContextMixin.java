package net.replaceitem.secretspectator.gametest.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.UUIDUtil;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.*;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.phys.AABB;
import net.replaceitem.secretspectator.gametest.util.FakeTestConnection;
import net.replaceitem.secretspectator.gametest.util.FakeTestPlayer;
import net.replaceitem.secretspectator.gametest.util.TestContextExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(GameTestHelper.class)
public abstract class TestContextMixin implements TestContextExtension {

    @Shadow public abstract ServerLevel getLevel();

    @Shadow public abstract AABB getBounds();

    @Override
    public FakeTestPlayer createFakeTestPlayer(FakePlayerOptions options) {
        ServerLevel world = this.getLevel();
        MinecraftServer server = world.getServer();
        GameProfile profile = UUIDUtil.createOfflineProfile(options.getName());
        PlayerList playerManager = server.getPlayerList();
        FakeTestPlayer fakeTestPlayer = new FakeTestPlayer(server, world, profile, ClientInformation.createDefault(), options.getGameMode());
        setOpLevel(playerManager.getOps(), fakeTestPlayer.nameAndId(), options.getOpLevel());
        playerManager.placeNewPlayer(new FakeTestConnection(PacketFlow.SERVERBOUND), fakeTestPlayer, new CommonListenerCookie(profile, 0, fakeTestPlayer.clientInformation(), false));
        fakeTestPlayer.loadInitialData();
        fakeTestPlayer.snapTo(this.getBounds().getCenter(), 0, 0);
        return fakeTestPlayer;
    }

    @Unique
    private static void setOpLevel(ServerOpList opList, NameAndId profile, int level) {
        if(level == 0) {
            opList.remove(profile);
        } else {
            opList.add(new ServerOpListEntry(profile, level, false));
        }
    }
}
