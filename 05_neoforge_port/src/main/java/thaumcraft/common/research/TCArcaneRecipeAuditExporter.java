package thaumcraft.common.research;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCArcaneRecipeAuditExporter {
    private TCArcaneRecipeAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.arcaneRecipeAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.arcaneRecipeAuditPath",
                    "arcane_crafting/thaumcraft_1_21_arcane_recipe_audit.md"
            )).toAbsolutePath().normalize();
            TCArcaneRecipeAudit.Report report = TCArcaneRecipeAudit.writeMarkdown(
                    output,
                    event.getServer().getRecipeManager(),
                    event.getServer().registryAccess()
            );
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft arcane recipe audit to {}: checks={}/{} passed, recipes={}.",
                    output,
                    report.passed(),
                    report.passed() + report.failed(),
                    report.arcaneRecipeCount()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft arcane recipe audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft arcane recipe audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
