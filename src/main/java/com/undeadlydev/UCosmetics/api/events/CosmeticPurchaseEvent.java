package com.undeadlydev.UCosmetics.api.events;

import com.undeadlydev.UCosmetics.api.CosmeticType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CosmeticPurchaseEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CosmeticType type;
    private final int cosmeticId;
    private final int price;
    private boolean cancelled;

    public CosmeticPurchaseEvent(@NotNull Player who, CosmeticType type, int cosmeticId, int price) {
        super(who);
        this.type = type;
        this.cosmeticId = cosmeticId;
        this.price = price;
    }

    public CosmeticType getType() { return type; }
    public int getCosmeticId() { return cosmeticId; }
    public int getPrice() { return price; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}