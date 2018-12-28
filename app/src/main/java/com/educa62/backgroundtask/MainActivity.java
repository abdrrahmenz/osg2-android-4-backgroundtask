package com.educa62.backgroundtask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String BROADCAST_ACTION = "BROADCAST_ACTION";

    private ProgressDialog dialog;

    /**
     * broadcast receiver untuk menerima broadcast dari service
     */
    private BroadcastReceiver myBroadCastReceiver = new MyBroadCastReceiver();

    /**
     * membuat alarm manager dan pendingIntent untuk diinisialisasi diOnCreate
     */
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    /**
     * membuat firebaseJobdispatcher untuk diinisialisasi diOnCreate
     */
    private FirebaseJobDispatcher dispatcher ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(this, MyAlarmManager.class), 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        findViewById(R.id.btnThread).setOnClickListener(this);
        findViewById(R.id.btnAsyncTask).setOnClickListener(this);
        findViewById(R.id.btnScheduler).setOnClickListener(this);
        findViewById(R.id.btnIntentService).setOnClickListener(this);
        findViewById(R.id.btnService).setOnClickListener(this);
        findViewById(R.id.btnFirebaseJobDispathcer).setOnClickListener(this);
        findViewById(R.id.btnAlarm).setOnClickListener(this);

        registerMyReceiver();
    }

    /**
     * This method is responsible to register an action to BroadCastReceiver
     */
    private void registerMyReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadCastReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy() {
        // unregister broadcast receiver agar tidak terjadi leak (kebocoran) memory
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadCastReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnThread:
                runThread();
                break;
            case R.id.btnAsyncTask:
                runAsyncTask();
                break;
            case R.id.btnScheduler:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    runJobScheduler();
                }
                break;
            case R.id.btnIntentService:
                runIntentService();
                break;
            case R.id.btnService:
                runService();
                break;
            case R.id.btnFirebaseJobDispathcer:
                runJobDispatcher();
                break;
            case R.id.btnAlarm:
                runAlarm();
                break;

            default:

        }
    }

    /**
     * Menjalankan Firebase Job Dispatcher
     */
    private void runJobDispatcher() {
        // TODO panggil Firebase Job Dispatcher untuk menggantikan Job Scheduler
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobFirebaseDispatcher.class) // the JobService that will be called
                .setTag("my-unique-tag")        // uniquely identifies the job
                .build();

        dispatcher.mustSchedule(myJob);
    }

    /**
     * Menjalankan Alarm Manager
     */
    private void runAlarm() {
        // TODO panggil AlarmManager untuk memunculkan Toast dengan waktu tunggu 2 detik
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000,pendingIntent );
    }

    /**
     * Menjalankan worker thread menggunakan Runnable
     */
    private void runThread() {
        // munculkan dialog sebelum thread dijalankan
        dialog.show();

        new Thread(new Runnable() {
            // berjalan di worker thread
            @Override
            public void run() {
                // simulasi proses dengan menunggu 3 detik
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // jalankan aksi di ui thread karena harus mengubah UI (dialog)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        }).start();
    }

    /**
     * Menjalankan AsyncTask
     */
    private void runAsyncTask() {
        new MyAsyncTask(dialog).execute();
    }

    /**
     * Menjalankan JobScheduler
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void runJobScheduler() {
        ComponentName serviceComponent = new ComponentName(this, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());

    }

    /**
     * Menjalankan IntentService
     */
    private void runIntentService() {
        MyIntentService.startActionFromActivity(this, "hello", "world");
    }

    private void runService() {
        Intent intent = new Intent(this, MyBoundService.class);
        startService(intent);
    }

    /**
     * Class turunan dari AsyncTask
     */
    static class MyAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ProgressDialog dialog;

        MyAsyncTask(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        // fungsi ini berjalan di ui thread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        // fungsi ini berjalan di ui thread
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }

    /**
     * MyBroadCastReceiver is responsible to receive broadCast from register action
     */
    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // ambil nilai message dari intent extra
            String message = intent.getStringExtra("message");

            // jika message tidak kosong, tampilkan dalam Toast
            if (!TextUtils.isEmpty(message)) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
