package fi.hamk.calmfulnessV2.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import fi.hamk.calmfulnessV2.ExerciseActivity;
import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.MapsActivity;
import fi.hamk.calmfulnessV2.R;

/**
 * Helper class for showing and canceling message
 * notifications.
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class NotificationProvider {

    /**
     * The unique identifier for this type of notification.
     */
    private static final String TAG = NotificationProvider.class.getName();

    /**
     * Boolean to check whether notification was sent or not
     */
    private static boolean notificationSent;

    /**
     * Return <tt>true</tt> if notification was sent and <tt>false</tt> if not
     *
     * @return State of notification
     */
    public static boolean isNotificationSent() {
        return notificationSent;
    }

    public static void createNotification(final Context context) {

        final Resources resources = context.getResources();

        final String EXERCISE_NOTIFICATION_TITLE = resources.getString(R.string.exercise_notification_title);
        final String EXERCISE_NOTIFICATION_CONTENT = resources.getString(R.string.exercise_notification_content);
        final String ticker = resources.getString(R.string.exercise_notification_title);
        final String label = "x";
        final int id = 0;

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

        getManager(context).notify(label, id, notification);

        final String gpsId = SharedPreferences.getLastVisitedPoint(context);
        if (gpsId != null) {
            cancelNotification(context);
            SharedPreferences.setLastVisitedPoint(null, context);
        }
    }

    // Cancels notification
    public static void cancelNotification(final Context context) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(TAG, 0);
        notificationSent = false;
    }

    public static void cancelAllNotification(final Context context) {
        getManager(context).cancelAll();
    }

    // Vibrates phone
    public static void vibratePhone(final Context context, final long ms) {
        final Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26)
            mVibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        else {
            mVibrator.vibrate(ms);
        }
    }

    private static NotificationManager getManager(Context context) {
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        return manager;
    }
}

