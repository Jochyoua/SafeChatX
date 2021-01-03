package studio.thevipershow.safechat.chat.check.types;

import info.debatty.java.stringsimilarity.RatcliffObershelp;
import java.util.Objects;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.chat.check.CheckType;
import studio.thevipershow.safechat.chat.check.ChatCheck;
import studio.thevipershow.safechat.config.blacklist.BlacklistConfig;
import studio.thevipershow.safechat.config.blacklist.BlacklistSection;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

/**
 * Checks if a string is banned using a blacklist.
 */
public final class WordsBlacklistCheckTyped extends ChatCheck {

    private final BlacklistConfig blacklistConfig;
    private final RatcliffObershelp ratcliffObershelp = new RatcliffObershelp();

    public WordsBlacklistCheckTyped(BlacklistConfig blacklistConfig, CheckConfig checkConfig, MessagesConfig messagesConfig) {
        super(CheckType.WORDS, checkConfig, messagesConfig);
        this.blacklistConfig = blacklistConfig;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean check(final AsyncPlayerChatEvent e) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_BLACKLIST_CHECK));
        if (!enabled) {
            return false;
        }

        boolean checkSimilar = !((Boolean) checkConfig.getConfigValue(CheckSections.BLACKLIST_ALLOW_SIMILARITY));
        TomlArray words = blacklistConfig.getConfigValue(BlacklistSection.WORDS);
        int wordsSize = words.size();

        if (wordsSize == 0) {
            return false;
        }

        String s = e.getMessage();

        if (s.isEmpty()) {
            return false;
        }

        String[] ss = SPLIT_SPACE.split(s);

        if (checkSimilar) {

            double factor = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.BLACKLIST_MAXIMUM_SIMILARITY));
            for (int k = 0; k < wordsSize; k++) {
                String str = words.getString(k);
                for (final String value : ss) {
                    if (ratcliffObershelp.similarity(value, str) >= factor) {
                        return true;
                    }
                }
            }
        } else {

            for (int k = 0; k < wordsSize; k++) {
                String str = words.getString(k);
                for (final String value : ss) {
                    if (value.equalsIgnoreCase(str)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_BLACKLIST_WARNING));
    }

    @Override
    public @NotNull TomlArray getWarningMessages() {
        return Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.BLACKLIST_WARNING));
    }

    @Override
    public @NotNull String replacePlaceholders(String message, AsyncPlayerChatEvent event) {
        return message.replace(PLAYER_PLACEHOLDER, event.getPlayer().getName())
            .replace(PREFIX_PLACEHOLDER, SafeChat.PREFIX);
    }
}
