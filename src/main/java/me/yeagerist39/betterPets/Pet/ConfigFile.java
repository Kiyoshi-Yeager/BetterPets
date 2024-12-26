package me.yeagerist39.betterPets.Pet;


import me.yeagerist39.betterPets.BetterPets;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfigFile {
    public static final String feeding_pet_button = ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_menu.feeding_pet_button"));
    public static final String visible_pet_name_button_on =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_menu.visible_pet_name_button_on"));
    public static final String visible_pet_name_button_off = ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_menu.visible_pet_name_button_off"));
    public static final String play_with_pet_button_on =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_menu.play_with_pet_button_on"));
    public static final String play_with_pet_button_off =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_menu.play_with_pet_button_off"));
    public static final List<String> play_with_pet_button_lore = loreTranslate(BetterPets.getInstance().getConfig().getStringList("pet_menu.play_with_pet_button_lore"));
    public static final String pet_inventory_slot_lock = ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_menu.pet_inventory_slot_lock"));

    public static final String pet_feeding_menu_menu_name =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_feeding_menu.menu_name"));
    public static final String pet_feeding_menu_feed_button =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet_feeding_menu.feed_button"));

    public static final String messages_this_pet_not_tamed =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("messages.this_pet_not_tamed"));
    public static final String messages_its_not_your_pet =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("messages.its_not_your_pet"));

    public static final String awards_for_level_15_name =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("awards.for_level_15.name"));
    public static final List<String> awards_for_level_15_lore = loreTranslate(BetterPets.getInstance().getConfig().getStringList("awards.for_level_15.lore"));
    public static final String awards_for_level_30_name =  ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("awards.for_level_30.name"));
    public static final List<String> awards_for_level_30_lore = loreTranslate(BetterPets.getInstance().getConfig().getStringList("awards.for_level_30.lore"));

    public static final String default_name = ChatColor.translateAlternateColorCodes('&', BetterPets.getInstance().getConfig().getString("pet.default_name"));

    public static final Boolean setting_disable_shulkerbox_in_pet_inventory = BetterPets.getInstance().getConfig().getBoolean("setting.disable_shulkerbox_in_pet_inventory");
    public static final Long setting_pet_play_cooldown = BetterPets.getInstance().getConfig().getLong("setting.pet_play_cooldown");
    public static final String for_cooldown_setting = "533a4559-e55c-18b3-8456-555563322002";

    public static List<String> loreTranslate(List<String> lore) {
        List<String> translated = new ArrayList<>();
        for (String string: lore) {
            translated.add( ChatColor.translateAlternateColorCodes('&', string));
        }
        return translated;
    }
}
