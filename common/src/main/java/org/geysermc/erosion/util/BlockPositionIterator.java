package org.geysermc.erosion.util;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.common.util.Preconditions;
import org.cloudburstmc.protocol.common.util.VarInts;

public final class BlockPositionIterator {
    private final int minX;
    private final int minY;
    private final int minZ;

    private final int sizeX;
    private final int sizeZ;

    private int i = 0;
    private final int maxI;

    private BlockPositionIterator(int minX, int minY, int minZ, int sizeX, int sizeZ, int maxI) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        this.maxI = maxI;
    }

    public BlockPositionIterator(ByteBuf buf) {
        this.minX = VarInts.readInt(buf);
        this.minY = VarInts.readInt(buf);
        this.minZ = VarInts.readInt(buf);
        this.sizeX = VarInts.readInt(buf);
        this.sizeZ = VarInts.readInt(buf);
        this.maxI = VarInts.readInt(buf);
    }

    public static BlockPositionIterator fromMinMax(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Preconditions.checkArgument(maxX >= minX, "maxX is not greater than or equal to minX");
        Preconditions.checkArgument(maxY >= minY, "maxY is not greater than or equal to minY");
        Preconditions.checkArgument(maxZ >= minZ, "maxZ is not greater than or equal to minZ");

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;
        int maxI = sizeX * sizeY * sizeZ;
        return new BlockPositionIterator(minX, minY, minZ, sizeX, sizeZ, maxI);
    }

    public boolean hasNext() {
        return i < maxI;
    }

    public void next() {
        // Iterate in zxy order
        i++;
    }

    public void reset() {
        i = 0;
    }

    public int getX() {
        return ((i / sizeZ) % sizeX) + minX;
    }

    public int getY() {
        return (i / sizeZ / sizeX) + minY;
    }

    public int getZ() {
        return (i % sizeZ) + minZ;
    }

    public int getIteration() {
        return i;
    }

    public int getMaxIterations() {
        return maxI;
    }

    /**
     * Get the index of a position
     *
     * @return the index, or -1 if out of bounds
     */
    public int getIndex(int x, int y, int z) {
        int offsetX = x - minX;
        int offsetY = y - minY;
        int offsetZ = z - minZ;

        if (y >= minY && offsetY < maxI / (sizeX * sizeZ)
                && x >= minX && offsetX < sizeX
                && z >= minZ && offsetZ < sizeZ) {
            return offsetZ + (offsetX * sizeZ) + (offsetY * sizeX * sizeZ);
        }

        return -1;
    }

    public void serialize(ByteBuf buf) {
        VarInts.writeInt(buf, this.minX);
        VarInts.writeInt(buf, this.minY);
        VarInts.writeInt(buf, this.minZ);
        VarInts.writeInt(buf, this.sizeX);
        VarInts.writeInt(buf, this.sizeZ);
        VarInts.writeInt(buf, this.maxI);
    }

    @Override
    public String toString() {
        return "BlockPositionIterator{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                ", sizeX=" + sizeX +
                ", sizeZ=" + sizeZ +
                ", i=" + i +
                ", maxI=" + maxI +
                '}';
    }
}
