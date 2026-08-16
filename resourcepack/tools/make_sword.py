#!/usr/bin/env python3
"""Generate the Obsidian Dragon sword texture (purple/ender recolor).

Mirrors the armor palette from make_armor.py so the whole set matches.
"""
import os
import sys
from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from make_armor import SRC_BODY, recolor_pixel

SRC_SWORD = "/tmp/opencode/armor_src/netherite_sword.png"
OUT = ("/home/federico/Scrivania/Plugins/MythicGear/resourcepack/assets/"
       "mythicgear/textures/item/dragon_sword.png")


def main():
    src = Image.open(SRC_SWORD).convert("RGBA")
    px = src.load()
    for y in range(src.height):
        for x in range(src.width):
            px[x, y] = recolor_pixel(px[x, y][:3], px[x, y][3])
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    src.save(OUT)
    print("written:", OUT)


if __name__ == "__main__":
    main()
