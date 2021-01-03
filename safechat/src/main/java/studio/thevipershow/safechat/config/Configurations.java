package studio.thevipershow.safechat.config;

import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.config.address.AddressConfig;
import studio.thevipershow.safechat.config.address.AddressSection;
import studio.thevipershow.safechat.config.blacklist.BlacklistConfig;
import studio.thevipershow.safechat.config.blacklist.BlacklistSection;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.checks.CheckSections;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.config.messages.MessagesSection;
import studio.thevipershow.vtc.ConfigurationData;
import studio.thevipershow.vtc.SectionType;
import studio.thevipershow.vtc.TomlSectionConfiguration;

/**
 * The available configurations enum.
 */
public enum Configurations implements ConfigurationData<SafeChat> {

    /**
     * The words blacklist configuration.
     */
    BLACKLIST("words-blacklist.toml", BlacklistConfig.class, BlacklistSection.class),
    /**
     * The address whitelist configuration.
     */
    ADDRESS("address-whitelist.toml", AddressConfig.class, AddressSection.class),
    /**
     * The messages configuration.
     */
    MESSAGES("messages.toml", MessagesConfig.class, MessagesSection.class),
    /**
     * The checks settings configuration.
     */
    CHECKS_SETTINGS("checks-settings.toml", CheckConfig.class, CheckSections.class);

    private final String stringData;
    private final Class<? extends TomlSectionConfiguration<SafeChat, ?>> classData;
    private final Class<? extends SectionType> sectionTypeClass;

    Configurations(String stringData, Class<? extends TomlSectionConfiguration<SafeChat, ?>> classData, Class<? extends SectionType> sectionTypeClass) {
        this.stringData = stringData;
        this.classData = classData;
        this.sectionTypeClass = sectionTypeClass;
    }

    @Override
    public Class<? extends TomlSectionConfiguration<SafeChat, ?>> getTomlSectionClass() {
        return classData;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Enum<T> & SectionType> Class<? extends T> getSectionClass() {
        return (Class<? extends T>) sectionTypeClass;
    }

    @Override
    public String getConfigurationName() {
        return stringData;
    }
}
