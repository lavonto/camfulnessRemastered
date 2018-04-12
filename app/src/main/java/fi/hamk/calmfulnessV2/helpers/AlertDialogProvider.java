package fi.hamk.calmfulnessV2.helpers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import fi.hamk.calmfulnessV2.R;
import fi.hamk.calmfulnessV2.settings.SettingsFragment;

public class AlertDialogProvider extends AppCompatActivity {

    /**
     * Context used with <code>{@link android.app.AlertDialog.Builder}</code>
     */
    private Context context;

    /**
     * Gets a Context from associated activity when object of this class is created
     * @param context of associated activity
     */
    public AlertDialogProvider(final Context context) {
        this.context = context;
    }

    /**
     * Default public constructor
     */
    public AlertDialogProvider(){

    }

    /**
     * Creates a dialog and shows it
     *
     * @param message The dialog message
     * @param title   The dialog title
     */
    public void createAndShowDialog(final String title, final String message) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    /**
     * Show dialog that asks user to view Apache License
     * @param title Dialog title
     * @param message Dialog message
     */
    public void createAndShowApacheDialog(final String title, final String message){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.apache_url)));
                if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(browserIntent);
                }
            }
        });
        builder.setNegativeButton(context.getString(R.string.alert_cancel),null);
        builder.create().show();
    }

    /**
     * Creates a dialog and shows it with yes and cancelNotification button. If user chooses yes, an intent is fired to
     * redirect user to location settings where user can activate location.
     * If user chooses no, the dialog will be canceled and nothing happens
     *
     * @param message The dialog message
     * @param title   The dialog title
     */
    public void createAndShowLocationDialog(final String title, final String message) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(context.getString(R.string.alert_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                SettingsFragment.setChangedState(true);
            }
        });
        builder.setNegativeButton(context.getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }
}
