package org.geysermc.erosion.bukkit;

import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lectern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.geysermc.erosion.util.LecternUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BukkitLecterns {
    private final Plugin plugin;

    public BukkitLecterns(Plugin plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public NbtMap getLecternData(Block block, boolean isChunkLoad) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        BlockState state = BukkitUtils.getBlockState(block);
        if (!(state instanceof Lectern lectern)) {
            this.plugin.getLogger().warning("Lectern expected at: " + Vector3i.from(x, y, z).toString() + " but was not! " + block.toString());
            return null;
        }

        ItemStack itemStack = lectern.getInventory().getItem(0);
        if (itemStack == null || !(itemStack.getItemMeta() instanceof BookMeta bookMeta)) {
            if (!isChunkLoad) {
                // We need to update the lectern since it's not going to be updated otherwise
                return LecternUtils.getBaseLecternTag(x, y, z, 0).build();
            }
            // We don't care; return
            return null;
        }

        // On the count: allow the book to show/open even there are no pages. We know there is a book here, after all, and this matches Java behavior
        boolean hasBookPages = bookMeta.getPageCount() > 0;
        NbtMapBuilder lecternTag = LecternUtils.getBaseLecternTag(x, y, z, hasBookPages ? bookMeta.getPageCount() : 1);
        lecternTag.putInt("page", lectern.getPage() / 2);
        NbtMapBuilder bookTag = NbtMap.builder()
                .putByte("Count", (byte) itemStack.getAmount())
                .putShort("Damage", (short) 0)
                .putString("Name", "minecraft:writable_book");
        List<NbtMap> pages = new ArrayList<>(bookMeta.getPageCount());
        if (hasBookPages) {
            for (String page : bookMeta.getPages()) {
                NbtMapBuilder pageBuilder = NbtMap.builder()
                        .putString("photoname", "")
                        .putString("text", page);
                pages.add(pageBuilder.build());
            }
        } else {
            // Empty page
            NbtMapBuilder pageBuilder = NbtMap.builder()
                    .putString("photoname", "")
                    .putString("text", "");
            pages.add(pageBuilder.build());
        }

        bookTag.putCompound("tag", NbtMap.builder().putList("pages", NbtType.COMPOUND, pages).build());
        lecternTag.putCompound("book", bookTag.build());
        return lecternTag.build();
    }
}
