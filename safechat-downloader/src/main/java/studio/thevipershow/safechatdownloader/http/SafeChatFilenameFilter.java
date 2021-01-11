package studio.thevipershow.safechatdownloader.http;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class SafeChatFilenameFilter implements FilenameFilter {

    public static final Pattern SAFECHAT_PLUGIN_PATTERN = Pattern.compile("^safechat-[0-9]\\.[0-9]+\\.[0-9]+(.*)?\\.jar$");

    @Override
    public boolean accept(File dir, String name) {
        return SAFECHAT_PLUGIN_PATTERN.matcher(name).matches();
    }
}
