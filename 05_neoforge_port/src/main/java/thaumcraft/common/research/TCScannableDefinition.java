package thaumcraft.common.research;

import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.research.ScanBlock;
import thaumcraft.api.research.ScanEntity;
import thaumcraft.api.research.ScanItem;
import thaumcraft.api.research.ScanOreDictionary;
import thaumcraft.api.research.ScanningManager;

sealed interface TCScannableDefinition permits TCScannableDefinition.ItemDefinition, TCScannableDefinition.BlockDefinition,
        TCScannableDefinition.EntityDefinition, TCScannableDefinition.OreDictionaryDefinition, TCScannableDefinition.TagDefinition {
    void register();

    default boolean matchesClientStack(ItemStack stack) {
        return false;
    }

    default boolean matchesClientBlock(BlockState state) {
        return false;
    }

    default boolean matchesClientEntity(Entity entity) {
        return false;
    }

    record ItemDefinition(String research, List<Item> items) implements TCScannableDefinition {
        public ItemDefinition {
            items = List.copyOf(items);
        }

        @Override
        public void register() {
            for (Item item : items) {
                ScanningManager.addScannableThing(new ScanItem(research, new ItemStack(item)));
            }
        }

        @Override
        public boolean matchesClientStack(ItemStack stack) {
            return !stack.isEmpty() && items.contains(stack.getItem());
        }
    }

    record BlockDefinition(String research, List<Block> blocks) implements TCScannableDefinition {
        public BlockDefinition {
            blocks = List.copyOf(blocks);
        }

        @Override
        public void register() {
            ScanningManager.addScannableThing(new ScanBlock(research, blocks.toArray(Block[]::new)));
        }

        @Override
        public boolean matchesClientStack(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            for (Block block : blocks) {
                if (stack.is(block.asItem())) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean matchesClientBlock(BlockState state) {
            return blocks.contains(state.getBlock());
        }
    }

    record EntityDefinition(String research, List<EntityType<?>> entityTypes) implements TCScannableDefinition {
        public EntityDefinition {
            entityTypes = List.copyOf(entityTypes);
        }

        @Override
        public void register() {
            for (EntityType<?> entityType : entityTypes) {
                ScanningManager.addScannableThing(new ScanEntity(research, entityType));
            }
        }

        @Override
        public boolean matchesClientEntity(Entity entity) {
            return entityTypes.contains(entity.getType());
        }
    }

    record OreDictionaryDefinition(String research, List<String> entries) implements TCScannableDefinition {
        public OreDictionaryDefinition {
            entries = List.copyOf(entries);
        }

        @Override
        public void register() {
            ScanningManager.addScannableThing(new ScanOreDictionary(research, entries.toArray(String[]::new)));
        }
    }

    record TagDefinition(String research, List<TagKey<Item>> itemTags, List<TagKey<Block>> blockTags) implements TCScannableDefinition {
        public TagDefinition {
            itemTags = List.copyOf(itemTags);
            blockTags = List.copyOf(blockTags);
        }

        @Override
        public void register() {
            ScanningManager.addScannableThing(new TCScanTag(research, itemTags, blockTags));
        }

        @Override
        public boolean matchesClientStack(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            for (TagKey<Item> tag : itemTags) {
                if (stack.is(tag)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean matchesClientBlock(BlockState state) {
            for (TagKey<Block> tag : blockTags) {
                if (state.is(tag)) {
                    return true;
                }
            }

            return false;
        }
    }

}
