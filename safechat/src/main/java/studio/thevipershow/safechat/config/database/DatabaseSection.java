package studio.thevipershow.safechat.config.database;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.SectionType;

public enum DatabaseSection implements SectionType {
    SQL_FLAVOR("database.sql-flavor", String.class),
    USERNAME("database.username", String.class),
    PASSWORD("database.password", String.class),
    DATABASE_NAME("database.db-name", String.class),
    PORT("database.port", Long.class),
    ADDRESS("database.address", String.class),
    TIMEOUT("database.timeout", Long.class),
    FILEPATH("database.filepath", String.class);

    private final String stringData;
    private final Class<?> classData;

    DatabaseSection(String stringData, Class<?> classData) {
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
