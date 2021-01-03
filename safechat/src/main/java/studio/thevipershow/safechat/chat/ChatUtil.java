package studio.thevipershow.safechat.chat;

import org.bukkit.ChatColor;

public final class ChatUtil {

    public static final char DEFAULT_COLOR_CHAR = 0x26;

    public static String color(final String string) {
        return ChatColor.translateAlternateColorCodes(DEFAULT_COLOR_CHAR, string);
    }
}
