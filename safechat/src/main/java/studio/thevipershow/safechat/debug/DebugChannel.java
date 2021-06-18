package studio.thevipershow.safechat.debug;

import org.jetbrains.annotations.NotNull;

public enum DebugChannel {
    HIBERNATE("safechat:hibernate"),
    COMMAND("safechat:command"),
    CHAT("safechat:chat"),
    CONFIG("safechat:config"),
    DEBUGGER("safechat:debugger");

    private final String channelName;

    DebugChannel(String channelName) {
        this.channelName = channelName;
    }

    @NotNull
    public final String getChannelName() {
        return channelName;
    }
}
