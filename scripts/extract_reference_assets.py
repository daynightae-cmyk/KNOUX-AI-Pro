#!/usr/bin/env python3
"""Extract decorative hero/icon crops from the user-supplied reference PNGs.

The references are design assets supplied by the project owner. We only cut
decorative artwork (never text or interactive UI) and feather the edges so it
blends into the native Compose surfaces. Source coordinates are documented so
the extraction is reproducible.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageChops

ROOT = Path(__file__).resolve().parent.parent
REF = ROOT / "design" / "refs20_complete"
OUT = ROOT / "app" / "src" / "main" / "res" / "drawable-nodpi"
OUT.mkdir(parents=True, exist_ok=True)

HOME = Image.open(REF / "02_Home_Dashboard.png").convert("RGBA")
CLEAN = Image.open(REF / "11_Smart_Clean_Center.png").convert("RGBA")


def smooth_mask(size, radius, blur, edge=0):
    w, h = size
    mask = Image.new("L", size, 0)
    d = ImageDraw.Draw(mask)
    d.rounded_rectangle((edge, edge, w - 1 - edge, h - 1 - edge), radius=radius, fill=255)
    return mask.filter(ImageFilter.GaussianBlur(blur))


def hero_asset(src, box, name, radius=26, blur=12, top=18, bottom=34, side=18):
    w = box[2] - box[0]
    h = box[3] - box[1]
    im = src.crop(box).convert("RGBA")
    mask = smooth_mask((w, h), radius, blur, edge=1)
    # Feather the long edges so the screenshot crop blends into the app canvas.
    px = mask.load()
    for y in range(h):
        fy = min(1.0, y / top, (h - 1 - y) / bottom)
        for x in range(w):
            fx = min(1.0, x / side, (w - 1 - x) / side)
            a = int(px[x, y] * fy * fx)
            px[x, y] = a
    im.putalpha(mask)
    im.save(OUT / name, optimize=True)
    print(name, im.size)


def icon_asset(src, center, name, box_size=96, feather=7):
    r = box_size // 2
    box = (center[0] - r, center[1] - r, center[0] + r, center[1] + r)
    im = src.crop(box).convert("RGBA")
    mask = Image.new("L", (box_size, box_size), 0)
    d = ImageDraw.Draw(mask)
    d.ellipse((5, 5, box_size - 6, box_size - 6), fill=255)
    mask = mask.filter(ImageFilter.GaussianBlur(feather))
    im.putalpha(mask)
    im.save(OUT / name, optimize=True)
    print(name, im.size)


def rect_icon_asset(src, box, name, feather=12):
    """Crop one decorative 3D icon and fade its rectangular edges away."""
    im = src.crop(box).convert("RGBA")
    w, h = im.size
    mask = Image.new("L", (w, h), 0)
    d = ImageDraw.Draw(mask)
    d.ellipse((5, 5, w - 6, h - 6), fill=255)
    mask = mask.filter(ImageFilter.GaussianBlur(feather))
    im.putalpha(mask)
    im.save(OUT / name, optimize=True)
    print(name, im.size)


# Home hero: a full-bleed scene below the native header, above the title.
# Keeping the ribbons at the edges is what makes the artwork feel embedded in
# the screen instead of like a banner card.
hero_asset(
    HOME,
    (70, 160, 872, 426),
    "knoux_home_hero.png",
    radius=26,
    blur=16,
    top=18,
    bottom=26,
    side=38,
)

# Four small 3D capability glyphs from the benefit strip, no text.
for name, center in {
    "knoux_home_benefit_clean.png": (150, 582),
    "knoux_home_benefit_optimize.png": (320, 582),
    "knoux_home_benefit_protect.png": (515, 582),
    "knoux_home_benefit_manage.png": (705, 582),
}.items():
    icon_asset(HOME, center, name, box_size=54, feather=4)
# Smart Clean hero: trash-can orb on the right, above the tabs. No text is included.
hero_asset(CLEAN, (560, 150, 858, 376), "knoux_clean_hero.png", radius=54, blur=16, top=18, bottom=24, side=18)

# Service-card glyphs: crop only the illuminated object and its pedestal so the
# image can float on the card instead of sitting inside a second icon tile.
# Square 150 px boxes are centred on the real 3D object (not the card), and the
# circular mask + luminance alpha remove the surrounding tile and its border.
for name, box in {
    "knoux_home_icon_clean.png": (125, 940, 240, 1060),
    "knoux_home_icon_duplicate.png": (520, 940, 625, 1060),
    "knoux_home_icon_storage.png": (125, 1115, 240, 1235),
    "knoux_home_icon_privacy.png": (520, 1115, 635, 1235),
    "knoux_home_icon_battery.png": (125, 1280, 240, 1400),
    "knoux_home_icon_ai.png": (520, 1280, 625, 1400),
}.items():
    hero_asset(HOME, box, name, radius=30, blur=8, top=6, bottom=6, side=6)

# Smart Clean rows: crop inside the row borders and stop before the title.
# Luminance alpha removes the dark row surface so no tile edge survives.
for name, box in {
    "knoux_clean_icon_cache.png": (130, 617, 225, 687),
    "knoux_clean_icon_temp.png": (130, 697, 225, 750),
    "knoux_clean_icon_apk.png": (130, 757, 225, 810),
    "knoux_clean_icon_empty.png": (130, 822, 225, 875),
    "knoux_clean_icon_recycle.png": (130, 887, 225, 940),
    "knoux_clean_icon_ai.png": (145, 990, 302, 1115),
    "knoux_clean_icon_gain.png": (145, 1160, 258, 1270),
}.items():
    hero_asset(CLEAN, box, name, radius=12, blur=6, top=6, bottom=6, side=6)

# ---------------------------------------------------------------------------
# Full-width decorative hero bands for the remaining reference screens.
# The band ends above each reference title so no reference text is copied.
# Screen blending in Compose dissolves the dark backdrop; the alpha feather
# keeps the left/right edges from reading as a rectangular banner.
# ---------------------------------------------------------------------------
HERO_BANDS = {
    "03_Privacy_Sweep.png": "knoux_hero_privacy.png",
    "04_Duplicate_Files_Finder.png": "knoux_hero_duplicates.png",
    "05_Deep_Scan.png": "knoux_hero_deepscan.png",
    "06_Reports_Insights.png": "knoux_hero_reports.png",
    "07_App_Residue_Cleanup.png": "knoux_hero_residue.png",
    "08_Storage_Analyzer.png": "knoux_hero_storage.png",
    "09_Battery_Saver.png": "knoux_hero_battery.png",
    "10_AI_Optimization.png": "knoux_hero_ai.png",
    "12_Duplicate_Group_Review.png": "knoux_hero_duplicate_group.png",
    "13_Privacy_Risk_Inspector.png": "knoux_hero_privacy_risk.png",
    "14_Deep_Scan_Summary.png": "knoux_hero_scan_summary.png",
    "15_Performance_Report_Detail.png": "knoux_hero_performance.png",
    "16_Residue_Item_Review.png": "knoux_hero_residue_item.png",
    "17_Installed_Apps_Review.png": "knoux_hero_apps.png",
    "18_Storage_Breakdown.png": "knoux_hero_storage_breakdown.png",
    "20_AI_Recommendations.png": "knoux_hero_ai_recommendations.png",
}
for filename, name in HERO_BANDS.items():
    src = Image.open(REF / filename).convert("RGBA")
    box = (58, 96, 883, 340)
    if name == "knoux_hero_ai_recommendations.png":
        box = (58, 96, 883, 314)
    hero_asset(src, box, name, radius=28, blur=14, top=10, bottom=18, side=24)

# Battery Diagnostics uses a compact object beside the title, not a band.
BATTERY_DIAG = Image.open(REF / "19_Battery_Diagnostics.png").convert("RGBA")
hero_asset(
    BATTERY_DIAG,
    (650, 135, 905, 355),
    "knoux_hero_battery_diag.png",
    radius=38,
    blur=16,
    top=14,
    bottom=20,
    side=16,
)

# ---------------------------------------------------------------------------
# Remove the dark rounded tile that surrounds the reference glyphs.
# Brightness becomes alpha, so the glowing object and its pedestal survive
# while the near-black card behind it becomes transparent. Screen blending in
# Compose then makes the glyph read as emitted light rather than a crop.
# ---------------------------------------------------------------------------
def luminance_alpha(name, floor=10, gain=1.55):
    path = OUT / name
    im = Image.open(path).convert("RGBA")
    src = im.load()
    w, h = im.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = src[x, y]
            lum = max(r, g, b)
            alpha = int(max(0, min(255, (lum - floor) * gain)))
            src[x, y] = (r, g, b, min(a, alpha))
    im.save(path, optimize=True)
    print("luminous", name)


# The Smart Clean orb is drawn over the app canvas, so its dark backdrop is
# removed with the same luminance-alpha rule used for the small glyphs.
luminance_alpha("knoux_clean_hero.png", floor=16, gain=1.15)

for name in sorted(p.name for p in OUT.glob("knoux_home_icon_*.png")):
    luminance_alpha(name, floor=22, gain=1.6)
for name in sorted(p.name for p in OUT.glob("knoux_home_benefit_*.png")):
    luminance_alpha(name, floor=8, gain=1.7)
for name in sorted(p.name for p in OUT.glob("knoux_clean_icon_*.png")):
    luminance_alpha(name, floor=22, gain=1.6)

# ---------------------------------------------------------------------------
# Service artwork for the Clean / Tools hubs. Each object is cut from its own
# reference screen so every tool gets a distinct 3D glyph, then the dark
# backdrop is removed with the luminance-alpha rule.
# ---------------------------------------------------------------------------
SERVICE_ART = {
    "knoux_svc_duplicates.png": ("04_Duplicate_Files_Finder.png", (340, 140, 540, 340)),
    "knoux_svc_deepscan.png": ("05_Deep_Scan.png", (340, 140, 540, 340)),
    "knoux_svc_residue.png": ("07_App_Residue_Cleanup.png", (340, 140, 540, 340)),
    "knoux_svc_apps.png": ("17_Installed_Apps_Review.png", (340, 118, 540, 296)),
    "knoux_svc_storage.png": ("08_Storage_Analyzer.png", (340, 140, 540, 340)),
    "knoux_svc_privacy.png": ("03_Privacy_Sweep.png", (340, 140, 540, 340)),
    "knoux_svc_battery.png": ("09_Battery_Saver.png", (340, 140, 540, 340)),
    "knoux_svc_ai.png": ("10_AI_Optimization.png", (340, 140, 540, 340)),
}
for name, (filename, box) in SERVICE_ART.items():
    src = Image.open(REF / filename).convert("RGBA")
    hero_asset(src, box, name, radius=64, blur=18, top=12, bottom=12, side=12)
    luminance_alpha(name, floor=18, gain=1.2)
