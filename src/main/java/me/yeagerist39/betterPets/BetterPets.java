package me.yeagerist39.betterPets;

import me.yeagerist39.betterPets.Pet.PetMain;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterPets extends JavaPlugin {

    private static BetterPets instance;
    public static BetterPets getInstance() {return instance;}


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getServer().getPluginManager().registerEvents(PetMain.getInstance(), this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}