package studio.thevipershow.safechat.api.checks;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatData {

    private final Player player;
    private final String message;
    private final long sentAt;
    public ChatData(@NotNull Player player, @NotNull String message, long sentAt) {
        this.player = player;
        this.message = message;
        this.sentAt = sentAt;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public long getSentAt() {
        return sentAt;
    }
}
