package net.replaceitem.secretspectator.gametest.util;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.level.GameType;

public interface TestContextExtension {
    FakeTestPlayer createFakeTestPlayer(FakePlayerOptions gameMode);
    
    class FakePlayerOptions {
        String name = "TestPlayer" + Long.toHexString(ThreadLocalRandom.current().nextLong(0xFFFFFFFFL));
        GameType gameMode = GameType.DEFAULT_MODE;
        int opLevel = 0;
        
        public FakePlayerOptions name(String value) {
            this.name = value;
            return this;
        }
        
        public FakePlayerOptions gameMode(GameType value) {
            this.gameMode = value;
            return this;
        }
        
        public FakePlayerOptions opLevel(int value) {
            this.opLevel = value;
            return this;
        }

        public String getName() {
            return name;
        }

        public GameType getGameMode() {
            return gameMode;
        }

        public int getOpLevel() {
            return opLevel;
        }
    }
}
