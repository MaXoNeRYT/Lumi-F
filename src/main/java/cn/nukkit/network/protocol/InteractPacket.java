package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.BinaryStream;
import lombok.ToString;

@ToString
public class InteractPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INTERACT_PACKET;

    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;
    public static final int ACTION_OPEN_NPC = 5;
    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;
    /**
     * @since 898
     */
    private Vector3f mousePosition;

    @Override
    public void decode() {
        this.action = this.getByte();
        this.target = this.getEntityRuntimeId();
        if (protocol >= ProtocolInfo.v1_21_130) {
            this.mousePosition = this.getOptional(null, BinaryStream::getVector3f);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action);
        this.putEntityRuntimeId(this.target);
        if (protocol >= ProtocolInfo.v1_21_130) {
            this.putOptionalNull(this.mousePosition, BinaryStream::putVector3f);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
