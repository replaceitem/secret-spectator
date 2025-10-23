package net.replaceitem.secretspectator.gametest.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.replaceitem.secretspectator.gametest.util.FakeTestPlayer;
import net.replaceitem.secretspectator.gametest.util.TestContextExtension;

@SuppressWarnings("unused")
public class SwitchGameModeTests {
    public static class SurvivalToSpectator {
        private ScenarioBuilder build() {
            return new ScenarioBuilder().switchingGamemodeFromTo(GameMode.SURVIVAL, GameMode.SPECTATOR);
        }

        @GameTest
        public void testNonOpObserving(TestContext context) {
            try {
                build().observingAs(GameMode.SURVIVAL, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameMode.SURVIVAL).expectedObserverGamemodeForSpectator(GameMode.SURVIVAL).runTest(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @GameTest
        public void testNonOpSpectatorObserving(TestContext context) {
            build().observingAs(GameMode.SPECTATOR, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameMode.SPECTATOR).expectedObserverGamemodeForSpectator(GameMode.SPECTATOR).runTest(context);
        }
        @GameTest
        public void testOpSurvivalObserving(TestContext context) {
            build().observingAs(GameMode.SURVIVAL, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameMode.SPECTATOR).expectedObserverGamemodeForSpectator(GameMode.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testOpSpectatorObserving(TestContext context) {
            build().observingAs(GameMode.SPECTATOR, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameMode.SPECTATOR).expectedObserverGamemodeForSpectator(GameMode.SPECTATOR).runTest(context);
        }
    }
    
    public static class SpectatorToSurvival {
        private ScenarioBuilder build() {
            return new ScenarioBuilder().switchingGamemodeFromTo(GameMode.SPECTATOR, GameMode.SURVIVAL);
        }

        @GameTest
        public void testNonOpSurvivalObserving(TestContext context) {
            build().observingAs(GameMode.SURVIVAL, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameMode.SURVIVAL).expectedObserverGamemodeForSpectator(GameMode.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testNonOpSpectatorObserving(TestContext context) {
            build().observingAs(GameMode.SPECTATOR, ScenarioBuilder.OpStatus.NON_OP).expectedSpectatorGamemodeForObserver(GameMode.SURVIVAL).expectedObserverGamemodeForSpectator(GameMode.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testOpSurvivalObserving(TestContext context) {
            build().observingAs(GameMode.SURVIVAL, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameMode.SURVIVAL).expectedObserverGamemodeForSpectator(GameMode.SURVIVAL).runTest(context);
        }
        @GameTest
        public void testOpSpectatorObserving(TestContext context) {
            build().observingAs(GameMode.SPECTATOR, ScenarioBuilder.OpStatus.OP).expectedSpectatorGamemodeForObserver(GameMode.SURVIVAL).expectedObserverGamemodeForSpectator(GameMode.SURVIVAL).runTest(context);
        }
    }



    public static class ScenarioBuilder {
        private GameMode from;
        private GameMode to;
        private GameMode observerGameMode;
        private boolean isObserverOp;
        private GameMode expectedSpectatorGamemodeForObserver;
        private GameMode expectedObserverGamemodeForSpectator;

        public ScenarioBuilder switchingGamemodeFromTo(GameMode from, GameMode to) {
            this.from = from;
            this.to = to;
            return this;
        }

        public ScenarioBuilder observingAs(GameMode gameMode, OpStatus opStatus) {
            this.observerGameMode = gameMode;
            this.isObserverOp = opStatus.isOp;
            return this;
        }

        public ScenarioBuilder expectedSpectatorGamemodeForObserver(GameMode expected) {
            this.expectedSpectatorGamemodeForObserver = expected;
            return this;
        }
        public ScenarioBuilder expectedObserverGamemodeForSpectator(GameMode expected) {
            this.expectedObserverGamemodeForSpectator = expected;
            return this;
        }

        public void runTest(TestContext context) {
            TestContextExtension testContextExtension = (TestContextExtension) context;
            FakeTestPlayer observer = testContextExtension.createFakeTestPlayer(new TestContextExtension.FakePlayerOptions().gameMode(observerGameMode).opLevel(isObserverOp ? 4 : 0));
            FakeTestPlayer spectator = testContextExtension.createFakeTestPlayer(new TestContextExtension.FakePlayerOptions().gameMode(from));

            spectator.changeGameMode(to);

            if(spectator.getPlayerList().getGameMode(spectator).orElseThrow() != to) {
                context.throwGameTestException(Text.of(String.format("Expected spectator to have its real gamemode %s on its player list, but got %s", to, spectator.getPlayerList().getGameMode(spectator).orElseThrow())));
            }

            if(observer.getPlayerList().getGameMode(spectator).orElseThrow() != expectedSpectatorGamemodeForObserver) {
                context.throwGameTestException(Text.of(String.format(
                        "A player changed from %s to %s. The %s observer was in %s. Expected the observer to see the player in %s, but was %s",
                        from, to,
                        isObserverOp ? "op" : "non-op",
                        observerGameMode,
                        expectedSpectatorGamemodeForObserver,
                        observer.getPlayerList().getGameMode(spectator).orElseThrow()
                )));
            }
            if(spectator.getPlayerList().getGameMode(observer).orElseThrow() != expectedObserverGamemodeForSpectator) {
                context.throwGameTestException(Text.of(String.format(
                        "A player changed from %s to %s. The %s observer was in %s. Expected the spectator to see the observer in %s, but was %s",
                        from, to,
                        isObserverOp ? "op" : "non-op",
                        observerGameMode,
                        expectedObserverGamemodeForSpectator,
                        spectator.getPlayerList().getGameMode(observer).orElseThrow()
                )));
            }

            context.complete();
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

