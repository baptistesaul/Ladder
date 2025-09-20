package fr.ladder.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.ladder.api.LadderAPI;
import fr.ladder.api.bukkit.item.BannerBuilder;
import fr.ladder.api.bukkit.item.BookBuilder;
import fr.ladder.api.bukkit.item.ItemBuilder;
import fr.ladder.api.bukkit.item.SkullBuilder;
import fr.ladder.api.bukkit.profile.SkinTexture;
import fr.ladder.api.i18n.Messages;
import fr.ladder.api.i18n.Var;
import fr.ladder.api.util.ItemUtils;
import fr.ladder.core.bukkit.item.LadderBannerBuilder;
import fr.ladder.core.bukkit.item.LadderBookBuilder;
import fr.ladder.core.bukkit.item.LadderItemBuilder;
import fr.ladder.core.bukkit.item.LadderSkullBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Snowtyy
 */
public class LadderItemUtils implements ItemUtils.Implementation {
    
    @Override
    public ItemBuilder toItemBuilder(ItemStack item) {
        return new LadderItemBuilder(item);
    }
    
    @Override
    public ItemBuilder createItem(Material material) {
        return new LadderItemBuilder(material);
    }
    
    @Override
    public ItemBuilder copyOf(ItemBuilder itemBuilder) {
        return new LadderItemBuilder(itemBuilder);
    }
    
    @Override
    public BannerBuilder createBanner(DyeColor color) {
        return new LadderBannerBuilder(color);
    }
    
    @Override
    public BannerBuilder copyOf(BannerBuilder itemBuilder) {
        return new LadderBannerBuilder(itemBuilder);
    }
    
    @Override
    public BookBuilder createBook() {
        return new LadderBookBuilder();
    }
    
    @Override
    public BookBuilder copyOf(BookBuilder itemBuilder) {
        return new LadderBookBuilder(itemBuilder);
    }
    
    @Override
    public SkullBuilder createSkull() {
        return new LadderSkullBuilder();
    }
    
    @Override
    public SkullBuilder copyOf(SkullBuilder itemBuilder) {
        return new LadderSkullBuilder(itemBuilder);
    }
    
    @Override
    public ItemBuilder createPotion(PotionType type, int level, boolean splash) {
        ItemStack item = new ItemStack(Material.POTION);
        // apply potion effect
        Potion potion = new Potion(type, level);
        potion.setSplash(splash);
        potion.apply(item);
        
        return this.toItemBuilder(item);
    }
    
    @Override
    public ItemBuilder getUnknownItem() {
        return this.createItem(Material.INK_SACK).setData((short) 8).setName(" ");
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public ItemBuilder getGlass(DyeColor color) {
        return this.createItem(Material.STAINED_GLASS_PANE)
                .setData(color.getWoolData())
                .setName(" ");
    }
    
    @Override
    public ItemBuilder getBackItem() {
        return this.createSkull()
                .setTexture(SkinTexture.BACK_ITEM.toProperty())
                .setName(Messages.get("fr.ladder.core.items.back"));
    }
    
    @Override
    public ItemBuilder getPreviousItem() {
        return this.createSkull()
                .setTexture(SkinTexture.PREVIOUS_ITEM.toProperty())
                .setName(Messages.get("fr.ladder.core.items.previous"));
    }
    
    @Override
    public ItemBuilder getNextItem() {
        return this.createSkull()
                .setTexture(SkinTexture.NEXT_ITEM.toProperty())
                .setName(Messages.get("fr.ladder.core.items.next"));
    }
    
    @Override
    public ItemBuilder getConfigItem() {
        return this.createItem(Material.CHEST)
                .setName(Messages.get("fr.ladder.core.items.config"));
    }

    @Override
    public BannerBuilder getTeamsItem(DyeColor color) {
        return this.createBanner(color)
                .setName(Messages.get("fr.ladder.core.items.teams"));
    }

    @Override
    public BannerBuilder getBannerMinus(DyeColor color, String value) {
        BannerBuilder banner = this.createBanner(color)
                .setDesign(BannerBuilder.Design.MINUS);
        return banner.setName(banner.toChatColor() +
                Messages.get("fr.ladder.core.items.banner_minus", Var.of("s", value)));
    }
    
    @Override
    public BannerBuilder getBannerPlus(DyeColor color, String value) {
        BannerBuilder banner = this.createBanner(color)
                .setDesign(BannerBuilder.Design.PLUS);
        return banner.setName(banner.toChatColor() +
                Messages.get("fr.ladder.core.items.banner_plus", Var.of("s", value)));
    }
    
    @Override
    public Item spawnItem(Location position, ItemStack itemStack) {
        Item item = position.getWorld().dropItem(position, itemStack);
        item.setVelocity(new Vector(0, 0.15, 0));
        item.teleport(position);
        
        return item;
    }
    
    @Override
    public List<Item> spawnItems(Location position, ItemStack... itemStacks) {
        return Arrays.stream(itemStacks)
                .map(it -> {
                    Item item = position.getWorld().dropItem(position, it);
                    item.setVelocity(new Vector(0, 0.15, 0));
                    item.teleport(position);
                    return item;
                }).collect(Collectors.toList());
    }
    
    @Nullable
    @Override
    public JsonElement serialize(@Nullable ItemStack itemStack) {
        if (itemStack == null)
            return null;
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", itemStack.getType().name());
        
        if(itemStack.getAmount() != 1)
            jsonObject.addProperty("amount", itemStack.getAmount());
        
        if(itemStack.getDurability() != 0)
            jsonObject.addProperty("damage", itemStack.getDurability());
        
        if(itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            
            JsonObject enchantments = new JsonObject();
            for(Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                enchantments.addProperty(entry.getKey().getName(), entry.getValue());
            }
            jsonObject.add("enchantments", enchantments);
            
            jsonObject.addProperty("unbreakable", meta.spigot().isUnbreakable());
            
            if(meta.hasDisplayName())
                jsonObject.addProperty("name", meta.getDisplayName());
            
            if(meta.hasLore()) {
                JsonArray lore = new JsonArray();
                for(String line : meta.getLore()) {
                    lore.add(new JsonPrimitive(line));
                }
                jsonObject.add("lore", lore);
            }
            
            if(!meta.getItemFlags().isEmpty()) {
                JsonArray itemFlags = new JsonArray();
                for(ItemFlag flag : meta.getItemFlags()) {
                    itemFlags.add(new JsonPrimitive(flag.name()));
                }
                jsonObject.add("item_flags", itemFlags);
            }
        }
        return jsonObject;
    }
    
    @Override
    public ItemStack deserialize(JsonElement jsonElement) {
        if(!jsonElement.isJsonObject())
            return null;
        
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        
        Material type = Material.getMaterial(jsonObject.get("type").getAsString());
        int amount = 1;
        short damage = 0;
        if(jsonObject.has("amount"))
            amount = jsonObject.get("amount").getAsInt();
        if(jsonObject.has("damage"))
            damage = jsonObject.get("damage").getAsShort();
        
        ItemStack itemStack = new ItemStack(type, amount, damage);
        
        boolean hasMeta = false;
        ItemMeta meta = itemStack.getItemMeta();
        
        if(jsonObject.has("enchantments")) {
            hasMeta = true;
            JsonObject enchantments = jsonObject.getAsJsonObject("enchantments");
            for(Map.Entry<String, JsonElement> entry : enchantments.entrySet()) {
                Enchantment enchantment = Enchantment.getByName(entry.getKey());
                if(enchantment != null)
                    meta.addEnchant(enchantment, entry.getValue().getAsInt(), true);
            }
        }
        
        if(jsonObject.has("unbreakable")) {
            hasMeta = true;
            meta.spigot().setUnbreakable(jsonObject.get("unbreakable").getAsBoolean());
        }
        
        if(jsonObject.has("name")) {
            hasMeta = true;
            meta.setDisplayName(jsonObject.get("name").getAsString());
        }
        
        if(jsonObject.has("lore")) {
            hasMeta = true;
            JsonArray jsonLore = jsonObject.getAsJsonArray("lore");
            List<String> lore = new ArrayList<>(jsonLore.size());
            for(JsonElement element : jsonLore) {
                lore.add(element.getAsString());
            }
            meta.setLore(lore);
        }
        
        if(jsonObject.has("item_flags")) {
            hasMeta = true;
            JsonArray jsonFlags = jsonObject.getAsJsonArray("item_flags");
            for(JsonElement element : jsonFlags) {
                ItemFlag flag = ItemFlag.valueOf(element.getAsString());
                meta.addItemFlags(flag);
            }
        }
        
        if(hasMeta)
            itemStack.setItemMeta(meta);
        
        return itemStack;
    }

    @Override
    public int getBonusWithFortune(ItemStack itemInHand) {
        if(itemInHand == null)
            return 0;
        int level = itemInHand.getEnchantments().getOrDefault(Enchantment.LOOT_BONUS_BLOCKS, 0);
        return LadderAPI.RANDOM.nextInt(1 + level);
    }
}
