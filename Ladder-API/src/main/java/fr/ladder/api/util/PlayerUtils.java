package fr.ladder.api.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Snowtyy
 */
public abstract class PlayerUtils {

    private static Implementation implementation;

    public static void clearInventory(Player player) {
        implementation.clearInventory(player);
    }

    public static void clearEffects(Player player) {
        implementation.clearEffects(player);
    }

    public static void disguise(Player player, GameProfile profile) {
        implementation.disguise(player, profile);
    }

    public static void resetSkin(Player player) {
        implementation.resetSkin(player);
    }

    /**
     * @apiNote It works only to apply a texture on a {@link fr.ladder.api.bukkit.item.SkullBuilder SkullBuilder}
     * @param textureUrl URL to the texture to apply
     * @return New instance of GameProfile with the texture
     */
    public static GameProfile getProfile(String textureUrl) {
        return implementation.getProfile(textureUrl);
    }

    /**
     * @param uuid UUID of the GameProfile searched
     * @return GameProfile of the player with the given player's uuid
     */
    public static GameProfile getProfile(UUID uuid) {
        return implementation.getProfile(uuid);
    }

    /**
     * @param name Profile's name
     * @param textures Profile's texture to apply
     * @return New instance of GameProfile with the given texture
     */
    public static GameProfile getProfile(String name, Property textures) {
        return implementation.getProfile(name, textures);
    }

    /**
     * @param texture Profile's texture to apply
     * @return New instance of GameProfile with the given texture and empty name
     */
    public static GameProfile getProfile(Property texture) {
        return implementation.getProfile(texture);
    }

    public interface Implementation {

        void clearInventory(Player player);

        void clearEffects(Player player);

        void disguise(Player player, GameProfile profile);

        void resetSkin(Player player);

        GameProfile getProfile(String textureUrl);

        GameProfile getProfile(UUID uuid);

        GameProfile getProfile(String name, Property textures);

        GameProfile getProfile(Property textures);

    }

}