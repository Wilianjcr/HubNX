package com.hubnx.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Periodic checks continue when the app is closed, subject to Android scheduling.
 * At most one notification per new version is posted to avoid spam.
 */
public final class UpdateWorker extends Worker {
    private static final String CHANNEL = "hubnx_updates";
    private static final String UNIQUE = "HubNX-Check-Releases";
    private static final String PREFS = "hubnx_updater";
    private static final int NOTIFY_ID = 2422;

    public UpdateWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    public static void schedule(Context context) {
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
            UpdateWorker.class, 1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build();
        WorkManager.getInstance(context.getApplicationContext())
            .enqueueUniquePeriodicWork(UNIQUE, ExistingPeriodicWorkPolicy.KEEP, request);
        createChannel(context);
    }

    private static void createChannel(Context context) {
        NotificationManager mgr =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mgr == null) return;
        NotificationChannel channel = new NotificationChannel(
            CHANNEL, "Atualizações do HubNX", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Avisa quando uma versão nova do HubNX estiver disponível.");
        mgr.createNotificationChannel(channel);
    }

    @Override public Result doWork() {
        Context app = getApplicationContext();
        try {
            ReleaseInfo official = ReleaseInfo.fetch();
            String version;
            String kind;
            if (official != null && official.isNewer()) {
                version = official.version;
                kind = "official";
            } else {
                DebugBuildInfo debug = DebugBuildInfo.fetch();
                if (debug == null || !debug.isNewer()) return Result.success();
                version = debug.version;
                kind = "debug";
            }
            if (Build.VERSION.SDK_INT >= 33 &&
                app.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) return Result.success();

            String notificationKey = kind + ":" + version;
            String last = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString("notification_version", "");
            if (notificationKey.equals(last)) return Result.success();

            Intent launch = new Intent(app, MainActivity.class);
            launch.putExtra("hubnx_check_update", true);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent tap = PendingIntent.getActivity(app, 0, launch,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            Notification notification = new Notification.Builder(app, CHANNEL)
                .setContentTitle("HubNX " + version +
                    ("debug".equals(kind) ? " — nova versão de teste" : " disponível"))
                .setContentText("debug".equals(kind) ?
                    "Toque para ver a compilação e fazer backup antes de instalar." :
                    "Toque para atualizar o HubNX.")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(tap)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();
            NotificationManager manager = (NotificationManager) app
                .getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(NOTIFY_ID, notification);
                app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().putString("notification_version", notificationKey).apply();
            }
            return Result.success();
        } catch (Exception error) {
            // WorkManager retries transient network problems with backoff.
            return Result.retry();
        }
    }
}
