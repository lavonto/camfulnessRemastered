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
public class UserNotification {

    /**
     * The unique identifier for this type of notification.
     */
    private static final String TAG = UserNotification.class.getName();

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

    public static void notify(final Context context) {

        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
//        final Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.stat_message_ic);

        final String EXERCISE_NOTIFICATION_TITLE = res.getString(R.string.exercise_notification_title);
        final String EXERCISE_NOTIFICATION_CONTENT = res.getString(R.string.exercise_notification_content);
        final String ticker = res.getString(R.string.exercise_notification_title);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and content
                .setSmallIcon(R.drawable.stat_message_ic)
                .setContentTitle(EXERCISE_NOTIFICATION_TITLE)
                .setContentText(EXERCISE_NOTIFICATION_CONTENT)

                // Gets color from colors.xml as hex string, then parse it to int
                // This method can be used in SDK (API) >= 23 and < 23, unlike just getColor(int id) that requires SDK >= 23
                .setColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorNotification))))

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Set vibration pattern
                .setVibrate(new long[]{1000L, 1000L})

                // Set ticker (preview) information for this notification.
                .setTicker(ticker)

                // Set the pending intent to be initiated when the user touches the notification.
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ExerciseActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))

                // Show expanded NOTIFICATION_CONTENT content on devices running Android 4.1 or later.
                .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(EXERCISE_NOTIFICATION_CONTENT)
                                .setBigContentTitle(EXERCISE_NOTIFICATION_TITLE)
//                        .setSummaryText(res.getString(R.string.notification_title))
                )

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        // If MapsActivity is focused, cancel sending notification and only vibrate phone
        // If not, continue with notify
        if (MainActivity.isFocused() || MapsActivity.isFocused()) {
            vibratePhone(context, 1000L);
            cancel(context);
        } else {
            notify(context, builder.build());
        }
    }

    // Sends notification
    public static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(TAG, 0, notification);
        notificationSent = true;
    }

    // Cancels notification
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(TAG, 0);
        notificationSent = false;
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
}

