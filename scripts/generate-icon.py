from pathlib import Path
from PIL import Image, ImageDraw

root = Path(__file__).resolve().parents[1]
background = "#16262d"
# All artwork uses this one absolute 16 x 16 grid, without antialiasing.
image = Image.new("RGB", (16,16), background)
draw = ImageDraw.Draw(image)

# Pickaxe: the head is symmetric about the handle, so only its top arm is written out
# (mirror axis x == y); the handle runs down to the bottom right, under the checkmark.
ARM = (
    "....LLLLLL....",
    "..LLMMMMMMLLL.",
    "..MMMDDDDDMMML",
    "...MD.....DDD.",
)
COLORS = {"L": "#d9fff8", "M": "#5fdccb", "D": "#2a8f88"}
for y, row in enumerate(ARM):
    for x, cell in enumerate(row):
        if cell in COLORS and x >= y:
            draw.point((1+x, 1+y), fill=COLORS[cell])
            draw.point((1+y, 1+x), fill=COLORS[cell])
for k in range(-1, 5):
    draw.point((5+k, 5+k), fill="#c9975f")
    draw.point((5+k, 6+k), fill="#8a5f3b")

# Identical 16-grid checkmark in all fix-mod project icons. Each # is one pixel.
CHECKMARK = (
    ".......##",
    "......###",
    ".....###.",
    "....###..",
    "##.###...",
    "#####....",
    ".###.....",
    "..#......",
)
# One grid-cell background border separates the check from the artwork.
cells = {(6+x, 7+y) for y, row in enumerate(CHECKMARK)
         for x, cell in enumerate(row) if cell == "#"}
for x, y in cells:
    for dx, dy in ((-1,0),(1,0),(0,-1),(0,1)):
        if (x+dx,y+dy) not in cells:
            draw.point((x+dx,y+dy), fill=background)
for cell in cells:
    draw.point(cell, fill="#ffe39b")
image.resize((256,256), Image.Resampling.NEAREST).save(root / "icon.png")
