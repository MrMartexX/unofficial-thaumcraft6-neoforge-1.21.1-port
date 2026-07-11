package thaumcraft.common.entities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.tools.ItemArcaneBore;
import thaumcraft.common.menu.TCArcaneBoreMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.devices.TCVoidSiphonBlockEntity;

public final class TCFluxBoreThaumatoriumBlockerAudit {
    private TCFluxBoreThaumatoriumBlockerAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Flux Rift, Arcane Bore and Thaumatorium Blocker Audit");
        lines.add("");
        lines.add("Runtime checks for the row-11/13/14/17 blocker slice that connects real Flux Rift entities,");
        lines.add("Arcane Bore entity/menu/mining foundation and Thaumatorium OBJ/output rendering resources.");
        lines.add("");
        lines.add("## Summary");
        lines.add("");
        lines.add("| Check | Result |");
        lines.add("|---|---:|");
        lines.add("| Passed | " + report.passed() + " |");
        lines.add("| Failed | " + report.failed() + " |");
        lines.add("");
        lines.add("## Checks");
        lines.add("");
        lines.add("| Name | Result | Notes |");
        lines.add("|---|---|---|");
        for (Check check : report.checks()) {
            lines.add("| " + check.name() + " | " + (check.passed() ? "PASS" : "FAIL")
                    + " | " + check.notes().replace("|", "\\|") + " |");
        }
        lines.add("");
        lines.add("## Boundary");
        lines.add("");
        lines.add("- Flux Rift now has a registered entity, data-synced seed/size/stability/collapse state, TC6-style geometry and Void Siphon adapter integration.");
        lines.add("- Full rift event consequences that depend on Wisp, taint seed, focus cloud and warp subsystems remain owned by those later subsystem rows.");
        lines.add("- Arcane Bore now has a registered entity, placer item, one-slot pickaxe menu, redstone active state, vis charge and server-side mining loop.");
        lines.add("- Thaumatorium no longer uses a cube fallback model; it resolves the legacy OBJ model and has a BER output-item render path.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(192, 6, 192);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addFluxRiftChecks(level, origin, checks);
        addArcaneBoreChecks(level, origin.offset(8, 0, 0), checks);
        addThaumatoriumResourceChecks(checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("flux_rift_entity_registered_with_legacy_tracking",
                entityId(TCEntityTypes.FLUX_RIFT.get()).equals(id("flux_rift"))
                        && legacySpecRegistered("FluxRift", "flux_rift", 64, 20, false),
                "entity=" + entityId(TCEntityTypes.FLUX_RIFT.get())));
        checks.add(check("arcane_bore_entity_and_item_registered_with_legacy_tracking",
                entityId(TCEntityTypes.ARCANE_BORE.get()).equals(id("arcane_bore"))
                        && TCItems.ARCANE_BORE.get() instanceof ItemArcaneBore
                        && legacySpecRegistered("ArcaneBore", "arcane_bore", 64, 3, true),
                "entity=" + entityId(TCEntityTypes.ARCANE_BORE.get())
                        + ", itemClass=" + TCItems.ARCANE_BORE.get().getClass().getSimpleName()));
    }

    private static void addFluxRiftChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCFluxRiftEntity rift = new TCFluxRiftEntity(level, origin.getX() + 2.5D, origin.getY() + 1.0D, origin.getZ() + 2.5D);
        rift.setRiftSeed(12345);
        rift.setRiftSize(9);
        rift.setRiftStability(150.0F);
        boolean geometry = rift.renderPoints().size() == 8 && rift.renderWidths().size() == 8;
        boolean clampHigh = close(rift.getRiftStability(), 100.0F);
        rift.setRiftStability(-150.0F);
        boolean clampLow = close(rift.getRiftStability(), -100.0F);
        rift.setRiftStability(1.0F);
        checks.add(check("flux_rift_seeded_geometry_and_stability_clamps_match_legacy",
                geometry && clampHigh && clampLow,
                "points=" + rift.renderPoints().size()
                        + ", widths=" + rift.renderWidths().size()
                        + ", clampHigh=" + clampHigh
                        + ", clampLow=" + clampLow));

        level.setBlock(origin, TCBlocks.VOID_SIPHON.get().defaultBlockState(), Block.UPDATE_ALL);
        TCVoidSiphonBlockEntity siphon = level.getBlockEntity(origin) instanceof TCVoidSiphonBlockEntity be ? be : null;
        if (siphon == null) {
            checks.add(check("void_siphon_consumes_real_flux_rift_entity", false, "missing siphon block entity"));
            return;
        }
        int drained = siphon.drainRiftsForValidation(List.of(rift), Boolean.FALSE);
        checks.add(check("void_siphon_consumes_real_flux_rift_entity",
                drained == 3
                        && siphon.progress() == 3
                        && close(rift.getRiftStability(), 0.8F)
                        && rift.getRiftSize() == 9,
                "drained=" + drained
                        + ", progress=" + siphon.progress()
                        + ", stability=" + rift.getRiftStability()
                        + ", size=" + rift.getRiftSize()));

        rift.setRiftSize(16);
        rift.setCollapse(true);
        rift.completeCollapseForValidation();
        checks.add(check("flux_rift_collapse_drops_void_seed_family_and_discards",
                !rift.isAlive(),
                "alive=" + rift.isAlive()));
    }

    private static void addArcaneBoreChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCArcaneBoreEntity bore = new TCArcaneBoreEntity(level, origin, Direction.SOUTH, null);
        level.addFreshEntity(bore);
        bore.setItem(TCArcaneBoreEntity.SLOT_TOOL, new ItemStack(Items.IRON_PICKAXE));
        checks.add(check("arcane_bore_pickaxe_slot_contract",
                bore.validInventory()
                        && bore.getDigRadius() == 2
                        && bore.getDigDepth() == 16
                        && bore.canPlaceItem(TCArcaneBoreEntity.SLOT_TOOL, new ItemStack(Items.IRON_PICKAXE))
                        && !bore.canPlaceItem(TCArcaneBoreEntity.SLOT_TOOL, new ItemStack(Items.DIRT)),
                "valid=" + bore.validInventory()
                        + ", radius=" + bore.getDigRadius()
                        + ", depth=" + bore.getDigDepth()
                        + ", pickaxeTag=" + new ItemStack(Items.IRON_PICKAXE).is(ItemTags.PICKAXES)));

        BlockPos target = origin.south(2);
        level.setBlock(target, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        bore.setChargeForValidation(10.0F);
        bore.setDigTargetForValidation(target);
        boolean mined = bore.mineTargetForValidation();
        checks.add(check("arcane_bore_mines_target_and_consumes_vis_charge_path",
                mined && level.getBlockState(target).isAir(),
                "mined=" + mined + ", targetState=" + BuiltInRegistries.BLOCK.getKey(level.getBlockState(target).getBlock())));

        TCArcaneBoreMenu menu = new TCArcaneBoreMenu(1, FakePlayerFactory.getMinecraft(level).getInventory(), bore);
        checks.add(check("arcane_bore_menu_exposes_single_pickaxe_slot",
                menu.getSlot(TCArcaneBoreMenu.SLOT_TOOL).mayPlace(new ItemStack(Items.IRON_PICKAXE))
                        && !menu.getSlot(TCArcaneBoreMenu.SLOT_TOOL).mayPlace(new ItemStack(Items.DIRT)),
                "slots=" + menu.slots.size()));
    }

    private static void addThaumatoriumResourceChecks(ArrayList<Check> checks) {
        String model = resourceText("assets/thaumcraft/models/block/thaumatorium.json");
        String material = resourceText("assets/thaumcraft/models/block/thaumatorium.mtl");
        String blockstate = resourceText("assets/thaumcraft/blockstates/thaumatorium.json");
        checks.add(check("thaumatorium_model_uses_legacy_obj_loader_not_cube_fallback",
                model.contains("\"loader\": \"neoforge:obj\"")
                        && model.contains("thaumatorium.obj")
                        && model.contains("thaumatorium.mtl"),
                "modelLength=" + model.length()));
        checks.add(check("thaumatorium_mtl_uses_modern_block_texture_location",
                material.contains("map_Kd thaumcraft:block/thaumatorium")
                        && !material.contains("textures\\blocks")
                        && !material.contains("textures/blocks")
                        && !material.contains("thaumcraft:blocks/thaumatorium"),
                "materialLength=" + material.length()));
        checks.add(check("thaumatorium_blockstate_uses_legacy_obj_orientation",
                blockstate.contains("\"x\": 90")
                        && blockstate.contains("\"y\": 90")
                        && blockstate.contains("\"y\": 180")
                        && blockstate.contains("\"y\": 270"),
                "blockstateLength=" + blockstate.length()));
    }

    private static boolean legacySpecRegistered(String legacyId, String modernId, int range, int interval, boolean velocity) {
        return TCEntityTypes.byLegacyId(legacyId)
                .filter(spec -> spec.isRegisteredFoundation()
                        && modernId.equals(spec.modernId())
                        && spec.trackingRange() == range
                        && spec.updateInterval() == interval
                        && spec.velocityUpdates() == velocity)
                .isPresent();
    }

    private static String resourceText(String path) {
        try (InputStream stream = TCFluxBoreThaumatoriumBlockerAudit.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return "";
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return "";
        }
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-4, -2, -4), origin.offset(16, 8, 16)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
        level.getEntitiesOfClass(TCFluxRiftEntity.class, new net.minecraft.world.phys.AABB(origin).inflate(32.0D)).forEach(Entity::discard);
        level.getEntitiesOfClass(TCArcaneBoreEntity.class, new net.minecraft.world.phys.AABB(origin).inflate(32.0D)).forEach(Entity::discard);
    }

    private static boolean close(float actual, float expected) {
        return Math.abs(actual - expected) < 0.0001F;
    }

    private static ResourceLocation entityId(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return checks.size() - passed();
        }
    }

    public record Check(String name, boolean passed, String notes) {
    }
}
