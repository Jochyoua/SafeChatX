package studio.thevipershow.safechatdownloader.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.thevipershow.safechatdownloader.ColoredLogger;
import studio.thevipershow.safechatdownloader.SafeChatDownloader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class JarDownloadedCallback {

    private final ColoredLogger coloredLogger = ColoredLogger.getInstance();
    private final Downloader downloader;

    public JarDownloadedCallback(@NotNull SafeChatDownloader safeChatDownloader, @NotNull Downloader downloader) {
        Objects.requireNonNull(safeChatDownloader);
        this.downloader = Objects.requireNonNull(downloader);
    }

    public void onFailure(@NotNull IOException e) {
        coloredLogger.warn("Something has wrong when contacting GitHub releases API:");
        e.printStackTrace();
    }

    @Nullable
    public SafeChatRelease onResponse(@NotNull Response response) throws IOException {
        final String jsonBody = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
        final JsonParser parser = downloader.getJsonParser();
        final JsonElement body = parser.parse(jsonBody);

        coloredLogger.info("&7The JSON parsing operation will begin.");

        if (!body.isJsonArray()) {
            throw new IllegalStateException("JSON request body was malformed.");
        }

        final JsonArray uploadsArray = body.getAsJsonArray();

        for (final JsonElement uploadedResource : uploadsArray) {
            if (!uploadedResource.isJsonObject()) {
                throw new IllegalStateException("update resource should have been object.");
            }

            final JsonObject bodyObject = uploadedResource.getAsJsonObject();
            final JsonElement assetsElement = bodyObject.get("assets");
            if (!assetsElement.isJsonArray()) {
                throw new IllegalStateException("JSON assets was not a json array type.");
            }
            final JsonArray assetsArray = assetsElement.getAsJsonArray();

            for (final JsonElement asset : assetsArray) {
                if (!asset.isJsonObject()) {
                    throw new IllegalStateException("Asset array contained a non-object element.");
                } else {
                    final JsonObject assetObject = asset.getAsJsonObject();

                    final JsonElement nameElement = assetObject.get("name");
                    if (!nameElement.isJsonPrimitive()) {
                        throw new IllegalStateException("name element should have been a primitive type.");
                    }

                    final String assetName = nameElement.getAsString();

                    if (!SafeChatFilenameFilter.SAFECHAT_PLUGIN_PATTERN.matcher(assetName).matches()) {
                        continue;
                    }

                    final JsonElement urlElement = assetObject.get("browser_download_url");

                    if (!urlElement.isJsonPrimitive()) {
                        throw new IllegalStateException("name element should've been a primitive json type.");
                    }

                    final String url = urlElement.getAsString();
                    final SafeChatRelease chosenRelease = new SafeChatRelease(url, assetName);

                    coloredLogger.info("&7The JSON parsing operation has finished!");
                    coloredLogger.info("&7Found this release:");
                    coloredLogger.info("  - &7JAR URL: &e" + chosenRelease.getUrl());
                    coloredLogger.info("  - &7Version: &e" + chosenRelease.getName());

                    // downloader.downloadJARFromUrl(chosenRelease);
                    return chosenRelease;
                }
            }
        }
        return null;
    }
}
