package studio.thevipershow.safechat.persistence;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.DB2400Dialect;
import org.hibernate.dialect.DB2400V7R3Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MariaDB53Dialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.jetbrains.annotations.NotNull;
import org.sqlite.hibernate.dialect.SQLiteDialect;
import studio.thevipershow.safechat.config.database.DatabaseConfig;
import studio.thevipershow.safechat.config.database.DatabaseSection;

public enum HibernateSQLMapping {
    /**
     * MySQL Server type.
     */
    MYSQL("mysql", "org.mysql.jdbc.Driver", generateUrl("mysql"), org.hibernate.dialect.MySQL57Dialect.class, false),
    /**
     * PostgreSQL Server type.
     */
    POSTGRESQL("postgresql", "studio.thevipershow.safechat.libs.org.postgresql.Driver", generateUrl("postgresql"), PostgreSQL95Dialect.class, false),
    /**
     * MariaDB Server type.
     */
    MARIADB("mariadb", "studio.thevipershow.safechat.libs.mariadb.jdbc.Driver", generateUrl("mariadb"), MariaDB53Dialect.class, false),
    /**
     * H2 Engine Type.
     */
    H2("h2", "studio.thevipershow.safechat.libs.org.h2.Driver", generateFileUrl("h2"), H2Dialect.class, true),
    /**
     * CockroachDB Server type.
     */
    COCKROACHDB("cockroachdb", "studio.thevipershow.safechat.libs.org.postgresql.Driver", generateUrl("postgresql"), org.hibernate.dialect.CockroachDB201Dialect.class, false),
    /**
     * HyperSQL Engine type.
     */
    HYPERSQL("hsql", "studio.thevipershow.safechat.libs.hsqldb.jdbc.JDBCDriver", generateFileUrl("hsqldb"), HSQLDialect.class, true),
    /**
     * Microsoft's SQLServer type.
     */
    SQLSERVER("sqlserver", "studio.thevipershow.safechat.libs.microsoft.sqlserver.jdbc.SQLServerDriver", generateUrl("sqlserver"), SQLServer2012Dialect.class, false),
    /**
     * IBM DB2 Server type.
     */
    DB2("db2", "studio.thevipershow.safechat.libs.com.ibm.db2.jcc.DB2Driver", generateFileUrl("db2"), DB2400V7R3Dialect.class, false),
    /**
     * SQLite (TESTING)
     */
    SQLITE("sqlite", "org.sqlite.JDBC", generateFileUrl("sqlite"), SQLiteDialect.class, true);

    private final String sqlFlavour;
    private final String driverClassName;
    private final String urlFormatter;
    private final Class<? extends Dialect> hibernateDialectClass;
    private final boolean fileBased;

    public static final String DB_TYPE_PLACEHOLDER = "{TYPE}";
    public static final String STD_JDBC_URL = "jdbc:" + DB_TYPE_PLACEHOLDER + "://%s:%d/%s";
    public static final String STD_JDBC_FILE_URL = "jdbc:" + DB_TYPE_PLACEHOLDER + ":%s";

    @NotNull
    private static String generateUrl(@NotNull String dbType) {
        return STD_JDBC_URL.replace(DB_TYPE_PLACEHOLDER, Objects.requireNonNull(dbType));
    }

    @NotNull
    private static String generateFileUrl(@NotNull String dbType) {
        return STD_JDBC_FILE_URL.replace(DB_TYPE_PLACEHOLDER, Objects.requireNonNull(dbType));
    }

    public Map<String, String> generateProperties(@NotNull DatabaseConfig dbConfig) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.dataSourceClassname", HikariCPConnectionProvider.class.getName());
        properties.put("hibernate.hikari.jdbcUrl", generateAppropriateUrl(this, dbConfig));
        properties.put("hibernate.hikari.username", dbConfig.getConfigValue(DatabaseSection.USERNAME));
        properties.put("hibernate.hikari.password", dbConfig.getConfigValue(DatabaseSection.PASSWORD));
        properties.put("hibernate.dataSourceClassName", HikariDataSource.class.getName());
        properties.put(Environment.DRIVER, driverClassName);
        // Optimizations:
        properties.put("hibernate.hikari.dataSource.cachePrepStmts", "true");
        properties.put("hibernate.hikari.dataSource.prepStmtCacheSize", "256");
        properties.put("hibernate.hikari.dataSource.useServerPrepStmts", "true");
        properties.put("hibernate.hikari.dataSource.useLocalSessionState", "true");
        properties.put("hibernate.hikari.dataSource.cacheResultSetMetadata", "true");
        properties.put("hibernate.hikari.dataSource.cacheServerConfiguration", "true");
        // End of Optimizations.
        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put(Environment.SHOW_SQL, "false");
        return properties;
    }

    @NotNull
    public static String generateAppropriateUrl(@NotNull HibernateSQLMapping hibernateSQLMapping, @NotNull DatabaseConfig dbConfig) {
        final String urlFormatter = hibernateSQLMapping.getUrlFormatter();
        if (hibernateSQLMapping.isFileBased()) {
            return String.format(urlFormatter, (String) Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.FILEPATH)));
        } else {
            String address = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.ADDRESS));
            Long port = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.PORT));
            String databaseName = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.DATABASE_NAME));

            return String.format(urlFormatter, address, port, databaseName);
        }
    }

    HibernateSQLMapping(@NotNull String sqlFlavour, @NotNull String driverClassName, @NotNull String urlFormatter, @NotNull Class<? extends Dialect> hibernateDialectClass, boolean fileBased) {
        this.sqlFlavour = sqlFlavour;
        this.driverClassName = driverClassName;
        this.urlFormatter = urlFormatter;
        this.hibernateDialectClass = hibernateDialectClass;
        this.fileBased = fileBased;
    }

    @NotNull
    public String getSqlFlavour() {
        return sqlFlavour;
    }

    @NotNull
    public String getDriverClassName() {
        return driverClassName;
    }

    @NotNull
    public String getUrlFormatter() {
        return urlFormatter;
    }

    @NotNull
    public Class<? extends Dialect> getHibernateDialectClass() {
        return hibernateDialectClass;
    }

    public boolean isFileBased() {
        return fileBased;
    }
}
