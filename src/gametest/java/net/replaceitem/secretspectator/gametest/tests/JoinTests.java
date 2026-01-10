package net.replaceitem.secretspectator.gametest.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.replaceitem.secretspectator.gametest.util.FakeTestPlayer;
import net.replaceitem.secretspectator.gametest.util.TestContextExtension;

@SuppressWarnings("unused")
public class JoinTests {
    public static class Survival {
        private ScenarioBuilder build() {
            return new ScenarioBuilder().inGameMode(GameType.SURVIVAL);
        }

        @GameTest
        public void testNonOpObserving(GameTestHelper context) {
            build().observingAs(GameType.SURVIVAL, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameType.SURVIVAL).expectedObserverGamemodeForSpectator(GameType.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testNonOpSpectatorObserving(GameTestHelper context) {
            build().observingAs(GameType.SPECTATOR, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameType.SURVIVAL).expectedObserverGamemodeForSpectator(GameType.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testOpSurvivalObserving(GameTestHelper context) {
            build().observingAs(GameType.SURVIVAL, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameType.SURVIVAL).expectedObserverGamemodeForSpectator(GameType.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testOpSpectatorObserving(GameTestHelper context) {
            build().observingAs(GameType.SPECTATOR, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameType.SURVIVAL).expectedObserverGamemodeForSpectator(GameType.SURVIVAL).runTest(context);
        }
    }
    
    public static class Spectator {
        private ScenarioBuilder build() {
            return new ScenarioBuilder().inGameMode(GameType.SPECTATOR);
        }

        @GameTest
        public void testNonOpObserving(GameTestHelper context) {
            build().observingAs(GameType.SURVIVAL, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameType.SURVIVAL).expectedObserverGamemodeForSpectator(GameType.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testNonOpSpectatorObserving(GameTestHelper context) {
            build().observingAs(GameType.SPECTATOR, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameType.SPECTATOR).expectedObserverGamemodeForSpectator(GameType.SPECTATOR).runTest(context);
        }
        @GameTest
        public void testOpSurvivalObserving(GameTestHelper context) {
            build().observingAs(GameType.SURVIVAL, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameType.SPECTATOR).expectedObserverGamemodeForSpectator(GameType.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testOpSpectatorObserving(GameTestHelper context) {
            build().observingAs(GameType.SPECTATOR, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameType.SPECTATOR).expectedObserverGamemodeForSpectator(GameType.SPECTATOR).runTest(context);
        }
    }



    public static class ScenarioBuilder {
        private GameType gameMode;
        private GameType observerGameMode;
        private boolean isObserverOp;
        private GameType expectedSpectatorGamemodeForObserver;
        private GameType expectedObserverGamemodeForSpectator;

        public ScenarioBuilder inGameMode(GameType from) {
            this.gameMode = from;
            return this;
        }

        public ScenarioBuilder observingAs(GameType gameMode, ScenarioBuilder.OpStatus opStatus) {
            this.observerGameMode = gameMode;
            this.isObserverOp = opStatus.isOp;
            return this;
        }

        public ScenarioBuilder expectedSpectatorGamemodeForObserver(GameType expected) {
            this.expectedSpectatorGamemodeForObserver = expected;
            return this;
        }
        public ScenarioBuilder expectedObserverGamemodeForSpectator(GameType expected) {
            this.expectedObserverGamemodeForSpectator = expected;
            return this;
        }

        public void runTest(GameTestHelper context) {
            TestContextExtension testContextExtension = (TestContextExtension) context;
            FakeTestPlayer observer = testContextExtension.createFakeTestPlayer(new TestContextExtension.FakePlayerOptions().gameMode(observerGameMode).opLevel(isObserverOp ? 4 : 0));
            FakeTestPlayer spectator = testContextExtension.createFakeTestPlayer(new TestContextExtension.FakePlayerOptions().gameMode(gameMode));

            if(spectator.getPlayerList().getGameMode(spectator).orElseThrow() != gameMode) {
                context.fail(Component.nullToEmpty(String.format("Expected spectator to have its real gamemode %s on its player list, but got %s", gameMode, spectator.getPlayerList().getGameMode(spectator).orElseThrow())));
            }

            if(observer.getPlayerList().getGameMode(spectator).orElseThrow() != expectedSpectatorGamemodeForObserver) {
                context.fail(Component.nullToEmpty(String.format(
                        "A player logged in as %s. The %s observer was in %s. Expected the observer to see the player in %s, but was %s",
                        gameMode,
                        isObserverOp ? "op" : "non-op",
                        observerGameMode,
                        expectedSpectatorGamemodeForObserver,
                        observer.getPlayerList().getGameMode(spectator).orElseThrow()
                )));
            }
            if(spectator.getPlayerList().getGameMode(observer).orElseThrow() != expectedObserverGamemodeForSpectator) {
                context.fail(Component.nullToEmpty(String.format(
                        "A player logged in as %s. The %s observer was in %s. Expected the spectator to see the observer in %s, but was %s",
                        gameMode,
                        isObserverOp ? "op" : "non-op",
                        observerGameMode,
                        expectedObserverGamemodeForSpectator,
                        spectator.getPlayerList().getGameMode(observer).orElseThrow()
                )));
            }

            context.succeed();
        }

        public enum OpStatus {
            NON_OP(false), OP(true);

            private final boolean isOp;

            OpStatus(boolean isOp) {
                this.isOp = isOp;
            }
        }
    }
}
