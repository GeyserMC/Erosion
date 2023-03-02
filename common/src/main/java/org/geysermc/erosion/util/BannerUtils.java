package org.geysermc.erosion.util;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;

public final class BannerUtils {

    public static CompoundTag getJavaPatternTag(String pattern, int color) {
        StringTag patternType = new StringTag("Pattern", pattern);
        IntTag colorTag = new IntTag("Color", color);
        CompoundTag tag = new CompoundTag("");
        tag.put(patternType);
        tag.put(colorTag);
        return tag;
    }

    private BannerUtils() {
    }
}
