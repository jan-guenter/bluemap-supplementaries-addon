/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.supplementaries.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.ResourcePool;
import de.bluecolored.bluemap.core.util.Key;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Replaces installed wrapper models only after every alias input is present. */
final class InstalledModelAliasInstaller {

    private InstalledModelAliasInstaller() {
    }

    static <T> boolean install(ResourcePool<T> models, Map<String, String> aliases) {
        Objects.requireNonNull(models, "models");
        Objects.requireNonNull(aliases, "aliases");
        if (aliases.isEmpty()) {
            return false;
        }

        Map<Key, T> replacements = replacements(models, aliases);
        if (replacements == null) {
            return false;
        }
        replacements.forEach(models::put);
        return true;
    }

    static <T> boolean canInstall(ResourcePool<T> models, Map<String, String> aliases) {
        Objects.requireNonNull(models, "models");
        Objects.requireNonNull(aliases, "aliases");
        return !aliases.isEmpty() && replacements(models, aliases) != null;
    }

    private static <T> Map<Key, T> replacements(
            ResourcePool<T> models,
            Map<String, String> aliases
    ) {
        Map<Key, T> replacements = new LinkedHashMap<>();
        for (Map.Entry<String, String> alias : aliases.entrySet()) {
            Key wrapper = Key.parse(alias.getKey());
            Key target = Key.parse(alias.getValue());
            T wrapperModel = models.get(wrapper);
            T targetModel = models.get(target);
            if (!models.containsKey(wrapper)
                    || wrapperModel == null
                    || !models.containsKey(target)
                    || targetModel == null) {
                return null;
            }
            replacements.put(wrapper, targetModel);
        }

        return replacements;
    }
}
