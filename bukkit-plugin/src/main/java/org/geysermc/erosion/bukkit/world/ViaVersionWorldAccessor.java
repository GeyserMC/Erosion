package org.geysermc.erosion.bukkit.world;

import com.viaversion.viaversion.api.data.MappingData;
import org.bukkit.World;

import java.util.List;

public final class ViaVersionWorldAccessor implements WorldAccessor {
    private final WorldAccessor parent;
    private final List<MappingData> mappingData;

    public ViaVersionWorldAccessor(final WorldAccessor parent, final List<MappingData> mappingData) {
        this.parent = parent;
        this.mappingData = mappingData;
    }

    @Override
    public int getBlockAt(World world, int x, int y, int z) {
        int networkId = parent.getBlockAt(world, x, y, z);
        for (int i = mappingData.size() - 1; i >= 0; i--) {
            networkId = mappingData.get(i).getNewBlockStateId(networkId);
        }
        return networkId;
    }
}
