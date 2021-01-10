package studio.thevipershow.safechat.api.checks;

import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public abstract class ChatCheck implements Check {

    public static final Pattern DOMAIN_REGEX = Pattern.compile("[a-z0-9-]{3,}\\.[a-z]{2,}", Pattern.CASE_INSENSITIVE);
    public static final Pattern IPV4_REGEX = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
    public static final Pattern SPLIT_SPACE = Pattern.compile("\\s+");
    public static final String PLAYER_PLACEHOLDER = "{PLAYER}";
    public static final String PREFIX_PLACEHOLDER = "{PREFIX}";

    private final String checkName;
    private final CheckPriority.Priority priority;

    public ChatCheck() {
        final Class<? extends ChatCheck> namedCheckClass = getClass();
        if (!namedCheckClass.isAnnotationPresent(CheckName.class)) {
            throw new UnsupportedOperationException("This check is not annotated with '@CheckName' annotation!");
        } else {
            this.checkName = Objects.requireNonNull(namedCheckClass.getAnnotation(CheckName.class).name());
        }
        if (namedCheckClass.isAnnotationPresent(CheckPriority.class)) {
            priority = namedCheckClass.getAnnotation(CheckPriority.class).priority();
        } else {
            priority =  CheckPriority.Priority.NORMAL;
        }
    }

    @Override
    public final @NotNull String getName() {
        return checkName;
    }

    @Override
    public final CheckPriority.Priority getCheckPriority() {
        return priority;
    }
}
