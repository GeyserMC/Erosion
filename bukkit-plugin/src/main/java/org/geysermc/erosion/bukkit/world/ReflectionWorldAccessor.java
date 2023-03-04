package org.geysermc.erosion.bukkit.world;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.geysermc.erosion.bukkit.BukkitUtils;
import org.geysermc.erosion.bukkit.ErosionBukkitUtils;
import org.geysermc.erosion.util.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class ReflectionWorldAccessor implements WorldAccessor {
    private final Method getBlockId;
    private final MethodHandle getBlockState;

    public ReflectionWorldAccessor() {
        try {
            Method getBlockState = ErosionBukkitUtils.findBlockNmsMethod();
            Class<?> blockClass = ReflectionUtils.getClassOrFallback("net.minecraft.world.level.block.Block",
                    BukkitUtils.getLegacyNmsPackage() + ".Block");
            this.getBlockId = Arrays.stream(blockClass.getMethods())
                    .filter(method -> Modifier.isStatic(method.getModifiers()))
                    .filter(method -> method.getReturnType() == int.class)
                    .filter(method -> {
                        Class<?>[] parameters = method.getParameterTypes();
                        return parameters.length == 1 && parameters[0] == getBlockState.getReturnType();
                    })
                    .findAny()
                    .orElseThrow(RuntimeException::new);
            this.getBlockState = MethodHandles.lookup().unreflect(getBlockState);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getBlockAt(Player player, int x, int y, int z) {
        World world = player.getWorld();
        if (!world.isChunkLoaded(x >> 4, z >> 4)) {
            return 0;
        }

        Block block = world.getBlockAt(x, y, z);
        try {
            Object blockState = this.getBlockState.bindTo(block)
                    .invoke();
            return (int) this.getBlockId.invoke(null, blockState);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
