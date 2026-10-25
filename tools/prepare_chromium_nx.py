#!/usr/bin/env python3
"""Adaptação experimental do Chromium NX para um HubNX independente (.nro).
Requer devkitPro e o ambiente oficial de compilação upstream; não compila sozinho.
"""
from pathlib import Path
import sys
import shutil

PROJECT = Path(__file__).resolve().parent.parent

def one(text, old, new):
    if text.count(old) != 1:
        raise RuntimeError("Patch não aplicado: fonte upstream incompatível: " + old[:55])
    return text.replace(old, new, 1)

def prepare(src):
    gx = src / "gx"
    paths = [gx / "host_nx.c", gx / "build-nx.sh", gx / "build-chromium.sh"]
    if not all(p.is_file() for p in paths):
        raise FileNotFoundError("Não encontrei as fontes oficiais Chromium NX em gx/")
    ui = src / "media-hub"
    ui.mkdir(exist_ok=True)
    for f in ("index.html", "firebase-config.js"):
        shutil.copy2(PROJECT / "switch" / "media-hub" / f, ui / f)
    shutil.copy2(PROJECT / "branding" / "hubnx-icon.svg", ui / "icon.svg")

    host = gx / "host_nx.c"
    t = host.read_text()
    t = one(t, '#define GX_DIR "sdmc:/switch/chromium-nx"', '#define GX_DIR "sdmc:/switch/hubnx"')
    begin = t.find("#define DEFAULT_COMMAND \\")
    end = t.find("/* The kernel's memory", begin)
    if begin < 0 or end < 0 or "chromium-shell" not in t[begin:end]:
        raise RuntimeError("DEFAULT_COMMAND mudou: revisar patch upstream")
    default = (
        '#define DEFAULT_COMMAND \\\n'
        '\t"-w -b -c3 @chromium-full.ext4 =profile.ext4 -A225,46,1150,80 " \\\n'
        '\t"/usr/lib/chromium/chromium --no-sandbox --single-process --no-zygote " \\\n'
        '\t"--ozone-platform=wayland --user-data-dir=/root/profile --no-first-run " \\\n'
        '\t"--no-default-browser-check --disable-breakpad --disable-crashpad-for-testing " \\\n'
        '\t"--test-type --password-store=basic --disable-background-networking " \\\n'
        '\t"--disable-component-update --disk-cache-size=33554432 " \\\n'
        '\t"--media-cache-size=33554432 --disable-quic --hide-crash-restore-bubble " \\\n'
        '\t"--enable-spatial-navigation --remote-debugging-pipe " \\\n'
        '\t"--use-gl=angle --use-angle=gles-egl --ignore-gpu-blocklist " \\\n'
        '\t"--disable-gpu-sandbox --in-process-gpu file:///usr/share/gx/hubnx/index.html"\n'
    )
    host.write_text(t[:begin] + default + t[end:])

    script = gx / "build-chromium.sh"
    t = script.read_text()
    key = 'mkdir -p "$ROOT"/{proc,sys,dev,tmp,run,root,lib,etc/ssl/certs,usr/lib,usr/share/gx,usr/share/fonts}'
    t = one(t, key, key +
        '\n\tmkdir -p "$ROOT/usr/share/gx/hubnx"' +
        '\n\tcp "$SRC/../media-hub/index.html" "$ROOT/usr/share/gx/hubnx/index.html" || exit 1' +
        '\n\tcp "$SRC/../media-hub/firebase-config.js" "$ROOT/usr/share/gx/hubnx/firebase-config.js" || exit 1')
    script.write_text(t)

    script = gx / "build-nx.sh"
    t = script.read_text()
    changes = [
        ('nacptool --create "Chromium NX" "shodowlo" "0.1"',
         'nacptool --create "HubNX" "HubNX" "0.2.3"'),
        ('--icon="$SRC/nx/icon.jpg"',
         '--icon="$SRC/../media-hub/icon.jpg"'),
        ('APP=$DIST/switch/chromium-nx','APP=$DIST/switch/hubnx'),
        ('cp "$OUT/gx.nro" "$APP/chromium-nx.nro"',
         'cp "$OUT/gx.nro" "$APP/hubnx.nro"'),
        ('ls -la "$APP"',
         'rm -f "$APP/programs.txt"\n' +
         '[ -f "$APP/chromium-full.ext4" ] || { echo "Chromium completo ausente"; exit 1; }\n' +
         '[ -f "$APP/profile.ext4.gz" ] || { echo "Perfil persistente ausente"; exit 1; }\n' +
         'ls -la "$APP"')
    ]
    for a,b in changes: t=one(t,a,b)
    script.write_text(t)
    print("Fontes preparadas:", src)
    print("Ícone HubNX: gere media-hub/icon.jpg a partir de media-hub/icon.svg antes do build Switch.")
    print("A compilação exige o ambiente completo Chromium NX.")

if __name__ == "__main__":
    if len(sys.argv)!=2: raise SystemExit("Uso: prepare_chromium_nx.py <caminho-chromium-nx>")
    prepare(Path(sys.argv[1]).resolve())
