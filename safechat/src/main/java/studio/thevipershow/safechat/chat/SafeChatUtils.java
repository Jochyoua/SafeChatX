package studio.thevipershow.safechat.chat;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;

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
}
