package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

import java.io.IOException;

@ToString(exclude = "namedtag")
public class UpdateEquipmentPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_EQUIPMENT_PACKET;
    public int windowId;
    public int windowType;
    public int size = 0;
    public long eid;
    public CompoundTag namedtag;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {}

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
        this.putByte((byte) this.windowType);
        this.putVarInt(this.size);
        this.putEntityUniqueId(this.eid);

        try {
            this.put(NBTIO.writeNetwork(namedtag));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
