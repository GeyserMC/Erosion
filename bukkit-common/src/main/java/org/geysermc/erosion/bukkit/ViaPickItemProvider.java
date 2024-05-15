package org.geysermc.erosion.bukkit;

import com.viaversion.viaversion.api.minecraft.Holder;
import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey;
import com.viaversion.viaversion.api.minecraft.item.data.BannerPatternLayer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_20_5to1_20_3.Protocol1_20_5To1_20_3;
import com.viaversion.viaversion.protocols.protocol1_20_5to1_20_3.data.BannerPatterns1_20_5;
import com.viaversion.viaversion.util.SerializerVersion;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;

public class ViaPickItemProvider implements PickItemProvider {

    @Override
    public Int2ObjectMap<byte[]> getPickItem(final Block block) {
        try {
            final BlockState state = BukkitUtils.getBlockState(block);
            if (!(state instanceof final Banner banner)) {
                return null;
            }

            final var mappings = Protocol1_20_5To1_20_3.MAPPINGS.getDataComponentSerializerMappings();

            Int2ObjectMap<byte[]> components = new Int2ObjectOpenHashMap<>();

            final BannerPatternLayer[] layers = new BannerPatternLayer[banner.numberOfPatterns()];
            for (int i = 0; i < banner.numberOfPatterns(); i++) {
                final Pattern pattern = banner.getPattern(i);
                final String identifier = BannerPatterns1_20_5.compactToFullId(pattern.getPattern().getIdentifier());
                layers[i] = new BannerPatternLayer(Holder.of(BannerPatterns1_20_5.keyToId(identifier)), pattern.getColor().ordinal());
            }
            ByteBuf buf = Unpooled.buffer();
            BannerPatternLayer.ARRAY_TYPE.write(buf, layers);
            components.put(mappings.id(StructuredDataKey.BANNER_PATTERNS.identifier()), ByteBufUtil.getBytes(buf));

            String name = banner.getCustomName();
            if (name != null && !name.isBlank()) {
                buf = Unpooled.buffer();
                Tag tag = SerializerVersion.V1_20_5.toTag(SerializerVersion.V1_20_5.toComponent(name));
                Type.TAG.write(buf, tag);
                components.put(mappings.id(StructuredDataKey.CUSTOM_NAME.identifier()), ByteBufUtil.getBytes(buf));
            }

            return components;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
