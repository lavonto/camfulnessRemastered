package fi.hamk.calmfulnessV2.helpers;

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
import android.util.Log;

import fi.hamk.calmfulnessV2.ExerciseActivity;
import fi.hamk.calmfulnessV2.R;

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
     * @return State of notification
     */
    public static boolean isNotificationSent() {
        return notificationSent;
    }

    /**
     * Creates and sends a new notification
     * @param context context of calling activity
     */
    public static void createNotification(final Context context) {

        final Resources resources = context.getResources();

        final String EXERCISE_NOTIFICATION_TITLE = resources.getString(R.string.exercise_notification_title);
        final String EXERCISE_NOTIFICATION_CONTENT = resources.getString(R.string.exercise_notification_content);
        final String ticker = resources.getString(R.string.exercise_notification_title);
        final String label = "x"; // TODO: Add gpsPoint id here
        final int id = 0;

        //
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(EXERCISE_NOTIFICATION_TITLE)
                .setContentText(EXERCISE_NOTIFICATION_CONTENT)
                .setSmallIcon(R.drawable.stat_message_ic)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorNotification))))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(ticker)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ExerciseActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));

        Notification notification = builder.build();

        if (isNotificationSent()) {
            Log.d(TAG, "Canceling previously sent notification...");
            cancelNotification(context, label, id);
        }

        getManager(context).notify(label, id, notification);
        Log.d(TAG, "Notification sent. LABEL: " + label + " ID: " + id);

        notificationSent = true;
    }

    // Cancels notification
    public static void cancelNotification(final Context context,String tag, int id) {
        getManager(context).cancel(tag,id);
        Log.d(TAG, "Notification canceled. ID: " + id);
        notificationSent = false;
    }

    public static void cancelAllNotifications(final Context context) {
        getManager(context).cancelAll();
        Log.d(TAG, "Canceled all notifications");
    }

    // Vibrates phone
    public static void vibratePhone(final Context context, final long pattern) {
        final Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26)
            mVibrator.vibrate(VibrationEffect.createOneShot(pattern, VibrationEffect.DEFAULT_AMPLITUDE));
        else {
            mVibrator.vibrate(pattern);
        }
    }

    private static NotificationManager getManager(Context context) {
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        return manager;
    }
}

