package studio.thevipershow.safechatdownloader.http;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SafeChatRelease {

    private final String url;
    private final String name;

    public SafeChatRelease(@NotNull String url, @NotNull String name) {
        this.url = Objects.requireNonNull(url);
        this.name = Objects.requireNonNull(name);
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
