package fr.ladder.core.bukkit.item;


import fr.ladder.api.bukkit.item.BookBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Snowtyy
 */
public class LadderBookBuilder extends LadderItemBuilder implements BookBuilder {
    
    public LadderBookBuilder() {
        super(Material.ENCHANTED_BOOK);
    }
    
    public LadderBookBuilder(BookBuilder bookBuilder) {
        super(bookBuilder.build().clone());
    }
    
    @Override
    public LadderBookBuilder setType(Material material) {
        throw new UnsupportedOperationException("You can't modify the material for BookBuilder.");
    }
    
    @Override
    public LadderBookBuilder setData(short data) {
        return (LadderBookBuilder) super.setData(data);
    }
    
    @Override
    public LadderBookBuilder setAmount(int amount) {
        return (LadderBookBuilder) super.setAmount(amount);
    }
    
    @Override
    public LadderBookBuilder setName(String name) {
        return (LadderBookBuilder) super.setName(name);
    }
    
    @Override
    public LadderBookBuilder setLore(List<String> lore) {
        return (LadderBookBuilder) super.setLore(lore);
    }

    @Override
    public LadderBookBuilder addLore(List<String> loreToAdd) {
        return (LadderBookBuilder) super.addLore(loreToAdd);
    }

    @Override
    public LadderBookBuilder addEnchantment(Enchantment enchant, int level) {
        ((EnchantmentStorageMeta)this.meta).addStoredEnchant(enchant, level, true);
        this.item.setItemMeta(meta);
        return this;
    }
    
    @Override
    public LadderBookBuilder removeEnchantment(Enchantment enchant) {
        ((EnchantmentStorageMeta)this.meta).removeStoredEnchant(enchant);
        this.item.setItemMeta(meta);
        return this;
    }
    
    @Override
    public LadderBookBuilder setEnchanted(boolean enchanted) {
        return this;
    }
    
    @Override
    public LadderBookBuilder addAllItemFlags() {
        return (LadderBookBuilder) super.addAllItemFlags();
    }
    
    @Override
    public LadderBookBuilder setUnbreakable(boolean unbreakable) {
        return (LadderBookBuilder) super.setUnbreakable(unbreakable);
    }
    
    @Override
    public LadderBookBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> biConsumer) {
        return (LadderBookBuilder) super.setClickEvent(biConsumer);
    }
    
}
