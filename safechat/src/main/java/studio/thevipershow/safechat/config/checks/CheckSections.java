package studio.thevipershow.safechat.config.checks;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.SectionType;

public enum CheckSections implements SectionType {
    ENABLE_BLACKLIST_CHECK("blacklist.enable-check", Boolean.class),
    ENABLE_BLACKLIST_WARNING("blacklist.enable-warning", Boolean.class),
    ENABLE_BLACKLIST_LOGGING("blacklist.enable-logging", Boolean.class),
    ENABLE_BLACKLIST_FALLBACK("blacklist.fallback", Boolean.class),
    ENABLE_BLACKLIST_STRIPPING("blacklist.strip-abnormal-characters", Boolean.class),
    BLACKLIST_ALLOW_SIMILARITY("blacklist.allow-similarity", Boolean.class),
    BLACKLIST_MAXIMUM_SIMILARITY("blacklist.maximum-similarity", Double.class),
    BLACKLIST_PUNISH_AFTER("blacklist.punish-after", Long.class),
    BLACKLIST_PUNISH_COMMAND("blacklist.punish-command", String.class),

    ENABLE_ADDRESS_CHECK("address.enable-check", Boolean.class),
    ENABLE_ADDRESS_WARNING("address.enable-warning", Boolean.class),
    ENABLE_ADDRESS_LOGGING("address.enable-logging", Boolean.class),
    ADDRESS_PUNISH_AFTER("address.punish-after", Long.class),
    ADDRESS_PUNISH_COMMAND("address.punish-command", String.class),

    ENABLE_FLOOD_CHECK("flood.enable-check", Boolean.class),
    ENABLE_FLOOD_WARNING("flood.enable-warning", Boolean.class),
    ENABLE_FLOOD_LOGGING("flood.enable-logging", Boolean.class),
    FLOOD_REQUIRED_DELAY("flood.required-delay", Double.class),
    FLOOD_PUNISH_AFTER("flood.punish-after", Long.class),
    FLOOD_PUNISH_COMMAND("flood.punish-command", String.class),

    ENABLE_REPETITION_CHECK("repetition.enable-check", Boolean.class),
    ENABLE_REPETITION_WARNING("repetition.enable-warning", Boolean.class),
    ENABLE_REPETITION_LOGGING("repetition.enable-logging", Boolean.class),
    REPETITION_ALLOW_SIMILARITY("repetition.allow-similarity", Boolean.class),
    REPETITION_MAXIMUM_SIMILARITY("repetition.maximum-similarity", Double.class),
    REPETITION_PUNISH_AFTER("repetition.punish-after", Long.class),
    REPETITION_PUNISH_COMMAND("repetition.punish-command", String.class),

    ENABLE_CAPS_CHECK("caps.enable-check", Boolean.class),
    ENABLE_CAPS_WARNING("caps.enable-warning", Boolean.class),
    ENABLE_CAPS_LOGGING("caps.enable-logging", Boolean.class),
    CAPS_UPPERCASE_CHARACTERS_LIMIT("caps.uppercase-characters-limit", Long.class),
    CAPS_PUNISH_AFTER("caps.punish-after", Long.class),
    CAPS_PUNISH_COMMAND("caps.punish-command", String.class);

    private final String stringData;
    private final Class<?> classData;

    CheckSections(String stringData, Class<?> classData) {
        this.stringData = stringData;
        this.classData = classData;
    }

    @Override
    public @NotNull Class<?> getClassData() {
        return classData;
    }

    @Override
    public @NotNull String getStringData() {
        return stringData;
    }
}
