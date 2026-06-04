package thaumcraft;


import thaumcraft.client.gui.TCKnowledgeGainHud;
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
import thaumcraft.client.gui.TCMenuScreens;
import thaumcraft.client.renderer.TCBlockEntityRenderers;
import thaumcraft.common.aspects.TCAspectAssignments;
import thaumcraft.common.aspects.TCAspectDumpExporter;
import thaumcraft.common.aspects.TCAspectReloadValidator;
import thaumcraft.common.aspects.TCGeneratedAspectRecipeGenerator;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCCreativeTabs;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCKnowledgeCommands;
import thaumcraft.common.research.TCKnowledgeNetwork;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.research.TCResearchDataAuditExporter;
import thaumcraft.common.research.TCResearchPageCatalogManager;
import thaumcraft.common.research.TCResearchPageCatalogAuditExporter;
import thaumcraft.common.research.TCResearchPageCatalogCommands;
import thaumcraft.common.research.TCThaumonomiconNetwork;
import thaumcraft.common.research.TCThaumonomiconProtocolAuditExporter;
import thaumcraft.common.research.TCResearchRequirementAuditCommands;
import thaumcraft.common.research.TCResearchRequirementAuditExporter;
import thaumcraft.common.research.TCScanningCommands;
import thaumcraft.common.research.TCScanningManager;
import thaumcraft.common.research.theorycraft.TCResearchTableAuditCommands;
import thaumcraft.common.research.theorycraft.TCResearchTableAuditExporter;
import thaumcraft.common.research.theorycraft.TCResearchTableNetwork;
import thaumcraft.common.research.theorycraft.TCTheorycraftManager;
import thaumcraft.common.warp.TCWarpCommands;
import thaumcraft.common.world.aura.TCAuraDebugCommands;
import thaumcraft.common.world.aura.TCAuraEvents;
import thaumcraft.common.world.aura.TCAuraNetwork;

@Mod(Thaumcraft.MODID)
public final class Thaumcraft {
    public static final String MODID = "thaumcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Thaumcraft(IEventBus modEventBus, ModContainer modContainer) {
        TCDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        TCBlocks.BLOCKS.register(modEventBus);
        TCBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        TCItems.ITEMS.register(modEventBus);
        TCMenus.MENUS.register(modEventBus);
        TCSounds.SOUND_EVENTS.register(modEventBus);
        TCCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            TCClientTooltipComponents.register(modEventBus);
            TCMenuScreens.register(modEventBus);
            TCBlockEntityRenderers.register(modEventBus);
            TCKnowledgeGainHud.register(modEventBus);
        }
        modEventBus.addListener(TCAuraNetwork::onRegisterPayloadHandlers);
        modEventBus.addListener(TCKnowledgeNetwork::onRegisterPayloadHandlers);
        modEventBus.addListener(TCResearchTableNetwork::onRegisterPayloadHandlers);
        modEventBus.addListener(TCThaumonomiconNetwork::onRegisterPayloadHandlers);
        modContainer.registerConfig(ModConfig.Type.COMMON, TCConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(TCAspectAssignments::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(TCResearchManager::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(TCResearchPageCatalogManager::onAddReloadListeners);
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
        NeoForge.EVENT_BUS.addListener(TCResearchRequirementAuditExporter::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCResearchDataAuditExporter::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCResearchPageCatalogAuditExporter::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCThaumonomiconProtocolAuditExporter::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCResearchPageCatalogCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCResearchTableAuditCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCResearchTableAuditExporter::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCScanningCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCWarpCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(TCScanningManager::onServerStarted);
        NeoForge.EVENT_BUS.addListener(TCResearchManager::onItemCrafted);
        TCAspectAssignments.bootstrap();
        TCResearchManager.bootstrap();
        TCResearchPageCatalogManager.bootstrap();
        TCTheorycraftManager.bootstrap();
        TCScanningManager.bootstrap();

        LOGGER.info("Thaumcraft NeoForge bootstrap initialized.");
    }
}
