package cn.nukkit.block.customblock.serializer.impl;

import cn.nukkit.block.customblock.serializer.CustomBlockDefinitionSerializer;
import cn.nukkit.nbt.tag.CompoundTag;

//1.21.110
public class CustomBlockDefinitionSerializer843 extends CustomBlockDefinitionSerializer {
    public static final CustomBlockDefinitionSerializer843 INSTANCE = new CustomBlockDefinitionSerializer843();

    @Override
    protected void reSerializeMaterials(CompoundTag nbt) {
        nbt.getCompound("components").getCompound("minecraft:material_instances").getCompound("materials").getAllTags().forEach(tag -> {
            CompoundTag compoundTag = (CompoundTag) tag;
            boolean faceDimming = compoundTag.getBoolean("face_dimming");
            compoundTag.remove("face_dimming");
            compoundTag.putByte("packed_bools", faceDimming ? 0x1 : 0x0);
        });
    }
}
