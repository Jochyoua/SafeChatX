package studio.thevipershow.safechatdownloader.config;

import java.util.Objects;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechatdownloader.SafeChatDownloader;

public class DefaultDownloaderConfig {

    private final SafeChatDownloader safeChatDownloader;

    public DefaultDownloaderConfig(@NotNull SafeChatDownloader safeChatDownloader) {
        this.safeChatDownloader = Objects.requireNonNull(safeChatDownloader);
    }

    public void storeData() {
        FileConfiguration configuration = safeChatDownloader.getConfig();
        autoUpdate = configuration.getBoolean("auto-update", false);
    }

    private boolean autoUpdate = false;

    public boolean isAutoUpdate() {
        return autoUpdate;
    }
}
