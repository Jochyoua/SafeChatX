package studio.thevipershow.safechat.chat.listeners;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.api.events.PlayerFailCheckEvent;
import studio.thevipershow.safechat.chat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.ChatData;
import studio.thevipershow.safechat.api.checks.Check;
import studio.thevipershow.safechat.api.checks.ChecksContainer;
import studio.thevipershow.safechat.persistence.SafeChatHibernate;
import studio.thevipershow.safechat.persistence.mappers.PlayerDataManager;
import studio.thevipershow.safechat.persistence.types.PlayerData;

public final class ChatListener implements Listener {

    private final SafeChat safeChat;
    private final static String DATA_MANAGER_ABSENT = "SafeChat's Hibernate PlayerDataManager wasn't configured yet!";

    public ChatListener(SafeChatHibernate safeChatHibernate, ChecksContainer checksContainer) {
        this.safeChatHibernate = safeChatHibernate;
        this.playerDataManager = Objects.requireNonNull(safeChatHibernate.getPlayerDataManager(), DATA_MANAGER_ABSENT);
        this.checksContainer = checksContainer;
        this.safeChat = safeChatHibernate.getSafeChat();
    }

    private final SafeChatHibernate safeChatHibernate;
    private final PlayerDataManager playerDataManager;
    private final ChecksContainer checksContainer;

    private static void sendWarning(@NotNull Check check, @NotNull ChatData data) {
        if (!check.hasWarningEnabled()) {
            return;
        }

        Player player = data.getPlayer();

        for (String msg : check.getWarningMessages()) {
            String message = check.replacePlaceholders(Objects.requireNonNull(msg), data);
            player.sendMessage(SafeChatUtils.color(message));
        }
    }

    private void checkFlagsAmount(@NotNull Check check, @NotNull ChatData chatData) {
        String checkName = check.getName();
        PlayerData playerData = playerDataManager.getPlayerData(chatData.getPlayer());
        final int flagAmount;
        if (playerData == null) {
            flagAmount = 1;
        } else {

            Integer i = playerData.getFlagsMap().get(checkName);
            if (i == null) {
                flagAmount = 1;
            } else {
                flagAmount = i;
            }
        }

        if (flagAmount % check.getPunishmentRequiredValue() == 0) {
            dispatchCommands(check, chatData);
        }
    }

    private void dispatchCommands(@NotNull Check check, @NotNull ChatData chatData) {
        Server server = safeChat.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        String formattedCommand = check.replacePlaceholders(check.getPunishmentCommand(), chatData);
        server.dispatchCommand(console, formattedCommand);
    }

    private void updateData(@NotNull Player player, @NotNull String checkName) {
        playerDataManager.addOrUpdatePlayerData(player, checkName);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        ChatData data = new ChatData(event.getPlayer(), event.getMessage(), System.currentTimeMillis());

        Collection<Check> sortedPriorityChecks = checksContainer.getActiveChecks().stream()
                .sorted(ChecksContainer.CHECK_PRIORITY_COMPARATOR)
                .collect(Collectors.toList());

        for (Check check : sortedPriorityChecks) {
            if (check.check(data)) {
                PlayerFailCheckEvent playerFailCheckEvent = new PlayerFailCheckEvent(check, data);
                safeChat.getServer().getPluginManager().callEvent(playerFailCheckEvent);

                if (playerFailCheckEvent.isCancelled()) {
                    continue;
                }

                event.setCancelled(true);
                sendWarning(check, data);
                updateData(data.getPlayer(), check.getName());
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onPlayerFailCheck(PlayerFailCheckEvent event) {
        BukkitScheduler scheduler = safeChat.getServer().getScheduler();
        scheduler.runTask(safeChat, () -> this.checkFlagsAmount(event.getCheck(), event.getChatData()));
    }

    @NotNull
    public SafeChatHibernate getSafeChatHibernate() {
        return safeChatHibernate;
    }

    @NotNull
    public SafeChat getSafeChat() {
        return safeChat;
    }

    @NotNull
    public ChecksContainer getChecksContainer() {
        return checksContainer;
    }
}
