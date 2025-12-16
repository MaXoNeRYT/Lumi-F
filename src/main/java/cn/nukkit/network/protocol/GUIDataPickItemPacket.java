package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class GUIDataPickItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;

    public String description;
    public String itemEffects;
    public int hotbarSlot;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.description);
        this.putString(this.itemEffects);
        this.putLInt(this.hotbarSlot);
    }

    @Override
    public void decode() {
        this.description = this.getString();
        this.itemEffects = this.getString();
        this.hotbarSlot = this.getLInt();
    }
}
