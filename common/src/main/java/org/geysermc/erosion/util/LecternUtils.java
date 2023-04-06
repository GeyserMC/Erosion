package org.geysermc.erosion.util;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

public final class LecternUtils {

    public static NbtMapBuilder getBaseLecternTag(int x, int y, int z, int totalPages) {
        NbtMapBuilder builder = NbtMap.builder()
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z)
                .putString("id", "Lectern");
        if (totalPages != 0) {
            builder.putByte("hasBook", (byte) 1);
            builder.putInt("totalPages", totalPages);
        } else {
            // Not usually needed, but helps with kicking out Bedrock players from reading the UI
            builder.putByte("hasBook", (byte) 0);
        }
        return builder;
    }

    private LecternUtils() {
    }
}
