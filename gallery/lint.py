#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated Supplementaries gallery without starting Minecraft."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def in_bounds(x: int, y: int, z: int) -> bool:
    minimum_x, minimum_y, minimum_z, maximum_x, maximum_y, maximum_z = (
        cases.ENVELOPE
    )
    return (
        minimum_x <= x <= maximum_x
        and minimum_y <= y <= maximum_y
        and minimum_z <= z <= maximum_z
    )


def main() -> int:
    for relative, payload in generate.generated_files().items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            raise ValueError(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    load_tag = json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )
    if load_tag != {"values": [f"{cases.NAMESPACE}:load"]}:
        raise ValueError("load tag differs from the exact namespace")

    if len(cases.PLACEMENTS) < 12:
        raise ValueError("gallery lost the first-tranche review cells")
    case_ids = [placement.case_id for placement in cases.PLACEMENTS]
    if len(case_ids) != len(set(case_ids)):
        raise ValueError("duplicate case id")
    coordinates = [
        (placement.x, placement.y, placement.z)
        for placement in cases.PLACEMENTS
    ]
    if len(coordinates) != len(set(coordinates)):
        raise ValueError("duplicate placement coordinate")
    for placement in cases.PLACEMENTS:
        if not in_bounds(placement.x, placement.y, placement.z):
            raise ValueError(f"placement escaped envelope: {placement.case_id}")
        if not placement.block_state.startswith("supplementaries:"):
            raise ValueError(f"non-Supplementaries fixture: {placement.case_id}")
        for position in placement.support_positions:
            if not in_bounds(*position):
                raise ValueError(f"support escaped envelope: {placement.case_id}")

    interaction_ids = {row.case_id for row in cases.INTERACTIONS}
    if not interaction_ids.issubset(case_ids):
        raise ValueError("interaction note references an unknown case")
    required_ids = {
        "way-sign",
        "rope-knot-mimic",
        "rope-middle",
        "flower-box",
        "item-shelf",
        "jar-empty",
        "blackboard-pattern",
        "timber-frame-mimic",
        "crystal-display",
    }
    if not required_ids.issubset(case_ids):
        raise ValueError("gallery lost a required custom-renderer family")

    function_root = ROOT / f"datapack/data/{cases.NAMESPACE}/function"
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )
    setblocks = len(re.findall(r"^setblock ", functions, re.MULTILINE))
    if setblocks < len(cases.PLACEMENTS):
        raise ValueError("generated build omits placements")
    lowered = functions.lower()
    for forbidden in ("summon ", "op ", "deop ", "stop "):
        if forbidden in lowered:
            raise ValueError(f"forbidden gallery command: {forbidden}")
    if "scaffold_not_implemented" in lowered:
        raise ValueError("placeholder marker remains")

    print(
        "Supplementaries gallery lint passed: "
        f"{len(cases.PLACEMENTS)} fixtures in one bounded grid"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
