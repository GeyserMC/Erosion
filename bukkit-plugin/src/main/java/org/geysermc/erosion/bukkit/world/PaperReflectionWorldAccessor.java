package org.geysermc.erosion.bukkit.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.geysermc.erosion.bukkit.BukkitUtils;
import xyz.jpenilla.reflectionremapper.ReflectionRemapper;
import xyz.jpenilla.reflectionremapper.proxy.ReflectionProxyFactory;
import xyz.jpenilla.reflectionremapper.proxy.annotation.MethodName;
import xyz.jpenilla.reflectionremapper.proxy.annotation.Proxies;
import xyz.jpenilla.reflectionremapper.proxy.annotation.Static;
import xyz.jpenilla.reflectionremapper.proxy.annotation.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Used as a fallback that will *probably* work for new versions.
 */
public final class PaperReflectionWorldAccessor implements WorldAccessor {
    private final BlockProxy blockProxy;
    private final MethodHandle getBlockState;

    public PaperReflectionWorldAccessor() {
        ReflectionRemapper remapper = ReflectionRemapper.forReobfMappingsInPaperJar();
        ReflectionProxyFactory factory = ReflectionProxyFactory.create(remapper,
                getClass().getClassLoader());
        this.blockProxy = factory.reflectionProxy(BlockProxy.class);

        String craftBlockData = BukkitUtils.getCraftBukkitPackage() + ".block.CraftBlock";
        try {
            Class<?> blockDataClazz = Class.forName(craftBlockData);
            Method getBlockState = blockDataClazz.getMethod("getNMS");
            this.getBlockState = MethodHandles.lookup().unreflect(getBlockState);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
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
            return blockProxy.getBlockId(blockState);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Proxies(className = "net.minecraft.world.level.block.Block")
    public interface BlockProxy {

        @Static
        @MethodName("getId")
        int getBlockId(@Type(className = "net.minecraft.world.level.block.state.BlockState") Object o);
    }
}
