#!/usr/bin/env python3
"""Scan the phone's loopback for an adbd (wireless debugging) endpoint.

The proot container shares the device network namespace, so the phone's own
127.0.0.1 ports are visible here. We speak just enough of the ADB wire
protocol (CNXN) to identify an adbd and read its banner.
"""

import asyncio
import struct
import sys

MAGIC = 0xFFFFFFFF


def cnxn(payload: bytes) -> bytes:
    return struct.pack("<6I", 0x4E584E43, 0x01000000, 0x00100000, len(payload), MAGIC, 0)[:20] + payload


async def probe(port: int, timeout: float = 1.5) -> str:
    try:
        reader, writer = await asyncio.wait_for(asyncio.open_connection("127.0.0.1", port), timeout)
    except Exception:
        return ""
    try:
        writer.write(cnxn(b"host::features=shell_v2,cmd,stat_v2,fixed_push_mkdir=\x00"))
        await writer.drain()
        data = await asyncio.wait_for(reader.read(512), timeout)
        if len(data) >= 8 and data[0:4] == b"CNXN":
            length = struct.unpack("<I", data[16:20])[0]
            banner = data[24:24 + length].decode("utf-8", "replace")
            return banner
    except Exception:
        pass
    finally:
        try:
            writer.close()
        except Exception:
            pass
    return ""


async def main():
    ports = range(1, 65536)
    sem = asyncio.Semaphore(512)
    found = []

    async def worker(p):
        async with sem:
            banner = await probe(p)
            if banner:
                found.append((p, banner))
                print("HIT port %d -> %s" % (p, banner), flush=True)

    tasks = [worker(p) for p in ports]
    for i in range(0, len(tasks), 2000):
        await asyncio.gather(*tasks[i:i + 2000])
        print("...scanned %d" % min(i + 2000, len(tasks)), flush=True)

    print("=== RESULT ===")
    for p, b in sorted(found):
        print("%d\t%s" % (p, b))
    if not found:
        print("no adbd found on 127.0.0.1")


if __name__ == "__main__":
    asyncio.run(main())
