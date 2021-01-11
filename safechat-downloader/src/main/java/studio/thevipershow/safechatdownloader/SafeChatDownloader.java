package studio.thevipershow.safechatdownloader;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechatdownloader.http.Downloader;

public final class SafeChatDownloader extends JavaPlugin {

    public final static String PREFIX = "&8[&eSafeChat-Downloader&8]&7: ";

    private File pluginFolder;
    private Downloader downloader;
    private ColoredLogger coloredLogger;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupFolder() {
        pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
    }

    private void setupDownloader() {
        if (downloader == null) {
            downloader = Downloader.getInstance(this);
        } else {
            coloredLogger.warn("Do not reload your server or this plugin!");
            coloredLogger.warn("This plugin will shutdown.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void beginDownloading() {
        if (downloader != null) {
            downloader.startDownload();
        } else {
            coloredLogger.warn("The downloader was null, something has went wrong?");
        }
    }

    @Override
    public void onEnable() {
        coloredLogger = ColoredLogger.getInstance();
        setupFolder();
        setupDownloader();
        beginDownloading();
    }

    @NotNull
    public File getPluginFolder() {
        return pluginFolder;
    }
}
