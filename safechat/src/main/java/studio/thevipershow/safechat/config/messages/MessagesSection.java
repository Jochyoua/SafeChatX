package studio.thevipershow.safechat.config.messages;

import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.vtc.SectionType;

/**
 * All of the sections in the messages config.
 */
public enum MessagesSection implements SectionType {
    /**
     * The messages sent when someone sends an address.
     */
    ADDRESS_WARNING("messages.address-warning", TomlArray.class),
    /**
     * The messages sent when someone used a blacklisted word.
     */
    BLACKLIST_WARNING("messages.blacklisted-word-warning", TomlArray.class),
    /**
     * The messages sent when someone repeats the same message.
     */
    REPETITION_WARNING("messages.text-repetition-warning", TomlArray.class),
    /**
     * The messages sent when someone writes too fast.
     */
    FLOOD_WARNING("messages.chat-flood-warning", TomlArray.class),
    /**
     * The messages sent when too many uppercase characters are used.
     */
    CAPS_WARNING("messages.caps-warning", TomlArray.class);

    private final String stringData;
    private final Class<?> classData;

    MessagesSection(String stringData, Class<?> classData) {
        this.stringData = stringData;
        this.classData = classData;
    }

    @Override
    public final @NotNull Class<?> getClassData() {
        return classData;
    }

    @Override
    public final @NotNull String getStringData() {
        return stringData;
    }
}
