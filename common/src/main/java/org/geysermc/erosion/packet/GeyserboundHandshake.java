package org.geysermc.erosion.packet;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.unix.DomainSocketAddress;
import org.geysermc.erosion.Constants;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public final class GeyserboundHandshake {
    private final int version;
    private final TransportType transportType;
    private final List<String> capabilities;

    public static ByteBuf create() {
        ByteBuf buf = Unpooled.buffer();
        VarInts.writeUnsignedInt(buf, Constants.VERSION);
        buf.writeByte(0);
        TransportType transportType = new UnixDomainTransportType("/tmp/geyser.sock");
        transportType.write(buf);
        VarInts.writeUnsignedInt(buf, 0);
        return buf;
    }

    public GeyserboundHandshake(ByteBuf buf) {
        this.version = VarInts.readUnsignedInt(buf);
        int transportType = buf.readByte();
        if (transportType == 0) {
            this.transportType = new UnixDomainTransportType(buf);
        } else {
            throw new IllegalArgumentException();
        }
        int size = VarInts.readUnsignedInt(buf);
        this.capabilities = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            capabilities.add(ProtocolUtils.readString(buf));
        }
    }

    public int getVersion() {
        return version;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public interface TransportType {
        SocketAddress getSocketAddress();

        void write(ByteBuf buf);
    }

    public static final class UnixDomainTransportType implements TransportType {
        private final String address;

        public UnixDomainTransportType(ByteBuf buf) {
            this.address = ProtocolUtils.readString(buf);
        }

        public UnixDomainTransportType(String address) {
            this.address = address;
        }

        @Override
        public SocketAddress getSocketAddress() {
            return new DomainSocketAddress(address);
        }

        @Override
        public void write(ByteBuf buf) {
            ProtocolUtils.writeString(buf, address);
        }
    }
}
