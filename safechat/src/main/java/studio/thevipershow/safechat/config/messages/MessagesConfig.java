package studio.thevipershow.safechat.config.messages;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.vtc.TomlSectionConfiguration;

/**
 * Config for the messages.
 */
public final class MessagesConfig extends TomlSectionConfiguration<SafeChat, MessagesSection> {

    public MessagesConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends MessagesSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
