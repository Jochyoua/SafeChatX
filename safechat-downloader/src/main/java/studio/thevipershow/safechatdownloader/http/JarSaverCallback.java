package studio.thevipershow.safechatdownloader.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechatdownloader.ColoredLogger;
import studio.thevipershow.safechatdownloader.SafeChatDownloader;

public class JarSaverCallback implements Callback {

    private final SafeChatDownloader safeChatDownloader;
    private final Downloader downloader;
    private final SafeChatRelease safeChatRelease;
    private final ColoredLogger coloredLogger = ColoredLogger.getInstance();
    private File pluginFile;

    public JarSaverCallback(@NotNull SafeChatDownloader safeChatDownloader, @NotNull Downloader downloader, @NotNull SafeChatRelease safeChatRelease) {
        this.safeChatDownloader = Objects.requireNonNull(safeChatDownloader);
        this.downloader = Objects.requireNonNull(downloader);
        this.safeChatRelease = Objects.requireNonNull(safeChatRelease);
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        coloredLogger.warn("Something has went wrong when downloading JAR from GitHub releases:");
        e.printStackTrace();
    }

    private void attemptLoadPlugin() {
        PluginLoader pluginLoader = safeChatDownloader.getPluginLoader();
        coloredLogger.info("&7SafeChat has been downloaded, we are trying to load it.");
        try {
            Plugin plugin = pluginLoader.loadPlugin(pluginFile);
            coloredLogger.info("&7SafeChat has been loaded correctly!");
        } catch (InvalidPluginException e) {
            coloredLogger.warn("We could not load SafeChat JAR...");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) {
        final ResponseBody responseBody = response.body();

        final File jarFile = new File(safeChatDownloader.getPluginFolder(), safeChatRelease.getName());

        coloredLogger.info("&7Checking and verifying JAR file data...");

        if (!jarFile.isFile()) {
            throw new IllegalStateException("The downloaded should have been a file type.");
        } else if (!jarFile.getName().endsWith(".jar")) {
            throw new IllegalStateException("The downloaded file should've been a JAR archive.");
        } else if (jarFile.exists()) {
            return;
        }

        coloredLogger.info("&7JAR data was valid, proceeding with copy...");

        setPluginFile(jarFile);
        long startMillis = System.currentTimeMillis();

        try (InputStream inputStream = responseBody.byteStream();
             FileOutputStream fileOutputStream = new FileOutputStream(jarFile)) {

            int length;
            final byte[] bytes = new byte[65536];

            while ((length = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        final float timeTaken = (System.currentTimeMillis() - startMillis) / 1e3f;
        coloredLogger.info(String.format("&7The JAR has been successfully been copied in &a%.2f &7seconds", timeTaken));
    }

    public void setPluginFile(@NotNull File pluginFile) {
        this.pluginFile = pluginFile;
    }
}
