package fr.ladder.api.game;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Snowtyy
 **/
public interface GameHandler {

    /**
     * @return Copy of a list players
     */
    List<? extends GamePlayer> getPlayers();

    /**
     * @param uuid Unique ID of a searched GamePlayer
     * @return {@link GamePlayer} who has the given Unique ID
     */
    GamePlayer getPlayer(UUID uuid);

    /**
     * @param name Name of the searched GamePlayer
     * @return {@link GamePlayer} who has the given name.
     */
    GamePlayer getPlayer(String name);

    /**
     * @param player Bukkit player of the searched GamePlayer
     * @return {@link GamePlayer} who has the given bukkit player
     */
    GamePlayer getPlayer(Player player);

    /**
     * @return Copy of a set of all living players
     */
    Set<? extends GamePlayer> getAlivePlayers();

    /**
     * @param uuid Unique ID of a Player
     * @return true if the player who has the given unique id is the host of the game otherwise false
     */
    boolean isHost(UUID uuid);

    /**
     * @return Optional of bukkit offline player instance
     */
    Optional<OfflinePlayer> getHost();

    /**
     * @param uuid Unique ID of a Player
     * @return true if the player who has the given unique id is a co-host of the game otherwise false
     */
    boolean isCoHost(UUID uuid);

    /**
     * @return Copy of a list of bukkit offline player instance
     */
    Stream<OfflinePlayer> getCoHosts();

    /**
     * @param uuid Unique ID of a player
     * @return true if the player who has the given unique id is a spectator of the game otherwise false
     */
    boolean isSpectator(UUID uuid);

    Stream<OfflinePlayer> getSpectators();

    /**
     * @param state State to test with the current state of the game
     * @return true if the given state is the current state of the game otherwise false
     */
    boolean isGState(GameState state);

}
