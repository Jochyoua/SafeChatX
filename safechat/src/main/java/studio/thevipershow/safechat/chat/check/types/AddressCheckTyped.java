package studio.thevipershow.safechat.chat.check.types;

import java.util.Objects;
import java.util.regex.Matcher;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.chat.check.CheckType;
import studio.thevipershow.safechat.chat.check.ChatCheck;
import studio.thevipershow.safechat.config.address.AddressConfig;
import studio.thevipershow.safechat.config.address.AddressSection;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

public final class AddressCheckTyped extends ChatCheck {

    private final AddressConfig addressConfig;

    private static final byte MINIMUM_DOMAIN_CHARS = 6;
    private static final byte MINIMUM_ADDRESS_CHARS = 7;

    public AddressCheckTyped(AddressConfig addressConfig, CheckConfig checkConfig, MessagesConfig messagesConfig) {
        super(CheckType.ADDRESS, checkConfig, messagesConfig);
        this.addressConfig = addressConfig;
    }

    @Override
    public boolean check(final AsyncPlayerChatEvent e) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_ADDRESS_CHECK));
        if (!enabled) {
            return false;
        }

        String s = e.getMessage();

        if (s.isEmpty()) {
            return false;
        }

        String[] ss;

        if (s.length() >= MINIMUM_DOMAIN_CHARS) {
            ss = SPLIT_SPACE.split(s);
            TomlArray allowedDomains = addressConfig.getConfigValue(AddressSection.ALLOWED_DOMAINS);

            for (final String sk : ss) {
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

            for (final String sk : ss) {
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
    public @NotNull TomlArray getWarningMessages() {
        return Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.ADDRESS_WARNING));
    }

    @Override
    public @NotNull String replacePlaceholders(String message, AsyncPlayerChatEvent event) {
        return message.replace(PLAYER_PLACEHOLDER, event.getPlayer().getName())
            .replace(PREFIX_PLACEHOLDER, SafeChat.PREFIX);
    }
}
