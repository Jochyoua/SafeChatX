package studio.thevipershow.safechat.chat.check;

import java.util.regex.Pattern;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.messages.MessagesConfig;

/**
 * A check for strings.
 */
public abstract class ChatCheck extends TypedCheck<AsyncPlayerChatEvent> {

    protected static final Pattern DOMAIN_REGEX = Pattern.compile("[a-z0-9-]{3,}\\.[a-z]{2,}", Pattern.CASE_INSENSITIVE);
    protected static final Pattern IPV4_REGEX = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
    protected static final Pattern SPLIT_SPACE = Pattern.compile("\\s+");
    protected static String PLAYER_PLACEHOLDER = "{PLAYER}";
    protected static String PREFIX_PLACEHOLDER = "{PREFIX}";

    public ChatCheck(CheckType checkType, CheckConfig checkConfig, MessagesConfig messagesConfig) {
        super(checkType, checkConfig, messagesConfig);
    }

    public abstract boolean hasWarningEnabled();

    @NotNull
    public abstract TomlArray getWarningMessages();

    @NotNull
    public abstract String replacePlaceholders(String message, AsyncPlayerChatEvent event);
}
