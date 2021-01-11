package studio.thevipershow.safechatdownloader;

import jdk.jfr.internal.LogLevel;
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

    private void logLevel(@NotNull LogLevel logLevel, @NotNull String msg) {
        if (logLevel == LogLevel.INFO) {
            commandSender.sendMessage(colorStr(SafeChatDownloader.PREFIX + "&c" + msg));
        } else if (logLevel == LogLevel.WARN) {
            commandSender.sendMessage(colorStr(SafeChatDownloader.PREFIX + msg));
        }
    }

    public void info(@NotNull String msg) {
        logLevel(LogLevel.INFO, msg);
    }

    public void warn(@NotNull String msg) {
        logLevel(LogLevel.WARN, msg);
    }
}
