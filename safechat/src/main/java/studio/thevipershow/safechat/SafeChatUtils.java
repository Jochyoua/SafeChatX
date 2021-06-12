package studio.thevipershow.safechat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.safechat.SafeChat;

public final class SafeChatUtils {

    private SafeChatUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    public static final char DEFAULT_COLOR_CHAR = 0x26;

    @NotNull
    public static String color(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes(DEFAULT_COLOR_CHAR, string);
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
            sender.sendMessage(color(SafeChat.PREFIX + "&cYou are missing permission &7" + permission));
            return false;
        }
    }

    private static CommandMap commandMap;

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
