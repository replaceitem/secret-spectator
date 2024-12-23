package net.replaceitem.secretspectator.gametest.util;

import net.minecraft.world.GameMode;

import java.util.concurrent.ThreadLocalRandom;

public interface TestContextExtension {
    FakeTestPlayer createFakeTestPlayer(FakePlayerOptions gameMode);
    
    class FakePlayerOptions {
        String name = "TestPlayer" + Long.toHexString(ThreadLocalRandom.current().nextLong(0xFFFFFFFFL));
        GameMode gameMode = GameMode.DEFAULT;
        int opLevel = 0;
        
        public FakePlayerOptions name(String value) {
            this.name = value;
            return this;
        }
        
        public FakePlayerOptions gameMode(GameMode value) {
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

        public GameMode getGameMode() {
            return gameMode;
        }

        public int getOpLevel() {
            return opLevel;
        }
    }
}
