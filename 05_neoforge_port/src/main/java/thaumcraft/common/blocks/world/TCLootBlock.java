package thaumcraft.common.blocks.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.items.curios.TCLootBagItem;

public class TCLootBlock extends Block {
    public enum Variant {
        CRATE,
        URN
    }

    private static final VoxelShape CRATE_SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final VoxelShape URN_SHAPE = box(2.0D, 1.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    private final Variant variant;
    private final TCLootBagItem.Tier tier;

    public TCLootBlock(Variant variant, TCLootBagItem.Tier tier, BlockBehaviour.Properties properties) {
        super(properties);
        this.variant = variant;
        this.tier = tier;
    }

    public Variant getVariant() {
        return variant;
    }

    public TCLootBagItem.Tier getTier() {
        return tier;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return variant == Variant.URN ? URN_SHAPE : CRATE_SHAPE;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack tool = params.getOptionalParameter(LootContextParams.TOOL);
        ServerLevel level = params.getLevel();
        boolean hasSilkTouch = false;
        if (tool != null && !tool.isEmpty()) {
            var enchantmentRegistry = level.registryAccess().lookup(Registries.ENCHANTMENT);
            if (enchantmentRegistry.isPresent()) {
                var silkTouch = enchantmentRegistry.get().get(Enchantments.SILK_TOUCH);
                if (silkTouch.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(silkTouch.get(), tool) > 0) {
                    hasSilkTouch = true;
                }
            }
        }
        if (hasSilkTouch) {
            return List.of(new ItemStack(this));
        }
        int dropCount = 1 + tier.ordinal() + level.random.nextInt(3);
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < dropCount; i++) {
            ItemStack drop = TCLootBagItem.generateLoot(level, tier);
            if (!drop.isEmpty()) {
                drops.add(drop.copy());
            }
        }
        return drops;
    }
}
