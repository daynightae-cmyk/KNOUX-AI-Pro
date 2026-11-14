#!/usr/bin/env python3
"""Synthesize the KNOUX AI splash click (no third-party sample).

One short percussive tick that plays when the smile finishes. Kept inside the
35-100 ms window from the splash storyboard and peak-normalised so it is
audible above the device's sonification stream without clipping.
"""
import math
import struct
import wave
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "raw" / "knoux_click.wav"
RATE = 44100
DURATION_MS = 90
PEAK = 0.85
TONE_HZ = 1650.0
TONE_HZ_2 = 2480.0


def main():
    frames = int(RATE * DURATION_MS / 1000)
    samples = []
    for index in range(frames):
        t = index / RATE
        # sharp attack, fast exponential decay
        attack = min(1.0, t / 0.0015)
        decay = math.exp(-t * 58.0)
        body = math.sin(2 * math.pi * TONE_HZ * t) * 0.72 + math.sin(2 * math.pi * TONE_HZ_2 * t) * 0.28
        click = 0.55 * decay * body + 0.45 * decay * (1.0 if (index % 3) else -0.6)
        samples.append(attack * decay * click * attack)

    peak = max(abs(value) for value in samples) or 1.0
    scale = PEAK / peak
    pcm = b"".join(
        struct.pack("<h", int(max(-1.0, min(1.0, value * scale)) * 32767)) for value in samples
    )

    OUT.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(OUT), "wb") as handle:
        handle.setnchannels(1)
        handle.setsampwidth(2)
        handle.setframerate(RATE)
        handle.writeframes(pcm)
    print(f"{OUT} · {DURATION_MS} ms · peak {PEAK} · {OUT.stat().st_size} bytes")


if __name__ == "__main__":
    main()
