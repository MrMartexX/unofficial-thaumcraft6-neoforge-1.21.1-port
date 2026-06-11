package thaumcraft.common.research;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

final class TCScanTag implements IScanThing {
    private final String research;
    private final List<TagKey<Item>> itemTags;
    private final List<TagKey<Block>> blockTags;

    TCScanTag(String research, List<TagKey<Item>> itemTags, List<TagKey<Block>> blockTags) {
        this.research = research;
        this.itemTags = List.copyOf(itemTags);
        this.blockTags = List.copyOf(blockTags);
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
}
