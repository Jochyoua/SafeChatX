package studio.thevipershow.safechatdownloader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public final class ColoredLogger {

    private static final ColoredLogger coloredLoggerInstance = new ColoredLogger();
    private final ConsoleCommandSender commandSender = Bukkit.getConsoleSender();

    public static synchronized ColoredLogger getInstance() {
        return coloredLoggerInstance;
    }

    private static String colorStr(@NotNull String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    enum IHateJava14 {
        WARN, INFO
    }

    private void logLevel(@NotNull IHateJava14 iHateJava14, @NotNull String msg) {
        if (iHateJava14 == IHateJava14.WARN) {
            commandSender.sendMessage(colorStr(SafeChatDownloader.PREFIX + "&c" + msg));
        } else if (iHateJava14 == IHateJava14.INFO) {
            commandSender.sendMessage(colorStr(SafeChatDownloader.PREFIX + msg));
        }
    }

    public void info(@NotNull String msg) {
        logLevel(IHateJava14.INFO, msg);
    }

    public void warn(@NotNull String msg) {
        logLevel(IHateJava14.WARN, msg);
    }
}
