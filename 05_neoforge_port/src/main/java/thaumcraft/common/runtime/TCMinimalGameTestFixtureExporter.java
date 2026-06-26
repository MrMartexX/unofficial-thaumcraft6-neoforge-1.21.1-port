package thaumcraft.common.runtime;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCMinimalGameTestFixtureExporter {
    private TCMinimalGameTestFixtureExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(TCMinimalGameTestFixture.ENABLE_PROPERTY, "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    TCMinimalGameTestFixture.OUTPUT_PROPERTY,
                    "runtime/thaumcraft_minimal_gametest_fixture.md"
            )).toAbsolutePath().normalize();
            TCMinimalGameTestFixture.Report report = TCMinimalGameTestFixture.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft minimal GameTest fixture report to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft minimal GameTest fixture failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft minimal GameTest fixture report", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
