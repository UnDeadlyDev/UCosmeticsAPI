# UCosmetics API

[![](https://jitpack.io/v/UndeadlyDev/UCosmeticsAPI.svg)](https://jitpack.io/#UndeadlyDev/UCosmeticsAPI)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

The official public API for **UCosmetics**, a Minecraft cosmetics plugin.
Manage cosmetics, query player collections, and listen to cosmetic events from your own addons.

This documentation describes **API 1.1.0**, including the player-scheduler helpers introduced for Paper/Folia integrations.

## Features

- 🎨 Equip, unequip, grant, and remove cosmetics.
- 📊 Query cosmetic metadata and player-owned cosmetic IDs.
- 🎯 Listen to cancellable equip and unequip requests; access the declared purchase event type.
- 🔌 Access the active provider through Bukkit's service registry.
- 🧵 Schedule player operations with `onPlayer` and asynchronous convenience methods.

## Requirements

- **Java 21** for this API build.
- A UCosmetics plugin build implementing **API 1.1.0**.
- A server version supported by that UCosmetics build. Folia integrations must follow the threading rules below; API helpers do not establish compatibility for every cosmetic or third-party integration.

The API JAR is a development dependency, **not a standalone server plugin**. Install the UCosmetics plugin on the server.

## Installation

Add JitPack and depend on the API using `compileOnly` or Maven's `provided` scope.
Do not shade or relocate the API classes into your addon.

The examples use the **`1.1.0` tag**. That tag must exist in this API repository and its JitPack build must succeed before these coordinates can resolve. If you are using synchronized source before a release is tagged, use the corresponding **API repository commit hash** as the dependency version instead. A source sync alone does not publish a new version tag.

### Gradle

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.UndeadlyDev:UCosmeticsAPI:1.1.0'
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

<dependencies>
    <dependency>
        <groupId>com.github.UndeadlyDev</groupId>
        <artifactId>UCosmeticsAPI</artifactId>
        <version>1.1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Plugin dependency

If UCosmetics is required by your addon, add this to its `plugin.yml`:

```yaml
depend: [UCosmetics]
```

For an optional integration, use `softdepend: [UCosmetics]` and handle an empty result from `UCosmeticsAPI.get()`.
Declare `folia-supported: true` only if your **whole addon** supports Folia.

## Quick Start

The asynchronous convenience methods schedule the operation on the target player's entity scheduler. They can be called from another region or an asynchronous task.

For example, inside your addon class extending `JavaPlugin`:

```java
import com.undeadlydev.UCosmetics.api.CosmeticType;
import com.undeadlydev.UCosmetics.api.UCosmeticsAPI;
import org.bukkit.entity.Player;

public void equipBalloon(Player player) {
    UCosmeticsAPI.get().ifPresent(api -> {
        api.equipCosmeticAsync(player, CosmeticType.BALLOON, 1)
            .thenAccept(equipped -> {
                // Logging is safe here. This callback does not guarantee
                // the player's scheduler for additional Bukkit operations.
                getLogger().info("Balloon equipped: " + equipped);
            })
            .exceptionally(error -> {
                getLogger().warning("Could not equip balloon: " + error.getMessage());
                return null;
            });
    });
}
```

Replace `1` with an ID configured in UCosmetics. Equipping does not grant ownership. The player must own the cosmetic or satisfy its automatic-grant permission, and their data must have finished loading.

## Run Several Operations with `onPlayer`

Use `onPlayer` to execute a short operation, or several related API calls, on the player's scheduler:

```java
UCosmeticsAPI.get().ifPresent(api -> {
    api.onPlayer(player, current -> {
        if (!current.isPlayerLoaded(player)) {
            return false;
        }

        int balloonId = 1;
        if (!current.hasCosmetic(player, CosmeticType.BALLOON, balloonId)
                && !current.giveCosmetic(player, CosmeticType.BALLOON, balloonId)) {
            return false;
        }

        return current.equipCosmetic(player, CosmeticType.BALLOON, balloonId);
    }).exceptionally(error -> {
        getLogger().warning("Cosmetic operation failed: " + error.getMessage());
        return false;
    });
});
```

`onPlayer` schedules the action; it does **not** wait for the player's database load to finish. If `isPlayerLoaded` is false, retry through your addon's loading flow when the player is ready. Keep the action short: perform network and database I/O outside the tick thread.

### Threading Rules

- On **Folia**, synchronous player methods must run on the target player's owning entity scheduler. Calling them from another region or an asynchronous task can throw `IllegalStateException`.
- On **Paper**, use the server thread for synchronous player methods. `onPlayer` and the asynchronous convenience methods are also suitable there.
- Never call `.join()` or blocking `.get()` on a returned future from a server tick thread.
- Future callbacks do not guarantee a Bukkit-safe execution context. Use the appropriate player/entity/location scheduler for additional Bukkit operations.
- A player going offline or retiring, a disabled API provider, or an exception in your action can complete the future exceptionally.
- Resolve `UCosmeticsAPI.get()` when needed. Do not keep using a provider after UCosmetics is disabled and enabled again.

## Player Collections and Equipped Cosmetics

Query player state inside `onPlayer`, returning immutable data for later processing:

```java
UCosmeticsAPI.get().ifPresent(api -> {
    api.onPlayer(player, current -> {
        return current.getOwnedCosmetics(player, CosmeticType.BALLOON);
    }).thenAccept(ids -> getLogger().info("Owned balloon IDs: " + ids));
});
```

`getOwnedCosmetics` returns an immutable snapshot of owned IDs. It does not include cosmetics available only through automatic-grant permissions.

`getEquippedCosmetic` returns `UCosmeticsAPI.NO_COSMETIC` (`-1`) when no selection is available, including when player data is unavailable. Check `isPlayerLoaded` before treating an empty/default result as the player's actual saved state. A selected cosmetic can be temporarily hidden by world or region restrictions; the returned ID is not proof that a visual entity is currently present.

## Cosmetic Metadata

Metadata queries do not need a player:

```java
import com.undeadlydev.UCosmetics.api.models.CosmeticData;
import java.util.List;
import java.util.Optional;

UCosmeticsAPI.get().ifPresent(api -> {
    Optional<CosmeticData> balloon = api.getCosmeticData(CosmeticType.BALLOON, 1);
    List<CosmeticData> allBalloons = api.getCosmetics(CosmeticType.BALLOON);
});
```

`CosmeticData` exposes `getId()`, `getName()`, `getType()`, `getRarity()`, `getPrice()`, `isBuyable()`, `getPermission()`, and `getIcon()`.
Its icon is an unpersonalized preview, not the player's purchase-menu item. Icons are defensively copied; `getIcon()` can return `null`. Schedule inventory changes on the receiving player's scheduler.

## API Methods

### Scheduling and Convenience Methods

| Method | Result | Purpose |
|---|---|---|
| `onPlayer(player, action)` | `CompletableFuture<T>` | Execute an API action on the player's scheduler |
| `equipCosmeticAsync(player, type, id)` | `CompletableFuture<Boolean>` | Schedule equip and report its outcome |
| `unequipCosmeticAsync(player, type)` | `CompletableFuture<Boolean>` | Schedule unequip and report its outcome |
| `giveCosmeticAsync(player, type, id)` | `CompletableFuture<Boolean>` | Schedule a grant and report whether ownership was newly added |

There are no dedicated `removeCosmeticAsync` or `clearCosmeticsAsync` methods in 1.1.0. Use `onPlayer`:

```java
api.onPlayer(player, current -> current.removeCosmetic(player, CosmeticType.PET, 5));

api.onPlayer(player, current -> {
    current.clearCosmetics(player);
    return null;
});
```

### Synchronous Player Methods

Use these on the player's scheduler as described above.

| Method | Result | Description |
|---|---|---|
| `isPlayerLoaded(player)` | `boolean` | Whether player data is in the loaded cache |
| `hasCosmetic(player, type, id)` | `boolean` | Check stored ownership |
| `equipCosmetic(player, type, id)` | `boolean` | Attempt to equip an allowed cosmetic |
| `unequipCosmetic(player, type)` | `boolean` | Unequip and clear its saved selection on success |
| `clearCosmetics(player)` | `void` | Attempt to unequip each type, respecting each cancellation |
| `giveCosmetic(player, type, id)` | `boolean` | Grant a configured cosmetic if not already owned |
| `removeCosmetic(player, type, id)` | `boolean` | Remove ownership, unequipping first when selected |
| `getEquippedCosmetic(player, type)` | `int` | Selected ID or `NO_COSMETIC` (`-1`) |
| `getOwnedCosmetics(player, type)` | `List<Integer>` | Immutable snapshot of owned IDs |
| `getCosmeticCount(player, type)` | `int` | Count stored ownership entries |

Current implementation note: `hasCosmetic` does not handle `BANNER` ownership in this build. Use `getOwnedCosmetics(player, CosmeticType.BANNER)` to inspect saved banner IDs; do not assume `hasCosmetic` covers this type.

### Provider and Catalog Methods

| Method | Result | Description |
|---|---|---|
| `UCosmeticsAPI.get()` | `Optional<UCosmeticsAPI>` | Resolve the active registered provider |
| `isLoaded()` | `boolean` | Whether the provider is enabled and its cosmetic manager is available |
| `getCosmeticData(type, id)` | `Optional<CosmeticData>` | Look up a configured cosmetic |
| `getCosmetics(type)` | `List<CosmeticData>` | List configured cosmetics, sorted by ID |
| `getPlugin()` | `Plugin` | Access the UCosmetics plugin instance |

### Return Values and Persistence

- `equipCosmetic` returns false when the request is rejected, including missing data, invalid IDs, ownership/permission checks, world/region restrictions, a cancelled equip event, or performance mode.
- `giveCosmetic` returns false for invalid IDs, unavailable player data, or an already-owned cosmetic. An already-owned result is not an error.
- `unequipCosmetic` returns false when no cosmetic is selected or unequipping is rejected/cancelled. A successful call also clears the saved selection.
- `removeCosmetic` preserves ownership if a required unequip is cancelled.
- `clearCosmetics` respects individual unequip cancellations; it does not guarantee that every cosmetic was removed.
- Boolean success describes the in-memory operation and queued persistence request. Database writes are asynchronous; success is not a database transaction acknowledgement.

## Events

| Event | Cancellable | Description |
|---|---|---|
| `CosmeticEquipEvent` | Yes | An equip request, before the equip operation |
| `CosmeticUnequipEvent` | Yes | An unequip request, before the unequip operation |
| `CosmeticPurchaseEvent` | Yes | Declared purchase event; the current plugin build does not dispatch it |

All three implement Bukkit's `Cancellable` interface. Equip and unequip events are request hooks, not database-save confirmations. `CosmeticPurchaseEvent` is exposed by the API, but this plugin build does not currently call it. Granting ownership with `giveCosmetic` does not fire a purchase event.

### Event Example

```java
import com.undeadlydev.UCosmetics.api.events.CosmeticEquipEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class CosmeticListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEquip(CosmeticEquipEvent event) {
        if (event.getPlayer().getWorld().getName().equals("pvp")) {
            event.setCancelled(true);
        }
    }
}
```

Register the listener from your addon:

```java
getServer().getPluginManager().registerEvents(new CosmeticListener(), this);
```

In Folia, keep player work in these handlers on the event player's region. Schedule work targeting other players or entities on their own schedulers.

## Cosmetic Types

| Type | Description |
|---|---|
| `HAT` | Wearable hats |
| `PET` | Companion pets |
| `BALLOON` | Floating balloons |
| `PARTICLE` | Particle effects |
| `MINIATURE` | Mini player models |
| `ANIMATED_HAT` | Animated hats |
| `GADGET` | Interactive gadgets |
| `BANNER` | Custom banners |

`JOIN_EFFECT`, `CLICK_EFFECT`, and `UNKNOWN` also exist in the enum as compatibility/reserved values; this build does not provide configured cosmetics for them.

## Migrating from 1.0.2

1. Use an API dependency and UCosmetics plugin build that both support 1.1.0.
2. Keep synchronous calls only where your code already owns the player's execution context. Use `onPlayer` or the `...Async` helpers everywhere else.
3. Check player readiness before collection queries or grants.
4. Handle both false results and exceptional future completion.
5. Use `UCosmeticsAPI.NO_COSMETIC` instead of internal plugin sentinel values.
6. Do not assume that an equip request succeeded just because it was submitted, or that a queued save has already committed.

## License

MIT License. See [LICENSE](LICENSE).

## Author

[UnDeadlyDev](https://github.com/UnDeadlyDev)
