package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimMaterialType;
import cn.nukkit.network.protocol.ProtocolInfo;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemAmethystShard extends StringItemBase implements ItemTrimMaterial {

    public ItemAmethystShard() {
        super(ItemNamespaceId.AMETHYST_SHARD, "Amethyst Shard");
    }

    @Override
    public ItemTrimMaterialType getMaterial() {
        return ItemTrimMaterialType.MATERIAL_AMETHYST;
    }
}
