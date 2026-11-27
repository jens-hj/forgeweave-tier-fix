from pathlib import Path
from PIL import Image

root = Path(__file__).resolve().parents[1]
BG = "#1b2030"
COLORS = {
    "L": "#e6fffa", "M": "#5fdccb", "D": "#2a8f88",   # head: rim light, body, underside
    "w": "#d9a56a", "W": "#8a5f3b",                    # handle: lit side, shaded side
}

# 16 x 16 pixel art, no antialiasing. The head is symmetric about the handle, so only
# its upper arm is written out (local 14 x 14 grid, mirror axis x + y == 13).
ARM = (
    "....LLLLLL....",
    ".LLLMMMMMMLL..",
    "LMMMDDDDDMMM..",
    ".DDD.....DM...",
)
N = 13
pixels = {}
for y, row in enumerate(ARM):
    for x, c in enumerate(row):
        if c != "." and x + y <= N:
            pixels[(x, y)] = c
            pixels[(N - y, N - x)] = c
# Handle: two pixels wide, from the socket down to the bottom-left.
for k in range(0, 9):
    pixels[(9 - k, 4 + k)] = "w"
    pixels[(9 - k, 5 + k)] = "W"

image = Image.new("RGB", (16, 16), BG)
for (x, y), c in pixels.items():
    px, py = x + 1, y + 1
    assert 0 <= px < 16 and 0 <= py < 16, (x, y)
    image.putpixel((px, py), tuple(int(COLORS[c][i:i + 2], 16) for i in (1, 3, 5)))
image.resize((256, 256), Image.Resampling.NEAREST).save(root / "icon.png")
