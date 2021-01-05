package studio.thevipershow.safechat.persistence;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.config.database.DatabaseConfig;
import studio.thevipershow.safechat.config.database.DatabaseSection;
import studio.thevipershow.safechat.persistence.mappers.PlayerDataManager;
import studio.thevipershow.safechat.persistence.types.PlayerData;

public final class SafeChatHibernate {

    private final DatabaseConfig dbConfig;
    private final SafeChat safeChat;
    private StandardServiceRegistry stdServiceRegistry;
    private SessionFactory sessionFactory;
    private PlayerDataManager playerDataManager;
    private HibernateSQLMapping hibernateSQLMapping;

    public SafeChatHibernate(@NotNull DatabaseConfig dbConfig, @NotNull SafeChat safeChat) {
        this.dbConfig = Objects.requireNonNull(dbConfig);
        this.safeChat = Objects.requireNonNull(safeChat);
    }

    /**
     * Setups and determines the hibernate sql mapping.
     * Must be called before anything else.
     */
    public void setupHibernateSQLMapping() {
        String sqlFlavour = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.SQL_FLAVOR));
        sqlFlavour = sqlFlavour.toLowerCase(Locale.ROOT);

        for (HibernateSQLMapping mapping : HibernateSQLMapping.values()) {
            if (mapping.getSqlFlavour().equals(sqlFlavour)) {
                this.hibernateSQLMapping = mapping;
                return;
            }
        }

        throw new RuntimeException(String.format("An unknown database type has been used (%s).", sqlFlavour));
    }

    public void shutdown() {
        if (stdServiceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(stdServiceRegistry);
        }
    }

    /**
     * Setups a session factory if the hibernate mapping was valid.
     */
    public void setupSessionFactory() {
        if (this.hibernateSQLMapping == null) {
            throw new RuntimeException("Tried to setup session factory with an invalid database!");
        } else {

            try {
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

                Map settings = hibernateSQLMapping.generateProperties(dbConfig);

                registryBuilder.applySettings(settings);
                stdServiceRegistry = registryBuilder.build();
                MetadataSources metadataSources = new MetadataSources(stdServiceRegistry).addAnnotatedClass(PlayerData.class);
                Metadata metadata = metadataSources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (HibernateException e) {
                shutdown();
                e.printStackTrace();
            }
        }
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
