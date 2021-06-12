package studio.thevipershow.safechat.chat.check.types;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.ChatCheck;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.CheckName;
import studio.thevipershow.safechat.api.checks.CheckPermission;
import studio.thevipershow.safechat.api.checks.CheckPriority;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

import java.util.List;
import java.util.Objects;

@CheckName(name = "Caps")
@CheckPermission(permission = "safechat.bypass.caps")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class CapsCheck extends ChatCheck {

    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public CapsCheck(@NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = checkConfig;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean check(@NotNull ChatData data) {

        Long capsLimit = checkConfig.getConfigValue(CheckSections.CAPS_UPPERCASE_CHARACTERS_LIMIT);

        if (capsLimit == null) {
            capsLimit = 8L;
        }

        short uppercaseCounter = 0;
        for (final char c : data.getMessage().toCharArray()) {
            if (Character.isUpperCase(c)) {
                uppercaseCounter++;
            }
        }

        return uppercaseCounter >= capsLimit;
    }

    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_CAPS_WARNING));
    }

    @Override
    public @NotNull List<String> getWarningMessages() {
        return SafeChatUtils.getStrings(Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.CAPS_WARNING)));
    }

    @Override
    public @NotNull String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        return message
                .replace(PLAYER_PLACEHOLDER, data.getPlayer().getName())
                .replace(PREFIX_PLACEHOLDER, SafeChat.PREFIX);
    }

    @Override
    public long getPunishmentRequiredValue() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.CAPS_PUNISH_AFTER));
    }

    @Override
    public @NotNull String getPunishmentCommand() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.CAPS_PUNISH_COMMAND));
    }
}
