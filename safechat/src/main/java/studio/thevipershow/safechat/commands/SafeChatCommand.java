package studio.thevipershow.safechat.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.Check;
import studio.thevipershow.safechat.api.checks.ChecksContainer;
import studio.thevipershow.safechat.persistence.mappers.PlayerDataManager;
import studio.thevipershow.safechat.persistence.types.PlayerData;
import studio.thevipershow.vtc.PluginConfigurationsData;

public class SafeChatCommand extends Command {

    private final SafeChat safeChat;

    public SafeChatCommand(@NotNull SafeChat safeChat) {
        super("safechat");
        this.safeChat = safeChat;
    }

    public static void onHelp(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color("&7&lSafeChat's Help Page&7:")); // │ ├
        sender.sendMessage(SafeChatUtils.color("&7CHEATSHEET:"));
        sender.sendMessage(SafeChatUtils.color(" - &7<&6arg&7> &f= &7required arg"));
        sender.sendMessage(SafeChatUtils.color(" - &7(&6arg&7) &f= &7optional arg"));
        sender.sendMessage(SafeChatUtils.color("&7  │"));
        sender.sendMessage(SafeChatUtils.color("&7  ├─ &8[&esafechat help&8]"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &8[Permission&8]&7: &o&fnone"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fUsed to open this help page."));
        sender.sendMessage(SafeChatUtils.color("&7  │"));
        sender.sendMessage(SafeChatUtils.color("&7  ├─ &8[&esafechat reload&8]"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &8[Permission&8]&7:"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fsafechat.commands.reload"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fUsed to reload configurations data."));
        sender.sendMessage(SafeChatUtils.color("&7  │"));
        sender.sendMessage(SafeChatUtils.color("&7  ├─ &8[&esafechat flags &8(&6flag-name&8) &8<&6player&8>&8]"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &8[Permission&8]&7:"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fsafechat.commands.flags"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fUsed to lookup at someone flags data."));
    }

    public final void reloadCommand(@NotNull CommandSender sender) {
        if (SafeChatUtils.permissionCheck("safechat.commands.reload", sender)) {
            long operationStartTime = System.nanoTime();
            sender.sendMessage(SafeChatUtils.color(SafeChat.PREFIX + "&7The plugin is going to be reloaded..."));

            PluginConfigurationsData<SafeChat> data = safeChat.getConfigData();
            data.exportAndLoadAllLoadedConfigs(false); // storing new values.

            float timeTaken = (System.nanoTime() - operationStartTime) / 1E6F;
            sender.sendMessage(SafeChatUtils.color(String.format("    &7The configurations have been reloaded in &6%.1f&7ms", timeTaken)));
        }
    }

    public static void unknownCommand(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(SafeChat.PREFIX + "&cYou have used an unknown argument."));
    }

    public static void unknownAmountOfArgs(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(SafeChat.PREFIX + "&cYou have used too many arguments."));
    }

    public void flagsTypeSearchCommand(@NotNull CommandSender commandSender, @NotNull String flagType, @NotNull String playerName) {
        if (getAvailableCheckNamesList().contains(flagType)) {
            PlayerDataManager dataManager = safeChat.getSafeChatHibernate().getPlayerDataManager();

            if (dataManager == null) {
                return;
            }

            final PlayerData playerData = dataManager.getPlayerData(playerName);
            if (playerData != null) {
                int flagAmount;
                Map<String, Integer> playerFlags = playerData.getFlagsMap();
                flagAmount = playerFlags.getOrDefault(flagType, 0);
                commandSender.sendMessage(SafeChatUtils.color(String.format("&7Player &e%s &7has &a%d &7flags of type &e%s", playerName, flagAmount, flagType)));
            } else {
                commandSender.sendMessage(SafeChatUtils.color("&7That player was not present in the database."));
            }
        } else {
            commandSender.sendMessage(SafeChatUtils.color(String.format("&7The check &e%s &7does not exist!", flagType)));
        }
    }

    public void withThreeArgs(@NotNull CommandSender commandSender, @NotNull final String[] args) {
        if ("flags".equals(args[0].toLowerCase(Locale.ROOT))) {
            if (SafeChatUtils.permissionCheck("safechat.commands.flags", commandSender)) {
                flagsTypeSearchCommand(commandSender, args[1], args[2]);
            }
        } else {
            unknownCommand(commandSender);
        }
    }

    public void allFlagsSearchCommand(@NotNull CommandSender commandSender, @NotNull String playerName) {
        PlayerDataManager dataManager = safeChat.getSafeChatHibernate().getPlayerDataManager();
        if (dataManager == null) {
            return;
        }
        final PlayerData playerData = dataManager.getPlayerData(playerName);
        if (playerData != null) {
            Map<String, Integer> playerFlags = playerData.getFlagsMap();
            playerFlags.forEach((k, v) -> {
                commandSender.sendMessage(SafeChatUtils.color(String.format("&7Player &e%s &7has &a%d &7flags of type &e%s", playerName, v, k)));
            });
        } else {
            commandSender.sendMessage(SafeChatUtils.color("&7That player was not present in the database."));
        }
    }

    public void withTwoArgs(@NotNull CommandSender commandSender, @NotNull final String[] args) {
        if (args[0].equalsIgnoreCase("flags")) {
            if (SafeChatUtils.permissionCheck("safechat.commands.flags", commandSender)) {
                allFlagsSearchCommand(commandSender, args[1]);
            }
        } else {
            unknownCommand(commandSender);
        }
    }

    public void withOneArgs(@NotNull String firstArg, @NotNull CommandSender sender) {
        switch (firstArg.toLowerCase(Locale.ROOT)) {
            case "reload":
                reloadCommand(sender);
                break;
            case "help":
                onHelp(sender);
                break;
            default:
                unknownCommand(sender);
                break;
        }
    }

    @Override
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        final int arguments = args.length;
        switch (arguments) {
            case 0:
                onHelp(sender);
                break;
            case 1:
                withOneArgs(args[0], sender);
                break;
            case 2:
                withTwoArgs(sender, args);
                break;
            case 3:
                withThreeArgs(sender, args);
                break;
            default:
                unknownAmountOfArgs(sender);
                break;
        }

        return true;
    }

    private final static List<String> BASE_ARGS = Arrays.asList("help", "reload", "flags");

    private List<String> getAvailableCheckNamesList() {
        ChecksContainer checksContainer = ChecksContainer.getInstance(safeChat);
        return checksContainer.getActiveChecks().stream().map(Check::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        final int length = args.length;
        switch (length) {
            case 1:
                return BASE_ARGS;
            case 2: {
                if (args[0].equalsIgnoreCase("flags")) {
                    return getAvailableCheckNamesList();
                }
            }
            break;
            default:
                break;
        }
        return Collections.emptyList();
    }
}
