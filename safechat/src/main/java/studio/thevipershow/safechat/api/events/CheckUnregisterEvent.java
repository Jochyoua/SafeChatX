package studio.thevipershow.safechat.api.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.api.checks.Check;

/**
 * This check is called when a check gets unregistered.
 */
public class CheckUnregisterEvent extends ChatCheckEvent {

    public static final HandlerList handlerList = new HandlerList();

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     *
     * @param check the check.
     */
    public CheckUnregisterEvent(@NotNull Check check) {
        super(check, false);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @NotNull
    public HandlerList getHandlerList() {
        return handlerList;
    }
}
