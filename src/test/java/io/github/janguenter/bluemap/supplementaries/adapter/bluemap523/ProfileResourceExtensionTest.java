/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.supplementaries.adapter.bluemap523;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bluecolored.bluemap.core.resources.pack.PackVersion;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.util.Key;
import com.flowpowered.math.vector.Vector3f;
import io.github.janguenter.bluemap.supplementaries.activation.AddonRuntime;
import io.github.janguenter.bluemap.supplementaries.profile.Supplementaries385Profile;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class ProfileResourceExtensionTest {

    @Test
    void exactArtifactsActivateOnlyAfterEveryAliasIsInstalled()
            throws ReflectiveOperationException {
        List<Path> roots = exactArtifactRoots();
        ResourcePack pack = completePack();
        AddonRuntime runtime = runtime();
        Map<Key, Model> targets = targetModels(pack);

        new ProfileResourceExtension(pack, runtime).loadResources(roots);

        assertTrue(runtime.active());
        assertEquals("exact-profile", runtime.detail());
        targets.forEach((wrapper, target) -> assertSame(target, pack.getModels().get(wrapper)));
    }

    @Test
    void missingRequiredModelKeepsAllWrappersStockAndRuntimeInactive()
            throws ReflectiveOperationException {
        List<Path> roots = exactArtifactRoots();
        ResourcePack pack = completePack();
        Map<Key, Model> wrappersBefore = wrapperModels(pack);
        pack.getModels().remove(Key.parse("supplementaries:block/goblet_empty"));
        AddonRuntime runtime = runtime();

        new ProfileResourceExtension(pack, runtime).loadResources(roots);

        assertFalse(runtime.active());
        assertEquals("required-installed-resource-missing", runtime.detail());
        wrappersBefore.forEach((wrapper, model) ->
                assertSame(model, pack.getModels().get(wrapper)));
    }

    private static List<Path> exactArtifactRoots() {
        String supplementaries = System.getProperty("supplementariesJar", "");
        String moonlight = System.getProperty("moonlightJar", "");
        Assumptions.assumeTrue(!supplementaries.isBlank() && !moonlight.isBlank());
        List<Path> roots = List.of(Path.of(supplementaries), Path.of(moonlight));
        Assumptions.assumeTrue(roots.stream().allMatch(Files::isRegularFile));
        return roots;
    }

    private static ResourcePack completePack() {
        ResourcePack pack = new ResourcePack(new PackVersion(34, 0));
        Supplementaries385Profile.MODEL_ALIASES.forEach((wrapper, target) -> {
            pack.getModels().put(Key.parse(wrapper), model());
            pack.getModels().put(Key.parse(target), model());
        });
        pack.getModels().put(Key.parse("supplementaries:block/globe_stand"), solidModel());
        pack.getModels().put(Key.parse("supplementaries:block/globe_the_world"), solidModel());
        pack.getModels().put(
                Key.parse("supplementaries:block/books/book_enchanted"),
                solidModel()
        );
        for (String block : List.of(
                "supplementaries:globe",
                "supplementaries:globe_sepia",
                "supplementaries:book_pile",
                "supplementaries:book_pile_horizontal"
        )) {
            pack.getBlockStates().put(Key.parse(block), state());
        }
        return pack;
    }

    private static Map<Key, Model> targetModels(ResourcePack pack) {
        Map<Key, Model> targets = new HashMap<>();
        Supplementaries385Profile.MODEL_ALIASES.forEach((wrapper, target) ->
                targets.put(Key.parse(wrapper), pack.getModels().get(Key.parse(target))));
        return targets;
    }

    private static Map<Key, Model> wrapperModels(ResourcePack pack) {
        Map<Key, Model> wrappers = new HashMap<>();
        Supplementaries385Profile.MODEL_ALIASES.keySet().forEach(wrapper -> {
            Key key = Key.parse(wrapper);
            wrappers.put(key, pack.getModels().get(key));
        });
        return wrappers;
    }

    private static Model model() {
        return new Model(new Element[0]);
    }

    private static Model solidModel() {
        return new Model(new Element(
                Vector3f.ZERO,
                Vector3f.ONE,
                Map.of()
        ));
    }

    private static BlockState state() {
        return new BlockState(new Variants(
                new VariantSet[0],
                new VariantSet(new Variant(new ResourcePath<Model>("minecraft:block/air")))
        ));
    }

    private static AddonRuntime runtime() throws ReflectiveOperationException {
        Constructor<AddonRuntime> constructor = AddonRuntime.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
