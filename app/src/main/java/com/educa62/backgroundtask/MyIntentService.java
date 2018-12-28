package com.educa62.backgroundtask;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class MyIntentService extends IntentService {
    private static final String ACTION_FROM_ACTIVITY = "com.educa62.backgroundtask.action.ACT";
    private static final String ACTION_FROM_JOB_SERVICE = "com.educa62.backgroundtask.action.JS";
    private static final String ACTION_FROM_JOB_FIREBASE_DISPATCHER = "com.educa62.backgroundtask.action.JFD";

    private static final String EXTRA_WORD1 = "com.educa62.backgroundtask.extra.WORD1";
    private static final String EXTRA_WORD2 = "com.educa62.backgroundtask.extra.WORD2";

    public MyIntentService() {
        super("MyIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFromActivity(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FROM_ACTIVITY);
        intent.putExtra(EXTRA_WORD1, param1);
        intent.putExtra(EXTRA_WORD2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionJobService(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FROM_JOB_SERVICE);
        intent.putExtra(EXTRA_WORD1, param1);
        intent.putExtra(EXTRA_WORD2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionJobFirebaseDispatcher(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FROM_JOB_FIREBASE_DISPATCHER);
        intent.putExtra(EXTRA_WORD1, param1);
        intent.putExtra(EXTRA_WORD2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FROM_ACTIVITY.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_WORD1);
                final String param2 = intent.getStringExtra(EXTRA_WORD2);
                handleActionFromActivity(param1, param2);
            } else if (ACTION_FROM_JOB_SERVICE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_WORD1);
                final String param2 = intent.getStringExtra(EXTRA_WORD2);
                handleActionFromJobService(param1, param2);
            } else if (ACTION_FROM_JOB_FIREBASE_DISPATCHER.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_WORD1);
                final String param2 = intent.getStringExtra(EXTRA_WORD2);
                handleActionFromJobFirebaseDispatcher(param1, param2);
            }
        }
    }

    /**
     * Handle action JobFirebaseDispatcher in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFromJobFirebaseDispatcher(String param1, String param2) {
        // Dikarenakan IntentService ini berjalan di worker thread, untuk memunculkan Toast atau mengubah UI,
        // harus dijalankan di main thread.
        sendBroadcastMessage(String.format("%s %s, from handleActionFromJobFirebaseDispatcher", param1, param2));
    }

    /**
     * Handle action FromActivity in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFromActivity(final String param1, final String param2) {
        // Dikarenakan IntentService ini berjalan di worker thread, untuk memunculkan Toast atau mengubah UI,
        // harus dijalankan di main thread.
        sendBroadcastMessage(String.format("%s %s, from handleActionFromActivity", param1, param2));
    }

    /**
     * Handle action JobService in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFromJobService(String param1, String param2) {
        // Dikarenakan IntentService ini berjalan di worker thread, untuk memunculkan Toast atau mengubah UI,
        // harus dijalankan di main thread.
        sendBroadcastMessage(String.format("%s %s, from handleActionFromJobService", param1, param2));
    }

    private void sendBroadcastMessage(String message) {
        // Kirim message ke main thread menggunakan LocalBroadcastManager.
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent localIntent = new Intent(MainActivity.BROADCAST_ACTION);
        localIntent.putExtra("message", message);
        localBroadcastManager.sendBroadcast(localIntent);
    }
}
