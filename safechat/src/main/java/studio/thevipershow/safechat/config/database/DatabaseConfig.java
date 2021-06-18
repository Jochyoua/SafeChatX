package studio.thevipershow.safechat.config.database;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.vtc.TomlSectionConfiguration;

public final class DatabaseConfig extends TomlSectionConfiguration<SafeChat, DatabaseSection> {

    public DatabaseConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends DatabaseSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
