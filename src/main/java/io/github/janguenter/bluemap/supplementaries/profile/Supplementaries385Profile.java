/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.supplementaries.profile;

import java.util.List;
import java.util.Map;

/** Exact All the Mons 1.2.0 profile `supplementaries-1.21.1-3.8.5`. */
public final class Supplementaries385Profile {

    public static final String PROFILE_ID = "supplementaries-1.21.1-3.8.5";
    public static final List<ArtifactPin> ARTIFACTS = List.of(
            new ArtifactPin(
                    "supplementaries",
                    "supplementaries",
                    "1.21.1-3.8.5",
                    "supplementaries-neoforge-1.21.1-3.8.5.jar",
                    13_469_336L,
                    "c05b1c9d39d37694d197ef84ffe70f9dbe995261333cc7c06610c7bff6d9599e"
            ),
            new ArtifactPin(
                    "moonlight",
                    "moonlight",
                    "1.21.1-3.3.0",
                    "moonlight-neoforge-1.21.1-3.3.0.jar",
                    2_135_671L,
                    "30420824c7f9fbca0317551c8fd6bbdce01c8d745edf5bc8d61e42393c5f0335"
            )
    );
    public static final Map<String, String> MODEL_ALIASES = Map.of(
            "supplementaries:block/jar", "supplementaries:block/jar_model",
            "supplementaries:block/goblet", "supplementaries:block/goblet_empty",
            "supplementaries:block/blackboard", "supplementaries:block/blackboard_frame",
            "supplementaries:block/flower_box", "supplementaries:block/flower_box_model",
            "supplementaries:block/flower_box_floor",
                    "supplementaries:block/flower_box_floor_model",
            "supplementaries:block/flower_box_ceiling",
                    "supplementaries:block/flower_box_ceiling_model",
            "supplementaries:block/faucet", "supplementaries:block/faucet_model",
            "supplementaries:block/faucet_on", "supplementaries:block/faucet_model_on"
    );

    private Supplementaries385Profile() {
    }
}
