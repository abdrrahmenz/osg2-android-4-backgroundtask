package com.educa62.backgroundtask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MyJobFirebaseDispatcher extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        // lakukan background task atau memanggil service
        MyIntentService.startActionJobFirebaseDispatcher(this, "hello", "world");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
