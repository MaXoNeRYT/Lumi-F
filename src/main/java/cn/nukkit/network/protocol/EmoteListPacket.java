package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.exception.DataPacketDecodeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@ToString
public class EmoteListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.EMOTE_LIST_PACKET;

    public long runtimeId;
    public final List<UUID> pieceIds = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() throws DataPacketDecodeException {
        this.runtimeId = this.getEntityRuntimeId();
        int size = (int) this.getUnsignedVarInt();
        if (size > 15) {
            throw new DataPacketDecodeException("Too big EmoteListPacket: " + size);
        }
        for (int i = 0; i < size; i++) {
            try {
                UUID id = this.getUUID();
                pieceIds.add(id);
            } catch (IllegalArgumentException e) {
                throw new DataPacketDecodeException("Invalid UUID");
            }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(runtimeId);
        this.putUnsignedVarInt(pieceIds.size());
        for (UUID id : pieceIds) {
            this.putUUID(id);
        }
    }
}
