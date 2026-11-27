from pathlib import Path
from PIL import Image, ImageDraw

root = Path(__file__).resolve().parents[1]
background = "#1d2233"
image = Image.new("RGB", (16, 16), background)
draw = ImageDraw.Draw(image)

# 16 x 16 pixel art, no antialiasing: a pickaxe with a rising tier arrow.
ART = (
    "................",
    "....HHHHHH......",
    "...HhhhhhhHS....",
    "..Hh....HHSS....",
    "..H....HSSH.....",
    ".......SSHhH....",
    "......SS..HhH...",
    ".....SS....HH...",
    "....SS.....HH...",
    "...SS...........",
    "..SS.......A....",
    "..S.......AAA...",
    ".........AAAAA..",
    "...........A....",
    "...........A....",
    "................",
)
COLORS = {"H": "#7fe3d4", "h": "#c9fff4", "S": "#8a6a4a", "A": "#ffe39b"}
for y, row in enumerate(ART):
    for x, cell in enumerate(row):
        if cell in COLORS:
            draw.point((x, y), fill=COLORS[cell])
image.resize((256, 256), Image.Resampling.NEAREST).save(root / "icon.png")
