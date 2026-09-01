/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.supplementaries.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import io.github.janguenter.bluemap.supplementaries.activation.AddonRuntime;
import io.github.janguenter.bluemap.supplementaries.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.supplementaries.profile.Supplementaries385Profile;

import java.nio.file.Path;

/** Exact-artifact admission hook for installed Supplementaries wrapper models. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private final ResourcePack resourcePack;
    private final AddonRuntime runtime;

    ProfileResourceExtension(ResourcePack resourcePack, AddonRuntime runtime) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.supplementaries.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesAll(roots, Supplementaries385Profile.ARTIFACTS)) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }
        Path supplementariesJar = ExactArtifactDetector.match(
                roots,
                Supplementaries385Profile.ARTIFACTS.getFirst()
        ).orElse(null);
        if (supplementariesJar == null) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }

        boolean installed;
        try {
            if (!InstalledModelAliasInstaller.canInstall(
                    resourcePack.getModels(),
                    Supplementaries385Profile.MODEL_ALIASES
            )) {
                runtime.inactive("required-installed-resource-missing");
                return;
            }
            installed = InstalledStaticModelInstaller.install(resourcePack, supplementariesJar)
                    && InstalledModelAliasInstaller.install(
                            resourcePack.getModels(),
                            Supplementaries385Profile.MODEL_ALIASES
                    );
        } catch (IllegalArgumentException exception) {
            runtime.fail("invalid-model-alias-profile");
            return;
        }
        if (!installed) {
            runtime.inactive("required-installed-resource-missing");
            return;
        }
        runtime.activate();
    }
}
