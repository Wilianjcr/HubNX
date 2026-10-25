package com.hubnx.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Procura Releases públicas assinadas do HubNX e facilita atualização.
 * Não instala silenciosamente: o Android exige confirmação do usuário.
 * A atualização só preserva o app instalado se todas as releases usarem a mesma chave.
 */
public final class UpdateChecker {
    private static final String API =
        "https://api.github.com/repos/Wilianjcr/HubNX/releases/latest";
    private static final String ASSET_NAME = "HubNX-Android.apk";
    private final Activity activity;
    private DownloadManager downloads;
    private BroadcastReceiver completed;
    private long pendingId = -1;

    public UpdateChecker(Activity activity) {
        this.activity = activity;
        this.downloads = (DownloadManager)
            activity.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private boolean checking = false;
    private long lastCheckTime = 0;
    public synchronized void check(boolean userRequested) {
        long now = System.currentTimeMillis();
        if (checking || (!userRequested && now - lastCheckTime < 10L * 60 * 1000)) return;
        checking = true;
        lastCheckTime = now;
        new Thread(() -> {
            try {
                ReleaseInfo latest = ReleaseInfo.fetch();
                if (latest == null) {
                    reportNoUpdate(userRequested, "Ainda não há uma Release Android assinada disponível.");
                    return;
                }
                if (!latest.isNewer()) {
                    reportNoUpdate(userRequested, "Seu HubNX está atualizado (" +
                        BuildConfig.VERSION_NAME + ").");
                    return;
                }
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing() || activity.isDestroyed()) return;
                    new AlertDialog.Builder(activity)
                        .setTitle("HubNX " + latest.version + " disponível")
                        .setMessage("Versão instalada: " + BuildConfig.VERSION_NAME
                            + "\nNova versão: " + latest.version
                            + "\n\nA atualização será baixada no próprio aplicativo. "
                            + "O Android pedirá confirmação para instalar.")
                        .setNegativeButton("Depois", null)
                        .setPositiveButton("Atualizar", (dialog, which) ->
                            download(latest.downloadUrl, latest.version))
                        .show();
                });
            } catch (Exception err) {
                if (userRequested) reportNoUpdate(true,
                    "Não foi possível verificar atualizações: " + err.getMessage());
            } finally {
                checking = false;
            }
        }, "hubnx-update").start();
    }

    private void reportNoUpdate(boolean show, String message) {
        if (show) activity.runOnUiThread(() -> {
            if (!activity.isFinishing() && !activity.isDestroyed())
                new AlertDialog.Builder(activity)
                    .setTitle("Atualização HubNX")
                    .setMessage(message)
                    .setPositiveButton("OK", null).show();
        });
    }

    private void download(String apkUrl, String version) {
        try {
            Uri uri = Uri.parse(apkUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setMimeType("application/vnd.android.package-archive");
            request.setTitle("HubNX " + version);
            request.setDescription("Atualização Android do HubNX");
            request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "HubNX-Android-" + version + ".apk");

            pendingId = downloads.enqueue(request);
            Toast.makeText(activity, "Baixando HubNX. Você receberá uma notificação.",
                Toast.LENGTH_LONG).show();

            if (completed == null) {
                completed = new BroadcastReceiver() {
                    @Override public void onReceive(Context context, Intent intent) {
                        if (intent == null || !DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(
                                intent.getAction())) return;
                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (id != pendingId) return;

                        DownloadManager.Query query = new DownloadManager.Query()
                            .setFilterById(id);
                        try (android.database.Cursor c = downloads.query(query)) {
                            if (c == null || !c.moveToFirst()) return;
                            int s = c.getInt(c.getColumnIndexOrThrow(
                                DownloadManager.COLUMN_STATUS));
                            if (s != DownloadManager.STATUS_SUCCESSFUL) {
                                Toast.makeText(activity, "Falha ao baixar atualização.",
                                    Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        Uri apk = downloads.getUriForDownloadedFile(id);
                        if (apk == null) return;
                        new AlertDialog.Builder(activity)
                            .setTitle("Atualização baixada")
                            .setMessage("Deseja iniciar a instalação agora? "
                                + "O Android solicitará autorização.")
                            .setNegativeButton("Depois", null)
                            .setPositiveButton("Instalar", (dialog, which) -> install(apk))
                            .show();
                    }
                };
                IntentFilter filter = new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                if (Build.VERSION.SDK_INT >= 33) {
                    activity.registerReceiver(completed, filter, Context.RECEIVER_NOT_EXPORTED);
                } else {
                    activity.registerReceiver(completed, filter);
                }
            }
        } catch (Exception e) {
            Toast.makeText(activity, "Falha ao iniciar download: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private void install(Uri apkUri) {
        try {
            if (Build.VERSION.SDK_INT >= 26 &&
                    !activity.getPackageManager().canRequestPackageInstalls()) {
                new AlertDialog.Builder(activity)
                    .setTitle("Permissão de instalação")
                    .setMessage("Autorize o HubNX a instalar atualizações. "
                        + "Depois, abra o APK na pasta Downloads.")
                    .setPositiveButton("Abrir configurações", (d, w) -> {
                        Intent settings = new Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivity(settings);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Abra o APK na pasta Downloads para instalar.",
                Toast.LENGTH_LONG).show();
        }
    }

    private static int compareVersions(String newer, String installed) {
        String[] a = newer.split("\\.");
        String[] b = installed.split("\\.");
        if (a.length != 3 || b.length != 3) return 0;
        for (int i = 0; i < 3; i++) {
            int x = Integer.parseInt(a[i]);
            int y = Integer.parseInt(b[i]);
            if (x != y) return Integer.compare(x, y);
        }
        return 0;
    }

    public void release() {
        if (completed != null) {
            try { activity.unregisterReceiver(completed); }
            catch (IllegalArgumentException ignored) {}
            completed = null;
        }
    }
}
