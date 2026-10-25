#!/usr/bin/env python3
"""Generate the Switch NRO icon from the canonical HubNX SVG source."""
from pathlib import Path
import io
from PIL import Image
import cairosvg

root = Path(__file__).resolve().parents[1]
src = root / "branding" / "hubnx-icon.svg"
dest = Path(__import__("sys").argv[1]) if len(__import__("sys").argv) > 1 else root / "icon.jpg"
dest.parent.mkdir(parents=True, exist_ok=True)
png = cairosvg.svg2png(url=str(src), output_width=256, output_height=256)
image = Image.open(io.BytesIO(png)).convert("RGB")
image.save(dest, "JPEG", quality=90, optimize=True)
print("Generated", dest, dest.stat().st_size, "bytes")
