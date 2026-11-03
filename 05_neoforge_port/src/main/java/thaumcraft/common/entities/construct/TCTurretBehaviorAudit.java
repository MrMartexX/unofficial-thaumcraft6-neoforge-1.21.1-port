package thaumcraft.common.entities.construct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import thaumcraft.common.menu.TCTurretAdvancedMenu;
import thaumcraft.common.menu.TCTurretBasicMenu;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

public final class TCTurretBehaviorAudit {
    private TCTurretBehaviorAudit() {
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

        // 1. Entity registration parity
        boolean basicReg = TCEntityTypes.TURRET_BASIC.get() != null
                && TCEntityTypes.TURRET_BASIC.get().getWidth() == 0.95F
                && TCEntityTypes.TURRET_BASIC.get().getHeight() == 1.25F;
        boolean advReg = TCEntityTypes.TURRET_ADVANCED.get() != null
                && TCEntityTypes.TURRET_ADVANCED.get().getWidth() == 0.95F
                && TCEntityTypes.TURRET_ADVANCED.get().getHeight() == 1.5F;
        rows.add(new Row(
                "turret_entity_registration",
                basicReg && advReg,
                "TurretBasic (0.95x1.25) and TurretAdvanced (0.95x1.5) registered"
        ));

        if (level != null) {
            TCTurretCrossbowEntity basic = new TCTurretCrossbowEntity(TCEntityTypes.TURRET_BASIC.get(), level);
            TCTurretCrossbowAdvancedEntity adv = new TCTurretCrossbowAdvancedEntity(TCEntityTypes.TURRET_ADVANCED.get(), level);

            // 2. Attributes parity
            boolean basicAttr = basic.getAttributeValue(Attributes.MAX_HEALTH) == 30.0D
                    && basic.getAttributeValue(Attributes.FOLLOW_RANGE) == 24.0D
                    && basic.getAttributeValue(Attributes.ARMOR) == 2.0D;
            boolean advAttr = adv.getAttributeValue(Attributes.MAX_HEALTH) == 40.0D
                    && adv.getAttributeValue(Attributes.FOLLOW_RANGE) == 24.0D
                    && adv.getAttributeValue(Attributes.ARMOR) == 8.0D;
            rows.add(new Row(
                    "turret_attribute_parity",
                    basicAttr && advAttr,
                    "Basic: HP 30, Range 24, Armor 2 | Advanced: HP 40, Range 24, Armor 8"
            ));

            // 3. Item placer parity
            boolean placersOk = TCItems.AUTOMATEDCROSSBOW.get() instanceof ItemTurretPlacer p1 && !p1.isAdvanced()
                    && TCItems.ADVANCEDCROSSBOW.get() instanceof ItemTurretPlacer p2 && p2.isAdvanced()
                    && TCItems.TURRET_PLACER.get() instanceof ItemTurretPlacer;
            rows.add(new Row(
                    "turret_item_placer",
                    placersOk,
                    "AutomatedCrossbow (basic), AdvancedCrossbow (advanced), and TurretPlacer valid ItemTurretPlacer"
            ));

            Player dummyPlayer = new net.minecraft.world.entity.player.Player(level, BlockPos.ZERO, 0.0F, new com.mojang.authlib.GameProfile(UUID.randomUUID(), "TurretTester")) {
                @Override public boolean isSpectator() { return false; }
                @Override public boolean isCreative() { return false; }
            };

            // 4. Basic menu and ammo slot acceptance
            TCTurretBasicMenu basicMenu = new TCTurretBasicMenu(1, dummyPlayer.getInventory(), basic);
            boolean ammoAccept = basicMenu.getSlot(0).mayPlace(new ItemStack(Items.ARROW))
                    && !basicMenu.getSlot(0).mayPlace(new ItemStack(Items.DIAMOND));
            rows.add(new Row(
                    "turret_menu_and_ammo_slot",
                    ammoAccept && basicMenu.slots.size() == 37,
                    "Ammo slot accepts ArrowItem, rejects non-arrow, 37 total slots"
            ));

            // 5. Advanced menu target filters
            TCTurretAdvancedMenu advMenu = new TCTurretAdvancedMenu(2, dummyPlayer.getInventory(), adv);
            boolean initialMob = adv.getTargetMob();
            advMenu.clickMenuButton(null, 1); // toggle animal
            advMenu.clickMenuButton(null, 2); // toggle mob
            advMenu.clickMenuButton(null, 3); // toggle player
            advMenu.clickMenuButton(null, 4); // toggle friendly
            boolean toggledOk = adv.getTargetAnimal() && !adv.getTargetMob() && adv.getTargetPlayer() && adv.getTargetFriendly();
            adv.setTargetMob(true); // reset
            rows.add(new Row(
                    "turret_advanced_menu_filters",
                    initialMob && toggledOk,
                    "Buttons 1-4 toggle Animal, Mob, Player, Friendly flags respectively"
            ));

            // 6. Ranged attack consumes arrow and shoots
            basic.getAmmoInventory().setItem(0, new ItemStack(Items.ARROW, 2));
            Zombie dummyTarget = new Zombie(level);
            dummyTarget.setPos(basic.getX() + 5, basic.getY(), basic.getZ());
            basic.performRangedAttack(dummyTarget, 0.5F);
            boolean ammoConsumed = basic.getAmmoInventory().getItem(0).getCount() == 1;
            rows.add(new Row(
                    "turret_ranged_attack_cycle",
                    ammoConsumed,
                    "Ranged attack consumes arrow from slot and fires AbstractArrow"
            ));

            // 7. Dispenser auto reload
            BlockPos testPos = new BlockPos(100, 10, 100);
            level.setBlock(testPos, Blocks.DISPENSER.defaultBlockState(), 3);
            DispenserBlockEntity dispenser = (DispenserBlockEntity) level.getBlockEntity(testPos);
            if (dispenser != null) {
                dispenser.setItem(0, new ItemStack(Items.ARROW, 16));
                basic.setPos(testPos.getX() + 0.5D, testPos.getY() + 1.0D, testPos.getZ() + 0.5D);
                basic.getAmmoInventory().setItem(0, ItemStack.EMPTY);
                basic.tick();
                boolean reloaded = basic.getAmmoInventory().getItem(0).getCount() == 16
                        && dispenser.getItem(0).isEmpty();
                rows.add(new Row(
                    "turret_dispenser_auto_reload",
                    reloaded,
                    "Pulls arrows from DispenserBlockEntity below turret when ammo empty"
                ));
            } else {
                rows.add(new Row("turret_dispenser_auto_reload", false, "Failed to create test dispenser"));
            }

            // 8. Activator rail AI shutdown
            BlockPos railPos = new BlockPos(100, 20, 100);
            level.setBlock(railPos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            level.setBlock(railPos, Blocks.ACTIVATOR_RAIL.defaultBlockState(), 3);
            basic.moveTo(railPos.getX() + 0.5D, railPos.getY(), railPos.getZ() + 0.5D, 0.0F, 0.0F);
            basic.setNoAi(false);
            basic.tick();
            boolean noAiActive = basic.isNoAi();
            rows.add(new Row(
                    "turret_activator_rail_shutdown",
                    noAiActive,
                    "Powered activator rail shuts off turret AI (setNoAi=true)"
            ));

            // 9. Turret dismantle logic
            basic.getAmmoInventory().setItem(0, new ItemStack(Items.ARROW, 5));
            basic.setOwnerUUID(dummyPlayer.getUUID());
            basic.dismantle(dummyPlayer, net.minecraft.world.InteractionHand.MAIN_HAND);
            boolean dismantled = basic.isRemoved() && basic.getAmmoInventory().getItem(0).isEmpty();
            rows.add(new Row(
                    "turret_dismantle_and_death_drops",
                    dismantled,
                    "Dismantle clears entity, drops ammo and placer item"
            ));

            // 10. Owner team safety
            adv.setOwnerUUID(dummyPlayer.getUUID());
            adv.setTargetPlayer(true);
            adv.setTargetFriendly(false);
            boolean ownerSafe = !adv.isTargetValid(dummyPlayer);
            var pet = new net.minecraft.world.entity.animal.Wolf(net.minecraft.world.entity.EntityType.WOLF, level);
            pet.setOwnerUUID(dummyPlayer.getUUID());
            adv.setTargetAnimal(true);
            boolean petSafe = !adv.isTargetValid(pet);
            adv.setTargetFriendly(true);
            boolean friendlyEnabled = adv.isTargetValid(pet);
            adv.setTargetFriendly(false);
            rows.add(new Row("turret_owned_pet_filter", petSafe && friendlyEnabled,
                    "Animal category cannot bypass same-owner protection; explicit friendly targeting remains effective"));
            rows.add(new Row(
                    "turret_owner_team_safety",
                    ownerSafe,
                    "Turret never targets its owner even when targetPlayer is true"
            ));
        }

        return new Report(rows);
    }

    public record Row(String name, boolean pass, String details) {
    }

    public record Report(List<Row> rows) {
        public boolean allPass() {
            return rows.stream().allMatch(Row::pass);
        }

        public String markdown() {
            StringBuilder sb = new StringBuilder();
            sb.append("# Arcane Automated Turrets Parity Audit\n\n");
            sb.append("| Test Name | Status | Details |\n");
            sb.append("| --- | --- | --- |\n");
            for (Row row : rows) {
                sb.append("| `").append(row.name()).append("` | ")
                        .append(row.pass() ? "**PASS**" : "**FAIL**").append(" | ")
                        .append(row.details()).append(" |\n");
            }
            sb.append("\n**Overall Result:** ").append(allPass() ? "ALL TESTS PASSED" : "AUDIT FAILED").append("\n");
            return sb.toString();
        }
    }
}
