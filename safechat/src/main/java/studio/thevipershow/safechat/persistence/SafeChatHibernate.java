package studio.thevipershow.safechat.persistence;

import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.postgresql.Driver;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.config.database.DatabaseConfig;
import studio.thevipershow.safechat.config.database.DatabaseSection;
import studio.thevipershow.safechat.persistence.mappers.PlayerDataManager;
import studio.thevipershow.safechat.persistence.sqlite.SQLiteDialect;
import studio.thevipershow.safechat.persistence.types.PlayerData;

public final class SafeChatHibernate {

    private final DatabaseConfig dbConfig;
    private final SafeChat safeChat;
    private SessionFactory sessionFactory;
    private PlayerDataManager playerDataManager;

    public SafeChatHibernate(@NotNull DatabaseConfig dbConfig, @NotNull SafeChat safeChat) {
        this.dbConfig = Objects.requireNonNull(dbConfig);
        this.safeChat = Objects.requireNonNull(safeChat);
    }

    @Nullable
    public static String translateSQLFlavourToDriverClassname(DatabaseConfig dbConfig) {
        String sqlFlavor = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.SQL_FLAVOR));
        switch (sqlFlavor.toLowerCase(Locale.ROOT)) {
            case "mysql":
                try {
                    return Class.forName("com.mysql.jdbc.Driver").getName();
                } catch (Exception ignored) {
                }
            case "sqlite":
                try {
                    return Class.forName("org.sqlite.JDBC").getName();
                } catch (Exception ignored) {
                }
            case "postgresql":
                return Driver.class.getName();
            case "mariadb":
                return org.mariadb.jdbc.Driver.class.getName();
            case "h2":
                return org.h2.Driver.class.getName();
            default:
                return null;
        }
    }

    public static final String JDBC_FORMAT = "jdbc:%s://%s:%d/%s";

    @Nullable
    public String generateJdbcURL(DatabaseConfig dbConfig) {
        String sqlFlavor = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.SQL_FLAVOR));
        String address = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.ADDRESS));
        Long port = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.PORT));
        String databaseName = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.DATABASE_NAME));

        return String.format(JDBC_FORMAT, sqlFlavor, address, port, databaseName);
    }

    @Nullable
    public static String getHibernateDialect(@NotNull DatabaseConfig databaseConfig) {
        String sqlFlavour = Objects.requireNonNull(databaseConfig.getConfigValue(DatabaseSection.SQL_FLAVOR));

        switch (sqlFlavour.toLowerCase(Locale.ROOT)) {
            case "mysql":
                return MySQL57Dialect.class.getName();
            case "postgresql":
                return PostgreSQL82Dialect.class.getName();
            case "mariadb":
                return MariaDBDialect.class.getName();
            case "sqlite":
                return SQLiteDialect.class.getName();
            case "h2":
                return H2Dialect.class.getName();
            default:
                return null;
        }
    }

    public enum HibernateProperty {
        CONNECTION_DRIVER_CLASS("hibernate.connection.driver_class"),
        CONNECTION_URL("hibernate.connection.url"),
        CONNECTION_USERNAME("hibernate.connection.username"),
        CONNECTION_PASSWORD("hibernate.connection.password"),
        DIALECT("hibernate.dialect"),
        HBM2DDL("hibernate.hbm2ddl.auto");

        HibernateProperty(String property) {
            this.property = property;
        }

        public final String property;
    }

    @NotNull
    public Properties buildConnectionProperties() {
        Properties properties = new Properties();
        properties.setProperty(HibernateProperty.CONNECTION_DRIVER_CLASS.property, Objects.requireNonNull(translateSQLFlavourToDriverClassname(dbConfig)));
        properties.setProperty(HibernateProperty.CONNECTION_URL.property, Objects.requireNonNull(generateJdbcURL(dbConfig)));
        properties.setProperty(HibernateProperty.CONNECTION_USERNAME.property, Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.USERNAME)));
        properties.setProperty(HibernateProperty.CONNECTION_PASSWORD.property, Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.PASSWORD)));
        properties.setProperty(HibernateProperty.DIALECT.property, Objects.requireNonNull(getHibernateDialect(dbConfig)));
        properties.setProperty(HibernateProperty.HBM2DDL.property, "update");
        return properties;
    }

    public void setupSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperties(buildConnectionProperties());
        configuration.addAnnotatedClass(PlayerData.class);
        this.sessionFactory = Objects.requireNonNull(configuration.buildSessionFactory());
    }

    public void setupPlayerDataManager() {
        playerDataManager = new PlayerDataManager(Objects.requireNonNull(sessionFactory), safeChat);
    }

    @Nullable
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Nullable
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
