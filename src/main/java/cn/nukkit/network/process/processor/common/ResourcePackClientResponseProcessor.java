package cn.nukkit.network.process.processor.common;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.Server;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class ResourcePackClientResponseProcessor extends DataPacketProcessor<ResourcePackClientResponsePacket> {

    public static final ResourcePackClientResponseProcessor INSTANCE = new ResourcePackClientResponseProcessor();

    @Override
    public void handle(@NotNull PlayerHandle handle, @NotNull ResourcePackClientResponsePacket packet) {
        Player p = handle.player;
        Server server = p.getServer();

        switch (packet.responseStatus) {
            case ResourcePackClientResponsePacket.STATUS_REFUSED -> handle.close("", "disconnectionScreen.noReason");

            case ResourcePackClientResponsePacket.STATUS_SEND_PACKS -> {
                if (handle.isShouldPack()
                        || packet.packEntries.length >
                        server.getResourcePackManager()
                                .getResourceStack().length) {
                    handle.close("", "disconnectionScreen.resourcePack");
                    return;
                }

                handle.setShouldPack(true);

                Set<String> uniqueIds = new LinkedHashSet<>();
                for (ResourcePackClientResponsePacket.Entry entry : packet.packEntries) {
                    ResourcePack pack =
                            server.getResourcePackManager()
                                    .getPackById(entry.uuid);

                    if (pack == null
                            || !uniqueIds.add(entry.uuid.toString())) {
                        handle.close("", "disconnectionScreen.resourcePack");
                        return;
                    }

                    p.dataPacket(pack.toNetwork());
                }
            }

            case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS -> {
                ResourcePackStackPacket stack = new ResourcePackStackPacket();
                stack.mustAccept =
                        server.getSettings().general().forceResources()
                                && !server.getSettings().general()
                                .forceResourcesAllowClientPacks();

                stack.resourcePackStack =
                        server.getResourcePackManager().getResourceStack();

                stack.experiments.addAll(handle.getExperiments());

                p.dataPacket(stack);
            }

            case ResourcePackClientResponsePacket.STATUS_COMPLETED -> {
                handle.setShouldLogin(true);

                if (handle.getPreLoginEventTask().isFinished()) {
                    handle.getPreLoginEventTask().onCompletion(server);
                }
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;
    }

    @Override
    public Class<ResourcePackClientResponsePacket> getPacketClass() {
        return ResourcePackClientResponsePacket.class;
    }
}
