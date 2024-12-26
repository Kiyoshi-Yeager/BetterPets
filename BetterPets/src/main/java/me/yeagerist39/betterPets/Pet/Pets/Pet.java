package me.yeagerist39.betterPets.Pet.Pets;

import me.yeagerist39.betterPets.Pet.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Wolf;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Pet {

    public static List<Pet> pets = new ArrayList<>();

    public UUID uuid;
    public UUID wolfUuid;
    public String name;
    public int friendLevel;
    public int friendPoint;
    public OfflinePlayer owner;
    public Long lastFriendReward;

    public Pet(int friendLevel, int friendPoint, OfflinePlayer owner, UUID uuid, Wolf wolf) {
        this.uuid = uuid;
        this.wolfUuid = wolf.getUniqueId();
        this.friendLevel = friendLevel;
        this.friendPoint = friendPoint;
        this.owner = owner;
        lastFriendReward = null;

        if (wolf.getCustomName() != null && wolf.getCustomName() != "") {
            this.name = wolf.getCustomName();
        } else {
            this.name = ConfigFile.default_name.replace("%owner_name%", owner.getPlayer().getDisplayName());
        }

        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING, uuid.toString());
        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("wolf_uuid"), PersistentDataType.STRING, wolf.getUniqueId().toString());
        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("pet_name"), PersistentDataType.STRING, name);
        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("friend_level"), PersistentDataType.INTEGER, friendLevel);
        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("friend_point"), PersistentDataType.INTEGER, friendPoint);
        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("owner_uuid"), PersistentDataType.STRING, owner.getUniqueId().toString());

        wolf.setCustomName(name);
        wolf.setCustomNameVisible(true);

        pets.add(this);
    }

    public Pet(Wolf wolf) {
        this.wolfUuid = wolf.getUniqueId();

        this.uuid = UUID.fromString(wolf.getPersistentDataContainer().get(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING));
        this.name = wolf.getPersistentDataContainer().get(NamespacedKey.fromString("pet_name"), PersistentDataType.STRING);
        this.friendLevel =  wolf.getPersistentDataContainer().get(NamespacedKey.fromString("friend_level"), PersistentDataType.INTEGER);
        this.friendPoint =  wolf.getPersistentDataContainer().get(NamespacedKey.fromString("friend_point"), PersistentDataType.INTEGER);
        this.owner = Bukkit.getOfflinePlayer(wolf.getPersistentDataContainer().get(NamespacedKey.fromString("owner_uuid"), PersistentDataType.STRING));
        this.lastFriendReward = null;

        pets.add(this);
    }
}
