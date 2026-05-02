package com.undeadlydev.UCosmetics.api;

import com.undeadlydev.UCosmetics.api.models.CosmeticData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * API pública de UCosmetics.
 * Usar {@link #get()} para obtener la instancia registrada.
 */
public abstract class UCosmeticsAPI {

    private static UCosmeticsAPI instance;

    /**
     * Obtiene la instancia de la API registrada por el plugin UCosmetics.
     * @return Optional con la API si el plugin está activo, vacío si no.
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
     * Verifica si un jugador posee un cosmético específico.
     * @param player Jugador
     * @param type Tipo de cosmético
     * @param id ID del cosmético
     * @return true si lo posee
     */
    public abstract boolean hasCosmetic(Player player, CosmeticType type, int id);

    /**
     * Equipa un cosmético a un jugador.
     * @param player Jugador
     * @param type Tipo de cosmético
     * @param id ID del cosmético
     * @return true si se equipó correctamente
     */
    public abstract boolean equipCosmetic(Player player, CosmeticType type, int id);

    /**
     * Desequipa un cosmético de un jugador.
     * @param player Jugador
     * @param type Tipo de cosmético
     * @return true si se desequipó correctamente
     */
    public abstract boolean unequipCosmetic(Player player, CosmeticType type);

    /**
     * Desequipa todos los cosméticos de un jugador.
     * @param player Jugador
     */
    public abstract void clearCosmetics(Player player);

    /**
     * Obtiene el ID del cosmético equipado por un jugador.
     * @param player Jugador
     * @param type Tipo de cosmético
     * @return ID del cosmético o -1 si no tiene
     */
    public abstract int getEquippedCosmetic(Player player, CosmeticType type);

    /**
     * Da un cosmético a un jugador.
     * @param player Jugador
     * @param type Tipo de cosmético
     * @param id ID del cosmético
     * @return true si se otorgó correctamente
     */
    public abstract boolean giveCosmetic(Player player, CosmeticType type, int id);

    /**
     * Remueve un cosmético de un jugador.
     * @param player Jugador
     * @param type Tipo de cosmético
     * @param id ID del cosmético
     * @return true si se removió correctamente
     */
    public abstract boolean removeCosmetic(Player player, CosmeticType type, int id);

    /**
     * Obtiene información de un cosmético.
     * @param type Tipo de cosmético
     * @param id ID del cosmético
     * @return Optional con la data del cosmético
     */
    public abstract Optional<CosmeticData> getCosmeticData(CosmeticType type, int id);

    /**
     * Obtiene todos los cosméticos de un tipo.
     * @param type Tipo de cosmético
     * @return Lista de datos de cosméticos
     */
    public abstract List<CosmeticData> getCosmetics(CosmeticType type);

    /**
     * Obtiene la cantidad de cosméticos que posee un jugador.
     * @param player Jugador
     * @param type Tipo de cosmético (null para total)
     * @return Cantidad de cosméticos
     */
    public abstract int getCosmeticCount(Player player, CosmeticType type);

    /**
     * Verdad si el plugin UCosmetics está completamente cargado.
     * @return true si está listo
     */
    public abstract boolean isLoaded();

    /**
     * Obtiene la instancia del plugin UCosmetics.
     * @return Plugin
     */
    public abstract Plugin getPlugin();
}