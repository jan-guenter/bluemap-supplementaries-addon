#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Exact Supplementaries custom-model and block-entity review cases."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "supplementaries_gallery"
ENVELOPE = (171, 99, 172, 204, 105, 204)
TELEPORT = (188, 112, 188, 0, 38)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    family: str
    expected: str = "model-visible"
    verify_state: str | None = None
    setup_commands: tuple[str, ...] = ()
    support_positions: tuple[tuple[int, int, int], ...] = ()

    @property
    def checked_state(self) -> str:
        return self.verify_state or self.block_state


@dataclass(frozen=True)
class Label:
    x: int
    y: int
    z: int
    line_1: str
    line_2: str


@dataclass(frozen=True)
class Interaction:
    case_id: str
    reason: str
    action: str


def at(
    case_id: str,
    label: str,
    x: int,
    z: int,
    block_state: str,
    family: str,
    *,
    y: int = 101,
    expected: str = "model-visible",
    verify_state: str | None = None,
    setup_commands: tuple[str, ...] = (),
    support_positions: tuple[tuple[int, int, int], ...] = (),
) -> Placement:
    return Placement(
        case_id,
        label,
        x,
        y,
        z,
        block_state,
        family,
        expected,
        verify_state,
        setup_commands,
        support_positions,
    )


def item_command(x: int, y: int, z: int, slot: int, item: str) -> str:
    return f"item replace block {x} {y} {z} container.{slot} with {item}"


SIGNPOST_NBT = (
    '{Mimic:{Name:"minecraft:oak_fence"},'
    'SignUp:{Active:1b,Left:0b,Yaw:0.0f,WoodType:"minecraft:oak"},'
    'SignDown:{Active:1b,Left:1b,Yaw:180.0f,WoodType:"minecraft:spruce"}}'
)
WALL_SIGNPOST_NBT = (
    '{Mimic:{Name:"minecraft:stone_bricks"},'
    'SignUp:{Active:1b,Left:0b,Yaw:90.0f,WoodType:"minecraft:birch"},'
    'SignDown:{Active:0b,Left:0b,Yaw:0.0f,WoodType:"minecraft:oak"}}'
)
BLACKBOARD_PIXELS = (
    "[L;1152921504606846977L,72057594037927952L,4503599627370752L,"
    "281474976714752L,17592186109952L,1099512676352L,68736253952L,"
    "4563402752L,4563402752L,68736253952L,1099512676352L,"
    "17592186109952L,281474976714752L,4503599627370752L,"
    "72057594037927952L,1152921504606846977L]"
)


PLACEMENTS = (
    at(
        "way-sign",
        "seeded standing oak and spruce signpost",
        176,
        176,
        "supplementaries:way_sign",
        "custom-model/signpost",
        expected="post-and-two-signs-visible",
        setup_commands=(f"data merge block 176 101 176 {SIGNPOST_NBT}",),
    ),
    at(
        "way-sign-wall",
        "seeded wall signpost",
        181,
        176,
        "supplementaries:way_sign_wall",
        "custom-model/signpost",
        expected="wall-host-and-sign-visible",
        setup_commands=(f"data merge block 181 101 176 {WALL_SIGNPOST_NBT}",),
        support_positions=((181, 101, 177),),
    ),
    at(
        "flower-box",
        "floor flower box with three plants",
        186,
        176,
        "supplementaries:flower_box[facing=north,face=floor]",
        "custom-model/flower-box",
        expected="box-and-three-plants-visible",
        setup_commands=(
            item_command(186, 101, 176, 0, "minecraft:poppy"),
            item_command(186, 101, 176, 1, "minecraft:blue_orchid"),
            item_command(186, 101, 176, 2, "minecraft:fern"),
        ),
    ),
    at(
        "jar-empty",
        "empty jar glass model",
        191,
        176,
        "supplementaries:jar",
        "custom-model/jar",
        expected="empty-glass-jar-visible",
    ),
    at(
        "blackboard-pattern",
        "blackboard with deterministic chalk X",
        196,
        176,
        "supplementaries:blackboard[facing=north]",
        "custom-model/blackboard",
        expected="frame-board-and-chalk-X-visible",
        setup_commands=(
            f"data merge block 196 101 176 {{Pixels:{BLACKBOARD_PIXELS},Waxed:1b}}",
        ),
        support_positions=((196, 101, 177),),
    ),
    at(
        "book-pile",
        "upright book pile",
        201,
        176,
        "supplementaries:book_pile",
        "custom-model/book-pile",
        expected="book-model-visible",
        setup_commands=(item_command(201, 101, 176, 0, "minecraft:enchanted_book"),),
    ),
    at(
        "book-pile-horizontal",
        "horizontal book pile",
        176,
        181,
        "supplementaries:book_pile_horizontal",
        "custom-model/book-pile",
        expected="horizontal-book-model-visible",
        setup_commands=(item_command(176, 101, 181, 0, "minecraft:written_book"),),
    ),
    at(
        "goblet-empty",
        "empty goblet custom model",
        181,
        181,
        "supplementaries:goblet",
        "custom-model/goblet",
    ),
    at(
        "faucet-on",
        "enabled faucet with water state",
        186,
        181,
        "supplementaries:faucet[facing=north,enabled=true,connected=false,powered=false,has_water=true]",
        "custom-model/faucet",
        verify_state="supplementaries:faucet",
        support_positions=((186, 101, 182),),
    ),
    at(
        "rope-knot-mimic",
        "rope knot around an oak fence",
        191,
        181,
        "supplementaries:rope_knot[axis=y,type=post,up=true,down=true]",
        "custom-model/rope-knot",
        expected="mimic-host-and-rope-visible",
        verify_state="supplementaries:rope_knot",
        setup_commands=(
            'data merge block 191 101 181 {Mimic:{Name:"minecraft:oak_fence"}}',
        ),
    ),
    at(
        "rope-buntings",
        "rope bunting custom model",
        196,
        181,
        "supplementaries:rope_buntings[up=true]",
        "custom-model/rope-bunting",
        verify_state="supplementaries:rope_buntings",
        support_positions=((196, 102, 181),),
    ),
    at(
        "awning-slanted",
        "red slanted awning extra rotation",
        201,
        181,
        "supplementaries:awning_red[slanted=true,facing=north,bottom=true]",
        "custom-model/awning",
        support_positions=((201, 101, 182),),
    ),
    at(
        "rope-top",
        "connected vertical rope top",
        176,
        186,
        "supplementaries:rope[up=true,down=true]",
        "connected-model/rope",
        y=103,
        verify_state="supplementaries:rope",
        support_positions=((176, 104, 186),),
    ),
    at(
        "rope-middle",
        "connected vertical rope middle",
        176,
        186,
        "supplementaries:rope[up=true,down=true]",
        "connected-model/rope",
        y=102,
        verify_state="supplementaries:rope",
    ),
    at(
        "rope-bottom",
        "connected vertical rope bottom knot",
        176,
        186,
        "supplementaries:rope[up=true,down=false,knot=true]",
        "connected-model/rope",
        verify_state="supplementaries:rope",
    ),
    at(
        "timber-frame-mimic",
        "brick-filled timber frame",
        181,
        186,
        "supplementaries:timber_frame[has_block=true]",
        "custom-model/frame",
        expected="timber-and-brick-fill-visible",
        setup_commands=(
            'data merge block 181 101 186 {Mimic:{Name:"minecraft:bricks"}}',
        ),
    ),
    at(
        "timber-brace-mimic",
        "deepslate-filled timber brace",
        186,
        186,
        "supplementaries:timber_brace[has_block=true,flipped=false]",
        "custom-model/frame",
        expected="brace-and-deepslate-fill-visible",
        setup_commands=(
            'data merge block 186 101 186 {Mimic:{Name:"minecraft:deepslate_tiles"}}',
        ),
    ),
    at(
        "timber-cross-brace-mimic",
        "mossy-filled timber cross brace",
        191,
        186,
        "supplementaries:timber_cross_brace[has_block=true]",
        "custom-model/frame",
        expected="cross-brace-and-mossy-fill-visible",
        setup_commands=(
            'data merge block 191 101 186 {Mimic:{Name:"minecraft:mossy_cobblestone"}}',
        ),
    ),
    at(
        "item-shelf",
        "item shelf with diamond sword",
        196,
        186,
        "supplementaries:item_shelf[facing=north]",
        "block-entity/item-display",
        expected="shelf-and-item-visible",
        setup_commands=(item_command(196, 101, 186, 0, "minecraft:diamond_sword"),),
        support_positions=((196, 101, 187),),
    ),
    at(
        "notice-board",
        "notice board with writable book",
        201,
        186,
        "supplementaries:notice_board[facing=north,has_book=true]",
        "block-entity/item-display",
        expected="board-and-book-visible",
        verify_state="supplementaries:notice_board",
        setup_commands=(item_command(201, 101, 186, 0, "minecraft:writable_book"),),
        support_positions=((201, 101, 187),),
    ),
    at(
        "pedestal",
        "pedestal with diamond",
        176,
        191,
        "supplementaries:pedestal",
        "block-entity/item-display",
        expected="pedestal-and-item-visible",
        verify_state="supplementaries:pedestal",
        setup_commands=(item_command(176, 101, 191, 0, "minecraft:diamond"),),
    ),
    at(
        "crystal-display",
        "fully powered crystal display",
        181,
        191,
        "supplementaries:crystal_display[power=15,facing=north]",
        "block-state/display",
    ),
    at(
        "globe",
        "standard globe",
        186,
        191,
        "supplementaries:globe[facing=north]",
        "block-entity/globe",
        expected="stand-and-sphere-visible",
    ),
    at(
        "globe-sepia",
        "sepia globe",
        191,
        191,
        "supplementaries:globe_sepia[facing=north]",
        "block-entity/globe",
        expected="stand-and-sepia-sphere-visible",
    ),
    at(
        "hourglass",
        "sand-filled upright hourglass",
        196,
        191,
        "supplementaries:hourglass[facing=up]",
        "block-entity/hourglass",
        expected="frame-and-sand-visible",
        verify_state="supplementaries:hourglass",
        setup_commands=(item_command(196, 101, 191, 0, "minecraft:sand"),),
    ),
    at(
        "clock-two-faced",
        "two-faced wall clock",
        201,
        191,
        "supplementaries:clock_block[facing=north,two_faced=true]",
        "block-entity/clock",
        expected="case-and-clock-hands-visible",
        support_positions=((201, 101, 192),),
    ),
    at(
        "flag-red",
        "red flag cloth",
        176,
        196,
        "supplementaries:flag_red",
        "block-entity/flag",
        expected="pole-and-cloth-visible",
    ),
    at(
        "bunting-red",
        "red ceiling bunting",
        181,
        196,
        "supplementaries:bunting_red[axis=x]",
        "block-entity/bunting",
        support_positions=((181, 102, 196),),
    ),
    at(
        "cage-empty",
        "empty cage",
        186,
        196,
        "supplementaries:cage",
        "block-entity/cage",
        expected="cage-frame-visible",
    ),
    at(
        "cannon",
        "neutral cannon",
        191,
        196,
        "supplementaries:cannon",
        "block-entity/cannon",
        expected="base-and-barrel-visible",
    ),
    at(
        "doormat",
        "plain doormat",
        196,
        196,
        "supplementaries:doormat[facing=north]",
        "block-entity/doormat",
    ),
    at(
        "wind-vane",
        "neutral wind vane",
        201,
        196,
        "supplementaries:wind_vane",
        "block-entity/wind-vane",
        expected="base-and-vane-visible",
    ),
    at(
        "statue",
        "default statue",
        176,
        201,
        "supplementaries:statue[facing=north]",
        "block-entity/statue",
        expected="base-and-statue-visible",
    ),
    at(
        "bellows",
        "neutral bellows",
        181,
        201,
        "supplementaries:bellows",
        "block-entity/bellows",
    ),
    at(
        "bubble-block",
        "bubble block renderer",
        186,
        201,
        "supplementaries:bubble_block",
        "block-entity/bubble",
        expected="transparent-bubble-visible",
    ),
    at(
        "jar-boat",
        "jar boat",
        191,
        201,
        "supplementaries:jar_boat[facing=north]",
        "block-entity/jar-boat",
    ),
    at(
        "urn",
        "empty urn",
        196,
        201,
        "supplementaries:urn",
        "block-entity/urn",
    ),
    at(
        "enderman-head",
        "enderman head with eye renderer",
        201,
        201,
        "supplementaries:enderman_head",
        "block-entity/head",
        expected="head-and-eyes-visible",
    ),
)


LABELS = (
    Label(173, 101, 176, "CUSTOM MODELS", "SIGNS TO BOOKS"),
    Label(173, 101, 181, "CUSTOM MODELS", "GOBLET TO AWNING"),
    Label(173, 101, 186, "ROPES FRAMES", "ITEM DISPLAYS"),
    Label(173, 101, 191, "DISPLAY BERS", "GLOBES CLOCK"),
    Label(173, 101, 196, "CLOTH OBJECTS", "CAGE TO VANE"),
    Label(173, 101, 201, "OTHER BERS", "STATUE TO HEAD"),
)


INTERACTIONS = (
    Interaction(
        "jar-empty",
        "fluid and captured-mob contents live in block-entity data",
        "right-click with a compatible filled container or prepare a captured jar",
    ),
    Interaction(
        "goblet-empty",
        "fluid contents live in the Moonlight soft-fluid tank",
        "right-click with a compatible drink or filled container",
    ),
    Interaction(
        "cage-empty",
        "the captured mob is stored in block-entity data",
        "capture a supported small mob with a cage item before placement",
    ),
    Interaction(
        "flag-red",
        "custom banner patterns live in block-entity data",
        "apply a patterned banner or edit through ordinary gameplay",
    ),
    Interaction(
        "doormat",
        "custom text lives in block-entity data",
        "use the in-game editor to add text",
    ),
    Interaction(
        "statue",
        "skin and pose data live in block-entity data",
        "configure the statue through ordinary gameplay",
    ),
    Interaction(
        "cannon",
        "aim and loaded-projectile states require gameplay interaction",
        "aim or load the cannon in-game for non-neutral variants",
    ),
    Interaction(
        "globe",
        "yaw, spin and sheared variants live in block-entity data",
        "interact with the globe for non-neutral variants",
    ),
)
