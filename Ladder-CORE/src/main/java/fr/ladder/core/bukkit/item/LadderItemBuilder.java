package fr.ladder.core.bukkit.item;

import fr.ladder.api.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Snowtyy
 */
public class LadderItemBuilder implements ItemBuilder {
    
    protected final ItemStack item;
    protected ItemMeta meta;
    private BiConsumer<Player, InventoryClickEvent> handleClick;

    public LadderItemBuilder(ItemStack item) {
        if(item.getType() == Material.AIR)
            throw new IllegalArgumentException("You can't create an item builder for item type: " + item.getType());
        this.item = item;
        this.meta = item.getItemMeta();
    }
    
    public LadderItemBuilder(Material material) {
        this(new ItemStack(material));
    }
    
    public LadderItemBuilder(ItemBuilder itemBuilder) {
        this(itemBuilder.build().clone());
    }
    
    // --- type methods ---
    
    @Override
    public Material getType() {
        return this.item.getType();
    }
    
    @Override
    public LadderItemBuilder setType(Material material) {
        this.item.setType(material);
        this.meta = this.item.getItemMeta();
        return this;
    }
    
    // --- data methods ---
    
    @Override
    public short getData() {
        return this.item.getDurability();
    }
    
    @Override
    public LadderItemBuilder setData(short data) {
        this.item.setDurability(data);
        return this;
    }
    
    // --- amount methods ---
    
    @Override
    public int getAmount() {
        return this.item.getAmount();
    }
    
    @Override
    public LadderItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }
    
    // --- name methods ---
    
    @Override
    public String getName() {
        return this.meta.getDisplayName();
    }
    
    @Override
    public LadderItemBuilder setName(String name) {
        this.meta.setDisplayName(name);
        this.item.setItemMeta(meta);
        return this;
    }
    
    @Override
    public boolean hasName() {
        return this.meta.hasDisplayName();
    }
    
    // --- lore methods ---
    
    @Override
    public List<String> getLore() {
        return this.meta.getLore();
    }
    
    @Override
    public LadderItemBuilder setLore(List<String> lore) {
        this.meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    @Override
    public ItemBuilder addLore(List<String> loreToAdd) {
        List<String> lore = this.meta.getLore();
        if(lore == null)
            lore = new ArrayList<>(loreToAdd.size());
        lore.addAll(loreToAdd);
        this.meta.setLore(lore);
        this.item.setItemMeta(this.meta);

        return this;
    }

    @Override
    public boolean hasLore() {
        return this.meta.hasLore();
    }
    
    // --- enchant methods ---
    
    @Override
    public LadderItemBuilder addEnchantment(Enchantment enchant, int level) {
        this.meta.addEnchant(enchant, level, true);
        this.item.setItemMeta(meta);
        return this;
    }
    
    @Override
    public LadderItemBuilder removeEnchantment(Enchantment enchant) {
        this.meta.removeEnchant(enchant);
        this.item.setItemMeta(meta);
        return this;
    }
    
    @Override
    public LadderItemBuilder setEnchanted(boolean enchanted) {
        if(enchanted) {
            this.meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else if(this.meta.hasEnchants()) {
            meta.getEnchants().clear();
        }
        this.item.setItemMeta(meta);
        return this;
    }
    
    // --- flag methods
    
    @Override
    public LadderItemBuilder addAllItemFlags() {
        this.meta.addItemFlags(ItemFlag.values());
        this.item.setItemMeta(meta);
        return this;
    }
    
    // --- unbreakable methods ---
    
    public boolean isUnbreakable() {
        return this.meta.spigot().isUnbreakable();
    }
    
    public LadderItemBuilder setUnbreakable(boolean unbreakable) {
        this.meta.spigot().setUnbreakable(unbreakable);
        this.item.setItemMeta(meta);
        return this;
    }
    
    // --- other methods ---
    
    @Override
    public LadderItemBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> biConsumer) {
        this.handleClick = biConsumer;
        return this;
    }
    
    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if(this.handleClick != null)
            this.handleClick.accept(player, event);
    }
    
    @Override
    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }
    
    @Override
    public boolean isSame(ItemBuilder item) {
        if(item == null)
            return false;
        return this.getType() == item.getType() && this.hasName()
                && this.getName().equals(item.getName());
    }
    
    @Override
    public boolean isSame(ItemStack item) {
        if(item == null)
            return false;
        return this.getType() == item.getType()
                && this.meta.getDisplayName().equals(item.getItemMeta().getDisplayName());
    }
    
    public LadderItemBuilder clone() {
        try {
            return new LadderItemBuilder(this.item.clone());
        }
        catch(Exception ignored) {}
        
        try {
            return (LadderItemBuilder) super.clone();
        }
        catch(Exception ignored) {}
        
        return null;
    }
    
    
}
