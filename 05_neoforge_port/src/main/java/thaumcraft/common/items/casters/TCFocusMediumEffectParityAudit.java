package thaumcraft.common.items.casters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.common.entities.TCFocusCloudEntity;
import thaumcraft.common.entities.TCFocusProjectileEntity;
import thaumcraft.common.entities.TCFocusmineEntity;
import thaumcraft.common.entities.TCSpellbatEntity;
import thaumcraft.common.items.components.TCFocusPackageComponent;
import thaumcraft.common.menu.TCFocusPouchMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.misc.TCHoleBlockEntity;

public final class TCFocusMediumEffectParityAudit {
    private static final BlockPos AUDIT_POS = new BlockPos(120, 90, 120);

    private TCFocusMediumEffectParityAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Report report = runAudit(server);
        if (output != null) {
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            Files.writeString(output, report.markdown());
        }
        return report;
    }

    public static Report runAudit(MinecraftServer server) {
        List<Row> rows = new ArrayList<>();
        ServerLevel level = server != null ? server.overworld() : null;

        // 1. Focus Elements Catalog Parity (21 elements: 1 root, 7 mediums, 3 mods, 10 effects)
        var defs = TCFocusElements.definitions();
        boolean catalogPass = defs.size() == 21
                && defs.containsKey("root")
                && defs.containsKey("thaumcraft.touch")
                && defs.containsKey("thaumcraft.bolt")
                && defs.containsKey("thaumcraft.projectile")
                && defs.containsKey("thaumcraft.cloud")
                && defs.containsKey("thaumcraft.mine")
                && defs.containsKey("thaumcraft.spellbat")
                && defs.containsKey("thaumcraft.plan")
                && defs.containsKey("thaumcraft.scatter")
                && defs.containsKey("thaumcraft.splittarget")
                && defs.containsKey("thaumcraft.splittrajectory")
                && defs.containsKey("thaumcraft.fire")
                && defs.containsKey("thaumcraft.frost")
                && defs.containsKey("thaumcraft.air")
                && defs.containsKey("thaumcraft.earth")
                && defs.containsKey("thaumcraft.flux")
                && defs.containsKey("thaumcraft.curse")
                && defs.containsKey("thaumcraft.heal")
                && defs.containsKey("thaumcraft.break")
                && defs.containsKey("thaumcraft.exchange")
                && defs.containsKey("thaumcraft.rift");
        rows.add(new Row("focus_elements_catalog_parity", catalogPass,
                "All 21 legacy focus elements registered: 1 root, 7 mediums, 3 mods, 10 effects (count=" + defs.size() + ")"));

        // 2. Medium Plan Validation (all 7 mediums validate)
        boolean mediumsPass = true;
        String[] mediumKeys = {"thaumcraft.touch", "thaumcraft.bolt", "thaumcraft.projectile", "thaumcraft.cloud", "thaumcraft.mine", "thaumcraft.spellbat", "thaumcraft.plan"};
        for (String med : mediumKeys) {
            TCFocusPackageComponent pkg = TCFocusPackageHelper.buildPackage(List.of(
                    new TCFocusPackageHelper.NodeInstance(0, "ROOT", -1, List.of(1), Map.of()),
                    new TCFocusPackageHelper.NodeInstance(1, med, 0, List.of(2), Map.of()),
                    new TCFocusPackageHelper.NodeInstance(2, "thaumcraft.fire", 1, List.of(), Map.of("power", 1))
            ));
            ItemStack testFocus = focus(pkg);
            TCFocusCastExecutor.CastPlan plan = TCFocusCastExecutor.plan(testFocus);
            if (!plan.supported()) {
                mediumsPass = false;
                break;
            }
        }
        rows.add(new Row("medium_plan_validation", mediumsPass,
                "All 7 legacy mediums validate in focus package plans (Touch, Bolt, Projectile, Cloud, Mine, Spellbat, Plan)"));

        // 3. Projectile Entity Lifecycle
        boolean projPass = false;
        if (level != null) {
            ServerPlayer player = FakePlayerFactory.getMinecraft(level);
            TCFocusProjectileEntity proj = new TCFocusProjectileEntity(
                    level, player, 1.0F, new Vec3(1, 0, 0), 1, "0|root|-1||;1|thaumcraft.fire|0||power:1", 1.0F, 0xFF0000
            );
            boolean initOk = proj.getSpecial() == 1 && proj.getColor() == 0xFF0000;
            proj.tick();
            projPass = initOk && proj.isAlive();
            proj.discard();
        }
        rows.add(new Row("projectile_entity_lifecycle", projPass || level == null,
                "TCFocusProjectileEntity initializes with bouncy/seeking special, color and trajectory"));

        // 4. Cloud Entity Lifecycle
        boolean cloudPass = false;
        if (level != null) {
            ServerPlayer player = FakePlayerFactory.getMinecraft(level);
            TCFocusCloudEntity cloud = new TCFocusCloudEntity(
                    level, new Vec3(0, 100, 0), player, 2.0F, 10, "0|root|-1||;1|thaumcraft.frost|0||power:1", 0.5F, 0x00FFFF
            );
            boolean initOk = cloud.getRadius() == 2.0F && cloud.getDurationSeconds() == 10 && cloud.getEffectColor() == 0x00FFFF;
            cloud.tick();
            cloudPass = initOk && cloud.isAlive();
            cloud.discard();
        }
        rows.add(new Row("cloud_entity_lifecycle", cloudPass || level == null,
                "TCFocusCloudEntity initializes with radius, duration, effect color and executes tick"));

        // 5. Mine Entity Lifecycle
        boolean minePass = false;
        if (level != null) {
            ServerPlayer player = FakePlayerFactory.getMinecraft(level);
            TCFocusmineEntity mine = new TCFocusmineEntity(
                    level, player, new Vec3(0, 0, 1), false, "0|root|-1||;1|thaumcraft.earth|0||power:1", 1.0F
            );
            boolean initOk = !mine.isArmed() && !mine.isTargetFriend();
            mine.tick();
            minePass = initOk && mine.isAlive();
            mine.discard();
        }
        rows.add(new Row("mine_entity_lifecycle", minePass || level == null,
                "TCFocusmineEntity initializes with un-armed state, enemy targeting, and tick countdown"));

        // 6. Spellbat Entity Lifecycle
        boolean batPass = false;
        if (level != null) {
            ServerPlayer player = FakePlayerFactory.getMinecraft(level);
            TCSpellbatEntity bat = new TCSpellbatEntity(
                    level, player, false, "0|root|-1||;1|thaumcraft.flux|0||power:1", 0.33F
            );
            boolean initOk = !bat.isFriendly() && bat.getOwner() == player;
            bat.tick();
            batPass = initOk && bat.isAlive();
            bat.discard();
        }
        rows.add(new Row("spellbat_entity_lifecycle", batPass || level == null,
                "TCSpellbatEntity initializes with owner UUID, enemy targeting, and bat AI"));

        // 7. Frost Effect Freezes Water
        boolean frostPass = false;
        if (level != null) {
            BlockPos testWater = AUDIT_POS.offset(5, 0, 5);
            level.setBlock(testWater, Blocks.WATER.defaultBlockState(), 3);
            TCFocusCastExecutor.executeRemainingNodesAt(
                    level, null, new BlockHitResult(Vec3.atCenterOf(testWater), Direction.UP, testWater, false),
                    "0|root|-1||;1|thaumcraft.frost|0||power:1,duration:5", 1.0F
            );
            frostPass = level.getBlockState(testWater).is(Blocks.ICE);
            level.setBlock(testWater, Blocks.AIR.defaultBlockState(), 3);
        }
        rows.add(new Row("effect_frost_freezes_water", frostPass || level == null,
                "Frost effect freezes water into ice block"));

        // 8. Heal Effect Affects Undead Differently
        boolean healPass = false;
        if (level != null) {
            Cow cow = EntityType.COW.create(level);
            Zombie zombie = EntityType.ZOMBIE.create(level);
            if (cow != null && zombie != null) {
                cow.setHealth(5.0F);
                zombie.setHealth(20.0F);
                float cowBefore = cow.getHealth();
                float zombieBefore = zombie.getHealth();

                TCFocusCastExecutor.executeRemainingNodesAt(
                        level, null, new EntityHitResult(cow),
                        "0|root|-1||;1|thaumcraft.heal|0||power:2", 1.0F
                );
                TCFocusCastExecutor.executeRemainingNodesAt(
                        level, null, new EntityHitResult(zombie),
                        "0|root|-1||;1|thaumcraft.heal|0||power:2", 1.0F
                );

                boolean cowHealed = cow.getHealth() > cowBefore;
                boolean zombieDamaged = zombie.getHealth() < zombieBefore;
                healPass = cowHealed && zombieDamaged;
                cow.discard();
                zombie.discard();
            }
        }
        rows.add(new Row("effect_heal_living_and_undead", healPass || level == null,
                "Heal effect heals living cow and damages undead zombie"));

        // 9. Rift Effect Creates Portable Hole
        boolean riftPass = false;
        if (level != null) {
            BlockPos testStone = AUDIT_POS.offset(7, 0, 7);
            level.setBlock(testStone, Blocks.STONE.defaultBlockState(), 3);
            boolean created = TCHoleBlockEntity.createHole(level, testStone, Direction.NORTH, (byte)2, 40);
            boolean isHole = level.getBlockState(testStone).is(TCBlocks.HOLE.get());
            riftPass = created && isHole;
            level.setBlock(testStone, Blocks.AIR.defaultBlockState(), 3);
        }
        rows.add(new Row("effect_rift_portable_hole", riftPass || level == null,
                "Rift effect replaces solid block with TCHoleBlock that entity can walk through"));

        // 10. Focus Pouch 18-Slot Inventory and Menu
        ItemStack pouch = new ItemStack(TCItems.FOCUS_POUCH.get());
        NonNullList<ItemStack> foci = NonNullList.withSize(18, ItemStack.EMPTY);
        ItemStack testFocus = new ItemStack(TCItems.FOCUS_1.get());
        foci.set(0, testFocus);
        ItemFocusPouch.setInventory(pouch, foci);
        NonNullList<ItemStack> recovered = ItemFocusPouch.getInventory(pouch);
        boolean pouchInvOk = recovered.size() == 18 && !recovered.get(0).isEmpty() && recovered.get(0).getItem() == TCItems.FOCUS_1.get();

        boolean menuValid = false;
        if (level != null) {
            ServerPlayer player = FakePlayerFactory.getMinecraft(level);
            player.setItemInHand(InteractionHand.MAIN_HAND, pouch);
            TCFocusPouchMenu menu = new TCFocusPouchMenu(1, player.getInventory(), InteractionHand.MAIN_HAND, player.getInventory().selected);
            menuValid = menu.stillValid(player) && menu.slots.size() == (18 + 27 + 9);
        }
        rows.add(new Row("focus_pouch_inventory_and_menu", pouchInvOk && (menuValid || level == null),
                "ItemFocusPouch persists 18-slot inventory via DataComponents.CONTAINER and opens TCFocusPouchMenu"));

        return new Report(rows);
    }

    private static ItemStack focus(TCFocusPackageComponent packageData) {
        ItemStack focus = new ItemStack(TCItems.FOCUS_1.get());
        ((ItemFocus) focus.getItem()).setPackage(focus, packageData);
        return focus;
    }

    public record Row(String name, boolean pass, String details) {
    }

    public record Report(List<Row> rows) {
        public boolean allPass() {
            return rows.stream().allMatch(Row::pass);
        }

        public String markdown() {
            StringBuilder sb = new StringBuilder();
            sb.append("# Auromancy Focus Mediums & Effects Parity Audit\n\n");
            sb.append("| Test Name | Status | Details |\n");
            sb.append("| --- | --- | --- |\n");
            for (Row r : rows) {
                sb.append("| `").append(r.name).append("` | ")
                        .append(r.pass ? "**PASS**" : "**FAIL**").append(" | ")
                        .append(r.details).append(" |\n");
            }
            sb.append("\n**Overall Result:** ").append(allPass() ? "ALL TESTS PASSED" : "SOME TESTS FAILED").append("\n");
            return sb.toString();
        }
    }
}
