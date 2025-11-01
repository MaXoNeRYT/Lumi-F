package cn.nukkit.network.protocol;

import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.ScriptDebugShape;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
import cn.nukkit.utils.BinaryStream;
import lombok.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerScriptDebugDrawerPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.SERVER_SCRIPT_DEBUG_DRAWER_PACKET;
    public List<ScriptDebugShape> shapes = new ArrayList<>();

    @Override
    public int packetId() {
        return NETWORK_ID;
    }

    @Override
    public byte pid() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void decode() {
        long shapeCount = getUnsignedVarInt();
        for (int i = 0; i < shapeCount; i++) {
            ScriptDebugShape shape;
            if(protocol < ProtocolInfo.v1_21_120) {
                shape = new ScriptDebugShape(
                        getUnsignedVarLong(), getOptional(null, BinaryStream::getScriptDebugShapeType),
                        Position.fromObject(getOptional(null, BinaryStream::getVector3f).asVector3()), getOptional(null, BinaryStream::getLFloat),
                        getOptional(null, BinaryStream::getVector3f), getOptional(null, BinaryStream::getLFloat),
                        getOptional(null, BinaryStream::getColor), getOptional(null, BinaryStream::getString),
                        getOptional(null, BinaryStream::getVector3f), getOptional(null, BinaryStream::getVector3f),
                        getOptional(null, BinaryStream::getLFloat), getOptional(null, BinaryStream::getLFloat),
                        getOptional(null, BinaryStream::getByte)
                );
            } else {
                shape = this.readShape();
            }

            shapes.add(shape);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(shapes.size());

        for (ScriptDebugShape shape : shapes) {
            this.putUnsignedVarLong(shape.getId());
            this.writeCommandShapeData(shape);
            if(protocol < ProtocolInfo.v1_21_120) {
                this.putOptionalNull(shape.getText(), this::putString);
                this.putOptionalNull(shape.getBoxBounds(), this::putVector3f);
                this.putOptionalNull(shape.getLineEndPosition(), this::putVector3f);
                this.putOptionalNull(shape.getArrowHeadLength(), this::putLFloat);
                this.putOptionalNull(shape.getArrowHeadRadius(), this::putLFloat);
                this.putOptionalNull(shape.getSegments(), (buffer, segments) -> buffer.putByte(segments.byteValue()));
            } else {
                this.putVarInt(shape.getPosition().getLevel() != null ? shape.getPosition().getLevel().getDimension() : 0);
                this.putUnsignedVarInt(toPayloadType(shape.getType()));
                this.writeShapeData(shape);
            }
        }
    }

    private void writeShapeData(ScriptDebugShape shape) {
        switch (shape.getType()) {
            case ARROW:
                this.putOptionalNull(shape.getLineEndPosition(), this::putVector3f);
                this.putOptionalNull(shape.getArrowHeadLength(), this::putLFloat);
                this.putOptionalNull(shape.getArrowHeadRadius(), this::putLFloat);
                this.putOptionalNull(shape.getSegments(), (buffer, segments) -> buffer.putByte(segments.byteValue()));
                break;
            case BOX:
                this.putOptionalNull(shape.getBoxBounds(), this::putVector3f);
                break;
            case CIRCLE, SPHERE:
                this.putOptionalNull(shape.getSegments(), (buffer, segments) -> buffer.putByte(segments.byteValue()));
                break;
            case LINE:
                this.putOptionalNull(shape.getLineEndPosition(), this::putVector3f);
                break;
            case TEXT:
                this.putOptionalNull(shape.getText(), this::putString);
                break;
        }
    }

    private void writeCommandShapeData(ScriptDebugShape shape) {
        this.putOptionalNull(shape.getType(), this::writeScriptDebugShapeType);
        this.putOptionalNull(shape.getPosition().asVector3f(), this::putVector3f);
        this.putOptionalNull(shape.getScale(), this::putLFloat);
        this.putOptionalNull(shape.getRotation(), this::putVector3f);
        this.putOptionalNull(shape.getTotalTimeLeft(), this::putLFloat);
        this.putOptionalNull(shape.getColor(), this::putColor);
    }

    private ScriptDebugShape readShape() {
        long id = getUnsignedVarLong();
        ScriptDebugShapeType type = getOptional(null, BinaryStream::getScriptDebugShapeType);
        Position position = Position.fromObject(getOptional(null, BinaryStream::getVector3f).asVector3());
        float scale = getOptional(null, BinaryStream::getLFloat);
        Vector3f rotation = getOptional(null, BinaryStream::getVector3f);
        float totalTimeLeft = getOptional(null, BinaryStream::getLFloat);
        Color color = getOptional(null, BinaryStream::getColor);
        //TODO: somehow setLevel for position to set right dimensionId or do it better way
        this.getVarInt(); //dimensionID
        this.getUnsignedVarInt(); //payloadType
        String text = null;
        Vector3f boxBounds = null;
        Vector3f lineEndPosition = null;
        Float arrowHeadLength = null;
        Float arrowHeadRadius = null;
        Integer segments = null;

        switch (type) {
            case ARROW:
                lineEndPosition = getOptional(null, BinaryStream::getVector3f);
                arrowHeadLength = getOptional(null, BinaryStream::getLFloat);
                arrowHeadRadius = getOptional(null, BinaryStream::getLFloat);
                segments = getOptional(null, BinaryStream::getByte);
                break;
            case BOX:
                boxBounds = getOptional(null, BinaryStream::getVector3f);
                break;
            case CIRCLE, SPHERE:
                segments = getOptional(null, BinaryStream::getByte);
                break;
            case LINE:
                lineEndPosition = getOptional(null, BinaryStream::getVector3f);
                break;
            case TEXT:
                text = getOptional(null, BinaryStream::getString);
                break;
        }

        return new ScriptDebugShape(id, type, position, scale, rotation, totalTimeLeft, color, text, boxBounds, lineEndPosition, arrowHeadLength, arrowHeadRadius, segments);
    }

    private int toPayloadType(ScriptDebugShapeType type) {
        if (type == null) {
            return 0;
        }

        return switch (type) {
            case ARROW -> 1;
            case TEXT -> 2;
            case BOX -> 3;
            case LINE -> 4;
            case SPHERE, CIRCLE -> 5;
            default -> throw new IllegalStateException("Unknown debug shape type");
        };
    }
}
