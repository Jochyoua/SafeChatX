package studio.thevipershow.safechat.debug;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Debugger {

    private static Debugger instance = null;
    private final Logger safechatLogger;
    private boolean enabled = false;

    private Debugger(@NotNull Logger safechatLogger) {
        this.safechatLogger = Objects.requireNonNull(safechatLogger);
    }

    public static synchronized Debugger getInstance(@NotNull Logger logger) {
        if (instance == null) {
            instance = new Debugger(logger);
        }
        return instance;
    }

    private void informateChannel(final Level level, final DebugChannel debugChannel, final String msg) {
        safechatLogger.log(level, debugChannel.getChannelName() + " -> " + msg);
    }

    @SuppressWarnings("ConstantConditions")
    public void infoChannel(@NotNull DebugChannel debugChannel, @NotNull String message) {
        if (debugChannel == null && message != null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to log \"" + message + "\" into a null debug channel.");
        } else if (message == null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to debug with a null message.");
        } else {
            informateChannel(Level.INFO, debugChannel, message);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void warnChannel(@NotNull DebugChannel debugChannel, @NotNull String message) {
        if (debugChannel == null && message != null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to log \"" + message + "\" into a null debug channel.");
        } else if (message == null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to debug with a null message.");
        } else {
            informateChannel(Level.WARNING, debugChannel, message);
        }
    }

    @NotNull
    public Logger getSafechatLogger() {
        return safechatLogger;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
