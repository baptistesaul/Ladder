package fr.ladder.core.game;

import fr.ladder.api.LadderAPI;
import fr.ladder.api.game.GamePlayer;
import fr.ladder.api.tool.Task;
import fr.ladder.api.util.PlayerUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Snowtyy
 */
public class LadderGamePlayer implements GamePlayer {

    private String name;

    private String displayName;

    private final UUID uuid;

    private Player bukkitPlayer;

    private Queue<String> messageQueue = null;

    private NBTTagCompound offlinePlayerData = null;

    private final Set<EntityDamageEvent.DamageCause> invulnerableDamageSet;

    private Integer offlineTaskId = null;

    private final Map<UUID, Long> lastDamagerMap = new HashMap<>(10);

    private LadderGamePlayer killer = null;

    private final List<LadderGamePlayer> assistants = new ArrayList<>(5);

    private final List<LadderGamePlayer> victims = new ArrayList<>(5);

    private EntityDamageEvent.DamageCause deathCause = null;

    private Location deathLocation = null;

    private ItemStack[] deathInventory = null;

    public LadderGamePlayer(Player player) {
        this.name = this.displayName = player.getName();
        this.uuid = player.getUniqueId();
        this.bukkitPlayer = player;
        this.invulnerableDamageSet = new HashSet<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.ifOnline(p -> p.setDisplayName(displayName));
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Player getPlayer() {
        return this.bukkitPlayer;
    }

    @Override
    public boolean isAlive() {
        return LadderAPI.getGameHandler().getAlivePlayers().contains(this);
    }

    @Override
    public boolean isOnline() {
        return this.bukkitPlayer != null;
    }

    @Override
    public void ifOnline(Consumer<Player> action) {
        if(this.bukkitPlayer == null)
            return;
        action.accept(this.bukkitPlayer);
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(message, false);
    }

    @Override
    public void sendMessage(String message, boolean forceIfOffline) {
        if(this.bukkitPlayer != null)
            this.bukkitPlayer.sendMessage(message);
        else if(forceIfOffline) {
            if(this.messageQueue == null)
                this.messageQueue = new PriorityQueue<>();
            this.messageQueue.add(message);
        }
    }

    @Override
    public Location getLocation() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getLocation();

        NBTTagList pos = this.offlinePlayerData.getList("Pos", 6);
        NBTTagList rot = this.offlinePlayerData.getList("Rotation", 5);

        long mostId = this.offlinePlayerData.getByte("WorldUUIDMost");
        long leastId = this.offlinePlayerData.getByte("WorldUUIDLeast");
        UUID worldId = new UUID(mostId, leastId);

        return new Location(Bukkit.getWorld(worldId), pos.d(0), pos.d(1), pos.d(2), rot.e(0), rot.e(1));
    }

    @Override
    public void teleport(Location newLocation) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.teleport(newLocation);
            return;
        }

        long mostId = newLocation.getWorld().getUID().getMostSignificantBits();
        long leastId = newLocation.getWorld().getUID().getLeastSignificantBits();
        this.offlinePlayerData.setLong("WorldUUIDMost", mostId);
        this.offlinePlayerData.setLong("WorldUUIDLeast", leastId);
        // save location
        NBTTagList pos = new NBTTagList();
        pos.add(new NBTTagDouble(newLocation.getX()));
        pos.add(new NBTTagDouble(newLocation.getY()));
        pos.add(new NBTTagDouble(newLocation.getZ()));
        this.offlinePlayerData.set("Pos", pos);
        // save rotation
        NBTTagList rot = new NBTTagList();
        rot.add(new NBTTagFloat(newLocation.getYaw()));
        rot.add(new NBTTagFloat(newLocation.getPitch()));
        this.offlinePlayerData.set("Rotation", rot);

    }

    @Override
    public GameMode getGameMode() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getGameMode();

        return switch(this.offlinePlayerData.getInt("playerGameType")) {
            case 0 -> GameMode.SURVIVAL;
            case 1 -> GameMode.CREATIVE;
            case 2 -> GameMode.ADVENTURE;
            case 3 -> GameMode.SPECTATOR;
            default ->
                    throw new IllegalStateException("Unexpected value: " + this.offlinePlayerData.getInt("playerGameType"));
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setGameMode(GameMode gameMode) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setGameMode(gameMode);
            return;
        }

        this.offlinePlayerData.setInt("playerGameType", gameMode.getValue());
    }

    @Override
    public double getHealth() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getHealth();

        return this.offlinePlayerData.getFloat("HealF");
    }

    @Override
    public void setHealth(double health) {
        double maxHealth = this.getMaxHealth();
        if(health < 0 || health > maxHealth)
            return;
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setHealth(health);
            return;
        }

        this.offlinePlayerData.setFloat("HealF", (float) health);
        this.offlinePlayerData.setShort("Health", (short) ((int) Math.ceil(health)));

    }

    @Override
    public double getMaxHealth() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getMaxHealth();

        NBTTagList attributes = this.offlinePlayerData.getList("Attributes", 10);
        for(int i = 0; i < attributes.size(); i++) {
            NBTTagCompound attribute = attributes.get(i);
            if(attribute.getString("Name").equals("generic.maxHealth"))
                return attribute.getDouble("Base");
        }

        return 20;
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setMaxHealth(maxHealth);
            return;
        }

        NBTTagList attributes = this.offlinePlayerData.getList("Attributes", 10);
        for(int i = 0; i < attributes.size(); i++) {
            NBTTagCompound attribute = attributes.get(i);
            if(attribute.getString("Name").equals("generic.maxHealth"))
                attribute.setDouble("Base", maxHealth);
        }
    }

    @Override
    public int getFoodLevel() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getFoodLevel();

        return this.offlinePlayerData.getInt("foodLevel");
    }

    @Override
    public void setFoodLevel(int foodLevel) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setFoodLevel(foodLevel);
            return;
        }

        this.offlinePlayerData.setInt("foodLevel", foodLevel);
    }

    @Override
    public float getSaturation() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getSaturation();

        return this.offlinePlayerData.getFloat("foodSaturationLevel");
    }

    @Override
    public void setSaturation(float saturation) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setSaturation(saturation);
            return;
        }

        this.offlinePlayerData.setFloat("foodSaturationLevel", saturation);
    }

    @Override
    public boolean getAllowFlight() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getAllowFlight();

        return this.offlinePlayerData.getCompound("abilities").getBoolean("mayfly");
    }

    @Override
    public void setAllowFlight(boolean allowFlight) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setAllowFlight(allowFlight);
            return;
        }

        if(!this.offlinePlayerData.hasKey("abilities"))
            return;
        this.offlinePlayerData.getCompound("abilities").setBoolean("mayfly", allowFlight);
    }

    @Override
    public boolean isFlying() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.isFlying();

        return this.offlinePlayerData.getCompound("abilities").getBoolean("flying");
    }

    @Override
    public void setFlying(boolean flying) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setFlying(flying);
            return;
        }

        if(!this.offlinePlayerData.hasKey("abilities"))
            return;
        this.offlinePlayerData.getCompound("abilities").setBoolean("flying", flying);
    }

    @Override
    public ItemStack[] getInventoryContents() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getInventory().getContents();

        ItemStack[] inventory = new ItemStack[36];
        NBTTagList items = this.offlinePlayerData.getList("Inventory", 10);
        for(int i = 0; i < items.size(); i++) {
            NBTTagCompound item = items.get(i);
            int slot = item.getInt("Slot");
            if(slot < 100) {
                var nmsItem = net.minecraft.server.v1_8_R3.ItemStack.createStack(item);
                inventory[slot] = CraftItemStack.asBukkitCopy(nmsItem);
            }
        }
        return inventory;
    }

    @Override
    public void setInventoryContents(ItemStack[] contents) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.getInventory().setContents(contents);
            return;
        }

        NBTTagList newInventory = new NBTTagList();
        for(int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if(item == null || item.getType() == Material.AIR)
                continue;
            NBTTagCompound itemData = new NBTTagCompound();
            itemData.setInt("Slot", i);
            CraftItemStack.asNMSCopy(item).save(itemData);
            newInventory.add(itemData);
        }

        NBTTagList oldInventory = this.offlinePlayerData.getList("Inventory", 10);
        for(int i = 0; i < oldInventory.size(); i++) {
            NBTTagCompound item = oldInventory.get(i);
            int slot = item.getInt("Slot");
            if(slot >= 100)
                newInventory.add(item);
        }

        this.offlinePlayerData.set("Inventory", newInventory);

        int slot = this.getSelectedItemSlot();
        ItemStack itemInHand = slot < contents.length ? contents[slot] : null;
        if(itemInHand == null) {
            this.offlinePlayerData.remove("SelectedItem");
        } else {
            NBTTagCompound itemData = new NBTTagCompound();
            CraftItemStack.asNMSCopy(itemInHand).save(itemData);
            this.offlinePlayerData.set("SelectedItem", itemData);
        }

    }

    @Override
    public ItemStack[] getInventoryArmors() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getInventory().getArmorContents();


        ItemStack[] inventory = new ItemStack[4];
        NBTTagList items = this.offlinePlayerData.getList("Inventory", 10);
        for(int i = 0; i < items.size(); i++) {
            NBTTagCompound item = items.get(i);
            int slot = item.getInt("Slot");
            if(slot < 100)
                continue;
            var nmsItem = net.minecraft.server.v1_8_R3.ItemStack.createStack(item);
            inventory[slot - 100] = CraftItemStack.asBukkitCopy(nmsItem);
        }
        return inventory;
    }

    @Override
    public void setInventoryArmors(ItemStack[] contents) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.getInventory().setArmorContents(contents);
            return;
        }

        NBTTagList newInventory = new NBTTagList();
        NBTTagList oldInventory = this.offlinePlayerData.getList("Inventory", 10);
        for(int i = 0; i < oldInventory.size(); i++) {
            NBTTagCompound item = oldInventory.get(i);
            int slot = item.getInt("Slot");
            if(slot < 100)
                newInventory.add(item);
        }

        for(int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if(item == null || item.getType() == Material.AIR)
                continue;
            NBTTagCompound itemData = new NBTTagCompound();
            itemData.setInt("Slot", 100 + i);
            CraftItemStack.asNMSCopy(item).save(itemData);
            newInventory.add(itemData);
        }

        this.offlinePlayerData.set("Inventory", newInventory);
    }

    @Nullable
    @Override
    public ItemStack getItem(int index) {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getInventory().getItem(index);

        NBTTagList inventory = this.offlinePlayerData.getList("Inventory", 10);
        for(int i = 0; i < inventory.size(); i++) {
            NBTTagCompound item = inventory.get(i);
            int slot = item.getInt("Slot");
            if(slot == (index < 36 ? index : 64 + index)) {
                var nmsItem = net.minecraft.server.v1_8_R3.ItemStack.createStack(item);
                return CraftItemStack.asBukkitCopy(nmsItem);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public ItemStack getItemInHand() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getInventory().getItemInHand();

        if(this.offlinePlayerData.hasKey("SelectedItem")) {
            var data = this.offlinePlayerData.getCompound("SelectedItem");
            var nmsItem = net.minecraft.server.v1_8_R3.ItemStack.createStack(data);
            return CraftItemStack.asBukkitCopy(nmsItem);
        } else {
            return null;
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.getInventory().setItem(index, item);
            return;
        }

        NBTTagList newInventory = new NBTTagList();
        NBTTagList oldInventory = this.offlinePlayerData.getList("Inventory", 10);
        for(int i = 0; i < oldInventory.size(); i++) {
            NBTTagCompound itemData = oldInventory.get(i);
            int slot = itemData.getInt("Slot");
            if(slot != (index < 36 ? index : 64 + index)) {
                CraftItemStack.asNMSCopy(item).save(itemData);
                newInventory.add(itemData);
            }
        }
        if(item != null && index >= 0 && index < 40) {
            NBTTagCompound itemData = new NBTTagCompound();
            itemData.setInt("Slot", index < 36 ? index : 64 + index);
            CraftItemStack.asNMSCopy(item).save(itemData);
            newInventory.add(itemData);
        }
        this.offlinePlayerData.set("Inventory", newInventory);

        int slot = this.getSelectedItemSlot();
        if(index == slot) {
            if(item == null) {
                this.offlinePlayerData.remove("SelectedItem");
            } else {
                NBTTagCompound itemData = new NBTTagCompound();
                CraftItemStack.asNMSCopy(item).save(itemData);
                this.offlinePlayerData.set("SelectedItem", itemData);
            }
        }
    }

    @Override
    public void setItemInHand(ItemStack item) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.getInventory().setItemInHand(item);
            return;
        }

        if(item == null) {
            this.offlinePlayerData.remove("SelectedItem");
        } else {
            NBTTagCompound itemData = new NBTTagCompound();
            CraftItemStack.asNMSCopy(item).save(itemData);
            this.offlinePlayerData.set("SelectedItem", itemData);
        }

        int slot = this.getSelectedItemSlot();
        this.setItem(slot, item);
    }

    @Override
    public int getSelectedItemSlot() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getInventory().getHeldItemSlot();

        return this.offlinePlayerData.getInt("SelectedItemSlot");
    }

    @Override
    public void setSelectedItemSlot(int slot) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.getInventory().setHeldItemSlot(slot);
            return;
        }

        this.offlinePlayerData.setInt("SelectedItemSlot", slot);
        this.setItemInHand(this.getItem(slot));
    }

    @Override
    public boolean isInvulnerable(EntityDamageEvent.DamageCause cause) {
        return this.invulnerableDamageSet.contains(cause);
    }

    @Override
    public void setInvulnerable(EntityDamageEvent.DamageCause... causes) {
        this.invulnerableDamageSet.addAll(Arrays.asList(causes));
    }

    @Override
    public void removeInvulnerability(EntityDamageEvent.DamageCause... causes) {
        Arrays.asList(causes).forEach(this.invulnerableDamageSet::remove);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Collection<PotionEffect> getActivePotionEffects() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getActivePotionEffects();

        NBTTagList old = this.offlinePlayerData.getList("ActiveEffects", 10);
        List<PotionEffect> potionEffects = new ArrayList<>(old.size());
        for(int i = 0; i < old.size(); i++) {
            NBTTagCompound effect = old.get(i);
            byte id = effect.getByte("Id");
            byte amplifier = effect.getByte("Amplifier");
            int duration = effect.getInt("Duration");
            boolean ambient = effect.getBoolean("Ambient");
            boolean showParticles = effect.getBoolean("ShowParticles");
            potionEffects.add(new PotionEffect(PotionEffectType.getById(id), amplifier, duration, ambient, showParticles));
        }

        return potionEffects;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addPotionEffect(PotionEffect potionEffect) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.addPotionEffect(potionEffect, true);
            return;
        }

        NBTTagList old = this.offlinePlayerData.getList("ActiveEffects", 10);
        NBTTagList activeEffects = new NBTTagList();
        for(int i = 0; i < old.size(); i++) {
            NBTTagCompound effect = old.get(i);
            if(effect.getInt("Id") != potionEffect.getType().getId())
                activeEffects.add(effect);
        }
        NBTTagCompound newEffect = new NBTTagCompound();
        newEffect.setByte("Id", (byte) potionEffect.getType().getId());
        newEffect.setByte("Amplifier", (byte) potionEffect.getAmplifier());
        newEffect.setInt("Duration", potionEffect.getDuration());
        newEffect.setBoolean("Ambient", potionEffect.isAmbient());
        newEffect.setBoolean("ShowParticles", potionEffect.hasParticles());

        activeEffects.add(newEffect);
        this.offlinePlayerData.set("ActiveEffects", activeEffects);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removePotionEffect(PotionEffectType potionType) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.removePotionEffect(potionType);
            return;
        }

        NBTTagList old = this.offlinePlayerData.getList("ActiveEffects", 10);
        NBTTagList activeEffects = new NBTTagList();
        for(int i = 0; i < old.size(); i++) {
            NBTTagCompound effect = old.get(i);
            if(effect.getInt("Id") != potionType.getId())
                activeEffects.add(effect);
        }
        this.offlinePlayerData.set("ActiveEffects", activeEffects);
    }

    @Override
    public void clearPotionEffects() {
        if(this.bukkitPlayer != null) {
            PlayerUtils.clearEffects(this.bukkitPlayer);
            return;
        }

        this.offlinePlayerData.remove("ActiveEffects");
        this.offlinePlayerData.remove("AbsorptionAmount");
        this.setWalkSpeed(0.2f);
    }

    public float getWalkSpeed() {
        if(this.bukkitPlayer != null)
            return this.bukkitPlayer.getWalkSpeed();

        return this.offlinePlayerData.getCompound("abilities").getFloat("walkSpeed") * 2.0f;
    }

    public void setWalkSpeed(float walkSpeed) {
        if(this.bukkitPlayer != null) {
            this.bukkitPlayer.setWalkSpeed(walkSpeed);
            return;
        }

        float value = Math.max(walkSpeed, 1.0E-4F) / 2.0F;
        if(!this.offlinePlayerData.hasKey("abilities"))
            return;
        this.offlinePlayerData.getCompound("abilities").setFloat("walkSpeed", value);
    }

    @Nullable
    public LadderGamePlayer getKiller() {
        return this.killer;
    }

    public List<LadderGamePlayer> getAssistants() {
        return List.copyOf(this.assistants);
    }

    @Override
    public Collection<LadderGamePlayer> getVictims() {
        return List.copyOf(this.victims);
    }

    @Nullable
    @Override
    public EntityDamageEvent.DamageCause getDeathCause() {
        return this.deathCause;
    }

    @Nullable
    @Override
    public Location getDeathLocation() {
        return this.deathLocation;
    }

    public void saveDeathInventory() {
        if(this.deathInventory == null)
            this.deathInventory = new ItemStack[40];
        ItemStack[] contents = this.getInventoryContents();
        ItemStack[] armors = this.getInventoryArmors();
        System.arraycopy(contents, 0, this.deathInventory, 0, contents.length);
        System.arraycopy(armors, 0, this.deathInventory, 36, armors.length);
    }

    public void giveDeathInventory() {
        if(this.deathInventory == null)
            return;

        ItemStack[] contents = new ItemStack[36];
        ItemStack[] armors = new ItemStack[4];
        System.arraycopy(this.deathInventory, 0, contents, 0, contents.length);
        System.arraycopy(this.deathInventory, 36, armors, 0, armors.length);
        this.setInventoryContents(contents);
        this.setInventoryArmors(armors);
    }

    // =============== EVENTS ===============

    public void onJoin(Player player) {
        this.name = player.getName();
        player.setDisplayName(this.displayName);
        this.bukkitPlayer = player;
        if(this.messageQueue != null) {
            this.messageQueue.forEach(player::sendMessage);
            this.messageQueue = null;
        }
        if(this.offlineTaskId != null) {
            Task.cancel(this.offlineTaskId);
            this.offlineTaskId = null;
            Bukkit.getLogger().info("Disconnect task of " + this.getName() + " has been cancelled.");
        }
    }

    public void onQuit(LadderGame game) {
        this.bukkitPlayer = null;
    }

    public void onDeath(LadderGame game, EntityDamageEvent.DamageCause cause) {
        System.out.println(this.name + " die, Cause: " + cause);
        if(this.bukkitPlayer.getKiller() != null)
            System.out.println("killer: " + this.bukkitPlayer.getKiller().getName());
        this.deathLocation = this.getLocation();
        this.saveDeathInventory();
        this.deathCause = cause;
        // filter killer and assistants
        long timestamp = System.currentTimeMillis();
        System.out.println("lastDamagerMap: " + this.lastDamagerMap.size());
        List<LadderGamePlayer> damagerList = this.lastDamagerMap
                .entrySet()
                .stream()
                .filter(e -> {
                    System.out.println("timestamp: " + timestamp);
                    System.out.println("value: " + e.getValue());
                    return timestamp < e.getValue();
                })
                .sorted((Map.Entry.comparingByValue()))
                .map(e -> game.getPlayer(e.getKey()))
                .toList();
        System.out.println("damagerList: " + damagerList.size());
        this.assistants.clear();
        if(damagerList.isEmpty()) {
            this.killer = null;
        } else {
            this.killer = damagerList.getFirst();
            this.assistants.addAll(damagerList.subList(1, damagerList.size()));
        }
    }

    public void onDamage(LadderGamePlayer damager) {
        long timestamp = System.currentTimeMillis();
        for(Map.Entry<UUID, Long> entry : this.lastDamagerMap.entrySet()) {
            if(timestamp < entry.getValue())
                continue;
            this.lastDamagerMap.remove(entry.getKey());
        }

        this.lastDamagerMap.put(damager.getUniqueId(), timestamp + 5000);
    }

    public void onKill(LadderGamePlayer damager) {
        this.victims.add(damager);
    }

    public void loadData(Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        this.offlinePlayerData = new NBTTagCompound();
        nmsPlayer.e(this.offlinePlayerData);
    }

    public void saveData(@Nullable Player player) {
        if(this.offlinePlayerData == null)
            return;
        File file = new File("world/playerdata/" + this.uuid.toString() + ".dat");
        try(OutputStream output = new FileOutputStream(file)) {
            NBTCompressedStreamTools.a(this.offlinePlayerData, output);
            if(player != null)
                ((CraftPlayer) player).getHandle().f(this.offlinePlayerData);
        }
        catch(IOException e) {
            LadderAPI.catchException("An error occurred while saving \"playerdata\" of " + this.name, e);
        }
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if(this == object)
            return true;
        if(!(object instanceof LadderGamePlayer that))
            return false;
        return Objects.equals(uuid, that.uuid);
    }
}
