package thaumcraft.client.renderer.gear;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import thaumcraft.common.registry.TCItems;

/**
 * Registers NeoForge 1.21.1 IClientItemExtensions for custom 3D Thaumcraft armors:
 * Thaumium Fortress Armor, Void Thaumaturge Robes, Cultist Knight Plates,
 * Cultist Cleric Robes, and Cultist Praetor / Leader Armor.
 */
public final class TCArmorClientExtensions {
    private TCArmorClientExtensions() {
    }

    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new FortressArmorExtension(),
                TCItems.THAUMIUM_FORTRESS_HELM.get(),
                TCItems.THAUMIUM_FORTRESS_HELM_GOGGLES.get(),
                TCItems.THAUMIUM_FORTRESS_CHEST.get(),
                TCItems.THAUMIUM_FORTRESS_LEGS.get());

        event.registerItem(new VoidRobeArmorExtension(),
                TCItems.VOID_ROBE_HELM.get(),
                TCItems.VOID_ROBE_CHEST.get(),
                TCItems.VOID_ROBE_LEGS.get());

        event.registerItem(new CultistRobeArmorExtension(),
                TCItems.CRIMSON_ROBE_HELM.get(),
                TCItems.CRIMSON_ROBE_CHEST.get(),
                TCItems.CRIMSON_ROBE_LEGS.get());

        event.registerItem(new CultistPlateArmorExtension(),
                TCItems.CRIMSON_PLATE_HELM.get(),
                TCItems.CRIMSON_PLATE_CHEST.get(),
                TCItems.CRIMSON_PLATE_LEGS.get());

        event.registerItem(new CultistLeaderArmorExtension(),
                TCItems.CRIMSON_PRAETOR_HELM.get(),
                TCItems.CRIMSON_PRAETOR_CHEST.get(),
                TCItems.CRIMSON_PRAETOR_LEGS.get());
    }

    private static final class FortressArmorExtension implements IClientItemExtensions {
        private TCFortressArmorModel outerModel;
        private TCFortressArmorModel innerModel;

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
            if (slot == EquipmentSlot.LEGS) {
                if (innerModel == null) {
                    innerModel = new TCFortressArmorModel(TCFortressArmorModel.createInnerLayer().bakeRoot(), true);
                }
                return innerModel;
            }
            if (outerModel == null) {
                outerModel = new TCFortressArmorModel(TCFortressArmorModel.createOuterLayer().bakeRoot(), false);
            }
            return outerModel;
        }

        @Override
        public void setupModelAnimations(LivingEntity entity, ItemStack stack, EquipmentSlot slot, Model model,
                                         float limbSwing, float limbSwingAmount, float partialTick,
                                         float ageInTicks, float netHeadYaw, float headPitch) {
            if (model instanceof TCFortressArmorModel fortressModel) {
                fortressModel.setupFortressProperties(entity, stack, slot);
            }
        }
    }

    private static final class VoidRobeArmorExtension implements IClientItemExtensions {
        private TCRobeArmorModel outerModel;
        private TCRobeArmorModel innerModel;

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
            if (slot == EquipmentSlot.LEGS) {
                if (innerModel == null) {
                    innerModel = new TCRobeArmorModel(TCRobeArmorModel.createInnerLayer().bakeRoot(), true);
                }
                return innerModel;
            }
            if (outerModel == null) {
                outerModel = new TCRobeArmorModel(TCRobeArmorModel.createOuterLayer().bakeRoot(), false);
            }
            return outerModel;
        }

        @Override
        public void setupModelAnimations(LivingEntity entity, ItemStack stack, EquipmentSlot slot, Model model,
                                         float limbSwing, float limbSwingAmount, float partialTick,
                                         float ageInTicks, float netHeadYaw, float headPitch) {
            if (model instanceof TCRobeArmorModel robeModel) {
                robeModel.setupRobeAnimations(limbSwing, limbSwingAmount);
            }
        }
    }

    private static final class CultistRobeArmorExtension implements IClientItemExtensions {
        private TCRobeArmorModel outerModel;
        private TCRobeArmorModel innerModel;

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
            if (slot == EquipmentSlot.LEGS) {
                if (innerModel == null) {
                    innerModel = new TCRobeArmorModel(TCRobeArmorModel.createInnerLayer().bakeRoot(), true);
                }
                return innerModel;
            }
            if (outerModel == null) {
                outerModel = new TCRobeArmorModel(TCRobeArmorModel.createOuterLayer().bakeRoot(), false);
            }
            return outerModel;
        }

        @Override
        public void setupModelAnimations(LivingEntity entity, ItemStack stack, EquipmentSlot slot, Model model,
                                         float limbSwing, float limbSwingAmount, float partialTick,
                                         float ageInTicks, float netHeadYaw, float headPitch) {
            if (model instanceof TCRobeArmorModel robeModel) {
                robeModel.setupRobeAnimations(limbSwing, limbSwingAmount);
            }
        }
    }

    private static final class CultistPlateArmorExtension implements IClientItemExtensions {
        private TCKnightArmorModel outerModel;
        private TCKnightArmorModel innerModel;

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
            if (slot == EquipmentSlot.LEGS) {
                if (innerModel == null) {
                    innerModel = new TCKnightArmorModel(TCKnightArmorModel.createInnerLayer().bakeRoot(), true);
                }
                return innerModel;
            }
            if (outerModel == null) {
                outerModel = new TCKnightArmorModel(TCKnightArmorModel.createOuterLayer().bakeRoot(), false);
            }
            return outerModel;
        }

        @Override
        public void setupModelAnimations(LivingEntity entity, ItemStack stack, EquipmentSlot slot, Model model,
                                         float limbSwing, float limbSwingAmount, float partialTick,
                                         float ageInTicks, float netHeadYaw, float headPitch) {
            if (model instanceof TCKnightArmorModel knightModel) {
                knightModel.setupKnightAnimations(limbSwing, limbSwingAmount);
            }
        }
    }

    private static final class CultistLeaderArmorExtension implements IClientItemExtensions {
        private TCLeaderArmorModel outerModel;
        private TCLeaderArmorModel innerModel;

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
            if (slot == EquipmentSlot.LEGS) {
                if (innerModel == null) {
                    innerModel = new TCLeaderArmorModel(TCLeaderArmorModel.createInnerLayer().bakeRoot(), true);
                }
                return innerModel;
            }
            if (outerModel == null) {
                outerModel = new TCLeaderArmorModel(TCLeaderArmorModel.createOuterLayer().bakeRoot(), false);
            }
            return outerModel;
        }

        @Override
        public void setupModelAnimations(LivingEntity entity, ItemStack stack, EquipmentSlot slot, Model model,
                                         float limbSwing, float limbSwingAmount, float partialTick,
                                         float ageInTicks, float netHeadYaw, float headPitch) {
            if (model instanceof TCLeaderArmorModel leaderModel) {
                leaderModel.setupLeaderAnimations(limbSwing, limbSwingAmount);
            }
        }
    }
}
