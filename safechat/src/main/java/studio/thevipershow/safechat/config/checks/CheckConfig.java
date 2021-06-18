package studio.thevipershow.safechat.config.checks;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.vtc.TomlSectionConfiguration;

public final class CheckConfig extends TomlSectionConfiguration<SafeChat, CheckSections> {

    public CheckConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends CheckSections> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
