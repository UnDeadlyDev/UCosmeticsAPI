package com.undeadlydev.UCosmetics.api;

import com.undeadlydev.UCosmetics.api.models.CosmeticData;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Public API for UCosmetics. (1.1.0)
 *
 * <p>Use {@link #get()} to obtain the registered API instance.
 *
 * <pre>
 * Optional&lt;UCosmeticsAPI&gt; api = UCosmeticsAPI.get();
 * api.ifPresent(ucosmetics -&gt; {
 *     ucosmetics.giveCosmetic(player, CosmeticType.HAT, 1);
 * });
 * </pre>
 */
public abstract class UCosmeticsAPI {

  public static final int NO_COSMETIC = -1;

  /** Resolve the active service each time, including after reloads. */
  public static Optional<UCosmeticsAPI> get() {
    RegisteredServiceProvider<UCosmeticsAPI> provider =
        org.bukkit.Bukkit.getServicesManager().getRegistration(UCosmeticsAPI.class);
    return provider == null || !provider.getPlugin().isEnabled()
        ? Optional.empty()
        : Optional.of(provider.getProvider());
  }

  /**
   * Run a short API operation on the player's entity scheduler. Safe from any thread; never
   * join/get this future on a server tick thread. Synchronous player methods require this scheduler
   * on Folia.
   *
   * @param player target player
   * @param action API operation
   * @param <T> result type
   * @return result or exceptional completion for offline/retired players
   */
  public <T> java.util.concurrent.CompletableFuture<T> onPlayer(
      Player player, java.util.function.Function<UCosmeticsAPI, T> action) {
    return java.util.concurrent.CompletableFuture.failedFuture(
        new UnsupportedOperationException("Upgrade UCosmetics"));
  }

  /**
   * @return whether this player's database state has loaded
   */
  public boolean isPlayerLoaded(Player player) {
    return false;
  }

  /**
   * @return immutable snapshot of owned IDs; use the player's scheduler
   */
  public List<Integer> getOwnedCosmetics(Player player, CosmeticType type) {
    return List.of();
  }

  /**
   * @return actual equip outcome, executed on the player scheduler
   */
  public java.util.concurrent.CompletableFuture<Boolean> equipCosmeticAsync(
      Player player, CosmeticType type, int id) {
    return onPlayer(player, api -> api.equipCosmetic(player, type, id));
  }

  /**
   * @return actual unequip outcome, executed on the player scheduler
   */
  public java.util.concurrent.CompletableFuture<Boolean> unequipCosmeticAsync(
      Player player, CosmeticType type) {
    return onPlayer(player, api -> api.unequipCosmetic(player, type));
  }

  /**
   * @return whether ownership was newly granted
   */
  public java.util.concurrent.CompletableFuture<Boolean> giveCosmeticAsync(
      Player player, CosmeticType type, int id) {
    return onPlayer(player, api -> api.giveCosmetic(player, type, id));
  }

  /**
   * Checks if a player owns a specific cosmetic.
   *
   * @param player the player to check
   * @param type the cosmetic type
   * @param id the cosmetic ID
   * @return true if the player owns the cosmetic
   */
  public abstract boolean hasCosmetic(Player player, CosmeticType type, int id);

  /**
   * Equips a cosmetic on a player. If the player already has a cosmetic of the same type equipped,
   * it will be unequipped first.
   *
   * @param player the player
   * @param type the cosmetic type
   * @param id the cosmetic ID
   * @return true if the cosmetic was equipped successfully
   */
  public abstract boolean equipCosmetic(Player player, CosmeticType type, int id);

  /**
   * Unequips a cosmetic of the given type from a player.
   *
   * @param player the player
   * @param type the cosmetic type to unequip
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
   * @param type the cosmetic type
   * @return the cosmetic ID, or -1 if none is equipped
   */
  public abstract int getEquippedCosmetic(Player player, CosmeticType type);

  /**
   * Gives a cosmetic to a player. The cosmetic will be added to their collection and persisted in
   * the database.
   *
   * @param player the player
   * @param type the cosmetic type
   * @param id the cosmetic ID
   * @return true if the cosmetic was given successfully
   */
  public abstract boolean giveCosmetic(Player player, CosmeticType type, int id);

  /**
   * Removes a cosmetic from a player's collection.
   *
   * @param player the player
   * @param type the cosmetic type
   * @param id the cosmetic ID
   * @return true if the cosmetic was removed successfully
   */
  public abstract boolean removeCosmetic(Player player, CosmeticType type, int id);

  /**
   * Gets information about a specific cosmetic.
   *
   * @param type the cosmetic type
   * @param id the cosmetic ID
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
   * @param type the cosmetic type
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
