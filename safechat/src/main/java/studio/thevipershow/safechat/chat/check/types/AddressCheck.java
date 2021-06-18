package studio.thevipershow.safechat.chat.check.types;

import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.ChatCheck;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.CheckName;
import studio.thevipershow.safechat.api.checks.CheckPermission;
import studio.thevipershow.safechat.api.checks.CheckPriority;
import studio.thevipershow.safechat.config.address.AddressConfig;
import studio.thevipershow.safechat.config.address.AddressSection;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;


@SuppressWarnings("unused")
@CheckName(name = "Address")
@CheckPermission(permission = "safechat.bypass.address")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class AddressCheck extends ChatCheck {

    public static final byte MINIMUM_DOMAIN_CHARS = 6;
    public static final byte MINIMUM_ADDRESS_CHARS = 7;

    private final AddressConfig addressConfig;
    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public AddressCheck(@NotNull AddressConfig addressConfig, @NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = Objects.requireNonNull(checkConfig);
        this.messagesConfig = Objects.requireNonNull(messagesConfig);
        this.addressConfig = Objects.requireNonNull(addressConfig);
    }

    /**
     * Perform a check on ChatData.
     * The check can consist in anything, but it must follow these criteria:
     * The check must return true if the player failed the check.
     * The check must return false if the player passed the check.
     *
     * @param data The chat data.
     * @return True if failed, false otherwise.
     */
    @Override
    public boolean check(@NotNull ChatData data) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_ADDRESS_CHECK));
        if (!enabled) {
            return false;
        }

        String s = data.getMessage();

        if (s.isEmpty()) {
            return false;
        }

        if (getLoggingEnabled()) {
            SafeChatUtils.logMessage(this, data.getPlayer(), data.getMessage());
        }

        String[] ss;

        if (s.length() >= MINIMUM_DOMAIN_CHARS) {
            ss = SPLIT_SPACE.split(s);
            TomlArray allowedDomains = addressConfig.getConfigValue(AddressSection.ALLOWED_DOMAINS);

            for (final String sk : ss) {
                if (sk.length() >= MINIMUM_DOMAIN_CHARS) {
                    Matcher match = DOMAIN_REGEX.matcher(sk);

                    whileLabel:
                    while (match.find()) {
                        final String gg = match.group().toLowerCase(Locale.ROOT);
                        for (int i = 0; i < Objects.requireNonNull(allowedDomains).size(); i++) {
                            final boolean matched = gg.contains(allowedDomains.getString(i));
                            if (matched) {
                                continue whileLabel;
                            }
                        }

                        if (getLoggingEnabled()) {
                            SafeChatUtils.logMessage(this, data.getPlayer(), data.getMessage());
                        }
                        return true;
                    }
                }
            }
        }

        if (s.length() >= MINIMUM_ADDRESS_CHARS) {
            ss = SPLIT_SPACE.split(s);
            TomlArray allowedIpv4s = addressConfig.getConfigValue(AddressSection.ALLOWED_ADDRESSES);

            for (final String sk : ss) {
                if (sk.length() >= MINIMUM_ADDRESS_CHARS) {
                    Matcher match = IPV4_REGEX.matcher(sk);
                    whileLabel:
                    while (match.find()) {
                        final String gg = match.group();
                        for (int i = 0; i < Objects.requireNonNull(allowedIpv4s).size(); i++) {
                            final boolean matched = gg.equals(allowedIpv4s.getString(i));
                            if (matched) {
                                continue whileLabel;
                            }
                        }
                        if (getLoggingEnabled()) {
                            SafeChatUtils.logMessage(this, data.getPlayer(), data.getMessage());
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get the warning messages status.
     *
     * @return True if a warning message should be sent
     * upon the player failing a check.
     */
    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_ADDRESS_WARNING));
    }

    /**
     * Get the warning messages that will be displayed when
     * the player fails a check.
     *
     * @return The warning messages.
     */
    @Override
    public @NotNull List<String> getWarningMessages() {
        TomlArray tomlArray = Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.ADDRESS_WARNING));
        return SafeChatUtils.getStrings(tomlArray);
    }

    /**
     * Provide placeholders for your own check.
     * Replace any placeholder with your data.
     *
     * @param message The message that may contain placeholders.
     * @param data    The data (used for placeholders).
     * @return The message, modified if it had placeholders support.
     */
    @Override
    public @NotNull String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        return message.replace(PLAYER_PLACEHOLDER, data.getPlayer().getName())
                .replace(PREFIX_PLACEHOLDER, SafeChat.getLocale().getString("prefix"));
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
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ADDRESS_PUNISH_AFTER));
    }

    /**
     * Get the command to execute when a punishment is required.
     * Placeholders may be used.
     *
     * @return The command to execute.
     */
    @Override
    public @NotNull String getPunishmentCommand() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ADDRESS_PUNISH_COMMAND));
    }


    /**
     * Gets the status of logging for this check from
     * the config
     *
     * @return if logging is enabled for this check
     */
    @Override
    public boolean getLoggingEnabled() {
        return checkConfig.getConfigValue(CheckSections.ENABLE_ADDRESS_LOGGING, Boolean.class);
    }
}
