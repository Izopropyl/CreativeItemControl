package net.kingidk.creativeItemControl;

import io.papermc.paper.datacomponent.DataComponentType;
import net.kingidk.creativeItemControl.Enums.AttributeAction;
import net.kingidk.creativeItemControl.Enums.EnchantAction;
import net.kingidk.creativeItemControl.Listeners.ItemListener;
import net.kingidk.creativeItemControl.Util.Command;
import net.kingidk.creativeItemControl.Util.ExcludedItemStore;
import net.kingidk.creativeItemControl.Util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
    public Set<String> worlds;
    public boolean worldsBlacklist;
    public boolean playerAlerts;
    public long alertCooldown;
    private final Map<Material, ItemMeta> defaultMetaCache = new EnumMap<>(Material.class);
    private final Map<Material, ItemStack> defaultItemCache = new EnumMap<>(Material.class);
    public List<String> components;
    public List<DataComponentType> resolvedComponents;
    private Map<String, ItemStack> excludedItems;
    private ExcludedItemStore excludedItemStore;


    private final Map<String, Long> giveCooldowns = new HashMap<>();
    public long giveCooldownSeconds;

    public ItemMeta getDefaultMeta(Material type) {
        return defaultMetaCache.computeIfAbsent(type, t -> new ItemStack(t, 1).getItemMeta());
    }

    public ItemStack getDefaultItem(Material type) {
        return defaultItemCache.computeIfAbsent(type, t -> new ItemStack(t, 1));
    }


    public void loadConfigCache() {
        masterEnabled = getConfig().getBoolean("config.enabled");
        attributesEnabled = getConfig().getBoolean("attributes.enabled");
        enchantmentsEnabled = getConfig().getBoolean("enchantments.enabled");
        potionsEnabled = getConfig().getBoolean("potions.enabled");
        componentsEnabled = getConfig().getBoolean("components.enabled");
        attributesAction = AttributeAction.valueOf(getConfig().getString("attributes.action", "REMOVE"));
        enchantmentsAction = EnchantAction.valueOf(getConfig().getString("enchantments.action", "REMOVE"));
        enchantmentsAllowIncompatible = getConfig().getBoolean("enchantments.allow-incompatible");
        worlds = new HashSet<>(getConfig().getStringList("config.worlds"));
        worldsBlacklist = getConfig().getBoolean("config.blacklist");
        playerAlerts = getConfig().getBoolean("config.playeralerts");
        alertCooldown = getConfig().getLong("config.alert-cooldown", 100);
        components = getConfig().getStringList("components.blocked");
        giveCooldownSeconds = getConfig().getLong("config.give-cooldown", 0);


        // Get component types from list in config
        resolvedComponents = new ArrayList<>();
        for (String name : components) {
            NamespacedKey key = NamespacedKey.fromString(name);
            if (key==null) continue;
            DataComponentType type = Registry.DATA_COMPONENT_TYPE.get(key);
            if (type == null) continue;
            resolvedComponents.add(type);
        }

    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        if (!getConfig().isSet("version")) {
            getLogger().warning("Old Config Detected, new keys will be added");
            updateConfig();
        }

        loadConfigCache();
        MessageUtil messageUtil = new MessageUtil(this);

        ItemListener listener = new ItemListener(this, messageUtil);
        getServer().getPluginManager().registerEvents(listener, this);

        var cicCmd = Objects.requireNonNull(getCommand("cic"));
        Command cicExecutor = new Command(this, messageUtil);
        cicCmd.setExecutor(cicExecutor);
        cicCmd.setTabCompleter(cicExecutor);


        excludedItemStore = new ExcludedItemStore(this);
        excludedItems = excludedItemStore.loadAll();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void storeExcludedItem(String id, ItemStack item) {
        excludedItems.put(id, item.clone());
        excludedItemStore.save(id, item);
    }


    public void removeExcludedItem(String id) {
        excludedItems.remove(id);
        excludedItemStore.remove(id);
    }

    public ItemStack getExcludedItem(String id) {
        return excludedItems.get(id);
    }

    public Map<String, ItemStack> getExcludedItems() {
        return Collections.unmodifiableMap(excludedItems);
    }

    public boolean isExcluded(ItemStack item) {
        return excludedItems.values().stream().anyMatch(e -> e.isSimilar(item));
    }

    public boolean isOnGiveCooldown(UUID targetId, String itemId) {
        String key = targetId + ":" + itemId;
        Long last = giveCooldowns.get(key);
        if (last == null) return false;
        return (System.currentTimeMillis() - last) < giveCooldownSeconds * 1000L;
    }
    public long getGiveCooldownRemaining(UUID targetId, String itemId) {
        String key = targetId + ":" + itemId;
        Long last = giveCooldowns.get(key);
        return giveCooldownSeconds - (System.currentTimeMillis() - last) / 1000L;
    }
    public void recordGive(UUID targetId, String itemId) {
        giveCooldowns.put(targetId + ":" + itemId, System.currentTimeMillis());
    }

    public void updateConfig() {
        getConfig().set("config.alert-cooldown", 1000);
        getConfig().set("config.give-cooldown", 0);
        getConfig().set("messages.alerts.components", "<b><red>Items with custom components are not allowed here!");
        getConfig().set("messages.alerts.attributes", "<b><red>Items with attribute modifiers are not allowed here!");
        getConfig().set("messages.alerts.enchantments", "<b><red>Items with impossible enchantments are not allowed here!");
        getConfig().set("messages.alerts.potions", "<b><red>Custom potions are not allowed here!");
        getConfig().set("messages.commands.reload", "&aCreativeItemControl config reloaded!");
        getConfig().set("messages.commands.exclude", "&aStored {type} as \"{id}\".");
        getConfig().set("messages.commands.remove", "&aRemoved excluded item \"{id}\".");
        getConfig().set("messages.commands.listempty", "&eNo excluded items stored.");
        getConfig().set("messages.commands.give", "&aGave \"{id}\" to &f&a{player}.");
        getConfig().set("messages.commands.giveinvalid", "&cNo excluded item found with id \"{id} \".");
        getConfig().set("messages.commands.giveothers", "&cYou do not have permission to give to other players!");
        getConfig().set("messages.commands.playernotfound", "&cPlayer \"{player}\" not found.");
        getConfig().set("messages.commands.specifyplayer", "&cSpecify a player: /cic give <id> <player>");
        getConfig().set("messages.commands.invalidworld", "&cYou cannot use this in this world!");
        getConfig().set("messages.commands.oncooldown", "&cYou must wait {time}s before receiving this item again.");
        getConfig().set("messages.commands.noperm", "&cYou do not have permission!");
        getConfig().set("version", 2);

        saveConfig();
        getLogger().info("Config migrated to version 2");
    }


}
