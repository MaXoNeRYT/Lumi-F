package cn.nukkit.recipe.descriptor;

import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.StringItem;
import cn.nukkit.utils.BinaryStream;

public class DefaultDescriptor extends ItemDescriptor {
    private final Item item;

    public DefaultDescriptor(Item item) {
        this.item = item.clone();
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean putRecipe(BinaryStream stream, int protocol) {
        try {
            int runtimeId = item.getId();
            int damage = item.hasMeta() ? item.getDamage() : Short.MAX_VALUE;

            RuntimeItemMapping mapping = RuntimeItems.getMapping(protocol);
            if (item instanceof StringItem) {
                runtimeId = mapping.getNetworkId(item);
            } else if (!item.hasMeta()) {
                RuntimeItemMapping.RuntimeEntry runtimeEntry = mapping.toRuntime(item.getId(), 0);
                runtimeId = runtimeEntry.getRuntimeId();
                damage = Short.MAX_VALUE;
            } else {
                RuntimeItemMapping.RuntimeEntry runtimeEntry = mapping.toRuntime(item.getId(), item.getDamage());
                runtimeId = runtimeEntry.getRuntimeId();
                damage = runtimeEntry.isHasDamage() ? 0 : item.getDamage();
            }

            if (item.getId() == 0) {
                if(stream != null) {
                    stream.putByte((byte) 0); //ItemDescriptorType.INVALID
                    stream.putVarInt(0); // item == null ? 0 : item.getCount()
                }
                return false;
            }

            if(stream != null) {
                stream.putByte((byte) 1); //ItemDescriptorType.DEFAULT

                stream.putLShort(runtimeId);
                stream.putLShort(damage);
                stream.putVarInt(item.getCount());
            }
        } catch (IllegalArgumentException e) {
            if(stream != null) {
                stream.putByte((byte) 0); //ItemDescriptorType.INVALID
                stream.putVarInt(0); // item == null ? 0 : item.getCount()
            }
            return false;
        }

        return true;
    }
}
