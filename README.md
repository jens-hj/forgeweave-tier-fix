# Forgeweave Tier Fix

Independent NeoForge 1.21.1 compatibility addon for Forgeweave 1.0.11.

Forgeweave's Diamond and Emerald modifiers compute `min(tier + 1, cap)` for the mining tier, which lowers any tool already above the cap. Two Mixin `@WrapOperation`s around the `Modifier.toolTierIndex` calls in `ForgeweaveModifiers.foldTierIndex` and `ModifierApplication.retuneToolTier` clamp the result for those two modifiers so it never drops below the incoming tier. Because the tier is baked into the item's `tool` component when a modifier is applied, already-downgraded tools are re-baked through Forgeweave's own `ModifierApplication.rebake` when a player equips them. No upstream code is copied or shipped.

## Development

```
nix develop -c ./gradlew build runTierTest
nix develop -c ./gradlew modrinth -PmodrinthDryRun
```

`runTierTest` boots a dedicated server with the unmodified Forgeweave jar plus this addon and checks tier folding, the repair of a downgraded tool and the legacy per-modifier path. The test mod is not part of the release jar.

License: MIT for this addon's original code. Forgeweave retains its own license.
