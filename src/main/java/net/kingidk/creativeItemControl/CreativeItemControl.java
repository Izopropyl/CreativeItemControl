package net.kingidk.creativeItemControl;

import net.kingidk.creativeItemControl.Enums.AttributeAction;
import net.kingidk.creativeItemControl.Enums.EnchantAction;
import net.kingidk.creativeItemControl.Listeners.CreativeListener;
import net.kingidk.creativeItemControl.Listeners.InventoryClickListener;
import net.kingidk.creativeItemControl.Listeners.InventorySlotListener;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class CreativeItemControl extends JavaPlugin {

    public boolean masterEnabled;
    public boolean attributesEnabled;
    public boolean enchantmentsEnabled;
    public boolean potionsEnabled;
    public boolean componentsEnabled;
    public boolean enchantmentsAllowIncompatible;
    public AttributeAction attributesAction;
    public EnchantAction enchantmentsAction;
    public List<String> worlds;
    public boolean worldsBlacklist;
    public boolean playerAlerts;
    private final Map<Material, ItemMeta> defaultMetaCache = new EnumMap<>(Material.class);
    private final Map<Material, ItemStack> defaultItemCache = new EnumMap<>(Material.class);
    public List<String> components;


    public ItemMeta getDefaultMeta(Material type) {
        return defaultMetaCache.computeIfAbsent(type, t -> new ItemStack(t, 1).getItemMeta());
    }

    public ItemStack getDefaultItem(Material type) {
        return defaultItemCache.computeIfAbsent(type, t -> new ItemStack(t, 1));
    }


    public void loadConfigCache() {
        masterEnabled = getConfig().getBoolean("toggles.master");
        attributesEnabled = getConfig().getBoolean("toggles.attributes");
        enchantmentsEnabled = getConfig().getBoolean("toggles.enchantments");
        potionsEnabled = getConfig().getBoolean("toggles.potions");
        componentsEnabled = getConfig().getBoolean("toggles.components");
        attributesAction = AttributeAction.valueOf(getConfig().getString("attributes.action", "REMOVE"));
        enchantmentsAction = EnchantAction.valueOf(getConfig().getString("enchantments.action", "REMOVE"));
        enchantmentsAllowIncompatible = getConfig().getBoolean("enchantments.allow-incompatible");
        worlds = getConfig().getStringList("config.worlds");
        worldsBlacklist = getConfig().getBoolean("config.blacklist");
        playerAlerts = getConfig().getBoolean("toggles.playeralerts");
        components = getConfig().getStringList("components.blocked");

    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        loadConfigCache();

        getServer().getPluginManager().registerEvents(new CreativeListener(this), this);
        getServer().getPluginManager().registerEvents(new InventorySlotListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Objects.requireNonNull(getCommand("cic")).setExecutor(new Command(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
