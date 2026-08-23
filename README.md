# BlueMap Supplementaries Add-on

A Java 21 BlueMap add-on for the exact `supplementaries-1.21.1-3.8.5` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: implemented prototype awaiting visual staging. After exact admission,
the add-on aliases eight Supplementaries wrapper models to concrete models
already installed by the exact candidate JAR. BlueMap then applies the stock
blockstates, rotations, texture collection, and model bake.

## Build

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the placeholder
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

## Install

Place the add-on JAR in BlueMap's add-on pack directory and restart the BlueMap
JVM. Removal plus one restart restores stock behavior; the add-on creates no
custom world state.

Set `-Dbluemap.supplementaries.disabled=true` to leave the exact profile inactive.

## Scope boundary

The implemented aliases cover empty jars and goblets, blackboard frames,
wall/floor/ceiling flower boxes, and the two faucet wrapper models. The add-on
does not draw fluids, blackboard pixels, flowers, live contents, particles, or
animation state. Missing or changed required models keep the full alias set
inactive, with no partial resource changes.

No Supplementaries binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
