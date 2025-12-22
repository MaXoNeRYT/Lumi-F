package cn.nukkit.network.process.processor.common;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerEditBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookAndQuill;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.BookEditPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author LT_Name
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookEditProcessor extends DataPacketProcessor<BookEditPacket> {

    public static final BookEditProcessor INSTANCE = new BookEditProcessor();

    @Override
    public void handle(@NotNull PlayerHandle handle, @NotNull BookEditPacket packet) {
        Player player = handle.player;

        if(packet.inventorySlot < 0 || packet.inventorySlot > player.getInventory().getSize()) {
            player.getServer().getLogger().debug(handle.getUsername() + ": BookEditPacket with invalid slot index (" + packet.inventorySlot + ")");
            return;
        }

        Item oldBook = player.getInventory().getItem(packet.inventorySlot);
        if (oldBook.getId() != Item.BOOK_AND_QUILL) {
            return;
        }

        if (packet.text != null && packet.text.length() > 256) {
            player.getServer().getLogger().debug(handle.getUsername() + ": BookEditPacket with too long text");
            return;
        }

        Item newBook = oldBook.clone();
        boolean success;
        switch (packet.action) {
            case REPLACE_PAGE:
                success = ((ItemBookAndQuill) newBook).setPageText(packet.pageNumber, packet.text);
                break;
            case ADD_PAGE:
                success = ((ItemBookAndQuill) newBook).insertPage(packet.pageNumber, packet.text);
                break;
            case DELETE_PAGE:
                success = ((ItemBookAndQuill) newBook).deletePage(packet.pageNumber);
                break;
            case SWAP_PAGES:
                success = ((ItemBookAndQuill) newBook).swapPages(packet.pageNumber, packet.secondaryPageNumber);
                break;
            case SIGN_BOOK:
                if (packet.title == null || packet.author == null || packet.xuid == null || packet.title.length() > 64 || packet.author.length() > 64 || packet.xuid.length() > 64) {
                    player.getServer().getLogger().debug(handle.getUsername() + ": Invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long");
                    return;
                }
                newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag());
                success = ((ItemBookWritten) newBook).signBook(packet.title, packet.author, packet.xuid, ItemBookWritten.GENERATION_ORIGINAL);
                break;
            default:
                return;
        }

        if (success) {
            PlayerEditBookEvent event = new PlayerEditBookEvent(player, oldBook, newBook, packet.action);
            if (event.call()) {
                player.getInventory().setItem(packet.inventorySlot, event.getNewBook());
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.BOOK_EDIT_PACKET);
    }

    @Override
    public Class<BookEditPacket> getPacketClass() {
        return BookEditPacket.class;
    }
}
