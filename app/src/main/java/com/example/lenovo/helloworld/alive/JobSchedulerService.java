package com.example.lenovo.helloworld.alive;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by LSH on 2018/3/27.
 * description：JobService 用于定时检查,启动WatchDog
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private static final String TAG = "JobSchedulerService";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob: ");
        startService(AliveService.getIntentAlarm(this));
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob: ");
        startService(AliveService.getIntentAlarm(this));
        return false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved: ");
        startService(AliveService.getIntentAlarm(this));
    }
}
