package net.kingidk.creativeItemControl.Handlers;

import net.kingidk.creativeItemControl.CreativeItemControl;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnchantmentHandler {

    private final CreativeItemControl plugin;

    public EnchantmentHandler(CreativeItemControl plugin) {
        this.plugin = plugin;
    }

    public boolean shouldDelete(ItemMeta meta, ItemStack item, Player p) {
        Map<Enchantment, Integer> enchants = meta.getEnchants();
        Set<Enchantment> seen = new HashSet<>();

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (checkIncompatibleItem(item, enchantment)) return true;
            if (checkImpossibleLevel(enchantment, level)) return true;
            if (checkIncompatible(enchantment, seen)) return true;
            seen.add(enchantment);
        }
        return false;

    }



    public ItemMeta changeEnchants(ItemMeta meta, ItemStack item, Player p) {
        Map<Enchantment, Integer> enchants = meta.getEnchants();
        Set<Enchantment> seen = new HashSet<>();

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();



            if (checkIncompatible(enchantment, seen) || checkIncompatibleItem(item, enchantment)) {
                meta.removeEnchant(enchantment);
                continue;
            }
            if (checkImpossibleLevel(enchantment, level)) {


                if (plugin.getConfig().getString("enchantments.action", "REMOVE").equalsIgnoreCase("REMOVE")) {
                    meta.removeEnchant(enchantment);
                    continue;
                }

                int maxLevel = enchantment.getMaxLevel();
                meta.addEnchant(enchantment, maxLevel, true);

            }
            seen.add(enchantment);

        }
        return meta;
    }



    public boolean checkIncompatible(Enchantment enchant, Set<Enchantment> seen) {
        return seen.stream().anyMatch(e -> enchant.conflictsWith((Enchantment) e));
    }

    public boolean checkImpossibleLevel(Enchantment enchant, int level) {
        return level > enchant.getMaxLevel();
    }

    public boolean checkIncompatibleItem(ItemStack item, Enchantment enchant) {
        return !enchant.canEnchantItem(item);
    }
}
