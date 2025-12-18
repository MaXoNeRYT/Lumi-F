package cn.nukkit.network.process.processor.common;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.data.skin.Skin;
import cn.nukkit.event.player.PlayerChangeSkinEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author LT_Name
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerSkinProcessor extends DataPacketProcessor<PlayerSkinPacket> {

    public static final PlayerSkinProcessor INSTANCE = new PlayerSkinProcessor();

    @Override
    public void handle(@NotNull PlayerHandle handle, @NotNull PlayerSkinPacket packet) {
        Skin skin = packet.skin;
        Player player = handle.player;

        if (!skin.isValid()) {
            player.getServer().getLogger().warning(handle.getUsername() + ": PlayerSkinPacket with invalid skin");
            player.close("", "disconnectionScreen.invalidSkin");
            return;
        }

        PlayerChangeSkinEvent event = new PlayerChangeSkinEvent(player, skin);
        int skinChangeCooldown = Server.getInstance().getSettings().player().skinChangeCooldown();
        if (TimeUnit.SECONDS.toMillis(skinChangeCooldown) > System.currentTimeMillis() - player.lastSkinChange) {
            event.setCancelled(true);
            Server.getInstance().getLogger().warning("Player " + handle.getUsername() + " change skin too quick!");
        }
        if (event.call()) {
            boolean personaSkins = Server.getInstance().getSettings().player().personaSkins();
            player.lastSkinChange = System.currentTimeMillis();
            player.setSkin(skin.isPersona() && !personaSkins ? Skin.NO_PERSONA_SKIN : skin);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.PLAYER_SKIN_PACKET);
    }

    @Override
    public Class<? extends DataPacket> getPacketClass() {
        return PlayerSkinPacket.class;
    }
}
