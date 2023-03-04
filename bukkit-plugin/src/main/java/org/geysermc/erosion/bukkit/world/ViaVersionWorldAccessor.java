package org.geysermc.erosion.bukkit.world;

import com.viaversion.viaversion.api.data.MappingData;
import org.bukkit.entity.Player;

import java.util.List;

public final class ViaVersionWorldAccessor implements WorldAccessor {
    private final WorldAccessor parent;
    private final List<MappingData> mappingData;

    public ViaVersionWorldAccessor(final WorldAccessor parent, final List<MappingData> mappingData) {
        this.parent = parent;
        this.mappingData = mappingData;
    }

    @Override
    public int getBlockAt(Player player, int x, int y, int z) {
        int networkId = parent.getBlockAt(player, x, y, z);
        for (int i = mappingData.size() - 1; i >= 0; i--) {
            networkId = mappingData.get(i).getNewBlockStateId(networkId);
        }
        return networkId;
    }

    @Override
    public String getLoggedName() {
        return WorldAccessor.super.getLoggedName() + "[" + this.parent.getLoggedName() + "]";
    }
}
