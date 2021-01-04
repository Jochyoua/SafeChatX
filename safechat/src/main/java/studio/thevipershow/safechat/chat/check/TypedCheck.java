package studio.thevipershow.safechat.chat.check;

import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.messages.MessagesConfig;


public abstract class TypedCheck<T> implements Check<T> {

    protected final CheckType checkType;
    protected final CheckConfig checkConfig;
    protected final MessagesConfig messagesConfig;

    public TypedCheck(CheckType checkType, CheckConfig checkConfig, MessagesConfig messagesConfig) {
        this.checkType = checkType;
        this.checkConfig = checkConfig;
        this.messagesConfig = messagesConfig;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public CheckConfig getCheckConfig() {
        return checkConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }
}
