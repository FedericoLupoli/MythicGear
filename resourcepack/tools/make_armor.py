#!/usr/bin/env python3
"""Generate custom 'Obsidian Dragon' armor textures from vanilla netherite.

Purple/ender recolor + horns on the helmet head region.
"""
from PIL import Image

SRC_BODY = "/tmp/opencode/armor_src/netherite.png"
SRC_LEGS = "/tmp/opencode/armor_src/netherite_leggings.png"
OUT_DIR = "/home/federico/Scrivania/Plugins/MythicGear/resourcepack/assets/mythicgear/textures/entity/equipment"


def shade(b):
    stops = [
        (0.00, (15, 11, 28)),
        (0.22, (34, 24, 64)),
        (0.45, (62, 42, 116)),
        (0.68, (112, 88, 184)),
        (0.85, (168, 148, 232)),
        (1.00, (224, 216, 255)),
    ]
    for i in range(len(stops) - 1):
        (b0, c0), (b1, c1) = stops[i], stops[i + 1]
        if b <= b1:
            t = (b - b0) / (b1 - b0) if b1 > b0 else 0
            return tuple(int(c0[k] + (c1[k] - c0[k]) * t) for k in range(3))
    return stops[-1][1]


def recolor_pixel(px, alpha):
    if alpha == 0:
        return (0, 0, 0, 0)
    r, g, b = px
    lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
    lum = max(0.0, min(1.0, (lum - 0.42) * 1.55 + 0.5))
    cr, cg, cb = shade(lum)
    return (cr, cg, cb, alpha)


def recolor(src, dst):
    im = Image.open(src).convert("RGBA")
    px = im.load()
    w, h = im.size
    for y in range(h):
        for x in range(w):
            px[x, y] = recolor_pixel(px[x, y][:3], px[x, y][3])
    im.save(dst)
    return im


def put(im, x, y, color):
    w, h = im.size
    if 0 <= x < w and 0 <= y < h:
        im.putpixel((x, y), color)


def line(im, x0, y0, x1, y1, color, thick=2):
    import math
    steps = max(abs(x1 - x0), abs(y1 - y0)) * 2
    for i in range(steps + 1):
        t = i / steps
        x = int(round(x0 + (x1 - x0) * t))
        y = int(round(y0 + (y1 - y0) * t))
        for dx in range(-(thick // 2), thick - thick // 2):
            for dy in range(-(thick // 2), thick - thick // 2):
                put(im, x + dx, y + dy, color)


def draw_horns(im):
    """Small horns at the top corners of the helmet (front + sides).

    Head front face x8..16 y8..16, sides x0..8 / x16..24 y8..16.
    Horns are 2px wide, 3px tall, mirror-symmetric, and stop above the eye
    visor so the face stays visible.
    """
    grad = {8: (205, 190, 245), 9: (165, 140, 225), 10: (120, 95, 190)}

    def paint(fill):
        for (c, y) in fill:
            r, g, b = grad[y]
            put(im, c, y, (r, g, b, 255))

    front = [(x, y) for y in (8, 9, 10) for x in (9, 10)]
    paint(front)
    paint([(24 - x, y) for x, y in front])          # right horn, mirrored

    side = [(x, y) for y in (8, 9, 10) for x in (1, 2)]
    paint(side)
    paint([(24 - x, y) for x, y in side])           # right side, mirrored


if __name__ == "__main__":
    import os
    os.makedirs(f"{OUT_DIR}/humanoid", exist_ok=True)
    os.makedirs(f"{OUT_DIR}/humanoid_leggings", exist_ok=True)

    body = recolor(SRC_BODY, f"{OUT_DIR}/humanoid/dragon.png")
    body.save(f"{OUT_DIR}/humanoid/dragon.png")
    recolor(SRC_LEGS, f"{OUT_DIR}/humanoid_leggings/dragon.png")
    print("written:")
    print(f"  {OUT_DIR}/humanoid/dragon.png")
    print(f"  {OUT_DIR}/humanoid_leggings/dragon.png")
