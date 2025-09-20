package fr.ladder.api.bukkit.item;

import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.function.BiConsumer;

public interface SkullBuilder extends ItemBuilder {
    
    @Override
    SkullBuilder setType(Material material);
    
    @Override
    SkullBuilder setData(short data);
    
    @Override
    SkullBuilder setAmount(int amount);
    
    @Override
    SkullBuilder setName(String name);
    
    @Override
    SkullBuilder setLore(List<String> lore);

    @Override
    SkullBuilder addLore(List<String> lore);

    @Override
    SkullBuilder addEnchantment(Enchantment enchantment, int level);
    
    @Override
    SkullBuilder removeEnchantment(Enchantment enchantment);
    
    @Override
    SkullBuilder setEnchanted(boolean enchanted);
    
    @Override
    SkullBuilder addAllItemFlags();
    
    @Override
    SkullBuilder setUnbreakable(boolean unbreakable);
    
    @Override
    SkullBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> handleClick);
    
    /**
     * @param owner
     * @return
     */
    SkullBuilder setOwner(String owner);
    
    /**
     * @param texture Texture to apply
     * @return Current builder
     */
    SkullBuilder setTexture(Property texture);
    
    /**
     * @param textureUrl URL of the texture to apply
     * @return Current builder
     */
    SkullBuilder setTexture(String textureUrl);
}
