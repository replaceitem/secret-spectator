package net.replaceitem.secretspectator.gametest.util;

import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public interface TestContextExtension {
    FakeTestPlayer createFakeTestPlayer(FakePlayerOptions gameMode);
    
    class FakePlayerOptions {
        String name = "TestPlayer" + Long.toHexString(ThreadLocalRandom.current().nextLong(0xFFFFFFFFL));
        GameType gameMode = GameType.DEFAULT_MODE;
        @Nullable LevelBasedPermissionSet opLevel;
        
        public FakePlayerOptions name(String value) {
            this.name = value;
            return this;
        }
        
        public FakePlayerOptions gameMode(GameType value) {
            this.gameMode = value;
            return this;
        }
        
        public FakePlayerOptions opLevel(@Nullable LevelBasedPermissionSet value) {
            this.opLevel = value;
            return this;
        }

        public String getName() {
            return name;
        }

        public GameType getGameMode() {
            return gameMode;
        }

        @Nullable
        public LevelBasedPermissionSet getOpLevel() {
            return opLevel;
        }
    }
}
