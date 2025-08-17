package cn.nukkit.block.material.tags;

import cn.nukkit.block.material.BlockType;
import cn.nukkit.block.material.tags.impl.SimpleBlockTag;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.Set;

public interface BlockTag {

    Set<BlockType> getBlockTypes();

    default boolean has(BlockType blockType) {
        return this.getBlockTypes().contains(blockType);
    }

    default BlockTag copyWith(BlockType... blockTypes) {
        Set<BlockType> newBlockTypes = new ObjectOpenHashSet<>(this.getBlockTypes());
        newBlockTypes.addAll(Arrays.asList(blockTypes));
        return new SimpleBlockTag(newBlockTypes);
    }

    static BlockTag of(BlockType... blockTypes) {
        return new SimpleBlockTag(blockTypes);
    }
}
