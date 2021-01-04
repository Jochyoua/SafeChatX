package studio.thevipershow.safechat.persistence.mappers;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.chat.check.CheckType;
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
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            PlayerData playerData = new PlayerData();
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

    public static void increasePlayerFlag(@NotNull PlayerData playerData, @NotNull CheckType checkType) {
        switch (checkType) {
            case FLOOD:
                playerData.setFloodFlags(1 + playerData.getFloodFlags());
                break;
            case WORDS:
                playerData.setBlacklistFlags(1 + playerData.getBlacklistFlags());
                break;
            case ADDRESS:
                playerData.setAddressFlags(1 + playerData.getAddressFlags());
                break;
            case REPETITION:
                playerData.setRepetitionFlags(1 + playerData.getRepetitionFlags());
                break;
            default:
                break;
        }
    }

    public void addOrUpdatePlayerData(@NotNull Player player, @NotNull CheckType checkType) {
        Transaction transaction = null;
        UUID uuid = player.getUniqueId();
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            PlayerData playerData = session.get(PlayerData.class, uuid.toString());

            if (playerData == null) {
                playerData = new PlayerData();
                playerData.setName(player.getName());
                playerData.setUuid(uuid.toString());
                increasePlayerFlag(playerData, checkType);
                session.save(playerData);
            } else {
                increasePlayerFlag(playerData, checkType);
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
        UUID uuid = player.getUniqueId();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            PlayerData playerData = session.get(PlayerData.class, uuid);
            transaction.commit();

            return playerData;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
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
