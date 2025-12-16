package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.network.protocol.ProtocolInfo;

public class ItemDoorMangrove extends StringItemBase {

    public ItemDoorMangrove() {
        super(MANGROVE_DOOR, "Mangrove Door");
        block = Block.get(MANGROVE_DOOR_BLOCK);
    }
}
