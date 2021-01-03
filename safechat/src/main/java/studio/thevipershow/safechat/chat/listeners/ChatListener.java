package studio.thevipershow.safechat.chat.listeners;

import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.chat.ChatUtil;
import studio.thevipershow.safechat.chat.check.ChatCheck;
import studio.thevipershow.safechat.chat.check.ChecksContainer;

public final class ChatListener implements Listener {

    public ChatListener(SafeChat safeChat, ChecksContainer checksContainer) {
        this.safeChat = safeChat;
        this.checksContainer = checksContainer;
    }

    private final SafeChat safeChat;
    private final ChecksContainer checksContainer;

    private static void sendWarning(ChatCheck check, AsyncPlayerChatEvent event) {
        if (!check.hasWarningEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        TomlArray messages = check.getWarningMessages();

        for (int k = 0; k < messages.size(); k++) {
            String message = check.replacePlaceholders(Objects.requireNonNull(messages.getString(k)), event);
            player.sendMessage(ChatUtil.color(message));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        for (ChatCheck chatCheck : checksContainer.getActiveChecks()) {
            if (chatCheck.check(event)) {
                event.setCancelled(true);
                sendWarning(chatCheck, event);
                break;
            }
        }
    }
}
