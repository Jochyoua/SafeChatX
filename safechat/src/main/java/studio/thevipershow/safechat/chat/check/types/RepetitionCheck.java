package studio.thevipershow.safechat.chat.check.types;

import info.debatty.java.stringsimilarity.RatcliffObershelp;
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

public final class RepetitionCheck extends ChatCheck {

    public RepetitionCheck(CheckConfig checkConfig, MessagesConfig messagesConfig) {
        super(CheckType.REPETITION, checkConfig, messagesConfig);
    }

    private final RatcliffObershelp ratcliffObershelp = new RatcliffObershelp();
    private final Map<UUID, String> lastMessageMap = new HashMap<>();

    @Override
    public boolean check(AsyncPlayerChatEvent e) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_REPETITION_CHECK));
        if (!enabled) {
            return false;
        }

        UUID uuid = e.getPlayer().getUniqueId();
        String message = e.getMessage();
        if (lastMessageMap.containsKey(uuid)) {
            String lastMessage = lastMessageMap.get(uuid);
            boolean allowSimilarity = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.REPETITION_ALLOW_SIMILARITY));
            if (!allowSimilarity) {
                double compare = ratcliffObershelp.similarity(lastMessage, message);
                double factor = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.REPETITION_MAXIMUM_SIMILARITY));
                if (compare >= factor) {
                    return true;
                } else {
                    lastMessageMap.put(uuid, message);
                }
            } else {
                if (message.equalsIgnoreCase(lastMessage)) {
                    return true;
                } else {
                    lastMessageMap.put(uuid, message);
                }
            }
        } else {
            lastMessageMap.put(uuid, message);
        }

        return false;
    }

    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_REPETITION_WARNING));
    }

    @Override
    public @NotNull TomlArray getWarningMessages() {
        return Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.REPETITION_WARNING));
    }

    @Override
    public @NotNull String replacePlaceholders(String message, AsyncPlayerChatEvent event) {
        return message.replace(PLAYER_PLACEHOLDER, event.getPlayer().getName())
            .replace(PREFIX_PLACEHOLDER, SafeChat.PREFIX);
    }
}
