package studio.thevipershow.safechat.commands;

import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.SafeChatUtils;
import studio.thevipershow.vtc.PluginConfigurationsData;

public class SafeChatCommand extends Command {

    private final SafeChat safeChat;

    public SafeChatCommand(@NotNull SafeChat safeChat) {
        super("safechat");
        this.safeChat = safeChat;
    }

    public static void onHelp(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color("&eSafeChat's Help Page&7:")); // │ ├
        sender.sendMessage(SafeChatUtils.color("&7  │"));
        sender.sendMessage(SafeChatUtils.color("&7  ├─ &8[&esafechat help&8]"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &8[Permission&8]&7: &o&fnone"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fUsed to open this help page."));
        sender.sendMessage(SafeChatUtils.color("&7  │"));
        sender.sendMessage(SafeChatUtils.color("&7  ├─ &8[&esafechat reload&8]"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &8[Permission&8]&7:"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fsafechat.commands.reload"));
        sender.sendMessage(SafeChatUtils.color("&7  │  &o&fUsed to reload configurations data."));
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
            default:
                unknownAmountOfArgs(sender);
                break;
        }

        return true;
    }
}
