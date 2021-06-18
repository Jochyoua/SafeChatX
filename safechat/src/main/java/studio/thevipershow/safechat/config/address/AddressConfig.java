package studio.thevipershow.safechat.config.address;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.vtc.TomlSectionConfiguration;

public final class AddressConfig extends TomlSectionConfiguration<SafeChat, AddressSection> {

    public AddressConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends AddressSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
