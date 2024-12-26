package me.yeagerist39.betterPets.Pet;

import org.bukkit.*;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.print.attribute.Attribute;
import java.util.Random;
import java.util.UUID;

public enum Food {
    MUTTON(2, false, Material.MUTTON),
    CHICKEN(2, false, Material.CHICKEN),
    BEEF(3, false, Material.BEEF),
    PORKCHOP(3, false, Material.PORKCHOP),
    RABBIT(3, false, Material.RABBIT),
    ROTTEN_FLESH(4, false, Material.ROTTEN_FLESH),
    COOKED_RABBIT(5, false, Material.COOKED_RABBIT),
    COOKED_MUTTON(6, false, Material.COOKED_MUTTON),
    COOKED_CHICKEN(6, false, Material.COOKED_CHICKEN),
    COOKED_PORKCHOP(8, false, Material.COOKED_PORKCHOP),
    COOKED_BEEF(8, false, Material.COOKED_BEEF),
    GOLDEN_APPLE(8, true, Material.GOLDEN_APPLE),
    ENCHANTED_GOLDEN_APPLE(10, true, Material.ENCHANTED_GOLDEN_APPLE),
    MILK_BUCKET(2, true, Material.MILK_BUCKET),
    SWEET_BERRIES(0, true, Material.SWEET_BERRIES),
    PUFFER_FISH(8, true, Material.PUFFERFISH),
    CHORUS_FRUIT(2, true, Material.CHORUS_FRUIT),
    GLOW_BERRIES(2, true, Material.GLOW_BERRIES),
    HONEY_BOTTLE(20, true, Material.HONEY_BOTTLE),
    SHULKER_SHELL(2, true, Material.SHULKER_SHELL),
    RABBIT_FOOT(8, true, Material.RABBIT_FOOT),
    FIRE_CHARGE(8, true, Material.FIRE_CHARGE),
    CAKE(8, true, Material.CAKE),
    POISONOUS_POTATO(8, true, Material.POISONOUS_POTATO);


    private final int heal;
    private final boolean specialEffect;
    private final Material material;

    Food(int heal, boolean specialEffect, Material material) {
        this.heal = heal;
        this.specialEffect = specialEffect;
        this.material = material;
    }

    public int getHeal() {
        return heal;
    }

    public boolean isSpecialEffect() {
        return specialEffect;
    }

    public Material getMaterial() {
        return material;
    }

    // Метод, который находит соответствующий элемент перечисления по типу материала
    public static Food fromMaterial(Material material) {
        for (Food food : Food.values()) {
            if (material == food.material) {
                return food;
            }
        }
        return null;
    }

    public static void applyEffect(Food food, Wolf wolf) {
        if (food.specialEffect) {
            if (food == GOLDEN_APPLE) {
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 4));
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 2));
            } else if (food == ENCHANTED_GOLDEN_APPLE) {
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 4));
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 6000, 16));
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 16));
            } else if (food == MILK_BUCKET) {
                for (PotionEffect effect : wolf.getActivePotionEffects()) {
                    wolf.removePotionEffect(effect.getType());
                }
            } else if (food == SWEET_BERRIES) {
                if (Math.random() >= 0.5) {
                    if (wolf.getMaxHealth() >= wolf.getHealth() + 4) {
                        wolf.setHealth(wolf.getHealth() + 4);
                    } else {
                        wolf.setHealth(wolf.getMaxHealth());
                    }
                } else {
                    wolf.damage(2);
                }
            } else if (food == PUFFER_FISH) {
                wolf.damage(0.1);
                if (wolf.isSitting()) {
                    wolf.setSitting(false);
                }
                Player player = Bukkit.getPlayer(UUID.fromString(wolf.getPersistentDataContainer().get(NamespacedKey.fromString("owner_uuid"), PersistentDataType.STRING)));
                wolf.setTarget(player);
            } else if (food == CHORUS_FRUIT) {
                wolf.setSitting(true);

                Random random = new Random();
                double x = wolf.getLocation().getX() + random.nextInt(40) - 20;
                double z = wolf.getLocation().getZ() + random.nextInt(40) - 20;
                double y = wolf.getLocation().getY();

                World world = wolf.getLocation().getWorld();

                Block block = world.getBlockAt(new Location(wolf.getLocation().getWorld(), x, y, z));
                Block blockdown = world.getBlockAt(new Location(wolf.getLocation().getWorld(), x, y - 1, z));

                if (block.getType() == Material.AIR && blockdown.getType() != Material.AIR) {
                    wolf.teleport(block.getLocation());
                } else if (block.getType() == Material.AIR && blockdown.getType() == Material.AIR) {
                    while (blockdown.getType() == Material.AIR) {
                        blockdown = world.getBlockAt(new Location(blockdown.getWorld(), blockdown.getX(), blockdown.getY() - 1, blockdown.getZ()));
                    }
                    wolf.teleport(new Location(blockdown.getLocation().getWorld(), blockdown.getLocation().getX(), blockdown.getLocation().getY() + 1, blockdown.getLocation().getZ()));
                } else if (block.getType() != Material.AIR) {
                    while (block.getType() != Material.AIR) {
                        block = world.getBlockAt(new Location(block.getWorld(), block.getX(), block.getY() + 1, block.getZ()));
                    }
                    wolf.teleport(block.getLocation());
                }
            } else if (food == GLOW_BERRIES) {
                for (PotionEffect effect : wolf.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.GLOWING) {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, wolf.getPotionEffect(effect.getType()).getDuration() + 1200, 1));
                        return;
                    }
                }
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 1));

            } else if (food == HONEY_BOTTLE) {
                for (PotionEffect effect : wolf.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.SLOWNESS) {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, wolf.getPotionEffect(effect.getType()).getDuration() + 1200, 1));
                        return;
                    }
                }
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 1));
            } else if (food == SHULKER_SHELL) {
                for (PotionEffect effect : wolf.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.LEVITATION) {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, wolf.getPotionEffect(effect.getType()).getDuration() + 40, 1));
                        return;
                    }
                }
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
            } else if (food == RABBIT_FOOT) {
                for (PotionEffect effect : wolf.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.JUMP_BOOST) {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, wolf.getPotionEffect(effect.getType()).getDuration() + 1200, 2));
                        return;
                    }
                }
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 2));
            } else if (food == FIRE_CHARGE) {
                wolf.setVisualFire(!wolf.isVisualFire());
            } else if (food == CAKE) {
                Location location = wolf.getLocation().add(0, 2, 0);

                ItemStack fireworkItem = new ItemStack(Material.FIREWORK_ROCKET);
                FireworkMeta fireworkMeta = (FireworkMeta) fireworkItem.getItemMeta();

                if (fireworkMeta != null) {
                    FireworkEffect effect = FireworkEffect.builder()
                            .withColor(Color.RED)
                            .withFade(Color.YELLOW)
                            .with(FireworkEffect.Type.BURST)
                            .trail(true)
                            .flicker(true)
                            .build();

                    fireworkMeta.addEffect(effect);
                    fireworkItem.setItemMeta(fireworkMeta);

                    location.getWorld().spawn(location, org.bukkit.entity.Firework.class, firework -> {
                        firework.setFireworkMeta(fireworkMeta);
                        firework.setVelocity(new Vector(0, 1, 0)); // Направление вверх
                    });
                }
            } else if (food == POISONOUS_POTATO) {
                for (PotionEffect effect : wolf.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.POISON) {
                        wolf.addPotionEffect(new PotionEffect(PotionEffectType.POISON, wolf.getPotionEffect(effect.getType()).getDuration() + 600, 2));
                        return;
                    }
                }
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 2));
            }
        }
    }
}
