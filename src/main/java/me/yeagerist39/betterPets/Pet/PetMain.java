package me.yeagerist39.betterPets.Pet;

import me.yeagerist39.betterPets.Pet.Pets.Pet;
import me.yeagerist39.betterPets.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.UUID;

public class PetMain implements Listener {
    private static PetMain instance = new PetMain();
    public static PetMain getInstance() {return instance;}
    private PetMain() {}

    @Nullable
    public Pet getPetByUUID(UUID uuid) {
        for (Pet pet: Pet.pets) {
            if (pet.uuid.equals(uuid)) {
                return pet;
            }
        }
        return null;
    }
    @Nullable
    public Pet getPetByWolf(Wolf wolf) {
        if (wolf.getPersistentDataContainer().has(NamespacedKey.fromString("pet_uuid"))) {
            UUID uuid = UUID.fromString(wolf.getPersistentDataContainer().get(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING));
            return getPetByUUID(uuid);
        } else {
            return null;
        }
    }

    public Inventory createInventory(Pet pet, Wolf wolf) {
        Inventory inventory = Bukkit.createInventory(null, 27, pet.name);

        if (wolf.isCustomNameVisible()) {
            inventory.setItem(10, new ItemBuilder(Material.LIME_DYE, 1).setName("Имя питомца показано!").addPersistent("pet_uuid", PersistentDataType.STRING, pet.uuid.toString()).build());
        } else {
            inventory.setItem(10, new ItemBuilder(Material.GRAY_DYE, 1).setName("Имя питомца скрыто!").addPersistent("pet_uuid", PersistentDataType.STRING, pet.uuid.toString()).build());
        }
        if (pet.lastFriendReward == null) {
            inventory.setItem(12, new ItemBuilder(Material.REDSTONE, 1).setName("Играть").build());
        } else {
            if (pet.lastFriendReward - System.currentTimeMillis() < 7000) {
                inventory.setItem(12, new ItemBuilder(Material.REDSTONE, 1).setName("Играть").build());
            } else {
                inventory.setItem(12, new ItemBuilder(Material.GUNPOWDER, 1).setName("Питомец не хочет играть!").build());
            }
        }
        inventory.setItem(14, new ItemBuilder(Material.BONE, 1).setName("Кормить питомца").build());
        return inventory;
    }

    @EventHandler
    public void onPetClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer().isSneaking() && event.getRightClicked().getType() == EntityType.WOLF) {
            Player player = event.getPlayer();
            Wolf wolf = (Wolf) event.getRightClicked();
            if (!wolf.isTamed()) {
                player.sendMessage("Этот питомец еще не приручен!");
                return;
            } else {
                event.setCancelled(true);
                Pet pet = getPetByWolf(wolf);
                if (pet != null) {
                    Inventory inventory = createInventory(pet, wolf);
                    player.openInventory(inventory);
                } else {
                    UUID uuid = UUID.randomUUID();
                    wolf.getPersistentDataContainer().set(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING, uuid.toString());
                    new Pet("Пес", 0, 0, player, uuid, wolf);
                    player.getPlayer().sendMessage("Питомец создан!");
                }
            }
        }
        return;
    }

    @EventHandler
    public void onTamedWolf(EntityTameEvent event) {
        if (event.getEntity() instanceof Wolf) {
            OfflinePlayer player = (OfflinePlayer) event.getOwner();
            Wolf wolf = (Wolf) event.getEntity();
            UUID uuid = UUID.randomUUID();
            wolf.getPersistentDataContainer().set(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING, uuid.toString());
            new Pet("Пес", 0, 0, player, uuid, wolf);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getItem(10).getItemMeta() != null) {
            if (event.getInventory().getItem(10).getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("pet_uuid"))) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                Inventory inventory = event.getInventory();
                int slot = event.getSlot();
                Pet pet = getPetByUUID(UUID.fromString(event.getInventory().getItem(10).getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING)));
                Wolf wolf = (Wolf) Bukkit.getEntity(pet.wolfUuid);
                if (slot == 10) {
                    changeNameVisible(wolf);
                }
            }
        }
    }

    public void changeNameVisible(Wolf wolf) {
        wolf.setCustomNameVisible(!wolf.isCustomNameVisible());
    }
}
