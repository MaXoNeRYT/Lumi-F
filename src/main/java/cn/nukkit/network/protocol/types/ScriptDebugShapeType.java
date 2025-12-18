package cn.nukkit.network.protocol.types;

import lombok.Getter;

@Getter
public enum ScriptDebugShapeType {
    LINE(0, 4),
    BOX(1, 3),
    SPHERE(2, 5),
    CIRCLE(3, 5),
    TEXT(4, 2),
    ARROW(5, 1);

    private final int id;
    private final int payloadType;

    public static final int PAYLOAD_TYPE_NONE = 0;
    public static final int PAYLOAD_TYPE_ARROW = 1;
    public static final int PAYLOAD_TYPE_TEXT = 2;
    public static final int PAYLOAD_TYPE_BOX = 3;
    public static final int PAYLOAD_TYPE_LINE = 4;
    public static final int PAYLOAD_TYPE_CIRCLE_OR_SPHERE = 5;

    ScriptDebugShapeType(int id, int payloadType) {
        this.id = id;
        this.payloadType = payloadType;
    }
}