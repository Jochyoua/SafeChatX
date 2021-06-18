package studio.thevipershow.safechat.config.address;

import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.vtc.SectionType;

public enum AddressSection implements SectionType {
    ALLOWED_DOMAINS("domains.allowed", TomlArray.class),
    ALLOWED_ADDRESSES("address.allowed", TomlArray.class);

    private final String stringData;
    private final Class<?> classData;

    AddressSection(String stringData, Class<?> classData) {
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
