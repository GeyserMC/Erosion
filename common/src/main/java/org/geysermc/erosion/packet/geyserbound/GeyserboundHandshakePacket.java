package org.geysermc.erosion.packet.geyserbound;

import org.cloudburstmc.protocol.common.util.VarInts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.unix.DomainSocketAddress;
import org.geysermc.erosion.Constants;
import org.geysermc.erosion.packet.ProtocolUtils;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GeyserboundHandshakePacket implements GeyserboundPacket {
    private final int version;
    private final TransportType transportType;
    private final List<String> capabilities;

    public GeyserboundHandshakePacket(String address) {
        this.version = Constants.VERSION;
        if (address == null) {
            this.transportType = new TcpTransportType();
        } else {
            this.transportType = new UnixDomainTransportType(address);
        }
        this.capabilities = Collections.emptyList();
    }

    @Override
    public void serialize(ByteBuf buf) {
        VarInts.writeUnsignedInt(buf, Constants.VERSION);
        buf.writeByte(this.transportType.getId());
        this.transportType.write(buf);

        VarInts.writeUnsignedInt(buf, this.capabilities.size());
        for (int i = 0; i < this.capabilities.size(); i++) {
            ProtocolUtils.writeString(buf, this.capabilities.get(i));
        }
    }

    public GeyserboundHandshakePacket(ByteBuf buf) {
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

    @Override
    public void handle(GeyserboundPacketHandler packetHandler) {
        packetHandler.handleHandshake(this);
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
