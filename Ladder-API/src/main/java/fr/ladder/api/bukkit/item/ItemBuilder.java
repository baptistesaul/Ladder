package fr.ladder.api.bukkit.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public interface ItemBuilder {
    
    /**
     * @return Item's type
     */
    Material getType();
    
    /**
     * @param material New material of the item
     * @return Current builder
     */
    ItemBuilder setType(Material material);
    
    /**
     * @return Item's durability
     */
    short getData();
    
    /**
     * @param data New durability of the item
     * @return Current builder
     */
    ItemBuilder setData(short data);
    
    /**
     * @return Item's amount
     */
    int getAmount();
    
    /**
     *
     * @param amount New amount of the item
     * @return Current builder
     */
    ItemBuilder setAmount(int amount);
    
    /**
     * @return Item's name
     */
    String getName();
    
    /**
     * @param name New name of the item
     * @return Current builder
     */
    ItemBuilder setName(String name);
    
    /**
     * @return True if item has name otherwise false
     */
    boolean hasName();
    
    /**
     * @return Item's lore
     */
    List<String> getLore();
    
    /**
     * @param lore New lore of the item
     * @return Current builder
     */
    ItemBuilder setLore(List<String> lore);

    /**
     * @param lore Lore to add of the item
     * @return The current {@code ItemBuilder} instance
     */
    ItemBuilder addLore(List<String> lore);

    /**
     * @return True if item has lore otherwise false
     */
    boolean hasLore();
    
    /**
     * @param enchantment Enchantement to add
     * @param level Level of the enchantment
     * @return Current builder
     */
    ItemBuilder addEnchantment(Enchantment enchantment, int level);
    
    /**
     * @param enchantment Enchantment to remove
     * @return Current builder
     */
    ItemBuilder removeEnchantment(Enchantment enchantment);
    
    /**
     * @param enchanted Whether to enchant the item or not
     * @return Current builder
     */
    ItemBuilder setEnchanted(boolean enchanted);
    
    /**
     * @return Current builder
     */
    ItemBuilder addAllItemFlags();
    
    /**
     * @return True if the item is unbreakable otherwise false
     */
    boolean isUnbreakable();
    
    /**
     * @param unbreakable Whether to set the item unbreakable or not
     * @return Current builder
     */
    ItemBuilder setUnbreakable(boolean unbreakable);
    
    /**
     * @param handleClick New called consumer when the item is clicked in a GUI
     * @return Current builder
     */
    ItemBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> handleClick);
    
    /**
     * @param player Player who click on the item in a GUI
     * @param event Event of the click in a GUI
     */
    void onClick(Player player, InventoryClickEvent event);
    
    /**
     * @return Built item
     */
    ItemStack build();
    
    /**
     * @param item ItemBuilder to compare
     * @return true if the items are the same otherwise false
     */
    boolean isSame(ItemBuilder item);
    
    /**
     * @param item ItemStack to compare
     * @return true if the items are the same otherwise false
     */
    boolean isSame(ItemStack item);
}
