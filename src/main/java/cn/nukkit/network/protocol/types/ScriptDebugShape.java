package cn.nukkit.network.protocol.types;

import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3f;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

@Value
public class ScriptDebugShape {
    long id;
    @Nullable
    ScriptDebugShapeType type;
    @Nullable
    Position position;
    @Nullable
    Float scale;
    @Nullable
    Vector3f rotation;
    @Nullable
    Float totalTimeLeft;
    @Nullable
    Color color;
    @Nullable
    String text;
    @Nullable
    Vector3f boxBounds;
    @Nullable
    Vector3f lineEndPosition;
    @Nullable
    Float arrowHeadLength;
    @Nullable
    Float arrowHeadRadius;
    @Nullable
    Integer segments;

}
