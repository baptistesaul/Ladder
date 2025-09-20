package fr.ladder.api.bukkit.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.function.BiConsumer;

public interface BookBuilder extends ItemBuilder {
    
    @Override
    BookBuilder setType(Material material);
    
    @Override
    BookBuilder setData(short data);
    
    @Override
    BookBuilder setAmount(int amount);
    
    @Override
    BookBuilder setName(String name);
    
    @Override
    BookBuilder setLore(List<String> lore);

    @Override
    BookBuilder addLore(List<String> lore);

    @Override
    BookBuilder addEnchantment(Enchantment enchantment, int level);
    
    @Override
    BookBuilder removeEnchantment(Enchantment enchantment);
    
    @Override
    BookBuilder setEnchanted(boolean enchanted);
    
    @Override
    BookBuilder addAllItemFlags();
    
    @Override
    BookBuilder setUnbreakable(boolean unbreakable);
    
    @Override
    BookBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> handleClick);
}
