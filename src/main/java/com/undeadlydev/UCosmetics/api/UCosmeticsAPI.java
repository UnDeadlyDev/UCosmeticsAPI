package com.undeadlydev.UCosmetics.api;

import com.undeadlydev.UCosmetics.api.models.CosmeticData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;
import java.util.Optional;

/**
 * Public API for UCosmetics. (1.0.1)
 * <p>
 * Use {@link #get()} to obtain the registered API instance.
 * </p>
 *
 * <pre>
 * Optional&lt;UCosmeticsAPI&gt; api = UCosmeticsAPI.get();
 * api.ifPresent(ucosmetics -&gt; {
 *     ucosmetics.giveCosmetic(player, CosmeticType.HAT, 1);
 * });
 * </pre>
 */
public abstract class UCosmeticsAPI {

    private static UCosmeticsAPI instance;

    /**
     * Gets the API instance registered by the UCosmetics plugin.
     *
     * @return Optional containing the API if the plugin is active, empty otherwise
     */
    public static Optional<UCosmeticsAPI> get() {
        if (instance == null) {
            RegisteredServiceProvider<UCosmeticsAPI> rsp =
                    org.bukkit.Bukkit.getServicesManager().getRegistration(UCosmeticsAPI.class);
            if (rsp != null) {
                instance = rsp.getProvider();
            }
        }
        return Optional.ofNullable(instance);
    }

    /**
     * Checks if a player owns a specific cosmetic.
     *
     * @param player the player to check
     * @param type   the cosmetic type
     * @param id     the cosmetic ID
     * @return true if the player owns the cosmetic
     */
    public abstract boolean hasCosmetic(Player player, CosmeticType type, int id);

    /**
     * Equips a cosmetic on a player. If the player already has a cosmetic of the
     * same type equipped, it will be unequipped first.
     *
     * @param player the player
     * @param type   the cosmetic type
     * @param id     the cosmetic ID
     * @return true if the cosmetic was equipped successfully
     */
    public abstract boolean equipCosmetic(Player player, CosmeticType type, int id);

    /**
     * Unequips a cosmetic of the given type from a player.
     *
     * @param player the player
     * @param type   the cosmetic type to unequip
     * @return true if a cosmetic was unequipped
     */
    public abstract boolean unequipCosmetic(Player player, CosmeticType type);

    /**
     * Removes all equipped cosmetics from a player.
     *
     * @param player the player
     */
    public abstract void clearCosmetics(Player player);

    /**
     * Gets the ID of the cosmetic currently equipped by a player for a given type.
     *
     * @param player the player
     * @param type   the cosmetic type
     * @return the cosmetic ID, or -1 if none is equipped
     */
    public abstract int getEquippedCosmetic(Player player, CosmeticType type);

    /**
     * Gives a cosmetic to a player. The cosmetic will be added to their collection
     * and persisted in the database.
     *
     * @param player the player
     * @param type   the cosmetic type
     * @param id     the cosmetic ID
     * @return true if the cosmetic was given successfully
     */
    public abstract boolean giveCosmetic(Player player, CosmeticType type, int id);

    /**
     * Removes a cosmetic from a player's collection.
     *
     * @param player the player
     * @param type   the cosmetic type
     * @param id     the cosmetic ID
     * @return true if the cosmetic was removed successfully
     */
    public abstract boolean removeCosmetic(Player player, CosmeticType type, int id);

    /**
     * Gets information about a specific cosmetic.
     *
     * @param type the cosmetic type
     * @param id   the cosmetic ID
     * @return Optional containing the cosmetic data, or empty if not found
     */
    public abstract Optional<CosmeticData> getCosmeticData(CosmeticType type, int id);

    /**
     * Gets all cosmetics of a given type.
     *
     * @param type the cosmetic type
     * @return list of cosmetic data
     */
    public abstract List<CosmeticData> getCosmetics(CosmeticType type);

    /**
     * Counts how many cosmetics of a given type a player owns.
     *
     * @param player the player
     * @param type   the cosmetic type
     * @return number of owned cosmetics
     */
    public abstract int getCosmeticCount(Player player, CosmeticType type);

    /**
     * Checks whether the UCosmetics plugin is fully loaded and ready.
     *
     * @return true if the plugin is loaded
     */
    public abstract boolean isLoaded();

    /**
     * Gets the UCosmetics plugin instance.
     *
     * @return the plugin instance
     */
    public abstract Plugin getPlugin();
}