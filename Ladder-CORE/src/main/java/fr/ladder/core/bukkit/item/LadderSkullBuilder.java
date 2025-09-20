package fr.ladder.core.bukkit.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.ladder.api.bukkit.item.ItemBuilder;
import fr.ladder.api.bukkit.item.SkullBuilder;
import fr.ladder.api.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Snowtyy
 */
public class LadderSkullBuilder extends LadderItemBuilder implements SkullBuilder {
    
    public LadderSkullBuilder() {
        super(Material.SKULL_ITEM);
        this.item.setDurability((short) 3);
    }
    
    public LadderSkullBuilder(ItemBuilder builder) {
        super(builder.build().clone());
    }
    
    @Override
    public LadderSkullBuilder setType(Material material) {
        throw new UnsupportedOperationException("You can't modify the material for SkullBuilder.");
    }
    
    @Override
    public LadderSkullBuilder setData(short data) {
        throw new UnsupportedOperationException("You can't modify the data for SkullBuilder.");
    }
    
    @Override
    public LadderSkullBuilder setAmount(int amount) {
        return (LadderSkullBuilder) super.setAmount(amount);
    }
    
    @Override
    public LadderSkullBuilder setName(String name) {
        return (LadderSkullBuilder) super.setName(name);
    }
    
    @Override
    public LadderSkullBuilder setLore(List<String> lore) {
        return (LadderSkullBuilder) super.setLore(lore);
    }

    @Override
    public LadderSkullBuilder addLore(List<String> loreToAdd) {
        return (LadderSkullBuilder) super.addLore(loreToAdd);
    }

    @Override
    public LadderSkullBuilder addEnchantment(Enchantment enchant, int level) {
        return (LadderSkullBuilder) super.addEnchantment(enchant, level);
    }
    
    @Override
    public LadderSkullBuilder removeEnchantment(Enchantment enchant) {
        return (LadderSkullBuilder) super.removeEnchantment(enchant);
    }
    
    @Override
    public LadderSkullBuilder setEnchanted(boolean enchanted) {
        return (LadderSkullBuilder) super.setEnchanted(enchanted);
    }
    
    @Override
    public LadderSkullBuilder addAllItemFlags() {
        return (LadderSkullBuilder) super.addAllItemFlags();
    }
    
    @Override
    public LadderSkullBuilder setUnbreakable(boolean unbreakable) {
        return (LadderSkullBuilder) super.setUnbreakable(unbreakable);
    }
    
    @Override
    public LadderSkullBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> biConsumer) {
        return (LadderSkullBuilder) super.setClickEvent(biConsumer);
    }
    
    // --- skull methods ---
    
    @Override
    public LadderSkullBuilder setOwner(String owner) {
        ((SkullMeta) this.meta).setOwner(owner);
        this.item.setItemMeta(this.meta);
        return this;
    }
    
    @Override
    public SkullBuilder setTexture(Property texture) {
        return this.setTextureOfGameProfile(PlayerUtils.getProfile(texture));
    }
    
    @Override
    public LadderSkullBuilder setTexture(String textureUrl) {
        return this.setTextureOfGameProfile(PlayerUtils.getProfile(textureUrl));
    }
    
    private LadderSkullBuilder setTextureOfGameProfile(GameProfile profile) {
        try {
            Field field = this.meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(this.meta, profile);
        } catch(Exception ignored) {}
        this.item.setItemMeta(this.meta);
        return this;
    }
}
