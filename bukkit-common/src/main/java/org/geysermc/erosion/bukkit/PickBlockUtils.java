package org.geysermc.erosion.bukkit;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.geysermc.erosion.util.BannerUtils;

public final class PickBlockUtils {

    public static CompoundTag pickBlock(Player player, int x, int y, int z) {
        Block block = player.getWorld().getBlockAt(x, y, z);
        BlockState state = BukkitUtils.getBlockState(block);
        if (state instanceof Banner) {
            Banner banner = (Banner) state;
            ListTag list = new ListTag("Patterns");
            for (int i = 0; i < banner.numberOfPatterns(); i++) {
                Pattern pattern = banner.getPattern(i);
                list.add(BannerUtils.getJavaPatternTag(pattern.getPattern().getIdentifier(), pattern.getColor().ordinal()));
            }

            return addToBlockEntityTag(list);
        }
        return null;
    }

    private static CompoundTag addToBlockEntityTag(Tag tag) {
        CompoundTag compoundTag = new CompoundTag("");
        CompoundTag blockEntityTag = new CompoundTag("BlockEntityTag");
        blockEntityTag.put(tag);
        compoundTag.put(blockEntityTag);
        return compoundTag;
    }

    private PickBlockUtils() {
    }
}
