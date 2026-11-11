#!/usr/bin/env python3
"""Wait for an adbd endpoint to appear on the container loopback and connect.

The phone's wireless-debugging adbd is forwarded into this environment
intermittently, so we poll the ephemeral range, identify adbd by its CNXN
banner and hand the port to `adb connect` immediately.
"""

import asyncio
import os
import struct
import subprocess
import sys
import time

MAGIC = 0xFFFFFFFF
ADB = "/opt/bin/adb"
RANGE_START = int(os.environ.get("SCAN_START", "20000"))
RANGE_END = int(os.environ.get("SCAN_END", "61000"))
DEADLINE = time.time() + float(os.environ.get("DEADLINE", "300"))


def cnxn() -> bytes:
    payload = b"host::features=shell_v2,cmd,stat_v2,fixed_push_mkdir=\x00"
    return struct.pack("<5I", 0x4E584E43, 0x01000000, 0x00100000, len(payload), MAGIC) + payload


async def probe(port: int, timeout: float = 0.8) -> bool:
    try:
        reader, writer = await asyncio.wait_for(
            asyncio.open_connection("127.0.0.1", port), timeout
        )
    except Exception:
        return False
    try:
        writer.write(cnxn())
        await writer.drain()
        data = await asyncio.wait_for(reader.read(64), timeout)
        return len(data) >= 4 and data[0:4] == b"CNXN"
    except Exception:
        return False
    finally:
        try:
            writer.close()
        except Exception:
            pass


async def scan_once(sem) -> list:
    hits = []

    async def worker(p):
        async with sem:
            if await probe(p):
                hits.append(p)

    await asyncio.gather(*[worker(p) for p in range(RANGE_START, RANGE_END)])
    return hits


def adb_connect(port: int) -> str:
    try:
        out = subprocess.run(
            [ADB, "connect", "127.0.0.1:%d" % port],
            capture_output=True, text=True, timeout=30,
        )
        return (out.stdout + out.stderr).strip()
    except Exception as e:
        return "error: %s" % e


async def main():
    sem = asyncio.Semaphore(700)
    attempt = 0
    while time.time() < DEADLINE:
        attempt += 1
        hits = await scan_once(sem)
        stamp = time.strftime("%H:%M:%S")
        if hits:
            print("[%s] attempt %d: adbd on %s" % (stamp, attempt, hits), flush=True)
            for port in hits:
                print("[%s]   connect -> %s" % (stamp, adb_connect(port)), flush=True)
            time.sleep(2)
            try:
                out = subprocess.run([ADB, "devices", "-l"], capture_output=True, text=True, timeout=20)
                print("[%s] devices:\n%s" % (stamp, out.stdout.strip()), flush=True)
                if "device" in out.stdout and "unauthorized" not in out.stdout:
                    print("CONNECTED", flush=True)
                    return
            except Exception as e:
                print("devices failed:", e, flush=True)
        else:
            print("[%s] attempt %d: no adbd yet" % (stamp, attempt), flush=True)
        await asyncio.sleep(2)
    print("TIMEOUT: no adbd connection", flush=True)


if __name__ == "__main__":
    asyncio.run(main())
