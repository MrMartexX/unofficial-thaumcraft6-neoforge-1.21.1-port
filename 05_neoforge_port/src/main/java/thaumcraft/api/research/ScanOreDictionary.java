package thaumcraft.api.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.Thaumcraft;

public class ScanOreDictionary implements IScanThing {
    private final String research;
    private final List<TagKey<Item>> itemTags;
    private final List<TagKey<Block>> blockTags;

    public ScanOreDictionary(String research, String... entries) {
        this.research = research;
        this.itemTags = new ArrayList<>();
        this.blockTags = new ArrayList<>();

        if (entries != null) {
            for (String entry : entries) {
                addEntry(entry);
            }
        }
    }

    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        ItemStack stack = ScanningManager.getItemFromParams(player, object);

        if (!stack.isEmpty()) {
            for (TagKey<Item> tag : itemTags) {
                if (stack.is(tag)) {
                    return true;
                }
            }
        }

        if (object instanceof BlockPos pos) {
            BlockState state = player.level().getBlockState(pos);
            for (TagKey<Block> tag : blockTags) {
                if (state.is(tag)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return research;
    }

    private void addEntry(String entry) {
        if (entry == null || entry.isBlank()) {
            return;
        }

        String commonPath = commonTagPath(entry);
        if (commonPath != null) {
            itemTags.add(itemTag("c", commonPath));
            blockTags.add(blockTag("c", commonPath));
        }

        String legacyPath = "legacy_ore_dictionary/" + camelToSnake(entry);
        itemTags.add(itemTag(Thaumcraft.MODID, legacyPath));
        blockTags.add(blockTag(Thaumcraft.MODID, legacyPath));
    }

    private static String commonTagPath(String entry) {
        String lower = entry.toLowerCase(Locale.ROOT);

        if (lower.startsWith("ore") && entry.length() > 3) {
            return "ores/" + camelToSnake(entry.substring(3));
        }

        if (lower.startsWith("ingot") && entry.length() > 5) {
            return "ingots/" + camelToSnake(entry.substring(5));
        }

        if (lower.startsWith("block") && entry.length() > 5) {
            return "storage_blocks/" + camelToSnake(entry.substring(5));
        }

        if (lower.startsWith("plate") && entry.length() > 5) {
            return "plates/" + camelToSnake(entry.substring(5));
        }

        if (lower.startsWith("gem") && entry.length() > 3) {
            return "gems/" + camelToSnake(entry.substring(3));
        }

        if (lower.startsWith("dust") && entry.length() > 4) {
            return "dusts/" + camelToSnake(entry.substring(4));
        }

        if (lower.startsWith("nugget") && entry.length() > 6) {
            return "nuggets/" + camelToSnake(entry.substring(6));
        }

        return null;
    }

    private static String camelToSnake(String value) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isUpperCase(ch) && builder.length() > 0) {
                builder.append('_');
            }
            builder.append(Character.toLowerCase(ch));
        }

        return builder.toString();
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static TagKey<Block> blockTag(String namespace, String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
