package me.yeagerist39.betterPets.Pet.Pets;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Wolf;

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

    public Pet(String name, int friendLevel, int friendPoint, OfflinePlayer owner, UUID uuid, Wolf wolf) {
        this.uuid = uuid;
        this.wolfUuid = wolf.getUniqueId();
        this.name = name;
        this.friendLevel = friendLevel;
        this.friendPoint = friendPoint;
        this.owner = owner;
        lastFriendReward = null;
        wolf.setCustomName(name);
        wolf.setCustomNameVisible(false);
        pets.add(this);
    }
}
