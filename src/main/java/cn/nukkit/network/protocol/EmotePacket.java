package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.exception.DataPacketDecodeException;
import lombok.ToString;

import java.util.UUID;

@ToString
public class EmotePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.EMOTE_PACKET;

    public static final int FLAG_SERVER = 0b1;
    public static final int FLAG_MUTE_ANNOUNCEMENT = 0b10;

    public long runtimeId;
    /**
     * @since v588
     */
    public String xuid;
    /**
     * @since 588
     */
    public String platformId;
    public UUID emoteID;
    public byte flags;
    /**
     * @since v729
     */
    public long emoteDuration;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() throws DataPacketDecodeException {
        this.runtimeId = this.getEntityRuntimeId();

        String id = this.getString();

        try {
            UUID uuid = UUID.fromString(id);
            if(uuid.version() != 4) {
                throw new DataPacketDecodeException("Invalid UUID Version: " + this.emoteID);
            }

            this.emoteID = uuid;
        } catch (IllegalArgumentException e) {
            throw new DataPacketDecodeException("Invalid UUID: " + this.emoteID);
        }

        if (this.protocol >= ProtocolInfo.v1_20_0_23) {
            if (this.protocol >= ProtocolInfo.v1_21_30) {
                this.emoteDuration = this.getUnsignedVarInt();
            }
            this.xuid = this.getString();
            this.platformId = this.getString();
        }
        this.flags = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.runtimeId);
        this.putUUID(this.emoteID);
        if (this.protocol >= ProtocolInfo.v1_20_0_23) {
            if (this.protocol >= ProtocolInfo.v1_21_30) {
                this.putUnsignedVarInt(this.emoteDuration);
            }
            this.putString(this.xuid != null ? this.xuid : "");
            this.putString(this.platformId != null ? this.platformId : "");
        }
        this.putByte(flags);
    }
}
