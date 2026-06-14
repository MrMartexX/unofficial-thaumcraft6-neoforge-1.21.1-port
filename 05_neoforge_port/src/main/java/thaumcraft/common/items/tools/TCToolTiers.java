package thaumcraft.common.items.tools;

import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import thaumcraft.common.registry.TCItems;

public enum TCToolTiers implements Tier {
    THAUMIUM(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            500,
            7.0F,
            2.5F,
            22,
            () -> Ingredient.of(TCItems.THAUMIUM_INGOT.get())
    ),
    VOID(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            150,
            8.0F,
            3.0F,
            10,
            () -> Ingredient.of(TCItems.VOID_METAL_INGOT.get())
    ),
    ELEMENTAL(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            1500,
            9.0F,
            3.0F,
            18,
            () -> Ingredient.of(TCItems.THAUMIUM_INGOT.get())
    ),
    PRIMAL_VOID(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            500,
            8.0F,
            4.0F,
            20,
            () -> Ingredient.of(TCItems.VOID_METAL_INGOT.get())
    );

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    TCToolTiers(
            TagKey<Block> incorrectBlocksForDrops,
            int uses,
            float speed,
            float attackDamageBonus,
            int enchantmentValue,
            Supplier<Ingredient> repairIngredient
    ) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
