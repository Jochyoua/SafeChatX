package studio.thevipershow.safechat;

import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.api.checks.ChecksContainer;
import studio.thevipershow.safechat.chat.check.types.AddressCheck;
import studio.thevipershow.safechat.chat.check.types.CapsCheck;
import studio.thevipershow.safechat.chat.check.types.FloodCheck;
import studio.thevipershow.safechat.chat.check.types.RepetitionCheck;
import studio.thevipershow.safechat.chat.check.types.WordsBlacklistCheck;
import studio.thevipershow.safechat.chat.listeners.ChatListener;
import studio.thevipershow.safechat.commands.SafeChatCommand;
import studio.thevipershow.safechat.config.Configurations;
import studio.thevipershow.safechat.config.address.AddressConfig;
import studio.thevipershow.safechat.config.blacklist.BlacklistConfig;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.localization.Localization;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.debug.Debugger;
import studio.thevipershow.safechat.persistence.SafeChatHibernate;
import studio.thevipershow.vtc.PluginConfigurationsData;
import studio.thevipershow.vtc.PluginsConfigurationsManager;

import java.util.Objects;

/**
 * Main class of this plugin.
 */
public final class SafeChat extends JavaPlugin {

    public static final short PLUGIN_ID = 9876;

    private static final String VAULT_NAME = "Vault";
    public static Localization localization;

    private PluginsConfigurationsManager configManager;
    private PluginConfigurationsData<SafeChat> configData;

    private ChecksContainer checksContainer;
    private Economy economy;
    private ChatListener chatListener;
    private Metrics metrics;
    private SafeChatHibernate safeChatHibernate;
    private Debugger debugger;

    private SafeChatCommand safechatCommand;

    public static Localization getLocale() {
        return localization;
    }

    private void setupMetrics() {
        metrics = new Metrics(this, PLUGIN_ID);
    }

    private boolean setupEconomy() {
        Server server = getServer();
        if (server.getPluginManager().getPlugin(VAULT_NAME) == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyRsp = server.getServicesManager().getRegistration(Economy.class);
        if (economyRsp == null) {
            return false;
        }
        economy = economyRsp.getProvider();
        return economy != null;
    }

    private void setupConfigs() {
        configManager = PluginsConfigurationsManager.getInstance();
        PluginConfigurationsData<SafeChat> pluginData = Objects.requireNonNull(configManager.loadPluginData(this));
        this.configData = pluginData;
        pluginData.setConsoleDebuggingInfo(true);
        pluginData.loadAllConfigs(Configurations.class);
        pluginData.exportAndLoadAllLoadedConfigs(false);
        localization.loadTranslation(Objects.requireNonNull(pluginData.getConfig(Configurations.MESSAGES)));
    }

    @SuppressWarnings("unchecked")
    private void setupChecksContainer() {
        checksContainer = ChecksContainer.getInstance(this);
        PluginConfigurationsData<SafeChat> pluginConfigurationsData = Objects.requireNonNull(getConfigData());
        MessagesConfig messagesConfig = Objects.requireNonNull(pluginConfigurationsData.getConfig(Configurations.MESSAGES));
        CheckConfig checkConfig = Objects.requireNonNull(pluginConfigurationsData.getConfig(Configurations.CHECKS_SETTINGS));
        AddressConfig addressConfig = Objects.requireNonNull(pluginConfigurationsData.getConfig(Configurations.ADDRESS));
        BlacklistConfig blacklistConfig = Objects.requireNonNull(pluginConfigurationsData.getConfig(Configurations.BLACKLIST));

        AddressCheck addressCheck = new AddressCheck(addressConfig, checkConfig, messagesConfig);
        FloodCheck floodCheck = new FloodCheck(checkConfig, messagesConfig);
        RepetitionCheck repetitionCheck = new RepetitionCheck(checkConfig, messagesConfig);
        WordsBlacklistCheck wordsBlacklistCheck = new WordsBlacklistCheck(blacklistConfig, checkConfig, messagesConfig);
        CapsCheck capsCheck = new CapsCheck(checkConfig, messagesConfig);

        checksContainer.register(addressCheck);
        checksContainer.register(floodCheck);
        checksContainer.register(repetitionCheck);
        checksContainer.register(wordsBlacklistCheck);
        checksContainer.register(capsCheck);
    }

    private void setupListeners() {
        PluginManager pManager = getServer().getPluginManager();
        chatListener = new ChatListener(safeChatHibernate, checksContainer);
        pManager.registerEvents(chatListener, this);
    }

    private void setupCommands() {
        safechatCommand = new SafeChatCommand(this);
        try {
            CommandMap commandMap = SafeChatUtils.getCommandMap();
            commandMap.register("safechat", safechatCommand);
        } catch (Exception e) {
            getLogger().warning("Could not register the \"safechat\" command.");
        }
    }

    @SuppressWarnings("unchecked")
    private void setupHibernate() {
        safeChatHibernate = new SafeChatHibernate(Objects.requireNonNull(configData.getConfig(Configurations.DATABASE_SETTINGS)), this);
        safeChatHibernate.setupHibernateSQLMapping();
        safeChatHibernate.setupSessionFactory();
        safeChatHibernate.setupPlayerDataManager();
    }

    private void setupDebugger() {
        debugger = Debugger.getInstance(getLogger());
    }

    @Override
    public void onEnable() {
        localization = new Localization();
        setupMetrics();
        setupDebugger();
        setupConfigs();
        if (!setupEconomy()) {
            getLogger().warning("Vault not present, cannot use economy functionalities.");
        }
        setupHibernate();
        setupChecksContainer();
        setupCommands();
        setupListeners();
    }

    private void unregisterCommands() {
        try {
            final CommandMap map = SafeChatUtils.getCommandMap();
            if (!safechatCommand.unregister(map)) {
                getLogger().warning("Could not unregister \"safechat\" command");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().warning("Could not get command map!");
        }
    }

    @Override
    public void onDisable() {
        safeChatHibernate.shutdown();
        unregisterCommands();
    }

    @NotNull
    public PluginsConfigurationsManager getConfigManager() {
        return configManager;
    }

    @NotNull
    public PluginConfigurationsData<SafeChat> getConfigData() {
        return configData;
    }

    @NotNull
    public ChecksContainer getChecksContainer() {
        return checksContainer;
    }

    @NotNull
    public Economy getEconomy() {
        return economy;
    }

    @NotNull
    public ChatListener getChatListener() {
        return chatListener;
    }

    @NotNull
    public Metrics getMetrics() {
        return metrics;
    }

    @NotNull
    public SafeChatHibernate getSafeChatHibernate() {
        return safeChatHibernate;
    }

    public Debugger getDebugger() {
        return debugger;
    }
}
