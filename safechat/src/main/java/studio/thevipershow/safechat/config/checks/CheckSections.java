package studio.thevipershow.safechat.config.checks;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.SectionType;

public enum CheckSections implements SectionType {
    ENABLE_BLACKLIST_CHECK("blacklist.enable-check", Boolean.class),
    ENABLE_BLACKLIST_WARNING("blacklist.enable-warning", Boolean.class),
    BLACKLIST_ALLOW_SIMILARITY("blacklist.allow-similarity", Boolean.class),
    BLACKLIST_MAXIMUM_SIMILARITY("blacklist.maximum-similarity", Double.class),

    ENABLE_ADDRESS_CHECK("address.enable-check", Boolean.class),
    ENABLE_ADDRESS_WARNING("address.enable-warning", Boolean.class),

    ENABLE_FLOOD_CHECK("flood.enable-check", Boolean.class),
    ENABLE_FLOOD_WARNING("flood.enable-warning", Boolean.class),
    FLOOD_REQUIRED_DELAY("flood.required-delay", Double.class),

    ENABLE_REPETITION_CHECK("repetition.enable-check", Boolean.class),
    ENABLE_REPETITION_WARNING("repetition.enable-warning", Boolean.class),
    REPETITION_ALLOW_SIMILARITY("repetition.allow-similarity", Boolean.class),
    REPETITION_MAXIMUM_SIMILARITY("repetition.maximum-similarity", Double.class);

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
