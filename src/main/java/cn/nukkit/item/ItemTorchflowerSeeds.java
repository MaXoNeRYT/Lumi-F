package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.network.protocol.ProtocolInfo;

public class ItemTorchflowerSeeds extends StringItemBase {

    public ItemTorchflowerSeeds() {
        super(TORCHFLOWER_SEEDS, "Torchflower Seeds");
        this.block = Block.get(Block.TORCHFLOWER_CROP);
    }

    @Override
    public boolean isSupportedOn(int protocolId) {
        return protocolId >= ProtocolInfo.v1_20_0;
    }
}