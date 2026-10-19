# UCosmetics API

[![](https://jitpack.io/v/UndeadlyDev/UCosmeticsAPI.svg)](https://jitpack.io/#UndeadlyDev/UCosmeticsAPI)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

The official public API for **UCosmetics** - A powerful Minecraft cosmetics plugin for Paper 1.21+.

## Features

- 🎨 Full cosmetic management - Equip, unequip, give, and remove cosmetics
- 📊 Cosmetic data access - Retrieve cosmetic information at runtime
- 🎯 Event-driven - Listen to equip, unequip, and purchase events
- 🔌 Easy integration - Simple and clean API design
- 🛡️ Thread-safe - Works with Folia and Paper servers

## Installation

### Gradle
```gradle

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.UndeadlyDev:UCosmeticsAPI:1.0.2'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.UndeadlyDev</groupId>
    <artifactId>UCosmeticsAPI</artifactId>
    <version>1.0.2</version>
    <scope>provided</scope>
</dependency>
```

Quick Start

```java
import com.undeadlydev.UCosmetics.api.UCosmeticsAPI;
import com.undeadlydev.UCosmetics.api.CosmeticType;
import com.undeadlydev.UCosmetics.api.models.CosmeticData;

Optional<UCosmeticsAPI> api = UCosmeticsAPI.get();

if (api.isPresent()) {
    UCosmeticsAPI ucosmetics = api.get();
    
    // Give a free hat to a new player
    ucosmetics.giveCosmetic(player, CosmeticType.HAT, 1);
    
    // Check if player owns a pet
    if (ucosmetics.hasCosmetic(player, CosmeticType.PET, 5)) {
        ucosmetics.equipCosmetic(player, CosmeticType.PET, 5);
    }
    
    // Get all available balloons
    List<CosmeticData> balloons = ucosmetics.getCosmetics(CosmeticType.BALLOON);
}
```

API Methods

Method Description
hasCosmetic(player, type, id) Check if player owns a cosmetic
equipCosmetic(player, type, id) Equip a cosmetic on a player
unequipCosmetic(player, type) Unequip a cosmetic from a player
clearCosmetics(player) Remove all equipped cosmetics
giveCosmetic(player, type, id) Give a cosmetic to a player
removeCosmetic(player, type, id) Remove a cosmetic from a player
getEquippedCosmetic(player, type) Get equipped cosmetic ID (-1 if none)
getCosmeticData(type, id) Get cosmetic information
getCosmetics(type) Get all cosmetics of a type
getCosmeticCount(player, type) Count player's owned cosmetics
isLoaded() Check if UCosmetics is loaded
getPlugin() Get the UCosmetics plugin instance

Events

Event Cancellable
CosmeticEquipEvent ✅
CosmeticUnequipEvent ✅
CosmeticPurchaseEvent ✅

Event Example

```java
@EventHandler
public void onEquip(CosmeticEquipEvent event) {
    if (event.getPlayer().getWorld().getName().equals("pvp")) {
        event.setCancelled(true);
    }
}
```

Cosmetic Types

Type Description
HAT Wearable hats
PET Companion pets
BALLOON Floating balloons
PARTICLE Particle effects
MINIATURE Mini player models
ANIMATED_HAT Animated hat effects
GADGET Interactive gadgets
BANNER Custom banners

Requirements

· Paper 1.21+ (or compatible fork)
· UCosmetics Plugin installed on the server

License

MIT License

Author

UnDeadlyDev - GitHub

```
