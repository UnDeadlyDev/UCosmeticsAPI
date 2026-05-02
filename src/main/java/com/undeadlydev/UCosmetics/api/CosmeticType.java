package com.undeadlydev.UCosmetics.api;

public enum CosmeticType {
    HAT,
    PET,
    BALLOON,
    PARTICLE,
    MINIATURE,
    ANIMATED_HAT,
    GADGET,
    BANNER,
    JOIN_EFFECT,
    CLICK_EFFECT,
    UNKNOWN;

    public static CosmeticType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}