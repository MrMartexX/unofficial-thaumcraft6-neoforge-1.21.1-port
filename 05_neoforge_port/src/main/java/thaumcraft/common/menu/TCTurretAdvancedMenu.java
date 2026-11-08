package thaumcraft.common.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.construct.TCTurretCrossbowAdvancedEntity;
import thaumcraft.common.registry.TCMenus;

public class TCTurretAdvancedMenu extends TCTurretBasicMenu {

    public TCTurretAdvancedMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        super(TCMenus.TURRET_ADVANCED.get(), containerId, inventory, clientTurret(inventory, extraData));
    }

    public TCTurretAdvancedMenu(int containerId, Inventory inventory, TCTurretCrossbowAdvancedEntity turret) {
        super(TCMenus.TURRET_ADVANCED.get(), containerId, inventory, turret);
    }

    @Override
    protected void addTurretSlot() {
        // Slot 0 at 42, 29 for advanced turret
        addSlot(new Slot(ammoContainer, 0, 42, 29) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
            }
        });
    }

    public TCTurretCrossbowAdvancedEntity getAdvancedTurret() {
        return turret instanceof TCTurretCrossbowAdvancedEntity adv ? adv : null;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        TCTurretCrossbowAdvancedEntity adv = getAdvancedTurret();
        if (adv == null || player == null || !stillValid(player)) {
            return false;
        }
        return switch (buttonId) {
            case 1 -> {
                adv.setTargetAnimal(!adv.getTargetAnimal());
                yield true;
            }
            case 2 -> {
                adv.setTargetMob(!adv.getTargetMob());
                yield true;
            }
            case 3 -> {
                adv.setTargetPlayer(!adv.getTargetPlayer());
                yield true;
            }
            case 4 -> {
                adv.setTargetFriendly(!adv.getTargetFriendly());
                yield true;
            }
            default -> super.clickMenuButton(player, buttonId);
        };
    }

    private static TCTurretCrossbowAdvancedEntity clientTurret(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return null;
        }
        int id = extraData.readInt();
        return inventory.player.level().getEntity(id) instanceof TCTurretCrossbowAdvancedEntity t ? t : null;
    }
}
