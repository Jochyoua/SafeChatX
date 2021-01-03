package studio.thevipershow.safechat.chat.check;

import java.util.HashSet;
import java.util.Set;
import studio.thevipershow.safechat.SafeChat;

public final class ChecksContainer {

    private static ChecksContainer instance = null;
    private final SafeChat safeChat;

    public ChecksContainer(SafeChat safeChat) {
        this.safeChat = safeChat;
    }

    public static ChecksContainer getInstance(SafeChat safeChat) {
        if (instance == null) {
            instance = new ChecksContainer(safeChat);
        }
        return instance;
    }

    private final Set<ChatCheck> activeChecks = new HashSet<>();

    public final boolean register(ChatCheck check) {
        return this.activeChecks.add(check);
    }

    public final boolean unregister(ChatCheck check) {
        return this.activeChecks.remove(check);
    }

    public final void registerAllDefault() {
        for (CheckType checkType : CheckType.values()) {
            register(checkType.create(safeChat));
        }
    }

    public Set<ChatCheck> getActiveChecks() {
        return activeChecks;
    }
}
