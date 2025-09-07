package cn.nukkit.blockentity.impl;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityMusic extends BlockEntity {

    public BlockEntityMusic(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!this.namedTag.contains("note")) {
            this.namedTag.putByte("note", 0);
        }

        if (!this.namedTag.contains("powered")) {
            this.namedTag.putBoolean("note", false);
        }

        super.initBlockEntity();
    }

    public void changePitch() {
        this.namedTag.putByte("note", (this.namedTag.getByte("note") + 1) % 25);
    }

    public int getPitch() {
        return this.namedTag.getByte("note");
    }

    public boolean isPowered() {
        return this.namedTag.getBoolean("powered");
    }

    public void setPowered(boolean powered) {
        this.namedTag.putBoolean("powered", powered);
    }
}
