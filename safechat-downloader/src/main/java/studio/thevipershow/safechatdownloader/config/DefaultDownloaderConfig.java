package studio.thevipershow.safechatdownloader.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechatdownloader.SafeChatDownloader;

import java.util.Objects;

public class DefaultDownloaderConfig {

    private final SafeChatDownloader safeChatDownloader;
    private boolean autoUpdate = false;

    public DefaultDownloaderConfig(@NotNull SafeChatDownloader safeChatDownloader) {
        this.safeChatDownloader = Objects.requireNonNull(safeChatDownloader);
    }

    public void storeData() {
        FileConfiguration configuration = safeChatDownloader.getConfig();
        autoUpdate = configuration.getBoolean("auto-update", false);
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }
}
