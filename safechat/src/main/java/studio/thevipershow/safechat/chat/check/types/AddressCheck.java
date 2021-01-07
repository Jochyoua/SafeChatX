package studio.thevipershow.safechat.chat.check.types;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.api.checks.CheckPriority;
import studio.thevipershow.safechat.chat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.ChatCheck;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.CheckName;
import studio.thevipershow.safechat.config.address.AddressConfig;
import studio.thevipershow.safechat.config.address.AddressSection;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

@CheckName(name = "Address")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class AddressCheck extends ChatCheck {

    public static final byte MINIMUM_DOMAIN_CHARS = 6;
    public static final byte MINIMUM_ADDRESS_CHARS = 7;

    private final AddressConfig addressConfig;
    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public AddressCheck(@NotNull AddressConfig addressConfig, @NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = checkConfig;
        this.messagesConfig = messagesConfig;
        this.addressConfig = addressConfig;
    }

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

        String[] ss;

        if (s.length() >= MINIMUM_DOMAIN_CHARS) {
            ss = SPLIT_SPACE.split(s);
            TomlArray allowedDomains = addressConfig.getConfigValue(AddressSection.ALLOWED_DOMAINS);

            for (String sk : ss) {
                if (sk.length() >= MINIMUM_DOMAIN_CHARS) {
                    Matcher match = DOMAIN_REGEX.matcher(sk);
                    while (match.find()) {
                        String gg = match.group();
                        for (int i = 0; i < Objects.requireNonNull(allowedDomains).size(); i++) {
                            if (!allowedDomains.getString(i).equalsIgnoreCase(gg)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        if (s.length() >= MINIMUM_ADDRESS_CHARS) {
            ss = SPLIT_SPACE.split(s);
            TomlArray allowedIpv4s = addressConfig.getConfigValue(AddressSection.ALLOWED_ADDRESSES);

            for (String sk : ss) {
                if (sk.length() >= MINIMUM_ADDRESS_CHARS) {
                    Matcher match = IPV4_REGEX.matcher(sk);
                    while (match.find()) {
                        String gg = match.group();
                        for (int i = 0; i < Objects.requireNonNull(allowedIpv4s).size(); i++) {
                            if (!allowedIpv4s.getString(i).equals(gg)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_ADDRESS_WARNING));
    }

    @Override
    public @NotNull List<String> getWarningMessages() {
        TomlArray tomlArray = Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.ADDRESS_WARNING));
        return SafeChatUtils.getStrings(tomlArray);
    }

    @Override
    public @NotNull String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        return message.replace(PLAYER_PLACEHOLDER, data.getPlayer().getName())
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
}
