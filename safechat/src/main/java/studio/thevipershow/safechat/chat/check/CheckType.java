package studio.thevipershow.safechat.chat.check;

import studio.thevipershow.safechat.SafeChat;
import studio.thevipershow.safechat.chat.check.types.AddressCheckTyped;
import studio.thevipershow.safechat.chat.check.types.FloodCheckTyped;
import studio.thevipershow.safechat.chat.check.types.RepetitionCheck;
import studio.thevipershow.safechat.chat.check.types.WordsBlacklistCheckTyped;
import studio.thevipershow.safechat.config.Configurations;
import studio.thevipershow.safechat.config.address.AddressConfig;
import studio.thevipershow.safechat.config.blacklist.BlacklistConfig;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.vtc.PluginConfigurationsData;

public enum CheckType {

    WORDS("Words") {
        @Override
        public final ChatCheck create(SafeChat safeChat) {
            PluginConfigurationsData<SafeChat> configData = safeChat.getConfigData();
            BlacklistConfig blacklistConfig = configData.getConfig(Configurations.BLACKLIST);
            CheckConfig checkConfig = configData.getConfig(Configurations.CHECKS_SETTINGS);
            MessagesConfig messagesConfig = configData.getConfig(Configurations.MESSAGES);
            return new WordsBlacklistCheckTyped(blacklistConfig, checkConfig, messagesConfig);
        }
    },

    ADDRESS("Address") {
        @Override
        public final ChatCheck create(SafeChat safeChat) {
            PluginConfigurationsData<SafeChat> configData = safeChat.getConfigData();
            AddressConfig addressConfig = configData.getConfig(Configurations.ADDRESS);
            CheckConfig checkConfig = configData.getConfig(Configurations.CHECKS_SETTINGS);
            MessagesConfig messagesConfig = configData.getConfig(Configurations.MESSAGES);
            return new AddressCheckTyped(addressConfig, checkConfig, messagesConfig);
        }
    },

    REPETITION("Repetition") {
        @Override
        public final ChatCheck create(SafeChat safeChat) {
            PluginConfigurationsData<SafeChat> configData = safeChat.getConfigData();
            MessagesConfig messagesConfig = configData.getConfig(Configurations.MESSAGES);
            return new RepetitionCheck(configData.getConfig(Configurations.CHECKS_SETTINGS), messagesConfig);
        }
    },

    FLOOD("Flood") {
        @Override
        public final ChatCheck create(SafeChat safeChat) {
            PluginConfigurationsData<SafeChat> configData = safeChat.getConfigData();
            MessagesConfig messagesConfig = configData.getConfig(Configurations.MESSAGES);
            return new FloodCheckTyped(configData.getConfig(Configurations.CHECKS_SETTINGS), messagesConfig);
        }
    };

    CheckType(final String checkName) {
        this.checkName = checkName;
    }

    public abstract ChatCheck create(SafeChat safeChat);

    private final String checkName;

    public final String getCheckName() {
        return checkName;
    }
}
