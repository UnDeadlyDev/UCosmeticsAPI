package com.undeadlydev.UCosmetics.api.events;

import com.undeadlydev.UCosmetics.api.CosmeticType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * It's called when a player equips a cosmetic item.
 */
public class CosmeticEquipEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CosmeticType type;
    private final int cosmeticId;
    private boolean cancelled;

    public CosmeticEquipEvent(@NotNull Player who, CosmeticType type, int cosmeticId) {
        super(who);
        this.type = type;
        this.cosmeticId = cosmeticId;
    }

    public CosmeticType getType() { return type; }
    public int getCosmeticId() { return cosmeticId; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}