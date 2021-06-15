package studio.thevipershow.safechat.config.localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class FileResClassLoader extends ClassLoader {
    // SOURCE: https://github.com/EssentialsX/Essentials/blob/8b23c2c4cd140afc0ed697ec4f691a3d7295682e/Essentials/src/main/java/com/earth2me/essentials/I18n.java
    private final File dataFolder;

    FileResClassLoader(final ClassLoader classLoader, final File dataFolder) {
        super(classLoader);
        this.dataFolder = dataFolder;
    }

    @Override
    public URL getResource(final String string) {
        final File file = new File(dataFolder, string);
        if (file.exists()) {
            try {
                return file.toURI().toURL();
            } catch (final MalformedURLException ignored) {
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(final String string) {
        final File file = new File(dataFolder, string);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (final FileNotFoundException ignored) {
            }
        }
        return null;
    }
}