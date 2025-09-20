package fr.ladder.core.util;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import fr.ladder.api.LadderAPI;
import fr.ladder.api.util.PlayerUtils;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LadderPlayerUtils implements PlayerUtils.Implementation {

    private final Map<UUID, PropertyMap> profiles = Maps.newHashMap();
    
    @Override
    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }
    
    @Override
    public void clearEffects(Player player) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setWalkSpeed(0.2f);
        if(player instanceof CraftPlayer craftPlayer)
            craftPlayer.getHandle().setAbsorptionHearts(0f);
    }
    
    @Override
    public void disguise(Player player, GameProfile profile) {
        GameProfile current = ((CraftPlayer) player).getProfile();
        if(!this.profiles.containsKey(player.getUniqueId()))
            this.profiles.put(player.getUniqueId(), this.copyMap(current.getProperties(), new PropertyMap()));
        
        current.getProperties().clear();
        this.copyMap(profile.getProperties(), current.getProperties());
        this.refreshSkin(player);
    }
    
    @Override
    public void resetSkin(Player player) {
        if(!this.profiles.containsKey(player.getUniqueId()))
            return;
        GameProfile current = ((CraftPlayer) player).getProfile();
        PropertyMap properties = this.profiles.get(player.getUniqueId());
        current.getProperties().clear();
        this.copyMap(properties, current.getProperties());
        this.refreshSkin(player);
    }
    
    private void refreshSkin(Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        List<Packet<?>> packets = List.of(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, nmsPlayer),
                new PacketPlayOutEntityDestroy(player.getEntityId()),
                new PacketPlayOutNamedEntitySpawn(nmsPlayer),
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nmsPlayer),
                new PacketPlayOutRespawn(
                        nmsPlayer.getWorld().worldProvider.getDimension(),
                        nmsPlayer.getWorld().getDifficulty(),
                        nmsPlayer.getWorld().getWorldData().getType(),
                        nmsPlayer.playerInteractManager.getGameMode()
                )
        );
        
        for(Player target : Bukkit.getOnlinePlayers()) {
            if(target.canSee(player) && !target.getUniqueId().equals(player.getUniqueId())) {
                target.hidePlayer(player);
                target.showPlayer(player);
            }
        }

        packets.forEach(packet -> nmsPlayer.playerConnection.sendPacket(packet));
        player.teleport(player.getLocation());
        player.getInventory().setHeldItemSlot(player.getInventory().getHeldItemSlot());
        player.updateInventory();
    }
    
    private PropertyMap copyMap(PropertyMap from, PropertyMap to) {
        for(Map.Entry<String, Property> entry : from.entries()) {
            to.put(entry.getKey(), entry.getValue());
        }
        return to;
    }
    
    @Override
    public GameProfile getProfile(String textureUrl) {
        if(textureUrl == null || textureUrl.isEmpty())
            textureUrl = "https://textures.minecraft.net/texture/bc8ea1f51f253ff5142ca11ae45193a4ad8c3ab5e9c6eec8ba7a4fcb7bac40";
        
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", textureUrl).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        
        return profile;
    }

    
    @Override
    public GameProfile getProfile(UUID uuid) {
        GameProfile profile = new GameProfile(uuid, "");

        final String API_PROFILE_LINK = "https://sessionserver.mojang.com/session/minecraft/profile/";
        try(var httpClient = HttpClient.newHttpClient()) {
            // build request
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_PROFILE_LINK + uuid.toString() + "?unsigned=false"))
                    .GET()
                    .build();

            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(httpResponse.statusCode() != 200) {
                return new GameProfile(uuid, "");
            }

            JsonObject session = new JsonParser()
                    .parse(httpResponse.body())
                    .getAsJsonObject();

            JsonObject properties = session.get("properties")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject();

            String value = properties.get("value").getAsString();
            String signature = properties.get("signature").getAsString();
            profile.getProperties().put("textures", new Property("textures", value, signature));

        } catch (IOException | InterruptedException e) {
            LadderAPI.catchException("An error occurred while fetching game profile of uuid: " + uuid, e);
        }

        return profile;
    }
    
    @Override
    public GameProfile getProfile(String name, Property textures) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", textures);
        
        return profile;
    }
    
    @Override
    public GameProfile getProfile(Property textures) {
        return this.getProfile("", textures);
    }
}
