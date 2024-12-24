package me.yeagerist39.betterPets.utils;

import me.yeagerist39.betterPets.BetterPets;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class  ConfigManager {

    public final static ConfigManager instance = new ConfigManager();

    public Map<String, YamlConfiguration> configs = new HashMap<>();

    public void init(String fileName) {
        fileName = fileName + ".yml";

        File file = new File(BetterPets.getInstance().getDataFolder().getAbsolutePath() + "/" + fileName);

        if (!file.exists()) {
            BetterPets.getInstance().saveResource(fileName, false);
        }

        configs.put(fileName, YamlConfiguration.loadConfiguration(file));
    }
}
