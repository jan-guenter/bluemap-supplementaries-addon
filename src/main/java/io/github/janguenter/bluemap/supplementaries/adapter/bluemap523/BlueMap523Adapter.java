/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.supplementaries.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.supplementaries.activation.AddonRuntime;

/** BlueMap 5.23 feature-backport registration boundary. */
public final class BlueMap523Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    Key.parse("bluemap_supplementaries:exact_profile"),
                    pack -> new ProfileResourceExtension(pack, RUNTIME)
            );

    private BlueMap523Adapter() {
    }

    /** Registers the fail-closed exact-profile resource extension. */
    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        return true;
    }
}
