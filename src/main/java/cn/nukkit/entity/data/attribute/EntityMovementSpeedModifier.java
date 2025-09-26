package cn.nukkit.entity.data.attribute;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EntityMovementSpeedModifier {
    private final String identifier;
    private final float value;
    private final Operation operation;
    private final boolean send;

    public EntityMovementSpeedModifier(String identifier, float value, Operation operation) {
        this(identifier, value, operation, true);
    }

    public EntityMovementSpeedModifier(String identifier, float value, Operation operation, boolean send) {
        this.identifier = identifier;
        this.value = value;
        this.operation = operation;
        this.send = send;
    }

    public enum Operation {
        ADD,
        MULTIPLY
    }
}
