package studio.thevipershow.safechat.persistence.mappers;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.persistence.types.PlayerData;

public final class PlayerDataManager {

    private final SessionFactory sessionFactory;
    private final SafeChat safeChat;

    public PlayerDataManager(@NotNull SessionFactory sessionFactory, @NotNull SafeChat safeChat) {
        this.sessionFactory = sessionFactory;
        this.safeChat = safeChat;
    }

    public void addPlayerData(@NotNull UUID uuid, @NotNull String username) {
        Transaction transaction = null;
        try (final Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            final PlayerData playerData = new PlayerData();
            playerData.setUuid(uuid.toString());
            playerData.setName(username);
            session.save(playerData);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void addPlayerData(@NotNull Player player) {
        addPlayerData(player.getUniqueId(), player.getName());
    }

    public static void increasePlayerFlag(@NotNull PlayerData playerData, @NotNull String checkName) {
        final Map<String, Integer> data = playerData.getFlagsMap();
        if (data.containsKey(checkName)) {
            data.compute(checkName, (k, v) -> v = Objects.requireNonNull(v) + 1);
        } else {
            data.put(checkName, 1);
        }
    }

    public void addOrUpdatePlayerData(@NotNull Player player, @NotNull String checkName) {
        Transaction transaction = null;
        final UUID uuid = player.getUniqueId();
        try (final Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            PlayerData playerData = session.get(PlayerData.class, uuid.toString());

            if (playerData == null) {
                playerData = new PlayerData();
                playerData.setName(player.getName());
                playerData.setUuid(uuid.toString());
                increasePlayerFlag(playerData, checkName);
                session.save(playerData);
            } else {
                increasePlayerFlag(playerData, checkName);
                session.update(playerData);
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Nullable
    public PlayerData getPlayerData(@NotNull Player player) {

        final UUID uuid = player.getUniqueId();
        Transaction transaction = null;

        try (final Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            final TypedQuery<PlayerData> query = session.createQuery("SELECT a FROM PlayerData a LEFT JOIN FETCH a.flagsMap WHERE a.uuid= :uuid", PlayerData.class);
            query.setParameter("uuid", uuid.toString());

            final PlayerData resultData = query.getSingleResult();

            transaction.commit();

            return resultData;
        } catch (NoResultException noResultException) {
            return null;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public PlayerData getPlayerData(@NotNull String username) {

        Transaction transaction = null;
        try (final Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            final TypedQuery<PlayerData> query = session.createQuery("SELECT a FROM PlayerData a LEFT JOIN FETCH a.flagsMap WHERE a.name= :name", PlayerData.class);
            query.setParameter("name", username);


            final PlayerData resultData = query.getSingleResult();

            transaction.commit();

            return resultData;
        } catch (NoResultException noResultException) {
            return null;
        } catch (HibernateException exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            exception.printStackTrace();
        }

        return null;
    }

    @NotNull
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @NotNull
    public SafeChat getSafeChat() {
        return safeChat;
    }
}
