package fr.ladder.api.util;

import com.google.gson.JsonElement;
import fr.ladder.api.bukkit.item.BannerBuilder;
import fr.ladder.api.bukkit.item.BookBuilder;
import fr.ladder.api.bukkit.item.ItemBuilder;
import fr.ladder.api.bukkit.item.SkullBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Snowtyy
 */
public abstract class ItemUtils {
    
    private static Implementation implementation;
    
    /**
     * @param item Item on which the ItemBuilder is created
     * @return New instance of an ItemBuilder of the item
     */
    public static ItemBuilder toItemBuilder(ItemStack item) {
        return implementation.toItemBuilder(item);
    }
    
    /**
     * @param material Material of the item to build
     * @return New instance of an ItemBuilder
     */
    public static ItemBuilder createItem(Material material) {
        return implementation.createItem(material);
    }
    
    public static ItemBuilder copyOf(ItemBuilder itemBuilder) {
        return implementation.copyOf(itemBuilder);
    }
    
    /**
     * @param color Color of the banner to build
     * @return New instance of a BannerBuilder
     */
    public static BannerBuilder createBanner(DyeColor color) {
        return implementation.createBanner(color);
    }
    
    public static BannerBuilder copyOf(BannerBuilder itemBuilder) {
        return implementation.copyOf(itemBuilder);
    }
    
    /**
     * @return New instance of a BookBuilder
     */
    public static BookBuilder createBook() {
        return implementation.createBook();
    }
    
    public static BookBuilder copyOf(BookBuilder itemBuilder) {
        return implementation.copyOf(itemBuilder);
    }
    
    /**
     * @return New instance of a SkullBuilder
     */
    public static SkullBuilder createSkull() {
        return implementation.createSkull();
    }
    
    public static SkullBuilder copyOf(SkullBuilder itemBuilder) {
        return implementation.copyOf(itemBuilder);
    }
    
    /**
     * @param type   Potion's type
     * @param level  Potion's level
     * @param splash Whether to can splash the potion or not
     * @return New instance of an ItemBuilder which build a potion
     */
    public static ItemBuilder createPotion(PotionType type, int level, boolean splash) {
        return implementation.createPotion(type, level, splash);
    }
    
    /**
     * @return Default unknown item
     */
    public static ItemBuilder getUnknownItem() {
        return implementation.getUnknownItem();
    }
    
    /**
     * @param color Glass's color
     * @return Default colored glass
     */
    public static ItemBuilder getGlass(DyeColor color) {
        return implementation.getGlass(color);
    }
    
    /**
     * @return Default back item
     */
    public static ItemBuilder getBackItem() {
        return implementation.getBackItem();
    }
    
    /**
     * @return Default previous item
     */
    public static ItemBuilder getPreviousItem() {
        return implementation.getPreviousItem();
    }
    
    /**
     * @return Default next item
     */
    public static ItemBuilder getNextItem() {
        return implementation.getNextItem();
    }
    
    /**
     * @param color Banner's color
     * @param value Banner's label
     * @return Default Banner which represent button to remove anything
     */
    public static BannerBuilder getBannerMinus(DyeColor color, String value) {
        return implementation.getBannerMinus(color, value);
    }
    
    /**
     * @return Default item to manage the host configuration
     */
    public static ItemBuilder getConfigItem() {
        return implementation.getConfigItem();
    }

    /**
     * @return Default item to open teams menu and choose your team
     */
    public static BannerBuilder getTeamsItem() {
        return implementation.getTeamsItem(DyeColor.WHITE);
    }

    public static BannerBuilder getTeamsItem(DyeColor color) {
        return implementation.getTeamsItem(color);
    }

    /**
     * @param color Banner's color
     * @param value Banner's label
     * @return Default Banner which represent button to add anything
     */
    public static BannerBuilder getBannerPlus(DyeColor color, String value) {
        return implementation.getBannerPlus(color, value);
    }
    
    /**
     * @param location Position where the item spawn
     * @param item Item which spawn
     * @return Instance of the spawned Item
     */
    public static Item spawnItem(Location location, ItemStack item) {
        return implementation.spawnItem(location, item);
    }
    
    /**
     * @param entity Entity where the item spawn
     * @param item Item which spawn
     * @return Instance of the spawned Item
     */
    public static Item spawnItem(Entity entity, ItemStack item) {
        return implementation.spawnItem(entity.getLocation().add(0, 1, 0), item);
    }
    
    /**
     * @param block Block where the item spawn
     * @param item Item which spawn
     * @return Instance of the spawned Item
     */
    public static Item spawnItem(Block block, ItemStack item) {
        return implementation.spawnItem(block.getLocation().add(0.5, 0.1, 0.5), item);
    }
    
    /**
     * @param location Location where the item spawn
     * @param items Items to make spawn
     * @return Instance of the spawned Items
     */
    public static List<Item> spawnItems(Location location, ItemStack... items) {
        return implementation.spawnItems(location, items);
    }
    
    /**
     * @param entity Entity where the item spawn
     * @param items Items to make spawn
     * @return Instance of the spawned Items
     */
    public static List<Item> spawnItems(Entity entity, ItemStack... items) {
        return implementation.spawnItems(entity.getLocation().add(0, 1, 0), items);
    }
    
    /**
     * @param block Block where the item spawn
     * @param items Items to make spawn
     * @return Instance of the spawned Items
     */
    public static List<Item> spawnItems(Block block, ItemStack... items) {
        return implementation.spawnItems(block.getLocation().add(0.5, 0.1, 0.5), items);
    }
    
    /**
     * @param item Item to serialize
     * @return Result of serializing the item
     */
    @Nullable
    public static JsonElement serialize(@Nullable ItemStack item) {
        return implementation.serialize(item);
    }
    
    
    /**
     * @param element Json element to deserialize
     * @return Result of deserializing the element
     */
    public static ItemStack deserialize(JsonElement element) {
        return implementation.deserialize(element);
    }

    public static int getBonusWithFortune(ItemStack item) {
        return implementation.getBonusWithFortune(item);
    }
    
    public interface Implementation {
        
        ItemBuilder toItemBuilder(ItemStack item);
        
        ItemBuilder createItem(Material material);
        
        ItemBuilder copyOf(ItemBuilder itemBuilder);
        
        BannerBuilder createBanner(DyeColor color);
        
        BannerBuilder copyOf(BannerBuilder itemBuilder);
        
        BookBuilder createBook();
        
        BookBuilder copyOf(BookBuilder itemBuilder);
        
        SkullBuilder createSkull();
        
        SkullBuilder copyOf(SkullBuilder itemBuilder);
        
        ItemBuilder createPotion(PotionType type, int level, boolean splash);
        
        ItemBuilder getUnknownItem();
        
        ItemBuilder getGlass(DyeColor color);
        
        ItemBuilder getBackItem();
        
        ItemBuilder getPreviousItem();
        
        ItemBuilder getNextItem();
        
        ItemBuilder getConfigItem();

        BannerBuilder getTeamsItem(DyeColor color);

        BannerBuilder getBannerMinus(DyeColor color, String value);
        
        BannerBuilder getBannerPlus(DyeColor color, String value);
        
        Item spawnItem(Location position, ItemStack item);
        
        List<Item> spawnItems(Location position, ItemStack... items);
        
        JsonElement serialize(ItemStack itemStack);
        
        ItemStack deserialize(JsonElement jsonElement);

        int getBonusWithFortune(ItemStack itemInHand);
        
    }
}
