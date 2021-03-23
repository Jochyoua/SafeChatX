package studio.thevipershow.safechat.chat.check.types;

import info.debatty.java.stringsimilarity.Levenshtein;
import info.debatty.java.stringsimilarity.RatcliffObershelp;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.api.checks.CheckPermission;
import studio.thevipershow.safechat.api.checks.CheckPriority;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.CheckName;
import studio.thevipershow.safechat.api.checks.ChatCheck;
import studio.thevipershow.safechat.config.blacklist.BlacklistConfig;
import studio.thevipershow.safechat.config.blacklist.BlacklistSection;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;

/**
 * Checks if a string is banned using a blacklist.
 */
@CheckName(name = "Blacklist")
@CheckPermission(permission = "safechat.bypass.blacklist")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class WordsBlacklistCheck extends ChatCheck {

    private final Levenshtein levenshteinDistance = new Levenshtein();
    private final BlacklistConfig blacklistConfig;
    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public WordsBlacklistCheck(@NotNull BlacklistConfig blacklistConfig, @NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = Objects.requireNonNull(checkConfig);
        this.messagesConfig = Objects.requireNonNull(messagesConfig);
        this.blacklistConfig = Objects.requireNonNull(blacklistConfig);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean check(@NotNull ChatData data) {
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

        String s = data.getMessage();

        if (s.isEmpty()) {
            return false;
        }

        String[] ss = SPLIT_SPACE.split(s);

        if (checkSimilar) {

            double factor = ((Number) Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.BLACKLIST_MAXIMUM_SIMILARITY))).doubleValue();
            for (int k = 0; k < wordsSize; k++) {
                final String str = words.getString(k);
                for (final String value : ss) {
                    if (levenshteinDistance.distance(value, str) >= factor) {
                        return true;
                    } else if (levenshteinDistance.distance(value.toLowerCase(Locale.ROOT), str) >= factor) {
                        return true;
                    }
                }
            }
        } else {

            for (int k = 0; k < wordsSize; k++) {
                final String str = words.getString(k);
                if (str.equalsIgnoreCase(s)) {
                    return true;
                } else {
                    for (final String value : ss) {
                        if (value.equalsIgnoreCase(str)) {
                            return true;
                        }
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
    public @NotNull List<String> getWarningMessages() {
        TomlArray array = Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.BLACKLIST_WARNING));
        return SafeChatUtils.getStrings(array);
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
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.BLACKLIST_PUNISH_AFTER));
    }

    /**
     * Get the command to execute when a punishment is required.
     * Placeholders may be used.
     *
     * @return The command to execute.
     */
    @Override
    public @NotNull String getPunishmentCommand() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.BLACKLIST_PUNISH_COMMAND));
    }
}
