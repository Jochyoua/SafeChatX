package studio.thevipershow.safechat;

import java.util.Objects;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import studio.thevipershow.safechat.chat.check.ChecksContainer;
import studio.thevipershow.safechat.chat.listeners.ChatListener;
import studio.thevipershow.safechat.config.Configurations;
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

    private void setupChecksContainer() {
        checksContainer = ChecksContainer.getInstance(this);
        checksContainer.registerAllDefault();
    }

    private void setupListeners() {
        PluginManager pManager = getServer().getPluginManager();
        chatListener = new ChatListener(this, checksContainer);

        pManager.registerEvents(chatListener, this);
    }

    @Override
    public void onEnable() {
        setupMetrics();
        if (setupEconomy()) {
            getLogger().info("Vault not present, cannot use economy functionalities.");
        }
        setupConfigs();
        setupChecksContainer();
        setupListeners();
    }

    public PluginsConfigurationsManager getConfigManager() {
        return configManager;
    }

    public PluginConfigurationsData<SafeChat> getConfigData() {
        return configData;
    }

    public ChecksContainer getChecksContainer() {
        return checksContainer;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ChatListener getChatListener() {
        return chatListener;
    }

    public Metrics getMetrics() {
        return metrics;
    }
}
