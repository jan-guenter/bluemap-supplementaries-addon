# Changelog

## 0.1.0-alpha.2 - 2026-09-01

- Migrated the add-on to the exact BlueMap 5.23 feature backport.
- Replaced three local compatibility helpers with the pinned shared Adapter API.
- Preserved the accepted Supplementaries renderer, profile, and gallery behavior.

## 0.1.0-alpha.1 - 2026-08-25

- Generated a fail-closed Java 21 BlueMap add-on seed for `supplementaries-1.21.1-3.8.5`.
- Added atomic installed-model fallbacks for jars, goblets, blackboards, flower
  boxes, faucets, timber members, bellows, jar boats, and book piles.
- Reconstructed both static globe variants from exact installed models and
  atlas textures without bundling Supplementaries assets.
- Kept NBT-driven contents, mimic fills, text, and animation outside this pass.
- Passed disposable full-pack visual staging and owner acceptance.
