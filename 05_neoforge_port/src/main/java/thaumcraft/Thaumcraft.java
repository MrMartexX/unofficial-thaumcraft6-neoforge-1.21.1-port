package thaumcraft;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import thaumcraft.client.gui.TCClientTooltipComponents;
import thaumcraft.common.aspects.TCAspectAssignments;
import thaumcraft.common.aspects.TCAspectDumpExporter;
import thaumcraft.common.aspects.TCAspectReloadValidator;
import thaumcraft.common.aspects.TCGeneratedAspectRecipeGenerator;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCCreativeTabs;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCKnowledgeCommands;
import thaumcraft.common.research.TCKnowledgeNetwork;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.research.TCResearchRequirementAuditCommands;
import thaumcraft.common.research.TCScanningCommands;
import thaumcraft.common.research.TCScanningManager;
import thaumcraft.common.world.aura.TCAuraDebugCommands;
import thaumcraft.common.world.aura.TCAuraEvents;
import thaumcraft.common.world.aura.TCAuraNetwork;

@Mod(Thaumcraft.MODID)
public final class Thaumcraft {
    public static final String MODID = "thaumcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Thaumcraft(IEventBus modEventBus, ModContainer modContainer) {
        TCBlocks.BLOCKS.register(modEventBus);
        TCItems.ITEMS.register(modEventBus);
        TCSounds.SOUND_EVENTS.register(modEventBus);
        TCCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            TCClientTooltipComponents.register(modEventBus);
        }
        modEventBus.addListener(TCAuraNetwork::onRegisterPayloadHandlers);
        modEventBus.addListener(TCKnowledgeNetwork::onRegisterPayloadHandlers);
        modContainer.registerConfig(ModConfig.Type.COMMON, TCConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(TCAspectAssignments::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(TCResearchManager::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(TCScanningManager::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(TCGeneratedAspectRecipeGenerator::onTagsUpdated);
        NeoForge.EVENT_BUS.addListener(TCAspectReloadValidator::onTagsUpdated);
        NeoForge.EVENT_BUS.addListener(TCAspectDumpExporter::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCAuraEvents::onServerTick);
        NeoForge.EVENT_BUS.addListener(TCAuraEvents::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(TCAuraEvents::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(TCAuraDebugCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCKnowledgeCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCKnowledgeNetwork::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(TCKnowledgeNetwork::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(TCKnowledgeNetwork::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(TCResearchRequirementAuditCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCScanningCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCScanningManager::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCResearchManager::onItemCrafted);
        TCAspectAssignments.bootstrap();
        TCResearchManager.bootstrap();
        TCScanningManager.bootstrap();

        LOGGER.info("Thaumcraft NeoForge bootstrap initialized.");
    }
}
