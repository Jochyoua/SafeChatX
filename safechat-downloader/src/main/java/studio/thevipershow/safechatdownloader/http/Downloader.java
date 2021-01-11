package studio.thevipershow.safechatdownloader.http;

import com.google.gson.JsonParser;
import java.io.File;
import java.io.FilenameFilter;
import java.time.Duration;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechatdownloader.ColoredLogger;
import studio.thevipershow.safechatdownloader.SafeChatDownloader;

@SuppressWarnings("FieldCanBeLocal")
public final class Downloader {

    private final ColoredLogger coloredLogger = ColoredLogger.getInstance();
    private static Downloader downloaderInstance;
    private final SafeChatDownloader safeChatDownloader;
    private final JsonParser jsonParser = new JsonParser();
    private final OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(Duration.ofMillis(1500L)).build();

    private Downloader(@NotNull SafeChatDownloader safeChatDownloader) {
        this.safeChatDownloader = Objects.requireNonNull(safeChatDownloader);
        safechatReleasesGetRequest = new Request.Builder()
                .header("User-Agent", "SafeChat-Downloader/" + safeChatDownloader.getDescription().getVersion())
                .url(SAFECHAT_GITHUB_RELEASES_API_URL)
                .get()
                .build();
    }

    @NotNull
    public static Downloader getInstance(@NotNull SafeChatDownloader safeChatDownloader) {
        if (downloaderInstance == null) {
            downloaderInstance = new Downloader(safeChatDownloader);
        }
        return downloaderInstance;
    }

    public static final String SAFECHAT_GITHUB_RELEASES_API_URL = "https://api.github.com/repos/TheViperShow/SafeChatX/releases";
    public static final FilenameFilter SAFECHAT_FILENAME_FILTER = new SafeChatFilenameFilter();

    private final Request safechatReleasesGetRequest;

    public boolean isSafeChatAlreadyDownloaded() {
        coloredLogger.info("Checking for SafeChat JARs...");
        File pluginsFolder = safeChatDownloader.getPluginFolder();
        File[] files = pluginsFolder.listFiles(SAFECHAT_FILENAME_FILTER);
        return files.length == 1;
    }

    protected void downloadJARFromUrl(@NotNull SafeChatRelease release) {
        final Request jarDownloadRequest = new Request.Builder()
                .header("User-Agent", "SafeChat-Downloader/" + safeChatDownloader.getDescription().getDescription())
                .url(release.getUrl())
                .get()
                .build();

        httpClient.newCall(jarDownloadRequest)
                .enqueue(new JarSaverCallback(safeChatDownloader, this, release));
    }

    private boolean isSafeChatPresentRuntime() {
        return safeChatDownloader.getServer().getPluginManager().getPlugin("SafeChat") != null;
    }



    public void startDownload() {
        if (isSafeChatAlreadyDownloaded()) {
            coloredLogger.info("A valid SafeChat JAR was found into the plugin, checking if it's loaded.");
            if (isSafeChatPresentRuntime()) {
                coloredLogger.info("SafeChat is already loaded, shutting down ...");
                safeChatDownloader.getServer().getPluginManager().disablePlugin(safeChatDownloader);
                return;
            }
            return;
        } else {
            coloredLogger.info("No SafeChat plugins were downloaded, we will start a download for latest version.");
        }

        httpClient.newCall(Objects.requireNonNull(safechatReleasesGetRequest, "There was an error with the request"))
                .enqueue(new JarDownloadedCallback(safeChatDownloader, this));
    }

    @NotNull
    public JsonParser getJsonParser() {
        return jsonParser;
    }
}
