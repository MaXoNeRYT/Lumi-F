package cn.nukkit.item;

import cn.nukkit.block.Block;

public abstract class ItemSign extends StringItemBase {

    protected ItemSign(String namespaceId, String name, int wallSignBlockId) {
        super(namespaceId, name);
        this.block = Block.get(wallSignBlockId);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}