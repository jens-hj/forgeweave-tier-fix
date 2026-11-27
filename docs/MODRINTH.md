# Forgeweave Tier Fix

Stops the **Diamond** and **Emerald** modifiers in [Forgeweave](https://modrinth.com/mod/forgeweave) 1.0.11 from lowering the mining tier of tools that are already better.

Forgeweave describes Diamond as raising a tool one mining tier, but never past diamond. In 1.0.11 a tool that starts above that cap is pulled down to it instead: a Netherite, Hardcinder, Warspar or Resonite tool with the Diamond modifier mines like a diamond tool and drops nothing from Cobalt, Ardite and the ores above them. Emerald does the same with an iron cap.

With this addon:

- Diamond and Emerald still raise lower-tier tools exactly as before, up to the same caps.
- Tools already above the cap keep their own tier.
- Tools that were downgraded before the addon was installed repair themselves the first time a player holds them. Nothing has to be removed or re-crafted.

Fortification, which lowers the tier on purpose, is left alone.

Requires **Minecraft 1.21.1**, **NeoForge 21.1.248 or newer** and **Forgeweave 1.0.11**. Install it on the server; clients only need it for singleplayer. No configuration.

This is an independent compatibility addon, not an official Forgeweave release. It contains no Forgeweave classes or assets. It is pinned to Forgeweave 1.0.11 and should be removed once Forgeweave fixes this itself.
