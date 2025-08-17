package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.network.protocol.ProtocolInfo;

public class ItemPitcherPod extends StringItemBase {

    public ItemPitcherPod() {
        super(PITCHER_POD, "Pitcher Pod");
        this.block = Block.get(Block.PITCHER_CROP);
    }

    @Override
    public boolean isSupportedOn(int protocolId) {
        return protocolId >= ProtocolInfo.v1_20_0;
    }
}