package com.undeadlydev.UCosmetics.api.models;

import com.undeadlydev.UCosmetics.api.CosmeticRarity;
import com.undeadlydev.UCosmetics.api.CosmeticType;
import org.bukkit.inventory.ItemStack;

/**
 * Datos de un cosmético (solo lectura).
 */
public class CosmeticData {

    private final int id;
    private final String name;
    private final CosmeticType type;
    private final CosmeticRarity rarity;
    private final int price;
    private final boolean isBuyable;
    private final String permission;
    private final ItemStack icon;

    public CosmeticData(int id, String name, CosmeticType type, CosmeticRarity rarity,
                        int price, boolean isBuyable, String permission, ItemStack icon) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.price = price;
        this.isBuyable = isBuyable;
        this.permission = permission;
        this.icon = icon;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public CosmeticType getType() { return type; }
    public CosmeticRarity getRarity() { return rarity; }
    public int getPrice() { return price; }
    public boolean isBuyable() { return isBuyable; }
    public String getPermission() { return permission; }
    public ItemStack getIcon() { return icon != null ? icon.clone() : null; }
}
