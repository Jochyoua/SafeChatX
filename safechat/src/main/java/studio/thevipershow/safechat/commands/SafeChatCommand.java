package studio.thevipershow.safechat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.safechat.api.checks.Check;
import studio.thevipershow.safechat.api.checks.ChecksContainer;
import studio.thevipershow.safechat.config.Configurations;
import studio.thevipershow.safechat.config.localization.Localization;
import studio.thevipershow.safechat.persistence.mappers.PlayerDataManager;
import studio.thevipershow.safechat.persistence.types.PlayerData;
import studio.thevipershow.vtc.PluginConfigurationsData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static studio.thevipershow.safechat.SafeChat.getLocale;


public class SafeChatCommand extends Command {

    private final static List<String> BASE_ARGS = Arrays.asList("help", "reload", "flags", "version");
    private final SafeChat safeChat;

    public SafeChatCommand(@NotNull SafeChat safeChat) {
        super("safechat");
        this.safeChat = safeChat;
    }

    public static void onHelp(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(SafeChat.getLocale().getString("help_command")));
    }

    public static void onVersion(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(SafeChat.getLocale().getString("version_command"))
                .replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")).replaceAll("(?i)\\{version}", SafeChat.getPlugin(SafeChat.class).getDescription().getVersion())
                .replaceAll("(?i)\\{server_version}", Bukkit.getServer().getVersion()));
    }

    public static void unknownCommand(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(SafeChat.getLocale().getString("unknown_command").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
    }

    public static void unknownAmountOfArgs(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(SafeChat.getLocale().getString("too_many_arguments").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
    }

    public final void reloadCommand(@NotNull CommandSender sender) {
        if (SafeChatUtils.permissionCheck("safechat.commands.reload", sender)) {
            long operationStartTime = System.nanoTime();
            sender.sendMessage(SafeChatUtils.color(SafeChat.getLocale().getString("reload_begin").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));

            PluginConfigurationsData<SafeChat> data = safeChat.getConfigData();
            data.exportAndLoadAllLoadedConfigs(false); // storing new values.

            ResourceBundle.clearCache();
            Localization localization = new Localization();
            localization.loadTranslation(Objects.requireNonNull(safeChat.getConfigData().getConfig(Configurations.MESSAGES)));
            SafeChat.localization = localization;

            float timeTaken = (System.nanoTime() - operationStartTime) / 1E6F;
            sender.sendMessage(SafeChatUtils.color(String.format(SafeChat.getLocale().getString("reload_finish").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), timeTaken)));
        }
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
                commandSender.sendMessage(SafeChatUtils.color(String.format(SafeChat.getLocale().getString("flag_information").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), playerName, flagAmount, flagType)));
            } else {
                commandSender.sendMessage(SafeChatUtils.color(SafeChat.getLocale().getString("not_found_in_database").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
            }
        } else {
            commandSender.sendMessage(SafeChatUtils.color(String.format(SafeChat.getLocale().getString("check_does_not_exist").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), flagType)));
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
            playerFlags.forEach((k, v) -> commandSender.sendMessage(SafeChatUtils.color(String.format(SafeChat.getLocale().getString("flag_information").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), playerName, v, k))));
        } else {
            commandSender.sendMessage(SafeChatUtils.color(SafeChat.getLocale().getString("not_found_in_database").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
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
            case "version":
                onVersion(sender);
                break;
            default:
                unknownCommand(sender);
                break;
        }
    }

    @Override
    public boolean execute(final @NotNull CommandSender sender, final @NotNull String label, final String[] args) {
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

    private List<String> getAvailableCheckNamesList() {
        ChecksContainer checksContainer = ChecksContainer.getInstance(safeChat);
        return checksContainer.getActiveChecks().stream().map(Check::getName).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final String[] args) throws IllegalArgumentException {
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
