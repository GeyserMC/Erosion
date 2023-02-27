package org.geysermc.erosion.packet;

import com.nukkitx.network.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.unix.DomainSocketAddress;
import org.geysermc.erosion.Constants;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public final class GeyserboundHandshake {
    private final int version;
    private final TransportType transportType;
    private final List<String> capabilities;

    public static ByteBuf create(String address) {
        ByteBuf buf = Unpooled.buffer();
        VarInts.writeUnsignedInt(buf, Constants.VERSION);
        TransportType transportType;
        if (address == null) {
            transportType = new TcpTransportType();
        } else {
            transportType = new UnixDomainTransportType(address);
        }
        buf.writeByte(transportType.getId());
        transportType.write(buf);

        VarInts.writeUnsignedInt(buf, 0); // Capabilities
        return buf;
    }

    public GeyserboundHandshake(ByteBuf buf) {
        this.version = VarInts.readUnsignedInt(buf);
        int transportType = buf.readByte();
        if (transportType == 0) {
            this.transportType = new UnixDomainTransportType(buf);
        } else if (transportType == 1) {
            this.transportType = new TcpTransportType();
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
        @Nullable
        SocketAddress getSocketAddress();

        int getId();

        void write(ByteBuf buf);
    }

    public static final class TcpTransportType implements TransportType {
        @Override
        public SocketAddress getSocketAddress() {
            return null;
        }

        @Override
        public int getId() {
            return 1;
        }

        @Override
        public void write(ByteBuf buf) {
        }
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
        public int getId() {
            return 0;
        }

        @Override
        public void write(ByteBuf buf) {
            ProtocolUtils.writeString(buf, address);
        }
    }
}
