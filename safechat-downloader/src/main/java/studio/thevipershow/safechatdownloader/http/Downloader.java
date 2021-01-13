package studio.thevipershow.safechatdownloader.http;

import com.google.gson.JsonParser;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.thevipershow.safechatdownloader.ColoredLogger;
import studio.thevipershow.safechatdownloader.DownloaderUtils;
import studio.thevipershow.safechatdownloader.SafeChatDownloader;

@SuppressWarnings("FieldCanBeLocal")
public class Downloader {

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

        JarSaverCallback saverCallback = null;
        try {
            Response response = httpClient.newCall(jarDownloadRequest).execute();
            saverCallback = new JarSaverCallback(safeChatDownloader, release);
            saverCallback.onResponse(response);
        } catch (IOException e) {
            if (saverCallback != null) {
                saverCallback.onFailure(e);
            } else {
                e.printStackTrace();
            }
        }
    }

    private Plugin getSafeChatRuntime() {
        return safeChatDownloader.getServer().getPluginManager().getPlugin("SafeChat");
    }

    private boolean isSafeChatPresentRuntime(@Nullable Plugin safechat) {
        return safechat != null;
    }

    private static final Pattern GET_VERSION = Pattern.compile("[0-9]\\.[0-9]+\\.[0-9]+");

    @NotNull
    private static String getVersionFromName(@NotNull String string) throws IllegalStateException {
        Matcher matcher = GET_VERSION.matcher(string);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new IllegalStateException("Plugin release name did not have a version? " + string);
        }
    }

    private static final Pattern SPLIT_VERSION = Pattern.compile("\\.");

    private static boolean isFirstVersionNewer(@NotNull String firstVersion, @NotNull String secondVersion) throws IllegalStateException {
        final String[] firstStringSplit = SPLIT_VERSION.split(firstVersion);
        final String[] secondStringSplit = SPLIT_VERSION.split(secondVersion);
        if (firstStringSplit.length != 3 || secondStringSplit.length != 3) {
            throw new IllegalStateException("Malformed SafeChat release version.");
        } else {
            final int[] firstVersionInt = new int[3];
            final int[] secondVersionInt = new int[3];
            for (int i = 0; i < 3; i++) {
                firstVersionInt[i] = Integer.parseInt(firstStringSplit[i]);
                secondVersionInt[i] = Integer.parseInt(secondStringSplit[i]);
            }

            final int f1 = firstVersionInt[0], f2 = firstVersionInt[1], f3 = firstVersionInt[2];
            final int s1 = secondVersionInt[0], s2 = secondVersionInt[1], s3 = secondVersionInt[2];
            if (f1 >= s1) {
                if (f2 >= s2) {
                    return f3 > s3;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public void deleteSafeChatWithVersion(@NotNull String version) {
        coloredLogger.info("Trying to delete outdated JARs...");
        File pluginsFolder = safeChatDownloader.getPluginFolder();
        File[] files = pluginsFolder.listFiles(SAFECHAT_FILENAME_FILTER);
        for (final File file : files) {
            final String name = file.getName();
            final String versionStr = getVersionFromName(name);
            // TODO: proper checking
            try {
                if (Files.deleteIfExists(file.toPath())) {
                    coloredLogger.info("Deleted SafeChat JAR " + name);
                } else {
                    coloredLogger.warn("Could not delete SafeChat JAR " + name);
                }
            } catch (IOException e) {
                coloredLogger.warn("Could not delete SafeChat JAR " + name);
                e.printStackTrace();
            }
        }
    }

    public void startDownload() {
        JarDownloadedCallback jarDownloadedCallback = null;
        try {
            jarDownloadedCallback = new JarDownloadedCallback(safeChatDownloader, this);

            final Response response = httpClient.newCall(Objects.requireNonNull(safechatReleasesGetRequest, "There was an error with the request!"))
                    .execute();

            SafeChatRelease latestRelease = jarDownloadedCallback.onResponse(response);
            if (latestRelease != null) {

                if (isSafeChatAlreadyDownloaded()) {
                    final boolean shouldUpdate = safeChatDownloader.getDefaultConfig().isAutoUpdate();
                    coloredLogger.info("&7Another SafeChat JAR has been found, " +
                            (shouldUpdate ? "we are going to try and update it" : "auto-update is disabled: exiting process."));
                    if (shouldUpdate) {
                        coloredLogger.info("A valid SafeChat JAR was found into the plugin, checking if it's loaded.");
                        Plugin safechatInstance = getSafeChatRuntime();
                        if (isSafeChatPresentRuntime(safechatInstance)) {
                            coloredLogger.info("SafeChat is already loaded, checking version...");

                            final String currentVersion = getVersionFromName(safechatInstance.getDescription().getVersion());
                            final String latestVersion = getVersionFromName(latestRelease.getName());

                            if (isFirstVersionNewer(latestVersion, currentVersion)) {
                                coloredLogger.info("We have found a newer version &e" + latestVersion);

                                DownloaderUtils.unload(safechatInstance);

                                deleteSafeChatWithVersion(currentVersion);

                                downloadJARFromUrl(latestRelease);
                            } else {
                                coloredLogger.info("You have the latest version available &e" + latestVersion);
                            }
                        }
                    }
                } else {
                    coloredLogger.info("No SafeChat plugins were downloaded, we will start a download for latest version.");
                    downloadJARFromUrl(latestRelease);
                }
            } else {
                coloredLogger.warn("Something has went wrong with the download.");
            }
        } catch (IOException e) {
            if (jarDownloadedCallback != null) {
                jarDownloadedCallback.onFailure(e);
            } else {
                e.printStackTrace();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public JsonParser getJsonParser() {
        return jsonParser;
    }
}
