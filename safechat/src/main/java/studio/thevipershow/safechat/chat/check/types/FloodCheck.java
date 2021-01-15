package studio.thevipershow.safechat.chat.check.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.api.checks.CheckPermission;
import studio.thevipershow.safechat.api.checks.CheckPriority;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.ChatCheck;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.CheckName;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

@CheckName(name = "Flood")
@CheckPermission(permission = "safechat.bypass.flood")
@CheckPriority(priority = CheckPriority.Priority.HIGH)
public final class FloodCheck extends ChatCheck {

    private static final String TIME_PLACEHOLDER = "{TIME}";

    private final Map<UUID, Long> lastWriteMap = new HashMap<>();
    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public FloodCheck(@NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = Objects.requireNonNull(checkConfig);
        this.messagesConfig = Objects.requireNonNull(messagesConfig);
    }

    @Override
    public boolean check(@NotNull ChatData data) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_FLOOD_CHECK));
        if (!enabled) {
            return false;
        }

        UUID uuid = data.getPlayer().getUniqueId();
        double delaySeconds = ((Number) Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.FLOOD_REQUIRED_DELAY))).doubleValue();

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
    public @NotNull List<String> getWarningMessages() {
        TomlArray tomlArray = Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.FLOOD_WARNING));
        return SafeChatUtils.getStrings(tomlArray);
    }

    @Override
    public @NotNull String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        Player player = data.getPlayer();
        double delay = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.FLOOD_REQUIRED_DELAY));
        double missingTime = delay - ((System.currentTimeMillis() - lastWriteMap.getOrDefault(player.getUniqueId(), System.currentTimeMillis())) / 1000f);
        return message.replace(PLAYER_PLACEHOLDER, player.getName())
            .replace(PREFIX_PLACEHOLDER, SafeChat.PREFIX)
            .replace(TIME_PLACEHOLDER, String.format("%.1fs", missingTime));
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
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.FLOOD_PUNISH_AFTER));
    }

    /**
     * Get the command to execute when a punishment is required.
     * Placeholders may be used.
     *
     * @return The command to execute.
     */
    @Override
    public @NotNull String getPunishmentCommand() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.FLOOD_PUNISH_COMMAND));
    }
}
