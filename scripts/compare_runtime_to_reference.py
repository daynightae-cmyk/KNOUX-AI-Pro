#!/usr/bin/env python3
"""Build side-by-side comparison sheets: reference mockup vs real device capture.

Usage:
    python3 scripts/compare_runtime_to_reference.py 02 home-095343.png
    python3 scripts/compare_runtime_to_reference.py --all

The reference PNGs are marketing mockups with a phone frame; the runtime
captures are real 1080x2436 device screenshots. Both are normalised to the same
height so the visual differences can be judged side by side.
"""
import sys
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
REFS = ROOT / "design" / "refs20_complete"
RUNTIME = ROOT / "qa-evidence" / "runtime"
OUT = ROOT / "qa-evidence" / "knoux-ai-visual-finish" / "VISUAL_COMPARISONS"

# Inner phone screen area of the reference mockups (status bar .. bottom edge).
REF_SCREEN_BOX = (92, 26, 852, 1648)

# reference number -> runtime capture name (latest wins when several exist)
ROUTE_BY_REF = {
    "01": ("splash", "00_splash"),
    "02": ("home", "home"),
    "03": ("privacy", "privacy"),
    "04": ("duplicates", "duplicates"),
    "05": ("deepscan", "deepscan"),
    "06": ("reports", "reports"),
    "07": ("residue", "residue"),
    "08": ("storage", "storage"),
    "09": ("battery", "battery"),
    "10": ("ai", "ai"),
    "11": ("smartclean", "smartclean"),
    "12": ("duplicate-group", "duplicate-group"),
    "13": ("privacy-risk", "privacy-risk"),
    "14": ("deep-scan-summary", "deep-scan-summary"),
    "15": ("performance-report", "performance-report"),
    "16": ("residue-item", "residue-item"),
    "17": ("installed-apps", "installed-apps"),
    "18": ("storage-breakdown", "storage-breakdown"),
    "19": ("battery-diagnostics", "battery-diagnostics"),
    "20": ("ai-recommendations", "ai-recommendations"),
}


def latest_runtime(prefix):
    candidates = sorted(RUNTIME.glob(f"{prefix}-*.png"))
    return candidates[-1] if candidates else None


def build(ref_number, capture_path):
    ref_file = sorted(REFS.glob(f"{ref_number}_*.png"))[0]
    ref_screen = Image.open(ref_file).convert("RGB").crop(REF_SCREEN_BOX)
    runtime = Image.open(capture_path).convert("RGB")

    height = 1500
    ref_scaled = ref_screen.resize((int(ref_screen.width * height / ref_screen.height), height))
    run_scaled = runtime.resize((int(runtime.width * height / runtime.height), height))

    gap = 24
    label_h = 34
    sheet = Image.new("RGB", (ref_scaled.width + run_scaled.width + gap * 3, height + label_h + 20), (10, 6, 22))
    draw = ImageDraw.Draw(sheet)
    draw.text((gap, 10), f"REFERENCE {ref_number} — {ref_file.stem}", fill=(255, 255, 255))
    draw.text((gap * 2 + ref_scaled.width, 10), f"DEVICE — {Path(capture_path).name}", fill=(255, 255, 255))
    sheet.paste(ref_scaled, (gap, label_h + 12))
    sheet.paste(run_scaled, (gap * 2 + ref_scaled.width, label_h + 12))
    OUT.mkdir(parents=True, exist_ok=True)
    out_path = OUT / f"ref{ref_number}_vs_{Path(capture_path).stem}.png"
    sheet.save(out_path, optimize=True)
    return out_path


def main():
    args = sys.argv[1:]
    if not args:
        print(__doc__)
        return
    if args[0] == "--all":
        for number, (route, prefix) in ROUTE_BY_REF.items():
            capture = latest_runtime(prefix)
            if capture is None:
                print(f"ref {number}: no runtime capture yet")
                continue
            print(f"ref {number}: {build(number, capture)}")
        return
    number = args[0]
    capture = Path(args[1])
    if not capture.is_absolute():
        capture = RUNTIME / capture
    print(build(number, capture))


if __name__ == "__main__":
    main()
