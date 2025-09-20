package fr.ladder.api.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Snowtyy
 */
public interface GamePlayer {

    /**
     * @return The player's name.
     */
    String getName();

    /**
     * @return The player's displayed name on the chat
     */
    String getDisplayName();

    /**
     * @param displayName the new displayed name on the chat
     */
    void setDisplayName(String displayName);

    /**
     * @return The player's unique ID.
     */
    UUID getUniqueId();

    /**
     * @return The Bukkit player's instance.
     */
    Player getPlayer();

    /**
     * @return true if the player is alive, otherwise false.
     */
    boolean isAlive();

    /**
     * @return true if the player is online, otherwise false.
     */
    boolean isOnline();

    /**
     * Executes the specified action if the player is online.
     *
     * @param action The action to perform, represented as a {@link Consumer<Player>}.
     *               The {@link Player} instance is passed to the action if the player is online.
     *               If the player is offline, the action is not executed.
     */
    void ifOnline(Consumer<Player> action);

    /**
     * @param message Message to send
     */
    void sendMessage(String message);

    /**
     * @param message Message to send
     * @param
     */
    void sendMessage(String message, boolean forceIfOffline);

    /**
     * @return The current player's location.
     */
    Location getLocation();

    /**
     * Teleports the player to a new location.
     *
     * @param newLocation The location to teleport the player to.
     */
    void teleport(Location newLocation);

    /**
     * @return The player's current game mode.
     */
    GameMode getGameMode();

    /**
     * Sets the player's game mode.
     *
     * @param gameMode The new game mode to set for the player.
     */
    void setGameMode(GameMode gameMode);

    /**
     * @return The current health of the player.
     */
    double getHealth();

    /**
     * Sets the player's health to a specific value.
     *
     * @param health The new health value to set for the player.
     *               It should be between 0 and the player's maximum health.
     */
    void setHealth(double health);

    /**
     * @return The maximum health of the player.
     */
    double getMaxHealth();

    /**
     * Sets the player's maximum health.
     *
     * @param maxHealth The new maximum health value to set for the player.
     */
    void setMaxHealth(double maxHealth);

    /**
     * @return the player's food level
     */
    int getFoodLevel();

    /**
     * @return the player's saturation level
     */
    float getSaturation();

    /**
     * Sets the player's food level (0 to 20).
     *
     * @param foodLevel the new food level
     */
    void setFoodLevel(int foodLevel);

    /**
     * Sets the player's saturation level (0.0 to 20.0).
     *
     * @param saturation the new saturation level
     */
    void setSaturation(float saturation);

    /**
     * @return true if the player is allowed to fly
     */
    boolean getAllowFlight();

    /**
     * Sets whether the player is allowed to fly.
     *
     * @param allowFlight true to allow flight, false otherwise
     */
    void setAllowFlight(boolean allowFlight);

    /**
     * @return true if the player is currently flying
     */
    boolean isFlying();

    /**
     * Sets whether the player is currently flying.
     *
     * @param flying true to make the player fly, false to make them stop flying
     */
    void setFlying(boolean flying);

    ItemStack[] getInventoryContents();

    void setInventoryContents(ItemStack[] contents);

    ItemStack[] getInventoryArmors();

    void setInventoryArmors(ItemStack[] armors);

    @Nullable
    ItemStack getItem(int index);

    void setItem(int index, ItemStack item);

    @Nullable
    ItemStack getItemInHand();

    void setItemInHand(ItemStack item);

    int getSelectedItemSlot();

    void setSelectedItemSlot(int slot);

    /**
     * @param cause The damage cause to check for the player's invulnerability.
     *              This can be one of the values from the {@link EntityDamageEvent.DamageCause} enum,
     *              such as fall damage, fire, physical attacks, etc.
     * @return true if the player is invulnerable to this damage cause, otherwise false
     */
    boolean isInvulnerable(EntityDamageEvent.DamageCause cause);

    /**
     * @param causes The damage causes to set for the player's invulnerability.
     *               This can be one or more values from the {@link EntityDamageEvent.DamageCause} enum,
     *               representing the types of damage the player will be invulnerable to, such as fire, fall damage, etc.
     */
    void setInvulnerable(EntityDamageEvent.DamageCause... causes);

    /**
     * Removes the specified damage causes from the player's invulnerability.
     *
     * @param causes Damage causes to remove from invulnerability.
     */
    void removeInvulnerability(EntityDamageEvent.DamageCause... causes);

    Collection<PotionEffect> getActivePotionEffects();

    void addPotionEffect(PotionEffect effect);

    void removePotionEffect(PotionEffectType potionType);

    void clearPotionEffects();

    @Nullable
    GamePlayer getKiller();

    Collection<? extends GamePlayer> getAssistants();

    Collection<? extends GamePlayer> getVictims();

    @Nullable
    EntityDamageEvent.DamageCause getDeathCause();

    @Nullable
    Location getDeathLocation();

}
