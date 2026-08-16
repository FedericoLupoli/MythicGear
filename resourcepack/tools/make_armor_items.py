#!/usr/bin/env python3
"""Generate the Obsidian Dragon armor ITEM textures (inventory/held render).

Recolors the vanilla netherite armor item textures with the same ender/purple
palette as the worn (equipment) textures, so the armor looks custom both in
the inventory and on the body.
"""
import os
import sys
from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from make_armor import recolor_pixel

SRC = "/tmp/opencode/armor_src"
OUT_DIR = ("/home/federico/Scrivania/Plugins/MythicGear/resourcepack/assets/"
           "mythicgear/textures/item")

PIECES = [
    ("netherite_helmet", "dragon_helmet"),
    ("netherite_chestplate", "dragon_chestplate"),
    ("netherite_leggings", "dragon_leggings"),
    ("netherite_boots", "dragon_boots"),
]


def main():
    os.makedirs(OUT_DIR, exist_ok=True)
    for src_name, out_name in PIECES:
        src = Image.open(f"{SRC}/{src_name}.png").convert("RGBA")
        px = src.load()
        for y in range(src.height):
            for x in range(src.width):
                px[x, y] = recolor_pixel(px[x, y][:3], px[x, y][3])
        out = f"{OUT_DIR}/{out_name}.png"
        src.save(out)
        print("written:", out)


if __name__ == "__main__":
    main()
