package studio.thevipershow.safechat.api.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.api.checks.Check;

/**
 * This event is called when a check has been registered.
 */
public class CheckRegisterEvent extends ChatCheckEvent {

    public static final HandlerList handlerList = new HandlerList();

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     *
     * @param check the check.
     */
    public CheckRegisterEvent(@NotNull Check check) {
        super(check, false);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public final @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
