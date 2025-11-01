package cn.nukkit.entity.pathfinding;

import java.util.Objects;

public class PathNode {
    public final int x, y, z;
    public double gCost, hCost;
    public PathNode parent;

    public PathNode(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getFCost() {
        return gCost + hCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathNode node = (PathNode) o;
        return x == node.x && y == node.y && z == node.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "PathNode{" + x + ", " + y + ", " + z + "}";
    }
}