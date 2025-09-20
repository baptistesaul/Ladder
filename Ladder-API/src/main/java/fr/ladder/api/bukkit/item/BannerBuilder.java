package fr.ladder.api.bukkit.item;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface BannerBuilder extends ItemBuilder {
    
    @Override
    BannerBuilder setType(Material material);
    
    @Override
    BannerBuilder setData(short data);
    
    @Override
    BannerBuilder setAmount(int amount);
    
    @Override
    BannerBuilder setName(String name);
    
    @Override
    BannerBuilder setLore(List<String> lore);

    @Override
    BannerBuilder addLore(List<String> lore);

    @Override
    BannerBuilder addEnchantment(Enchantment enchantment, int level);
    
    @Override
    BannerBuilder removeEnchantment(Enchantment enchantment);
    
    @Override
    BannerBuilder setEnchanted(boolean enchanted);
    
    @Override
    BannerBuilder addAllItemFlags();
    
    @Override
    BannerBuilder setUnbreakable(boolean unbreakable);
    
    @Override
    BannerBuilder setClickEvent(BiConsumer<Player, InventoryClickEvent> handleClick);
    
    /**
     * @param color Color of pattern to add on the banner
     * @param type Type of pattern to add on the banner
     * @return Current builder
     */
    BannerBuilder addPattern(DyeColor color, PatternType type);
    
    /**
     * @param design Design to apply on the banner
     * @return Current builder
     */
    BannerBuilder setDesign(Design design);
    
    /**
     * @return Associated chat color for the banner
     */
    ChatColor toChatColor();
    
    /**
     * Enum of default designs for the banners
     */
    enum Design {
        
        MINUS(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE),
                new Pattern(color, PatternType.BORDER)
        )),
        PLUS(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.STRAIGHT_CROSS),
                new Pattern(color, PatternType.BORDER),
                new Pattern(color, PatternType.STRIPE_BOTTOM),
                new Pattern(color, PatternType.STRIPE_TOP)
        )),
        HEART(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE),
                new Pattern(color, PatternType.SQUARE_TOP_LEFT),
                new Pattern(color, PatternType.SQUARE_TOP_RIGHT),
                new Pattern(color, PatternType.TRIANGLE_TOP)
        )),
        FLOWER(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.FLOWER)
        )),
        STAR(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL),
                new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR),
                new Pattern(color, PatternType.STRIPE_MIDDLE),
                new Pattern(DyeColor.WHITE, PatternType.FLOWER),
                new Pattern(color, PatternType.STRIPE_TOP),
                new Pattern(color, PatternType.STRIPE_BOTTOM),
                new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)
        )),
        CROSS(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.CROSS),
                new Pattern(color, PatternType.CURLY_BORDER)
        )),
        RIGHT_ARROWHEAD(color -> List.of(
            new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE),
            new Pattern(color, PatternType.HALF_VERTICAL)
        )),
        CIRCLE(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE)
        )),
        SQUARE(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL),
                new Pattern(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR),
                new Pattern(color, PatternType.STRIPE_TOP),
                new Pattern(color, PatternType.STRIPE_BOTTOM),
                new Pattern(color, PatternType.BORDER)
        )),
        YIN_YANG(color -> List.of(
                new Pattern(DyeColor.WHITE, PatternType.DIAGONAL_RIGHT),
                new Pattern(DyeColor.WHITE, PatternType.SQUARE_TOP_LEFT),
                new Pattern(color, PatternType.SQUARE_BOTTOM_RIGHT),
                new Pattern(color, PatternType.TRIANGLES_TOP),
                new Pattern(DyeColor.WHITE, PatternType.TRIANGLES_BOTTOM),
                new Pattern(color, PatternType.STRIPE_LEFT),
                new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT)
        ));
        
        final Function<DyeColor, List<Pattern>> patternsFunction;
        
        Design(Function<DyeColor, List<Pattern>> patternsFunction) {
            this.patternsFunction = patternsFunction;
        }
        
        /**
         * @param color Color of the banner on which the design is applied
         * @return A list of all patterns to add for the banner
         */
        public List<Pattern> getPatterns(DyeColor color) {
            return this.patternsFunction.apply(color);
        }
    }
}
