package studio.safechat.tests;

import com.sun.istack.NotNull;
import studio.thevipershow.safechat.api.checks.ChatCheck;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.CheckName;
import studio.thevipershow.safechat.api.checks.CheckPriority;

import java.util.Arrays;
import java.util.List;

@CheckName(name = "MessageTooLong")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class MessageTooLongCheck extends ChatCheck {

    private final List<String> myWarnings = Arrays.asList(
            "&8[&cWARNING&8]&7: &e{PLAYER}&7 your message was too long!",
            "&7The maximum allowed size is {MAX_SIZE}");
    private final short maximumAllowedMessageLength = 32;
    private final long myPunishmentAmount = 5;
    private final boolean hasWarning = true;
    private final String punishment = "kick {PLAYER} Your message was {MSG_SIZE} but maximum allowed is {MAX_SIZE}!";

    @Override
    public boolean check(@NotNull ChatData data) {
        return data.getMessage().length() > maximumAllowedMessageLength;
    }

    @Override
    public boolean hasWarningEnabled() {
        return hasWarning;
    }

    @Override
    public @NotNull
    List<String> getWarningMessages() {
        return myWarnings;
    }

    @Override
    public @NotNull
    String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        return message
                .replace("{PLAYER}", data.getPlayer().getName())
                .replace("{MSG_SIZE}", Integer.toString(message.length()))
                .replace("{MAX_SIZE}", Short.toString(maximumAllowedMessageLength));
    }

    @Override
    public long getPunishmentRequiredValue() {
        return myPunishmentAmount;
    }

    @Override
    public @NotNull
    String getPunishmentCommand() {
        return punishment;
    }

    @Override
    public boolean getLoggingEnabled() {
        return false;
    }
}
