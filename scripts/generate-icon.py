from pathlib import Path
from PIL import Image, ImageDraw

root = Path(__file__).resolve().parents[1]
background = "#1d2233"
image = Image.new("RGB", (16, 16), background)
draw = ImageDraw.Draw(image)

# 16 x 16 pixel art, no antialiasing: an outlined pickaxe with a rising tier arrow.
ART = (
    "................",
    "....OOOOOOO.....",
    "...OhhhHHHHOO...",
    "...OHHOOOHHHdO..",
    "....OO..OsHHHO..",
    ".......OsSOHHdO.",
    "......OsSO.OHHO.",
    ".....OsSO..OHHO.",
    "....OsSO...OHdO.",
    "...OsSO....OHdO.",
    "..OsSO....A.OdO.",
    ".OsSO....AAA.O..",
    ".OSO....AAAAA...",
    "..O......AAA....",
    ".........AAA....",
    "................",
)
COLORS = {
    "O": "#0c0f1a",  # outline
    "h": "#d6fff6", "H": "#7fe3d4", "d": "#3fa89c",  # head
    "s": "#b98d5e", "S": "#7d5a3c",  # handle
    "A": "#ffe39b",  # tier arrow
}
assert all(len(row) == 16 for row in ART) and len(ART) == 16
for y, row in enumerate(ART):
    for x, cell in enumerate(row):
        if cell in COLORS:
            draw.point((x, y), fill=COLORS[cell])
image.resize((256, 256), Image.Resampling.NEAREST).save(root / "icon.png")
