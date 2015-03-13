package com.paypal.yamba2;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;
import com.thenewcircle.yamba.client.YambaStatus;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_REFRESH = "com.paypal.yamba2.action.REFRESH";
    private static final String ACTION_CHIRP = "com.paypal.yamba2.action.CHIRP";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM_REFRESH_NUM_CHIRPS = "com.paypal.yamba2.extra.refresh.NUM_CHIRPS";
    private static final String EXTRA_PARAM_CHIRP_MESSAGE = "com.paypal.yamba2.extra.chirp.MESSAGE";

    /**
     * Starts this service to perform action Refresh with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRefresh(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_REFRESH);
        intent.putExtra(EXTRA_PARAM_REFRESH_NUM_CHIRPS, param1);
        intent.putExtra(EXTRA_PARAM_CHIRP_MESSAGE, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Chirp with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionChirp(Context context, String msg) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_CHIRP);
        intent.putExtra(EXTRA_PARAM_CHIRP_MESSAGE, msg);
        context.startService(intent);
    }

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                final int numChirps = intent.getIntExtra(EXTRA_PARAM_REFRESH_NUM_CHIRPS, 10);
                handleActionRefresh(numChirps);
            } else if (ACTION_CHIRP.equals(action)) {
                final String msg = intent.getStringExtra(EXTRA_PARAM_CHIRP_MESSAGE);
                handleActionChirp(msg);
            }
        }
    }

    /**
     * Handle action Refresh in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRefresh(int numChirps) {
        try {
            YambaClient client = new YambaClient("student", "password",
                    "http://yamba.thenewcircle.com/api");
            List<YambaStatus> timeline = client.getTimeline(numChirps);
            for (YambaStatus chirp : timeline) {
                String msg = chirp.getMessage();
                Log.i("MyIntentService", "Chirp: " + msg);
            }
        } catch (YambaClientException e) {
            Log.e("MyIntentService", "Failed to download chirps", e);
        }
    }

    /**
     * Handle action Chirp in the provided background thread with the provided
     * parameters.
     */
    private void handleActionChirp(String msg) {
        LocationManager gps = (LocationManager) ((Context)this).getSystemService(Context.LOCATION_SERVICE);
        gps.getProvider(LocationManager.GPS_PROVIDER);
        gps.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location = gps.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        LocationListener listener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                Log.d("GPS", location.toString());
//            }
//            public void onStatusChanged(String provider, int status, Bundle extras) {}
//            public void onProviderEnabled(String provider) {}
//            public void onProviderDisabled(String provider) {}
//        };
//        gps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 1.0f, listener);
        try {
            YambaClient client = new YambaClient("student", "password", "http://yamba.thenewcircle.com/api");
            if (location != null) {
                client.postStatus(msg, location.getLatitude(), location.getLongitude());
            } else {
                client.postStatus(msg);
            }
            Intent intent = new Intent("com.paypal.yamba.TweetSuccessful").putExtra("com.paypal.MSG", msg);
            this.sendBroadcast(intent);
        } catch (YambaClientException e) {
            Log.e("MyIntentService", "Error posting status", e);
            e.printStackTrace();
        }
        Vibrator vibes = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        vibes.vibrate(1000);
        Intent intent = new Intent(this, HelloActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 12345, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notifier = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Title")
                .setContentText("Text")
                .setSmallIcon(android.R.drawable.sym_action_email)
                .setContentIntent(pending)
                .setAutoCancel(true)
                .build();
        notifier.notify(0, notification);

    }
}
