package studio.thevipershow.safechat;

import co.aikar.commands.PaperCommandManager;
import java.util.Objects;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.safechat.api.checks.ChecksContainer;
import studio.thevipershow.safechat.chat.check.types.AddressCheck;
import studio.thevipershow.safechat.chat.check.types.FloodCheck;
import studio.thevipershow.safechat.chat.check.types.RepetitionCheck;
import studio.thevipershow.safechat.chat.check.types.WordsBlacklistCheck;
import studio.thevipershow.safechat.chat.listeners.ChatListener;
import studio.thevipershow.safechat.commands.SafeChatCommand;
import studio.thevipershow.safechat.config.Configurations;
import studio.thevipershow.safechat.config.address.AddressConfig;
import studio.thevipershow.safechat.config.blacklist.BlacklistConfig;
import studio.thevipershow.safechat.config.checks.CheckConfig;
import studio.thevipershow.safechat.config.messages.MessagesConfig;
import studio.thevipershow.safechat.debug.Debugger;
import studio.thevipershow.safechat.persistence.SafeChatHibernate;
import studio.thevipershow.vtc.PluginConfigurationsData;
import studio.thevipershow.vtc.PluginsConfigurationsManager;

/**
 * Main class of this plugin.
 */
public final class SafeChat extends JavaPlugin {

    private static final String VAULT_NAME = "Vault";
    public static final short PLUGIN_ID = 9876;
    public static final String PREFIX = "&8[&6SafeChat&8] ";
    private PluginsConfigurationsManager configManager;
    private PluginConfigurationsData<SafeChat> configData;
    private ChecksContainer checksContainer;
    private Economy economy;
    private ChatListener chatListener;
    private Metrics metrics;
    private PaperCommandManager paperCommandManager;
    private SafeChatHibernate safeChatHibernate;
    private Debugger debugger;
    private SafeChatCommand safeChatCommand;

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
    }

    @SuppressWarnings("unchecked")
    private void setupChecksContainer() {
        checksContainer = ChecksContainer.getInstance(this);
        PluginConfigurationsData<SafeChat> configData = Objects.requireNonNull(getConfigData());
        MessagesConfig messagesConfig = Objects.requireNonNull(configData.getConfig(Configurations.MESSAGES));
        CheckConfig checkConfig = Objects.requireNonNull(configData.getConfig(Configurations.CHECKS_SETTINGS));
        AddressConfig addressConfig = Objects.requireNonNull(configData.getConfig(Configurations.ADDRESS));
        BlacklistConfig blacklistConfig = Objects.requireNonNull(configData.getConfig(Configurations.BLACKLIST));

        AddressCheck addressCheck = new AddressCheck(addressConfig, checkConfig, messagesConfig);
        FloodCheck floodCheck = new FloodCheck(checkConfig, messagesConfig);
        RepetitionCheck repetitionCheck = new RepetitionCheck(checkConfig, messagesConfig);
        WordsBlacklistCheck wordsBlacklistCheck = new WordsBlacklistCheck(blacklistConfig, checkConfig, messagesConfig);

        checksContainer.register(addressCheck);
        checksContainer.register(floodCheck);
        checksContainer.register(repetitionCheck);
        checksContainer.register(wordsBlacklistCheck);
    }

    private void setupListeners() {
        PluginManager pManager = getServer().getPluginManager();
        chatListener = new ChatListener(safeChatHibernate, checksContainer);
        pManager.registerEvents(chatListener, this);
    }

    private void setupCommands() {
        paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.enableUnstableAPI("help");
        safeChatCommand = new SafeChatCommand(this);
        paperCommandManager.registerCommand(safeChatCommand);
        // paperCommandManager.registerCommand(...);
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
        setupMetrics();
        if (setupEconomy()) {
            getLogger().warning("Vault not present, cannot use economy functionalities.");
        }

        setupConfigs();
        setupHibernate();
        setupChecksContainer();
        setupCommands();
        setupListeners();
    }

    @Override
    public void onDisable() {
        safeChatHibernate.shutdown();
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
    public PaperCommandManager getPaperCommandManager() {
        return paperCommandManager;
    }

    @NotNull
    public SafeChatHibernate getSafeChatHibernate() {
        return safeChatHibernate;
    }
}
