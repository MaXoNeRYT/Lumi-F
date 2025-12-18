package cn.nukkit.network.process.processor.common;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestAbilityPacket;
import cn.nukkit.network.protocol.types.PlayerAbility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestAbilityProcessor extends DataPacketProcessor<RequestAbilityPacket> {

    public static final RequestAbilityProcessor INSTANCE = new RequestAbilityProcessor();

    @Override
    public void handle(@NotNull PlayerHandle handle, @NotNull RequestAbilityPacket packet) {
        Player player = handle.player;
        if (player.protocol >= ProtocolInfo.v1_20_30_24) { //1.20.30开始飞行切换使用PlayerAuthInputPacket/PlayerActionPacket
            return;
        }
        PlayerAbility ability = packet.getAbility();
        if (ability != PlayerAbility.FLYING) {
            log.info("[" + player.getName() + "] has tried to trigger " + ability + " ability " + (packet.isBoolValue() ? "on" : "off"));
            return;
        }

        if (!player.getServer().getSettings().player().allowFlight() && packet.boolValue && !player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
            player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, "Flying is not enabled on this server");
            return;
        }

        PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(player, packet.isBoolValue());
        if (player.isSpectator()) {
            event.setCancelled();
        }

        if (!event.call()) {
            player.getAdventureSettings().update();
        } else {
            player.getAdventureSettings().set(AdventureSettings.Type.FLYING, event.isFlying());
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.REQUEST_ABILITY_PACKET);
    }

    @Override
    public Class<? extends DataPacket> getPacketClass() {
        return RequestAbilityPacket.class;
    }
}
