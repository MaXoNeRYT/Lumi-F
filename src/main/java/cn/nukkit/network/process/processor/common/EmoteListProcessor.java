package cn.nukkit.network.process.processor.common;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.EmoteListPacket;
import cn.nukkit.network.protocol.EmotePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmoteListProcessor extends DataPacketProcessor<EmoteListPacket> {

    public static final EmoteListProcessor INSTANCE = new EmoteListProcessor();

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull EmoteListPacket pk) {
        Player player = playerHandle.player;
        player.getAvailableEmotes().clear();
        player.getAvailableEmotes().addAll(pk.pieceIds);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.EMOTE_LIST_PACKET);
    }

    @Override
    public Class<EmoteListPacket> getPacketClass() {
        return EmoteListPacket.class;
    }
}
