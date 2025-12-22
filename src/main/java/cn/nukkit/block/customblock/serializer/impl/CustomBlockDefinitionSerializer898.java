package cn.nukkit.block.customblock.serializer.impl;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public class CustomBlockDefinitionSerializer898 extends CustomBlockDefinitionSerializer843 {
    public static final CustomBlockDefinitionSerializer898 INSTANCE = new CustomBlockDefinitionSerializer898();

    @Override
    protected void reSerializeCollisionBox(CompoundTag nbt) {
        CompoundTag compoundTag = nbt.getCompound("components").getCompound("minecraft:collision_box");
        ListTag<FloatTag> origin = compoundTag.getList("origin", FloatTag.class);
        ListTag<FloatTag> size = compoundTag.getList("size", FloatTag.class);
        compoundTag.remove("origin");
        compoundTag.remove("size");

        CompoundTag box = new CompoundTag();
        if(!origin.isEmpty()) {
            box.putFloat("minX", origin.get(0).parseValue() + 8.0f);
            box.putFloat("minY", origin.get(1).parseValue());
            box.putFloat("minZ", origin.get(2).parseValue() + 8.0f);
        }

        if(!size.isEmpty()) {
            box.putFloat("maxX", size.get(0).parseValue());
            box.putFloat("maxY", size.get(1).parseValue());
            box.putFloat("maxZ", size.get(2).parseValue());
        }

        compoundTag.putList("boxes", new ListTag<CompoundTag>().add(box));
    }
}
