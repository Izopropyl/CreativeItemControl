package net.kingidk.creativeItemControl;

import org.bukkit.plugin.java.JavaPlugin;

public final class CreativeItemControl extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new CreativeListener(this), this);
        getCommand("cc").setExecutor(new Command(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
