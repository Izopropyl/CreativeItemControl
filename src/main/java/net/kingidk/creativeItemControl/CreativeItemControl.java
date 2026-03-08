package net.kingidk.creativeItemControl;

import net.kingidk.creativeItemControl.Handlers.AttributeAction;
import net.kingidk.creativeItemControl.Handlers.EnchantAction;
import net.kingidk.creativeItemControl.Listeners.CreativeListener;
import net.kingidk.creativeItemControl.Listeners.InventoryClickListener;
import net.kingidk.creativeItemControl.Listeners.InventorySlotListener;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CreativeItemControl extends JavaPlugin {

    public boolean masterEnabled;
    public boolean attributesEnabled;
    public boolean enchantmentsEnabled;
    public boolean potionsEnabled;
    public boolean enchantmentsAllowIncompatible;
    public AttributeAction attributesAction;
    public EnchantAction enchantmentsAction;
    public List<String> worlds;
    public boolean worldsBlacklist;
    public boolean playerAlerts;
    private final Map<Material, ItemMeta> defaultMetaCache = new EnumMap<>(Material.class);

    public ItemMeta getDefaultMeta(Material type) {
        return defaultMetaCache.computeIfAbsent(type, t -> new ItemStack(t, 1).getItemMeta());
    }

    public void loadConfigCache() {
        masterEnabled = getConfig().getBoolean("toggles.master");
        attributesEnabled = getConfig().getBoolean("toggles.attributes");
        enchantmentsEnabled = getConfig().getBoolean("toggles.enchantments");
        potionsEnabled = getConfig().getBoolean("toggles.potions");
        attributesAction = AttributeAction.valueOf(getConfig().getString("attributes.action", "REMOVE"));
        enchantmentsAction = EnchantAction.valueOf(getConfig().getString("enchantments.action", "REMOVE"));
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
        getServer().getPluginManager().registerEvents(new InventorySlotListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getCommand("cic").setExecutor(new Command(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
