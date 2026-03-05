package net.kingidk.creativeItemControl;

import net.kingidk.creativeItemControl.Listeners.CreativeListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class CreativeItemControl extends JavaPlugin {

    public boolean masterEnabled;
    public boolean attributesEnabled;
    public boolean enchantmentsEnabled;
    public boolean enchantmentsAllowIncompatible;
    public String attributesAction;
    public String enchantmentsAction;
    public List<String> worlds;
    public boolean worldsBlacklist;
    public boolean playerAlerts;

    public void loadConfigCache() {
        masterEnabled = getConfig().getBoolean("toggles.master");
        attributesEnabled = getConfig().getBoolean("toggles.attributes");
        enchantmentsEnabled = getConfig().getBoolean("toggles.enchantments");
        attributesAction = getConfig().getString("attributes.action");
        enchantmentsAction = getConfig().getString("enchantments.action");
        enchantmentsAllowIncompatible = getConfig().getBoolean("enchantments.allow-incompatible");
        worlds = getConfig().getStringList("config.worlds");
        worldsBlacklist = getConfig().getBoolean("config.blacklist");
        playerAlerts = getConfig().getBoolean("toggles.playeralerts");
    }










    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        loadConfigCache();

        getServer().getPluginManager().registerEvents(new CreativeListener(this), this);
        getCommand("cic").setExecutor(new Command(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
