package studio.thevipershow.safechat.config.blacklist;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.vtc.TomlSectionConfiguration;

/**
 * Configuration for the words blacklist.
 */
public final class BlacklistConfig extends TomlSectionConfiguration<SafeChat, BlacklistSection> {

    public BlacklistConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends BlacklistSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
