package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.network.protocol.ProtocolInfo;

public class ItemDoorPaleOak extends StringItemBase {

    public ItemDoorPaleOak() {
        super(ItemNamespaceId.PALE_OAK_DOOR, "Pale Oak Door");
        block = Block.get(BlockID.PALE_OAK_DOOR);
    }

    @Override
    public boolean isSupportedOn(int protocolId) {
        return protocolId >= ProtocolInfo.v1_21_50;
    }
}