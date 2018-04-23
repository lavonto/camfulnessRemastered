package fi.hamk.calmfulness.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import fi.hamk.calmfulness.ExerciseActivity;
import fi.hamk.calmfulness.R;

/**
 * Helper class for showing and canceling message
 * notifications.
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class NotificationProvider {

    // Log tag
    private static final String TAG = NotificationProvider.class.getName();

    // Boolean to check whether notification was sent or not
    private static boolean notificationSent;

    /**
     * Returns <tt>true</tt> if notification was sent and <tt>false</tt> if not
     *
     * @return State of notification
     */
    public static boolean isNotificationSent() {
        return notificationSent;
    }

    public static void setNotificationSent(final boolean notificationSent) {
        NotificationProvider.notificationSent = notificationSent;
    }

    /**
     * Creates and sends a new notification
     *
     * @param context context of calling activity
     */
    public static void createNotification(final Context context, final String locationId) {

        final Resources resources = context.getResources();

        final String EXERCISE_NOTIFICATION_TITLE = resources.getString(R.string.notification_title);
        final String EXERCISE_NOTIFICATION_CONTENT = resources.getString(R.string.notification_content);
        final String ticker = resources.getString(R.string.notification_title);
        final int id = 0;

        Intent intent = new Intent(context, ExerciseActivity.class);
        intent.putExtra("locationId", locationId);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Exercises")
                .setContentTitle(EXERCISE_NOTIFICATION_TITLE)
                .setContentText(EXERCISE_NOTIFICATION_CONTENT)
                .setSmallIcon(R.drawable.stat_message_ic)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorNotification))))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(ticker)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));

        Notification notification = builder.build();

        if (isNotificationSent()) {
            cancelNotification(context, id);
        }

        getManager(context).notify(id, notification);

        notificationSent = true;
    }

    // Cancels notification
    public static void cancelNotification(final Context context, final int id) {
        getManager(context).cancel(id);
        notificationSent = false;
    }

    public static void cancelAllNotifications(final Context context) {
        getManager(context).cancelAll();
        notificationSent = false;
    }

    // Vibrates phone
    public static void vibratePhone(final Context context, final long ms) {
        final Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                mVibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                mVibrator.vibrate(ms);
            }
        }
    }

    private static NotificationManager getManager(final Context context) {

        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}

