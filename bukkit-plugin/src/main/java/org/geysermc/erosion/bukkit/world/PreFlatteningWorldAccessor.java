package org.geysermc.erosion.bukkit.world;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PreFlatteningWorldAccessor implements WorldAccessor {
    private final MappingData flattenMappings;
    private final List<MappingData> mappingData;

    public PreFlatteningWorldAccessor() {
        this.flattenMappings = Via.getManager().getProtocolManager()
                .getProtocol(Protocol1_13To1_12_2.class)
                .getMappingData();
        // Do not include 1.12 - we'll do that one manually
        this.mappingData = Via.getManager().getProtocolManager()
                .getProtocolPath(ProtocolVersion.v1_19_3.getVersion(),
                        ProtocolVersion.v1_13.getVersion())
                .stream()
                .map(entry -> entry.protocol().getMappingData())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getBlockAt(Player player, int x, int y, int z) {
        Block block = player.getWorld().getBlockAt(x, y, z);
        int networkId = (block.getType().getId() << 4) | (block.getData() & 0xF);
        networkId = flattenMappings.getNewBlockId(networkId);

        // Translate block entity differences - some information was stored in block tags and not block states
        BlockStorage storage = Via.getManager().getConnectionManager().getConnectedClient(player.getUniqueId())
                .get(BlockStorage.class);
        if (storage.isWelcome(networkId)) {
            BlockStorage.ReplacementData data = storage.get(new Position(x, y, z));
            if (data != null && data.getReplacement() != -1) {
                networkId = data.getReplacement();
            }
        }

        for (int i = mappingData.size() - 1; i >= 0; i--) {
            networkId = mappingData.get(i).getNewBlockStateId(networkId);
        }
        return networkId;
    }
}
