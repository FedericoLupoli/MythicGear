#!/usr/bin/env python3
"""Dark mage tunic with open front and gold trim.

UV layout (64x32 humanoid):
  Body FRONT:  x=20-27, y=20-31  (8x12) — open V-neck here
  Body RIGHT:  x=16-19, y=20-28  (4x9)
  Body LEFT:   x=28-31, y=20-28  (4x9)
  Body BACK:   x=32-39, y=20-31  (8x12)
  Right arm:   x=40-47, y=16-25  (8x10)
  Left arm:    x=48-55, y=20-25  (8x6)
  Right leg (overlay): x=0-15, y=26-31 (16x6)
"""
import os
from PIL import Image, ImageDraw

SRC_DIR = "/tmp/opencode/armor_src"
SRC_ASSETS = f"{SRC_DIR}/assets/minecraft/textures"
OUT_EQUIP = ("/home/federico/Scrivania/Plugins/MythicGear/resourcepack/assets/"
             "mythicgear/textures/entity/equipment")
OUT_ITEM = ("/home/federico/Scrivania/Plugins/MythicGear/resourcepack/assets/"
            "mythicgear/textures/item")

# Palette
C_FABRIC = (25, 18, 40)       # dark indigo fabric
C_FABRIC2 = (32, 24, 52)      # slightly lighter for folds
C_GOLD = (190, 155, 45)       # gold trim
C_GOLD_DK = (145, 115, 30)    # dark gold shadow
C_LEATHER = (60, 45, 30)      # belt/strap dark brown
C_BOOT = (40, 30, 55)         # boots dark purple


def set_px(px, x, y, color, a=255):
    if 0 <= x < 64 and 0 <= y < 32:
        px[x, y] = (*color, a)


def paint_humanoid(src_path, out_path):
    img = Image.open(src_path).convert("RGBA")
    px = img.load()

    # --- 1. Darken everything to dark fabric base ---
    for y in range(32):
        for x in range(64):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            avg = (r + g + b) // 3
            if avg < 60:
                px[x, y] = (*C_FABRIC, a)
            elif avg < 120:
                px[x, y] = (*C_FABRIC2, a)
            elif avg < 180:
                px[x, y] = (42, 32, 65, a)
            else:
                px[x, y] = (55, 42, 80, a)

    # --- 2. BODY FRONT bathrobe style (x=20-27, y=20-31) ---
    #    Open from top to belt: fabric on sides, transparent in center
    #    Below belt: two panels hanging open
    for y in range(20, 27):
        # Top to belt: transparent center
        for x in range(22, 26):
            px[x, y] = (0, 0, 0, 0)

    # Below belt (y=29-31): two panels with gap
    for y in range(29, 32):
        # Left panel: x=20-22, Right panel: x=25-27, gap: x=23-24
        for x in range(23, 25):
            px[x, y] = (0, 0, 0, 0)

    # --- 3. Gold trim along bathrobe opening edges ---
    # Left inner edge (x=21, all open area)
    for y in range(20, 27):
        set_px(px, 21, y, C_GOLD)
    # Right inner edge (x=25, all open area)
    for y in range(20, 27):
        set_px(px, 25, y, C_GOLD)
    # Below belt: edges of each panel
    for y in range(29, 32):
        set_px(px, 22, y, C_GOLD)
        set_px(px, 25, y, C_GOLD)

    # --- 4. Collar / neckline ---
    for x in range(20, 28):
        set_px(px, x, 20, C_GOLD)
    for x in range(16, 20):
        set_px(px, x, 20, C_GOLD)
    for x in range(28, 32):
        set_px(px, x, 20, C_GOLD)
    for x in range(32, 40):
        set_px(px, x, 20, C_GOLD)

    # --- 5. Belt (y=27-28, across full body) ---
    for x in range(16, 40):
        set_px(px, x, 27, C_GOLD)
        set_px(px, x, 28, C_GOLD_DK)
    # Belt buckle center
    set_px(px, 23, 27, C_GOLD)
    set_px(px, 24, 27, C_GOLD)
    set_px(px, 23, 28, C_GOLD)
    set_px(px, 24, 28, C_GOLD)

    # --- 6. Robe hem gold (y=30-31) ---
    for x in range(16, 40):
        if px[x, 30][3] > 0:
            set_px(px, x, 30, C_GOLD_DK)
        if px[x, 31][3] > 0:
            set_px(px, x, 31, C_GOLD)

    # --- 7. Arm cuffs gold ---
    # Right arm bottom (x=40-47, y=24-25)
    for x in range(40, 48):
        if px[x, 24][3] > 0:
            set_px(px, x, 24, C_GOLD)
        if px[x, 25][3] > 0:
            set_px(px, x, 25, C_GOLD_DK)
    # Left arm bottom (x=48-55, y=24-25)
    for x in range(48, 56):
        if px[x, 24][3] > 0:
            set_px(px, x, 24, C_GOLD)
        if px[x, 25][3] > 0:
            set_px(px, x, 25, C_GOLD_DK)

    # --- 8. Back emblem: small gold diamond at center back ---
    cx, cy = 36, 26
    set_px(px, cx, cy, C_GOLD)
    set_px(px, cx - 1, cy, C_GOLD)
    set_px(px, cx + 1, cy, C_GOLD)
    set_px(px, cx, cy - 1, C_GOLD)
    set_px(px, cx, cy + 1, C_GOLD)

    # --- 9. Right leg overlay (x=0-15, y=26-31): dark pants ---
    for y in range(26, 32):
        for x in range(0, 16):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            avg = (r + g + b) // 3
            if avg < 60:
                px[x, y] = (20, 15, 35, a)
            elif avg < 120:
                px[x, y] = (28, 22, 45, a)
            else:
                px[x, y] = (35, 28, 55, a)

    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    img.save(out_path)
    print(f"  {out_path}")


def paint_leggings(src_path, out_path):
    img = Image.open(src_path).convert("RGBA")
    px = img.load()
    w, h = img.size

    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            avg = (r + g + b) // 3
            if avg < 60:
                px[x, y] = (18, 14, 35, a)
            elif avg < 120:
                px[x, y] = (28, 22, 48, a)
            elif avg < 180:
                px[x, y] = (38, 30, 60, a)
            else:
                px[x, y] = (48, 38, 70, a)

    # Gold trim at top of leggings waistband
    for x in range(w):
        for y in range(h):
            if px[x, y][3] > 0 and y > 0 and px[x, y - 1][3] == 0:
                px[x, y] = (*C_GOLD, 255)

    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    img.save(out_path)
    print(f"  {out_path}")


def recolor_item(src_name, out_name):
    img = Image.open(f"{SRC_DIR}/{src_name}.png").convert("RGBA")
    px = img.load()
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            avg = (r + g + b) // 3
            if avg < 60:
                px[x, y] = (*C_FABRIC, a)
            elif avg < 120:
                px[x, y] = (*C_FABRIC2, a)
            elif avg < 180:
                px[x, y] = (45, 35, 70, a)
            else:
                px[x, y] = (60, 48, 85, a)
    out_path = f"{OUT_ITEM}/{out_name}.png"
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    img.save(out_path)
    print(f"  {out_path}")


def generate_staffs():
    """Medieval magical staffs: wooden handle, metal bands, glowing crystal orb."""
    print("Staff textures:")
    staffs = [
        ("fire_staff", (220, 80, 20), (255, 140, 40)),
        ("lightning_staff", (230, 210, 50), (255, 240, 100)),
        ("healing_staff", (40, 190, 70), (80, 255, 120)),
        ("flight_staff", (50, 170, 220), (100, 220, 255)),
    ]
    WOOD_DK = (45, 30, 18)
    WOOD = (75, 52, 30)
    WOOD_LT = (95, 68, 40)
    METAL = (120, 110, 80)
    METAL_DK = (70, 65, 50)
    GOLD = (170, 140, 50)

    for name, gem_color, glow_color in staffs:
        img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        px = img.load()

        def pt(x, y, c, a=255):
            if 0 <= x < 16 and 0 <= y < 16:
                px[x, y] = (*c, a)

        # --- Wooden shaft (x=7-8, y=5-14) with grain ---
        for y in range(5, 15):
            pt(7, y, WOOD_DK)
            pt(8, y, WOOD)
        # Slight twist: offset some pixels for gnarled look
        pt(6, 7, WOOD_DK, 180)
        pt(9, 10, WOOD_DK, 180)
        pt(6, 11, WOOD_LT, 160)
        pt(9, 8, WOOD_LT, 160)

        # --- Metal cuff top (y=5) ---
        pt(6, 5, METAL)
        pt(7, 5, GOLD)
        pt(8, 5, GOLD)
        pt(9, 5, METAL)

        # --- Metal cuff middle (y=9) ---
        pt(6, 9, METAL_DK)
        pt(7, 9, METAL)
        pt(8, 9, METAL)
        pt(9, 9, METAL_DK)

        # --- Crystal setting / prongs (y=3-4) ---
        pt(7, 4, METAL)
        pt(8, 4, METAL)
        pt(6, 3, METAL_DK, 180)
        pt(9, 3, METAL_DK, 180)
        pt(6, 4, METAL_DK, 120)
        pt(9, 4, METAL_DK, 120)

        # --- Crystal orb (y=1-3) ---
        pt(7, 2, gem_color)
        pt(8, 2, gem_color)
        pt(7, 1, glow_color, 220)
        pt(8, 1, glow_color, 220)
        pt(7, 3, gem_color, 200)
        pt(8, 3, gem_color, 200)
        # Side sparkle
        pt(6, 2, glow_color, 100)
        pt(9, 2, glow_color, 100)

        # --- Metal cap / pommel (y=14-15) ---
        pt(7, 14, METAL)
        pt(8, 14, METAL)
        pt(7, 15, METAL_DK)
        pt(8, 15, METAL_DK)

        out_path = f"{OUT_ITEM}/{name}.png"
        os.makedirs(os.path.dirname(out_path), exist_ok=True)
        img.save(out_path)
        print(f"  {out_path}")


if __name__ == "__main__":
    print("Equipment (humanoid):")
    paint_humanoid(
        f"{SRC_ASSETS}/entity/equipment/humanoid/netherite.png",
        f"{OUT_EQUIP}/humanoid/mage.png",
    )
    paint_humanoid(
        f"{SRC_ASSETS}/entity/equipment/humanoid/netherite.png",
        f"{OUT_EQUIP}/humanoid_baby/mage.png",
    )
    print("Equipment (leggings):")
    paint_leggings(
        f"{SRC_ASSETS}/entity/equipment/humanoid_leggings/netherite.png",
        f"{OUT_EQUIP}/humanoid_leggings/mage.png",
    )
    print("Item textures:")
    recolor_item("netherite_helmet", "mage_helmet")
    recolor_item("netherite_chestplate", "mage_chestplate")
    recolor_item("netherite_leggings", "mage_leggings")
    recolor_item("netherite_boots", "mage_boots")
    generate_staffs()
