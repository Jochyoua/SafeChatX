package studio.thevipershow.safechat.config.blacklist;

import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.vtc.SectionType;

/**
 * The sections of the blacklist config.
 */
public enum BlacklistSection implements SectionType {
    /**
     * Words list.
     */
    WORDS("words", TomlArray.class);

    private final String stringData;
    private final Class<?> classData;

    BlacklistSection(String stringData, Class<?> classData) {
        this.stringData = stringData;
        this.classData = classData;
    }

    /**
     * Get the class.
     *
     * @return the class.
     */
    @Override
    public @NotNull Class<?> getClassData() {
        return classData;
    }

    /**
     * This method will get the stored data.
     * The data will always be of the same type
     * annotated by this interface and should
     * never be null.
     *
     * @return The data.
     */
    @Override
    public @NotNull String getStringData() {
        return stringData;
    }
}
