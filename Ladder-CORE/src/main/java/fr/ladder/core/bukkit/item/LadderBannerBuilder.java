package fr.ladder.core.bukkit.item;

import fr.ladder.api.bukkit.item.BannerBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Snowtyy
 */
public class LadderBannerBuilder extends LadderItemBuilder implements BannerBuilder {
    
    public LadderBannerBuilder(DyeColor dyeColor) {
        super(Material.BANNER);
        BannerMeta meta = (BannerMeta) this.meta;
        meta.setBaseColor(dyeColor);
        meta.addItemFlags(ItemFlag.values());
    }
    
    public LadderBannerBuilder(BannerBuilder bannerBuilder) {
        super(bannerBuilder.build().clone());
    }
    
    @Override
    public LadderBannerBuilder setType(Material material) {
        throw new UnsupportedOperationException("You can't modify the material for BannerBuilder.");
    }
    
    @Override
    public LadderBannerBuilder setData(short data) {
        return (LadderBannerBuilder) super.setData(data);
    }
    
    @Override
    public LadderBannerBuilder setAmount(int amount) {
        return (LadderBannerBuilder) super.setAmount(amount);
    }
    
    @Override
    public LadderBannerBuilder setName(String name) {
        return (LadderBannerBuilder) super.setName(name);
    }
    
    @Override
    public LadderBannerBuilder setLore(List<String> lore) {
        return (LadderBannerBuilder) super.setLore(lore);
    }

    @Override
    public LadderBannerBuilder addLore(List<String> loreToAdd) {
        return (LadderBannerBuilder) super.addLore(loreToAdd);
    }

    @Override
    public LadderBannerBuilder addEnchantment(Enchantment enchant, int level) {
        return (LadderBannerBuilder) super.addEnchantment(enchant, level);
    }
    
    @Override
    public LadderBannerBuilder removeEnchantment(Enchantment enchant) {
        return (LadderBannerBuilder) super.removeEnchantment(enchant);
    }
    
    @Override
    public LadderBannerBuilder setEnchanted(boolean enchanted) {
        return (LadderBannerBuilder) super.setEnchanted(enchanted);
    }
    
    @Override
    public LadderBannerBuilder addAllItemFlags() {
        return (LadderBannerBuilder) super.addAllItemFlags();
    }
    
    @Override
    public LadderBannerBuilder setUnbreakable(boolean unbreakable) {
        return (LadderBannerBuilder) super.setUnbreakable(unbreakable);
    }
    
    @Override
    public LadderBannerBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> biConsumer) {
        return (LadderBannerBuilder) super.setClickEvent(biConsumer);
    }
    
    // --- banner methods ---
    
    @Override
    public LadderBannerBuilder addPattern(DyeColor color, PatternType type) {
        List<Pattern> patterns = ((BannerMeta)this.meta).getPatterns();
        patterns.add(new Pattern(color, type));
        ((BannerMeta)this.meta).setPatterns(patterns);
        this.item.setItemMeta(this.meta);
        return this;
    }
    
    @Override
    public LadderBannerBuilder setDesign(Design design) {
        if(design == null)
            return this;
        List<Pattern> patterns = design.getPatterns(((BannerMeta)this.meta).getBaseColor());
        ((BannerMeta)this.meta).setPatterns(patterns);
        this.item.setItemMeta(this.meta);
        return this;
    }
    
    @Override
    public ChatColor toChatColor() {
        return switch(((BannerMeta)this.meta).getBaseColor()) {
            case WHITE -> ChatColor.WHITE;
            case ORANGE -> ChatColor.GOLD;
            case MAGENTA, PINK -> ChatColor.LIGHT_PURPLE;
            case LIGHT_BLUE -> ChatColor.AQUA;
            case YELLOW -> ChatColor.YELLOW;
            case LIME -> ChatColor.GREEN;
            case GRAY -> ChatColor.DARK_GRAY;
            case SILVER -> ChatColor.GRAY;
            case CYAN -> ChatColor.DARK_AQUA;
            case PURPLE -> ChatColor.DARK_PURPLE;
            case BLUE -> ChatColor.BLUE;
            case BROWN, BLACK -> ChatColor.BLACK;
            case GREEN -> ChatColor.DARK_GREEN;
            case RED -> ChatColor.RED;
        };
    }
}
