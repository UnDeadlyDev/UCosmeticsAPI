# UCosmetics API 1.1.0

El módulo `api/` es la fuente de verdad. La implementación vive en el plugin.
Java 21. Usa `compileOnly` (Gradle) o `provided` (Maven); no incluyas ni reubiques
las clases API dentro de tu addon. Declara `depend: [UCosmetics]` o `softdepend`
y comprueba `UCosmeticsAPI.get()` después de que UCosmetics se habilite.

```java
UCosmeticsAPI.get().ifPresent(api -> {
    api.equipCosmeticAsync(player, CosmeticType.BALLOON, 1)
       .thenAccept(equipped -> getLogger().info("Equipado: " + equipped));
});
```

Los métodos síncronos que reciben un jugador deben ejecutarse en su scheduler
(en Folia); desde otra región o tarea asíncrona usa `onPlayer` o los métodos
`...Async`. Nunca llames `join()`/`get()` en el hilo del servidor. Los callbacks
del futuro no garantizan un hilo de Bukkit. Programa cualquier cambio de mundo,
inventario o mensaje en el scheduler correspondiente.

```java
api.onPlayer(player, current -> {
    if (!current.isPlayerLoaded(player)) return false;
    current.giveCosmetic(player, CosmeticType.BALLOON, 1);
    return current.equipCosmetic(player, CosmeticType.BALLOON, 1);
});
```

- `NO_COSMETIC = -1`; el centinela interno no se expone.
- `equipCosmetic` devuelve false si el mundo, región, permiso, evento o modo de
  rendimiento impide equipar. `giveCosmetic` no añade IDs inexistentes ni duplicados.
- `unequipCosmetic` borra la selección guardada cuando tiene éxito.
- `removeCosmetic` desequipa antes; si se cancela, conserva la propiedad.
- `getOwnedCosmetics` devuelve una copia inmutable. Los iconos se copian y la
  información pública ya no llama `getIcon(null)`.
- Los eventos públicos implementan `Cancellable`.
- `get()` resuelve el servicio activo en cada llamada. No conserves referencias
  al proveedor después de deshabilitar el plugin.
- Guardar propiedad en BD sigue siendo asíncrono: un resultado true indica el
  cambio en memoria y la solicitud de guardado, no una transacción ya confirmada.

## Repositorio independiente y sincronización

El archivo que probablemente faltaba era un workflow de GitHub Actions y/o el
secreto que le daba acceso al segundo repositorio. No puede recuperarse la
credencial anterior del código. Se incluye `.github/workflows/sync-api.yml`.

En el repositorio principal, Settings → Secrets and variables → Actions:

1. Variable `API_REPOSITORY`: `tu-usuario/tu-repositorio-api`.
2. Variable opcional `API_BRANCH`: rama destino (por defecto `main`). La rama
   debe existir; inicializa el repositorio destino con un README si está vacío.
3. Secret `API_SYNC_TOKEN`: token de GitHub con Contents read/write limitado al
   repositorio destino. No lo pegues en el código ni en gradle.properties.
4. Sube los cambios a main/master o ejecuta `Sync public API` manualmente.

El workflow exporta solo la API, la compila y publica un commit si cambió. No
hace force-push ni borra los workflows del repositorio destino. Para repositorios
con ramas protegidas adapta el paso de push a una PR o autoriza al bot.

También puedes exportar localmente:

```text
python scripts/export_api.py ../UCosmeticsAPI
cd ../UCosmeticsAPI
./gradlew clean build javadoc publishToMavenLocal
```

La publicación local usa `com.github.UndeadlyDev.UCosmetics:UCosmeticsAPI:1.1.0`.
Para distribuir por JitPack, etiqueta una versión en el repositorio API y usa
las coordenadas que JitPack genere para ese propietario/repositorio; no son
necesariamente iguales a las de Maven local. El workflow sincroniza código:
no crea tags, releases ni publica paquetes en GitHub Packages automáticamente.
