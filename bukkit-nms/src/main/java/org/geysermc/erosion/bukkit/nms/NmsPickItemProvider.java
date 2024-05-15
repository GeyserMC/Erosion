package org.geysermc.erosion.bukkit.nms;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.geysermc.erosion.bukkit.PickItemProvider;

@SuppressWarnings("unused")
public class NmsPickItemProvider implements PickItemProvider {

    @Override
    public Int2ObjectMap<byte[]> getPickItem(final Block block) {
        final CraftBlock craftBlock = (CraftBlock) block;
        final BlockState state = craftBlock.getNMS();
        final ServerLevel level = craftBlock.getCraftWorld().getHandle();
        final ItemStack stack = state.getBlock().getCloneItemStack(level, craftBlock.getPosition(), state);

        final Int2ObjectMap<byte[]> components = new Int2ObjectOpenHashMap<>();
        final var decorator = RegistryFriendlyByteBuf.decorator(level.registryAccess());
        for (final var entry : stack.getComponentsPatch().entrySet()) {
            final var key = entry.getKey();
            if (key == DataComponents.CUSTOM_DATA || key == DataComponents.CONTAINER
                    || key == DataComponents.CONTAINER_LOOT || key == DataComponents.BUNDLE_CONTENTS) {
                // Purely safeguards to not leak data.
                continue;
            }

            entry.getValue().ifPresent(value -> {
                RegistryFriendlyByteBuf buf = decorator.apply(Unpooled.buffer());
                encodeComponent(buf, key, value);
                components.put(BuiltInRegistries.DATA_COMPONENT_TYPE.getId(key), ByteBufUtil.getBytes(buf));
            });
        }

        return components;
    }

    // genericsssssss this is a Mojmap method
    private static <T> void encodeComponent(RegistryFriendlyByteBuf buf, DataComponentType<T> type, Object value) {
        type.streamCodec().encode(buf, (T) value);
    }
}
