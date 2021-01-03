package studio.thevipershow.safechat.chat.check;

/**
 * A check with a type of data to check.
 * @param <T> The data type.
 */
public interface Check<T> {

    boolean check(T t);
}
