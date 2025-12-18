package cn.nukkit.network.process.processor.common;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetLocalPlayerAsInitializedProcessor extends DataPacketProcessor<SetLocalPlayerAsInitializedPacket> {

    public static final SetLocalPlayerAsInitializedProcessor INSTANCE = new SetLocalPlayerAsInitializedProcessor();

    @Override
    public void handle(@NotNull PlayerHandle handle, @NotNull SetLocalPlayerAsInitializedPacket packet) {
        if (handle.player.locallyInitialized) {
            return;
        }

        handle.doFirstSpawn();

        new PlayerLocallyInitializedEvent(handle.player).call();
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET);
    }

    @Override
    public Class<? extends DataPacket> getPacketClass() {
        return SetLocalPlayerAsInitializedPacket.class;
    }
}
