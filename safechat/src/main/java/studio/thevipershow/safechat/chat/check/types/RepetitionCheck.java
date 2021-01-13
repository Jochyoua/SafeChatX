package studio.thevipershow.safechat.chat.check.types;

import com.zaxxer.hikari.HikariDataSource;
import info.debatty.java.stringsimilarity.RatcliffObershelp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.api.checks.CheckPriority;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.CheckName;
import studio.thevipershow.safechat.api.checks.ChatCheck;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

@CheckName(name = "RepetitionCheck")
@CheckPriority(priority = CheckPriority.Priority.HIGH)
public final class RepetitionCheck extends ChatCheck {

    private final RatcliffObershelp ratcliffObershelp = new RatcliffObershelp();
    private final Map<UUID, String> lastMessageMap = new HashMap<>();
    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public RepetitionCheck(@NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = checkConfig;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean check(@NotNull ChatData data) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_REPETITION_CHECK));
        if (!enabled) {
            return false;
        }

        UUID uuid = data.getPlayer().getUniqueId();
        String message = data.getMessage();
        if (lastMessageMap.containsKey(uuid)) {
            String lastMessage = lastMessageMap.get(uuid);
            boolean allowSimilarity = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.REPETITION_ALLOW_SIMILARITY));
            if (!allowSimilarity) {
                double compare = ratcliffObershelp.similarity(lastMessage, message);
                double factor = ((Number) Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.REPETITION_MAXIMUM_SIMILARITY))).doubleValue();
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
    public @NotNull List<String> getWarningMessages() {
        TomlArray array = Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.REPETITION_WARNING));
        return SafeChatUtils.getStrings(array);
    }

    @Override
    public @NotNull String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        return message
                .replace(PLAYER_PLACEHOLDER, data.getPlayer().getName())
                .replace(PREFIX_PLACEHOLDER, SafeChat.PREFIX);
    }

    /**
     * Get after how often should a player trigger a punish.
     * For example 2 will mean each 2 failed checks,
     * will trigger the punishment.
     *
     * @return The interval value.
     */
    @Override
    public long getPunishmentRequiredValue() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.REPETITION_PUNISH_AFTER));
    }

    /**
     * Get the command to execute when a punishment is required.
     * Placeholders may be used.
     *
     * @return The command to execute.
     */
    @Override
    public @NotNull String getPunishmentCommand() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.REPETITION_PUNISH_COMMAND));
    }
}
