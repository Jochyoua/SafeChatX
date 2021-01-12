package studio.thevipershow.safechatdownloader;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechatdownloader.config.DefaultDownloaderConfig;
import studio.thevipershow.safechatdownloader.http.Downloader;

public final class SafeChatDownloader extends JavaPlugin {

    public final static String PREFIX = "&8[&eSafeChat-Downloader&8]&7: ";

    private File pluginFolder;
    private Downloader downloader;
    private ColoredLogger coloredLogger;
    private DefaultDownloaderConfig defaultConfig;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupFolder() {
        pluginFolder = new File(getServer().getWorldContainer(),"plugins");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        } else if (!pluginFolder.isDirectory()) {
            throw new IllegalStateException("Plugins folder should've been a directory!");
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

    private void setupConfigs() {
        saveDefaultConfig();
        defaultConfig = new DefaultDownloaderConfig(this);
        defaultConfig.storeData();
    }

    @Override
    public void onEnable() {
        coloredLogger = ColoredLogger.getInstance();
        setupFolder();
        setupConfigs();
        setupDownloader();
        beginDownloading();
    }

    @NotNull
    public File getPluginFolder() {
        return pluginFolder;
    }

    @NotNull
    public DefaultDownloaderConfig getDefaultConfig() {
        return defaultConfig;
    }
}
