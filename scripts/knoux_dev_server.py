#!/usr/bin/env python3
"""Dev server for KNOUX AI on the phone.

Serves the release folder over the shared loopback (the phone browser and the
KNOUX debug app both reach 127.0.0.1:8099) and accepts runtime screenshots
posted by the debug build:

    POST /upload?name=<label>   body = raw PNG bytes
    -> stored in qa-evidence/runtime/<label>-<timestamp>.png
"""

import collections
import os
import re
import sys
import threading
import time
import http.server
import socketserver
import urllib.parse

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RELEASE = os.path.join(ROOT, "release")
SHOTS = os.path.join(ROOT, "qa-evidence", "runtime")
PORT = int(os.environ.get("KNOUX_PORT", "8099"))

os.makedirs(SHOTS, exist_ok=True)

# Debug-only route queue for the visual QA bridge. The debug app polls
# /qa/next, navigates there, and uploads a real PixelCopy screenshot.
QA_QUEUE = collections.deque()
QA_LOCK = threading.Lock()


class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *a, **kw):
        super().__init__(*a, directory=RELEASE, **kw)

    def log_message(self, fmt, *args):
        sys.stderr.write("[dev-server] %s\n" % (fmt % args))

    def _text(self, code, body):
        raw = body.encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        self.send_header("Content-Length", str(len(raw)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(raw)

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path == "/qa/next":
            with QA_LOCK:
                route = QA_QUEUE.popleft() if QA_QUEUE else None
            if route is None:
                self.send_response(204)
                self.send_header("Cache-Control", "no-store")
                self.end_headers()
            else:
                self._text(200, route)
            return
        if parsed.path == "/qa/status":
            with QA_LOCK:
                pending = list(QA_QUEUE)
            self._text(200, "%d\n%s" % (len(pending), "\n".join(pending)))
            return
        return super().do_GET()

    def do_POST(self):
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path == "/qa/enqueue":
            length = int(self.headers.get("Content-Length") or 0)
            body = self.rfile.read(length).decode("utf-8", "replace") if length else ""
            routes = [part.strip() for part in re.split(r"[\s,]+", body) if part.strip()]
            with QA_LOCK:
                QA_QUEUE.extend(routes)
            self._text(200, "queued %d" % len(routes))
            return
        if parsed.path == "/qa/clear":
            with QA_LOCK:
                QA_QUEUE.clear()
            self._text(200, "cleared")
            return
        if parsed.path != "/upload":
            self.send_error(404, "unknown endpoint")
            return
        qs = urllib.parse.parse_qs(parsed.query)
        name = (qs.get("name") or ["shot"])[0]
        name = re.sub(r"[^A-Za-z0-9._-]", "_", name)[:60] or "shot"
        length = int(self.headers.get("Content-Length") or 0)
        body = self.rfile.read(length) if length else b""
        stamp = time.strftime("%H%M%S")
        path = os.path.join(SHOTS, "%s-%s.png" % (name, stamp))
        with open(path, "wb") as fh:
            fh.write(body)
        sys.stderr.write("[dev-server] saved %s (%d bytes)\n" % (path, len(body)))
        self.send_response(200)
        self.send_header("Content-Type", "text/plain")
        self.send_header("Content-Length", "2")
        self.end_headers()
        self.wfile.write(b"ok")


class Server(socketserver.ThreadingTCPServer):
    allow_reuse_address = True
    daemon_threads = True


if __name__ == "__main__":
    # Loopback only: the phone browser and the debug app both talk to
    # 127.0.0.1, so binding every interface would only expose the build to
    # the local network for no benefit.
    with Server(("127.0.0.1", PORT), Handler) as httpd:
        sys.stderr.write("[dev-server] serving %s on 127.0.0.1:%d\n" % (RELEASE, PORT))
        httpd.serve_forever()
