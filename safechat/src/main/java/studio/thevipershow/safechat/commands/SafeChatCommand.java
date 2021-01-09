package studio.thevipershow.safechat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.chat.SafeChatUtils;
import studio.thevipershow.vtc.PluginConfigurationsData;

@CommandAlias("safechat")
public class SafeChatCommand extends BaseCommand {

    private final SafeChat safeChat;

    public SafeChatCommand(@NotNull SafeChat safeChat) {
        this.safeChat = safeChat;
    }

    @HelpCommand
    public static void onHelp(@NotNull CommandSender sender, @NotNull CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("safechat.commands.reload")
    public void reloadCommand(@NotNull CommandSender sender) {
        long operationStartTime = System.nanoTime();
        sender.sendMessage(SafeChatUtils.color(SafeChat.PREFIX + "&7The plugin is going to be reloaded..."));

        PluginConfigurationsData<SafeChat> data = safeChat.getConfigData();
        // data.loadAllConfigs(Configurations.class);
        data.exportAndLoadAllLoadedConfigs(false);

        float timeTaken = (System.nanoTime() - operationStartTime) / 1E6F;
        sender.sendMessage(SafeChatUtils.color(String.format("    &7The configurations have been reloaded in &6%.1f&7ms", timeTaken)));
    }
}
