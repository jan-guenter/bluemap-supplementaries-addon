/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.supplementaries.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockStateCondition;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.TextureVariable;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

/** Builds conservative static fallbacks from resources in the admitted mod JAR. */
final class InstalledStaticModelInstaller {

    private static final Key GLOBE_STAND = Key.parse("supplementaries:block/globe_stand");
    private static final Key GLOBE_BODY = Key.parse("supplementaries:block/globe_the_world");
    private static final Key BOOK = Key.parse("supplementaries:block/books/book_enchanted");
    private static final Key GLOBE = Key.parse("supplementaries:globe");
    private static final Key GLOBE_SEPIA = Key.parse("supplementaries:globe_sepia");
    private static final Key BOOK_PILE = Key.parse("supplementaries:book_pile");
    private static final Key BOOK_PILE_HORIZONTAL =
            Key.parse("supplementaries:book_pile_horizontal");
    private static final String GLOBE_TEXTURE =
            "assets/supplementaries/textures/entity/globes/globe_the_world.png";
    private static final String GLOBE_SEPIA_TEXTURE =
            "assets/supplementaries/textures/entity/globes/globe_the_world_sepia.png";
    private static final Key GLOBE_MODEL =
            Key.parse("bluemap_supplementaries:block/globe_static");
    private static final Key GLOBE_SEPIA_MODEL =
            Key.parse("bluemap_supplementaries:block/globe_sepia_static");

    private InstalledStaticModelInstaller() {
    }

    static boolean install(ResourcePack pack, Path supplementariesJar) {
        Model stand = pack.getModels().get(GLOBE_STAND);
        Model body = pack.getModels().get(GLOBE_BODY);
        Model book = pack.getModels().get(BOOK);
        if (!usable(stand) || !usable(body) || !usable(book)
                || pack.getBlockStates().get(GLOBE) == null
                || pack.getBlockStates().get(GLOBE_SEPIA) == null
                || pack.getBlockStates().get(BOOK_PILE) == null
                || pack.getBlockStates().get(BOOK_PILE_HORIZONTAL) == null) {
            return false;
        }

        GlobeResources normal;
        GlobeResources sepia;
        try {
            BufferedImage normalAtlas = loadImage(supplementariesJar, GLOBE_TEXTURE);
            BufferedImage sepiaAtlas = loadImage(supplementariesJar, GLOBE_SEPIA_TEXTURE);
            normal = createGlobeResources(stand, body, normalAtlas, "globe");
            sepia = createGlobeResources(stand, body, sepiaAtlas, "globe_sepia");
        } catch (IOException exception) {
            return false;
        }
        if (normal == null || sepia == null) {
            return false;
        }

        normal.textures().forEach(pack.getTextures()::put);
        sepia.textures().forEach(pack.getTextures()::put);
        pack.getModels().put(GLOBE_MODEL, normal.model());
        pack.getModels().put(GLOBE_SEPIA_MODEL, sepia.model());
        pack.getBlockStates().put(GLOBE, facingState(GLOBE_MODEL));
        pack.getBlockStates().put(GLOBE_SEPIA, facingState(GLOBE_SEPIA_MODEL));
        pack.getBlockStates().put(BOOK_PILE, singleModelState(BOOK, 90));
        pack.getBlockStates().put(BOOK_PILE_HORIZONTAL, singleModelState(BOOK, 0));
        return true;
    }

    private static boolean usable(Model model) {
        return model != null && model.getElements() != null && model.getElements().length > 0;
    }

    private static GlobeResources createGlobeResources(
            Model stand,
            Model body,
            BufferedImage image,
            String name
    ) throws IOException {
        if (image.getWidth() != 32 || image.getHeight() != 16) {
            return null;
        }

        Key leftKey = Key.parse("bluemap_supplementaries:generated/" + name + "_left");
        Key rightKey = Key.parse("bluemap_supplementaries:generated/" + name + "_right");
        Map<Key, Texture> textures = Map.of(
                leftKey, Texture.from(leftKey, image.getSubimage(0, 0, 16, 16)),
                rightKey, Texture.from(rightKey, image.getSubimage(16, 0, 16, 16))
        );

        Map<String, TextureVariable> variables = new LinkedHashMap<>();
        stand.getTextures().forEach((key, value) -> variables.put(key, value.copy()));
        variables.put("1", new TextureVariable(new ResourcePath<Texture>(leftKey)));
        variables.put("2", new TextureVariable(new ResourcePath<Texture>(rightKey)));

        Element[] standElements = stand.getElements();
        Element[] bodyElements = body.getElements();
        Element[] elements = new Element[standElements.length + bodyElements.length];
        for (int index = 0; index < standElements.length; index++) {
            elements[index] = standElements[index].copy();
        }
        for (int index = 0; index < bodyElements.length; index++) {
            elements[standElements.length + index] = bodyElements[index].copy();
        }
        return new GlobeResources(new Model(variables, elements), textures);
    }

    private static BufferedImage loadImage(Path jar, String entryName) throws IOException {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            ZipEntry entry = zip.getEntry(entryName);
            if (entry == null || entry.isDirectory()) {
                throw new IOException("required installed texture missing");
            }
            try (InputStream input = zip.getInputStream(entry)) {
                BufferedImage image = ImageIO.read(input);
                if (image == null) {
                    throw new IOException("required installed texture is not PNG");
                }
                return image;
            }
        }
    }

    private static BlockState facingState(Key model) {
        return new BlockState(new Variants(new VariantSet[]{
                facingVariant("north", model, 0),
                facingVariant("east", model, 90),
                facingVariant("south", model, 180),
                facingVariant("west", model, 270)
        }, null));
    }

    private static VariantSet facingVariant(String facing, Key model, float yRotation) {
        return new VariantSet(
                BlockStateCondition.property("facing", facing),
                new Variant(new ResourcePath<Model>(model), 0, yRotation, 0)
        );
    }

    private static BlockState singleModelState(Key model, float zRotation) {
        return new BlockState(new Variants(
                new VariantSet[0],
                new VariantSet(new Variant(new ResourcePath<Model>(model), 0, 0, zRotation))
        ));
    }

    private record GlobeResources(Model model, Map<Key, Texture> textures) {
    }
}
