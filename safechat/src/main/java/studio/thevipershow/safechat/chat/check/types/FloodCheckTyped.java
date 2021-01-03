package studio.thevipershow.safechat.chat.check.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.chat.check.ChatCheck;
import studio.thevipershow.safechat.chat.check.CheckType;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

public final class FloodCheckTyped extends ChatCheck {

    private final Map<UUID, Long> lastWriteMap = new HashMap<>();
    private static final String TIME_PLACEHOLDER = "{TIME}";

    public FloodCheckTyped(CheckConfig checkConfig, MessagesConfig messagesConfig) {
        super(CheckType.FLOOD, checkConfig, messagesConfig);
    }

    @Override
    public boolean check(AsyncPlayerChatEvent e) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_FLOOD_CHECK));
        if (!enabled) {
            return false;
        }

        UUID uuid = e.getPlayer().getUniqueId();
        double delaySeconds = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.FLOOD_REQUIRED_DELAY));

        if (lastWriteMap.containsKey(uuid)) {

            long lastTime = lastWriteMap.get(uuid);
            if (System.currentTimeMillis() - lastTime >= delaySeconds * 1000f) {
                lastWriteMap.put(uuid, System.currentTimeMillis());
                return false;
            } else {
                return true;
            }
        } else {
            lastWriteMap.put(uuid, System.currentTimeMillis());
            return false;
        }
    }

    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_FLOOD_WARNING));
    }

    @Override
    public @NotNull TomlArray getWarningMessages() {
        return Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.FLOOD_WARNING));
    }

    @Override
    public @NotNull String replacePlaceholders(String message, AsyncPlayerChatEvent event) {
        final double delay = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.FLOOD_REQUIRED_DELAY));
        final double missingTime = delay - ((System.currentTimeMillis() - lastWriteMap.getOrDefault(event.getPlayer().getUniqueId(), System.currentTimeMillis())) / 1000f);
        return message.replace(PLAYER_PLACEHOLDER, event.getPlayer().getName())
            .replace(PREFIX_PLACEHOLDER, SafeChat.PREFIX)
            .replace(TIME_PLACEHOLDER, String.format("%.1fs", missingTime));
    }
}
