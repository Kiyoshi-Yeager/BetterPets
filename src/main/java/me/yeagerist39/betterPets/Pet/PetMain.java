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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetMain implements Listener {
    private static PetMain instance = new PetMain();

    public static PetMain getInstance() {
        return instance;
    }

    private PetMain() {
    }

    @Nullable
    public Pet getPetByUUID(UUID uuid) {
        for (Pet pet : Pet.pets) {
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

        updateSlot10(wolf, inventory, pet);
        updateSlot12(pet, inventory);
        inventory.setItem(14, new ItemBuilder(Material.BONE, 1).setName(ConfigFile.feeding_pet_button).build());

        if (pet.friendLevel >= 5) {
            ItemStack itemInWolfMainHand = wolf.getEquipment().getItemInMainHand();
            if (itemInWolfMainHand.getType() != Material.AIR) {
                inventory.setItem(16, itemInWolfMainHand);
            }
        } else {
            ItemStack barrier = new ItemBuilder(Material.BARRIER, 1).setName(ConfigFile.pet_inventory_slot_lock)
                    .addPersistent("is_barrier", PersistentDataType.BOOLEAN, true).build();
            inventory.setItem(16, barrier);
        }

        ItemStack decorItem = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setName(" ").build();
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) == null && i != 16) {
                inventory.setItem(i, decorItem);
            }
        }

        return inventory;
    }

    public Inventory feedPetMenu(Pet pet) {
        Inventory inventory = Bukkit.createInventory(null, 9, ConfigFile.pet_feeding_menu_menu_name);
        inventory.setItem(8, new ItemBuilder(Material.LIME_DYE, 1)
                .setName(ConfigFile.pet_feeding_menu_feed_button)
                .addPersistent("wolf_uuid", PersistentDataType.STRING, pet.wolfUuid.toString())
                .build());

        return inventory;
    }

    @EventHandler
    public void onPetClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer().isSneaking() && event.getRightClicked().getType() == EntityType.WOLF) {
            Player player = event.getPlayer();
            Wolf wolf = (Wolf) event.getRightClicked();
            if (!wolf.isTamed()) {
                player.sendMessage(ConfigFile.messages_this_pet_not_tamed);
                return;
            } else {
                event.setCancelled(true);
                Pet pet = getPetByWolf(wolf);
                if (pet != null) {
                    if (UUID.fromString(wolf.getPersistentDataContainer().get(NamespacedKey.fromString("owner_uuid"), PersistentDataType.STRING)).equals(player.getUniqueId())) {
                        Inventory inventory = createInventory(pet, wolf);
                        player.openInventory(inventory);
                    } else {
                        player.sendMessage(ConfigFile.messages_its_not_your_pet);
                    }
                } else {
                    if (!wolf.getPersistentDataContainer().has(NamespacedKey.fromString("owner_uuid"), PersistentDataType.STRING)) {
                        UUID uuid = UUID.randomUUID();
                        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING, uuid.toString());
                        new Pet(1, 0, player, uuid, wolf);
                    } else {
                        new Pet(wolf);
                        pet = getPetByWolf(wolf);
                        Inventory inventory = createInventory(pet, wolf);
                        if (UUID.fromString(wolf.getPersistentDataContainer().get(NamespacedKey.fromString("owner_uuid"), PersistentDataType.STRING)).equals(player.getUniqueId())) {
                            player.openInventory(inventory);
                        } else {
                            player.sendMessage(ConfigFile.messages_its_not_your_pet);
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onTamedWolf(EntityTameEvent event) {
        if (event.getEntity() instanceof Wolf) {
            OfflinePlayer player = (OfflinePlayer) event.getOwner();
            Wolf wolf = (Wolf) event.getEntity();
            UUID uuid = UUID.randomUUID();
            wolf.getPersistentDataContainer().set(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING, uuid.toString());
            new Pet(1, 0, player, uuid, wolf);
        }
    }


    @EventHandler
    public void onPetMenuClick(InventoryClickEvent event) {
        if (event.getInventory().getSize() == 27) {
            if (event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory()) {
                if (event.getInventory().getItem(10) != null) {
                    if (event.getInventory().getItem(10).getItemMeta() != null) {
                        if (event.getInventory().getItem(10).getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("pet_uuid"))) {
                            event.setCancelled(true);
                            Player player = (Player) event.getWhoClicked();
                            Inventory inventory = event.getInventory();
                            int slot = event.getSlot();
                            Pet pet = getPetByUUID(UUID.fromString(event.getInventory().getItem(10).getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING)));
                            Wolf wolf = (Wolf) Bukkit.getEntity(pet.wolfUuid);
                            if (slot == 10) {
                                if (inventory.getItem(10).getType() == Material.LIME_DYE) {
                                    changeNameVisible(pet, wolf, false, inventory);
                                } else {
                                    changeNameVisible(pet, wolf, true, inventory);
                                }
                            }
                            if (slot == 12) {
                                if (inventory.getItem(12).getType() == Material.REDSTONE) {
                                    playWithPet(wolf, pet, inventory);
                                }
                                updateSlot12(pet, inventory);
                            }
                            if (slot == 14) {
                                player.openInventory(feedPetMenu(pet));
                            }
                            if (slot == 16) {
                                if (inventory.getItem(16) != null) {
                                    if (inventory.getItem(16).getItemMeta() != null) {
                                        if (inventory.getItem(16).getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("is_barrier"))) {
                                            event.setCancelled(true);
                                            return;
                                        }
                                    }
                                }
                                event.setCancelled(false);
                            }
                        }
                    }

                }
            }
        }
    }

    @EventHandler
    public void onPetFeedMenuClick(InventoryClickEvent event) {
        if (event.getInventory().getSize() == 9) {
            if (event.getInventory().getItem(8) != null) {
                if (event.getInventory().getItem(8).getItemMeta() != null) {
                    if (event.getInventory().getItem(8).getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("wolf_uuid"))) {
                        if (event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory()) {
                            if (event.getSlot() == 8) {
                                event.setCancelled(true);
                                Wolf wolf = (Wolf) Bukkit.getEntity(UUID.fromString(event.getInventory().getItem(8).getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("wolf_uuid"), PersistentDataType.STRING)));
                                for (int i = 0; i < 8; i++) {
                                    ItemStack itemStack = event.getInventory().getItem(i);
                                    if (itemStack != null) {
                                        Food food = Food.fromMaterial(itemStack.getType());
                                        if (food != null) {
                                            if (food.isSpecialEffect() && wolf.getHealth() == wolf.getMaxHealth()) {
                                                int cnt = itemStack.getAmount();
                                                for (int j = 0; j < cnt; j++) {
                                                    Food.applyEffect(food, wolf);
                                                    itemStack.setAmount(itemStack.getAmount() - 1);
                                                }
                                                continue;
                                            }
                                            if (wolf.getHealth() >= wolf.getMaxHealth()) {return;}
                                            if (wolf.getMaxHealth() >= wolf.getHealth() + food.getHeal() * 2 * itemStack.getAmount()) {
                                                wolf.setHealth(wolf.getHealth() + food.getHeal() * 2 * itemStack.getAmount());
                                                Food.applyEffect(food, wolf);
                                                event.getInventory().setItem(i, new ItemStack(Material.AIR));
                                                continue;
                                            } else if (itemStack.getAmount() > 1) {
                                                while (itemStack.getAmount() > 1) {
                                                    if (wolf.getMaxHealth() >= wolf.getHealth() + (food.getHeal() * 2)) {
                                                        itemStack.setAmount(itemStack.getAmount() - 1);
                                                        wolf.setHealth(wolf.getHealth() + (food.getHeal() * 2));
                                                        Food.applyEffect(food, wolf);
                                                    } else {
                                                        break;
                                                    }
                                                }
                                            }
                                            if (wolf.getMaxHealth() < wolf.getHealth() + (food.getHeal() * 2)) {
                                                wolf.setHealth(wolf.getMaxHealth());
                                                food.applyEffect(food, wolf);
                                                itemStack.setAmount(itemStack.getAmount() - 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPetFeedMenuClose(InventoryCloseEvent event) {
        if (event.getInventory().getSize() == 9) {
            if (event.getInventory().getItem(8) != null) {
                if (event.getInventory().getItem(8).getItemMeta() != null) {
                    if (event.getInventory().getItem(8).getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("wolf_uuid"))) {
                        for (int i = 0; i < 8; i++) {
                            ItemStack itemStack = event.getInventory().getItem(i);
                            if (itemStack != null) {
                                if (itemStack.getItemMeta() != null) {
                                    if (itemStack.getItemMeta().getDisplayName().equals(ConfigFile.for_cooldown_setting)) {
                                        event.getPlayer().setOp(true);
                                    }
                                }
                                if (event.getPlayer().getInventory().firstEmpty() != -1) {
                                    event.getPlayer().getInventory().addItem(itemStack);
                                } else {
                                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), itemStack);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPetMenuClosed(InventoryCloseEvent event) {
        if (event.getInventory().getSize() == 27) {
            if (event.getInventory().getItem(10) != null) {
                if (event.getInventory().getItem(10).getItemMeta() != null) {
                    if (event.getInventory().getItem(10).getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("pet_uuid"))) {
                        Pet pet = getPetByUUID(UUID.fromString(event.getInventory().getItem(10).getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("pet_uuid"), PersistentDataType.STRING)));
                        Wolf wolf = (Wolf) Bukkit.getEntity(pet.wolfUuid);

                        ItemStack itemStack = event.getInventory().getItem(16);
                        if (itemStack != null) {
                            if (itemStack.getItemMeta() != null) {
                                if (itemStack.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("is_barrier"))) {
                                    return;
                                }
                            }
                            if (itemStack.getType().name().endsWith("SHULKER_BOX") && ConfigFile.setting_disable_shulkerbox_in_pet_inventory) {
                                wolf.getEquipment().setItemInMainHand(null);
                                if (event.getPlayer().getInventory().firstEmpty() == -1) {
                                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), itemStack);
                                } else {
                                    event.getPlayer().getInventory().addItem(itemStack);
                                }
                                return;
                            }
                        }
                        wolf.getEquipment().setItemInMainHand(event.getInventory().getItem(16));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPetDamaged(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Wolf) {
            if (event.getDamager().getPersistentDataContainer().has(NamespacedKey.fromString("pet_uuid"))) {
                if (event.getDamager().getPersistentDataContainer().get(NamespacedKey.fromString("friend_level"), PersistentDataType.INTEGER) >= 20) {
                    event.setDamage(event.getDamage() + 2);
                }
            }
        }
    }

    public int pointForNextLevel(int lvl) {
        return lvl / 5 + 1;
    }


    public void updateSlot10(Wolf wolf, Inventory inventory, Pet pet) {
        if (wolf.isCustomNameVisible()) {

            inventory.setItem(10, new ItemBuilder(Material.LIME_DYE, 1).setName(ConfigFile.visible_pet_name_button_on).addPersistent("pet_uuid", PersistentDataType.STRING, pet.uuid.toString()).build());
        } else {
            inventory.setItem(10, new ItemBuilder(Material.GRAY_DYE, 1).setName(ConfigFile.visible_pet_name_button_off).addPersistent("pet_uuid", PersistentDataType.STRING, pet.uuid.toString()).build());
        }
    }

    public void updateSlot12(Pet pet, Inventory inventory) {
        List<String> lore_before = ConfigFile.play_with_pet_button_lore;
        List<String> lore = new ArrayList<>();
        for (String string: lore_before) {
            lore.add(string
                    .replace("%points%", pet.friendPoint+"")
                    .replace("%points_for_next_level%", pointForNextLevel(pet.friendLevel) + "")
                    .replace("%level%", pet.friendLevel+"")
            );
        }

        if (pet.lastFriendReward == null) {
            inventory.setItem(12, new ItemBuilder(Material.REDSTONE, 1).setName(ConfigFile.play_with_pet_button_on).setLore(lore).build());
        } else {
            if (System.currentTimeMillis() - pet.lastFriendReward > ConfigFile.setting_pet_play_cooldown) { //время кд кнопки играть
                inventory.setItem(12, new ItemBuilder(Material.REDSTONE, 1).setName(ConfigFile.play_with_pet_button_on).setLore(lore).build());
            } else {
                inventory.setItem(12, new ItemBuilder(Material.GUNPOWDER, 1).setName(ConfigFile.play_with_pet_button_off).setLore(lore).build());
            }
        }
    }

    public void changeNameVisible(Pet pet, Wolf wolf, Boolean visible, Inventory inventory) {
        if (visible) {
            if (wolf.getCustomName() != null && !wolf.getCustomName().isEmpty() && !wolf.getCustomName().equals("")) {
                pet.name = wolf.getCustomName();
            }
            wolf.setCustomName(pet.name);
            wolf.setCustomNameVisible(true);

        } else {
            if (wolf.getCustomName() != null && !wolf.getCustomName().isEmpty() && !wolf.getCustomName().equals("")) {
                pet.name = wolf.getCustomName();
            }
            wolf.setCustomName("");
            wolf.setCustomNameVisible(false);
        }
        updateSlot10(wolf, inventory, pet);
    }

    public void playWithPet(Wolf wolf, Pet pet, Inventory inventory) {
        pet.friendPoint += 1;
        wolf.getPersistentDataContainer().set(NamespacedKey.fromString("friend_point"), PersistentDataType.INTEGER, pet.friendPoint);
        pet.lastFriendReward = System.currentTimeMillis();
        if (pet.friendPoint == pointForNextLevel(pet.friendLevel)) {
            pet.friendPoint = 0;
            wolf.getPersistentDataContainer().set(NamespacedKey.fromString("friend_point"), PersistentDataType.INTEGER, pet.friendPoint);
            pet.friendLevel += 1;
            wolf.getPersistentDataContainer().set(NamespacedKey.fromString("friend_level"), PersistentDataType.INTEGER, pet.friendLevel);

            if (ConfigFile.setting_get_1_health_for_1_friend_level) {
                wolf.setMaxHealth(wolf.getMaxHealth() + 1);
            }

            Player player = Bukkit.getPlayer(UUID.fromString(wolf.getPersistentDataContainer().get(NamespacedKey.fromString("owner_uuid"), PersistentDataType.STRING)));

            if (pet.friendLevel == 10 && pet.friendPoint == 0) {
                wolf.setMaxHealth(wolf.getMaxHealth() + 4);
            } else if (pet.friendLevel == 15 && pet.friendPoint == 0) {
                ItemStack itemStack = new ItemBuilder(Material.BONE, 1).setName(ConfigFile.awards_for_level_15_name).setLore(ConfigFile.awards_for_level_15_lore).build();

                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(itemStack);
                } else {
                    player.getWorld().dropItem(player.getLocation(), itemStack);
                }
            } else if (pet.friendLevel == 30 && pet.friendPoint == 0) {
                ItemStack itemStack = new ItemBuilder(Material.LIME_DYE, 1).setName(ConfigFile.awards_for_level_30_name).setLore(ConfigFile.awards_for_level_30_lore).build();

                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(itemStack);
                } else {
                    player.getWorld().dropItem(player.getLocation(), itemStack);
                }
            }
            updateSlot12(pet, inventory);
            //
        }
    }
}