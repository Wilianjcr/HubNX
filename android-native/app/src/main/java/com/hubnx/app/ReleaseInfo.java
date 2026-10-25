package com.hubnx.app;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Shared trusted metadata reader for foreground dialogs and background notifications. */
public final class ReleaseInfo {
    private static final String API =
        "https://api.github.com/repos/Wilianjcr/HubNX/releases/latest";
    public final String version;
    public final String downloadUrl;

    private ReleaseInfo(String version, String downloadUrl) {
        this.version = version;
        this.downloadUrl = downloadUrl;
    }

    public static ReleaseInfo fetch() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API).openConnection();
        conn.setConnectTimeout(9000);
        conn.setReadTimeout(12000);
        conn.setRequestProperty("User-Agent", "HubNX-UpdateChecker");
        conn.setRequestProperty("Accept", "application/vnd.github+json");
        try {
            int code = conn.getResponseCode();
            if (code == 404) return null;  // Repo still has no published Release.
            if (code != 200) throw new IllegalStateException("GitHub HTTP " + code);
            byte[] bytes;
            try (InputStream in = conn.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] b = new byte[4096];
                int n;
                while ((n = in.read(b)) != -1) {
                    out.write(b, 0, n);
                    if (out.size() > 200_000) throw new IllegalStateException("Resposta muito grande");
                }
                bytes = out.toByteArray();
            }
            JSONObject release = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
            if (release.optBoolean("draft") || release.optBoolean("prerelease")) return null;
            String tag = release.optString("tag_name");
            if (!tag.matches("v[0-9]+\\.[0-9]+\\.[0-9]+")) return null;
            JSONArray assets = release.optJSONArray("assets");
            if (assets == null) return null;
            String expected = "https://github.com/Wilianjcr/HubNX/releases/download/"
                + tag + "/HubNX-Android.apk";
            for (int i = 0; i < assets.length(); i++) {
                JSONObject a = assets.getJSONObject(i);
                if ("HubNX-Android.apk".equals(a.optString("name"))
                    && expected.equals(a.optString("browser_download_url"))
                    && a.optLong("size", 0) > 10_000) {
                    return new ReleaseInfo(tag.substring(1), expected);
                }
            }
            return null;
        } finally {
            conn.disconnect();
        }
    }

    public boolean isNewer() {
        return compare(version, BuildConfig.VERSION_NAME) > 0;
    }

    public static int compare(String first, String second) {
        if (!first.matches("[0-9]+\\.[0-9]+\\.[0-9]+")
            || !second.matches("[0-9]+\\.[0-9]+\\.[0-9]+")) return 0;
        String[] a = first.split("\\.");
        String[] b = second.split("\\.");
        for (int i = 0; i < 3; i++) {
            int x = Integer.parseInt(a[i]);
            int y = Integer.parseInt(b[i]);
            if (x != y) return Integer.compare(x, y);
        }
        return 0;
    }
}
