# Supplementaries review gallery

This deterministic gallery covers the exact Supplementaries `3.8.5` custom
baked-model loaders and the block-entity renderers most likely to disappear or
lose detail in BlueMap. The 38 fixtures occupy `(176, 101, 176)` through
`(201, 103, 201)`. The clear envelope is `(171, 99, 172)` through
`(204, 105, 204)`.

Install the packaged data pack in the disposable test world, then run:

```mcfunction
/reload
/function supplementaries_gallery:build
/function supplementaries_gallery:tp
/function supplementaries_gallery:verify
```

The build function seeds signposts, a chalked blackboard, mimic-filled timber
frames, flower-box plants, books, an item shelf, a pedestal, and an hourglass.
It uses ordinary server commands and does not require a modified client.

`interactions.tsv` lists optional live-content variants that need ordinary
player interaction or additional block-entity data. They are deliberately not
required by the neutral deterministic gallery.

Regenerate and package with:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/supplementaries-gallery.zip
```
