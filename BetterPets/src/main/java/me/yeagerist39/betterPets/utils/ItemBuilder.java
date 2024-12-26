package me.yeagerist39.betterPets.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class ItemBuilder {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static ItemBuilder open(ItemStack item) {
        ItemBuilder itemBuilder = new ItemBuilder(item.getType(), item.getAmount());

        itemBuilder.itemStack = item;
        itemBuilder.itemMeta = item.getItemMeta();

        return itemBuilder;
    }

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(name);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        lore.replaceAll(ItemBuilder::format);

        itemMeta.setLore(lore);

        return this;
    }

    public ItemBuilder setLore(List<String> lore, Map<String, String> args) {
        for (int i = 0; i < lore.size(); i++) {
            for (String key: args.keySet()) {
                lore.set(i, lore.get(i).replace(key, args.get(key)));
            }

            lore.set(i,  ItemBuilder.format(lore.get(i)));
        }

        itemMeta.setLore(lore);

        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addPersistent(String key, PersistentDataType dataType, Object value) {
        itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString(key), dataType, value);

        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
