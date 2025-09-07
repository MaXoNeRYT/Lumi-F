package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNamespaceId;
import cn.nukkit.math.BlockFace;

import java.util.*;

public class BlockGlowLichen extends BlockLichen {
    public BlockGlowLichen() {
        this(0);
    }

    public BlockGlowLichen(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public int getId() {
        return GLOW_LICHEN;
    }

    @Override
    public boolean onActivate(Item item, Player player) {

        if (!item.getIdentifier().equals(ItemNamespaceId.BONE_MEAL)) {
            return false;
        }

        Map<Block, BlockFace> candidates = getCandidates();

        item.decrement(1);

        if (candidates.isEmpty()) {
            return true;
        }

        Set<Block> keySet = candidates.keySet();
        List<Block> keyList = new ArrayList<>(keySet);

        int rand = RANDOM.nextRange(0, candidates.size() - 1);

        Block random = keyList.get(rand);
        Block newLichen;

        if (random.getId() == BlockID.GLOW_LICHEN) {
            newLichen = random;
        } else {
            newLichen = Block.get(GLOW_LICHEN);
        }

        newLichen.setDamage(newLichen.getDamage() | (0b000001 << candidates.get(random).getDUSWNEIndex()));

        getLevel().setBlock(random, newLichen, true, true);

        return true;
    }

    private Map<Block, BlockFace> getCandidates() {
        Map<Block, BlockFace> candidates = new HashMap<>();
        for (BlockFace side : BlockFace.values()) {
            Block support = this.getSide(side);

            if (isGrowthToSide(side)) {
                BlockFace[] supportSides = side.getEdges().toArray(new BlockFace[0]);

                for (BlockFace supportSide : supportSides) {
                    Block supportNeighbor = support.getSide(supportSide);

                    if (!isSupportNeighborAdded(candidates, supportSide.getOpposite(), supportNeighbor)) {
                        continue;
                    }

                    Block supportNeighborOppositeSide = supportNeighbor.getSide(side.getOpposite());
                    if (shouldAddSupportNeighborOppositeSide(side, supportNeighborOppositeSide)) {
                        candidates.put(supportNeighborOppositeSide, side);
                    }

                }

            } else {
                if (support.isSolid()) {
                    candidates.put(this, side);
                }
            }
        }
        return candidates;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    private boolean isSupportNeighborAdded(Map<Block, BlockFace> candidates, BlockFace side, Block supportNeighbor) {
        // Air is a valid candidate!
        if (supportNeighbor.getId() == BlockID.AIR) {
            candidates.put(supportNeighbor, side);
        }

        // Other non solid blocks isn't a valid candidates
        return supportNeighbor.isSolid(side);
    }

    private boolean shouldAddSupportNeighborOppositeSide(BlockFace side, Block supportNeighborOppositeSide) {
        if (supportNeighborOppositeSide.getId() == BlockID.AIR || supportNeighborOppositeSide.getId() == BlockID.GLOW_LICHEN) {
            return supportNeighborOppositeSide.getId() != BlockID.GLOW_LICHEN ||
                    (!((BlockGlowLichen) supportNeighborOppositeSide).isGrowthToSide(side) &&
                            supportNeighborOppositeSide.getSide(side).getId() != BlockID.AIR);
        }
        return false;
    }

}
