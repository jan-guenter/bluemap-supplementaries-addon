/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.supplementaries.adapter.bluemap523;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bluecolored.bluemap.core.resources.pack.ResourcePool;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.supplementaries.profile.Supplementaries385Profile;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class InstalledModelAliasInstallerTest {

    @Test
    void installsAllExactAliasesFromTheirTargetModels() {
        ResourcePool<Object> models = completeModels();
        Map<Key, Object> targets = new HashMap<>();
        Supplementaries385Profile.MODEL_ALIASES.forEach((wrapper, target) ->
                targets.put(Key.parse(wrapper), models.get(Key.parse(target))));

        assertTrue(InstalledModelAliasInstaller.install(
                models,
                Supplementaries385Profile.MODEL_ALIASES
        ));

        targets.forEach((wrapper, target) -> assertSame(target, models.get(wrapper)));
    }

    @Test
    void missingTargetLeavesEveryWrapperUntouched() {
        ResourcePool<Object> models = completeModels();
        Map<Key, Object> wrappersBefore = wrapperModels(models);
        models.remove(Key.parse("supplementaries:block/faucet_model_on"));

        assertFalse(InstalledModelAliasInstaller.install(
                models,
                Supplementaries385Profile.MODEL_ALIASES
        ));
        wrappersBefore.forEach((wrapper, model) -> assertSame(model, models.get(wrapper)));
    }

    @Test
    void missingWrapperLeavesEveryOtherWrapperUntouched() {
        ResourcePool<Object> models = completeModels();
        Key missing = Key.parse("supplementaries:block/flower_box_floor");
        models.remove(missing);
        Map<Key, Object> wrappersBefore = wrapperModels(models);

        assertFalse(InstalledModelAliasInstaller.install(
                models,
                Supplementaries385Profile.MODEL_ALIASES
        ));
        wrappersBefore.forEach((wrapper, model) -> assertSame(model, models.get(wrapper)));
        assertFalse(models.containsKey(missing));
    }

    @Test
    void exactProfileHasFourteenDisjointWrapperAndTargetKeys() {
        assertEquals(14, Supplementaries385Profile.MODEL_ALIASES.size());
        assertEquals(
                28,
                Supplementaries385Profile.MODEL_ALIASES.entrySet().stream()
                        .flatMap(entry -> java.util.stream.Stream.of(
                                entry.getKey(), entry.getValue()))
                        .distinct()
                        .count()
        );
    }

    private static ResourcePool<Object> completeModels() {
        ResourcePool<Object> models = new ResourcePool<>();
        Supplementaries385Profile.MODEL_ALIASES.forEach((wrapper, target) -> {
            models.put(Key.parse(wrapper), new Object());
            models.put(Key.parse(target), new Object());
        });
        return models;
    }

    private static Map<Key, Object> wrapperModels(ResourcePool<Object> models) {
        Map<Key, Object> wrappers = new HashMap<>();
        Supplementaries385Profile.MODEL_ALIASES.keySet().forEach(wrapper -> {
            Key key = Key.parse(wrapper);
            wrappers.put(key, models.get(key));
        });
        return wrappers;
    }
}
