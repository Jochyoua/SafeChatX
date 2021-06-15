package studio.thevipershow.safechat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.api.checks.ChatCheck;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public final class SafeChatUtils {

    public static final char DEFAULT_COLOR_CHAR = 0x26;
    private static CommandMap commandMap;

    private SafeChatUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    @NotNull
    public static String color(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes(DEFAULT_COLOR_CHAR, string);
    }

    public static void logMessage(ChatCheck aClass, Player player, String message) {
        if (!aClass.getLoggingEnabled()) {
            return;
        }
        Logger logger = Logger.getLogger(aClass.getName() + " check");
        try {
            File file = new File(SafeChat.getPlugin(SafeChat.class).getDataFolder(), "logs/checkLogs.log");
            File directory = new File(SafeChat.getPlugin(SafeChat.class).getDataFolder(), "logs/");
            if (!directory.exists() & !file.mkdirs()) {
                return;
            }
            if (!file.exists() && !file.createNewFile()) {
                return;
            }
            FileHandler fileHandler = new FileHandler(file.getPath(), true);
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord logRecord) {
                    ZonedDateTime zdt = ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(logRecord.getMillis()), ZoneId.systemDefault());
                    String source;
                    source = logRecord.getLoggerName();
                    String message = formatMessage(logRecord);
                    return String.format("%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%n",
                            zdt,
                            source,
                            logRecord.getLoggerName(),
                            logRecord.getLevel().getLocalizedName(),
                            message);
                }
            });

            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.INFO);

            logger.log(Level.INFO, player.getName() + ": " + message + "\\n");
            fileHandler.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @NotNull
    public static List<String> getStrings(@NotNull TomlArray array) {
        int size = array.size();
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(array.getString(i));
        }
        return list;
    }

    public static boolean permissionCheck(@NotNull String permission, @NotNull CommandSender sender) {
        if (sender.hasPermission(permission)) {
            return true;
        } else {
            sender.sendMessage(color(SafeChat.getLocale().getString("missing_permission").replaceAll("(?i)\\{prefix}", SafeChat.getLocale().getString("prefix"))
                    .replaceAll("(?i)\\{permission}", permission)));
            return false;
        }
    }

    /**
     * This methods allows to use the CommandMap on different forks
     * This method has been provided by electroniccat , thanks to him!
     *
     * @return the CommandMap
     * @throws NoSuchFieldException   if field isn't found
     * @throws IllegalAccessException if access is invalid
     */
    public static CommandMap getCommandMap() throws NoSuchFieldException, IllegalAccessException {
        if (commandMap == null) {
            Class<? extends Server> serverClass = Bukkit.getServer().getClass();
            Field commandMapField = serverClass.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        }
        return commandMap;
    }

}
