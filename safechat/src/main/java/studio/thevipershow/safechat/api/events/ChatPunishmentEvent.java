package studio.thevipershow.safechat.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.api.checks.Check;

/**
 * An event called when a check punishment is going to be triggered.
 * Cancelling this event will cause the punishment to not be executed
 * anymore.
 */
public class ChatPunishmentEvent extends ChatCheckEvent implements Cancellable {

    public static final HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     *
     * @param check
     * @
     */
    public ChatPunishmentEvent(@NotNull Check check) {
        super(check, false);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
