package com.example.lenovo.helloworld.alive;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.lenovo.helloworld.BuildConfig;

/**
 * Created by LSH on 2018/3/27.
 * description：包活进程
 */

public class AliveService extends Service {
    //每2分钟检查一次链接状态，确保service不被杀掉
    private static final int KEEP_ALIVE_INTERVAL = BuildConfig.DEBUG ? 60 * 1000 : 2 * 60 * 1000;
    //不同Action
    private static final String ACTION_START = "AliveService.Action.Start";
    private static final String ACTION_ALARM = "AliveService.Action.Alarm";
    private static final String ACTION_END_START = "AliveService.Action.EndStart";
    //服务的id
    private static final int SERVICE_ID = -1001;
    private boolean mIsAddAliveAlarm = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (!mIsAddAliveAlarm) {
            addAliveAlarm();
            mIsAddAliveAlarm = true;
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
//        if (null != pIntent) {
//            switch (pIntent.getAction()) {
//                case ACTION_START:
//
//                    break;
//                case ACTION_ALARM:
//
//                    break;
//                case ACTION_END_START:
//
//                    break;
//            }
//        }
        if (!mIsAddAliveAlarm) {
            addAliveAlarm();
            mIsAddAliveAlarm = true;
        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent pIntent) {
        onEnd();
    }

    @Override
    public void onDestroy() {
        onEnd();
        super.onDestroy();
    }

    private void onEnd() {
        startService(getIntentEndStart(getApplicationContext()));
    }


    /**
     * 添加重复唤醒闹钟，用于不停唤起服务
     */
    private void addAliveAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//android5.0以上用JobScheduler比AlarmManager更高效
            JobInfo.Builder builder = new JobInfo.Builder(SERVICE_ID, new ComponentName(getApplication(), JobSchedulerService.class));
            builder.setPeriodic(KEEP_ALIVE_INTERVAL);
            builder.setRequiresCharging(true);
            builder.setPersisted(true);
            builder.setRequiresDeviceIdle(true);
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            if (jobScheduler == null) return;
            jobScheduler.schedule(builder.build());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(SERVICE_ID, new Notification());
        } else {
            PendingIntent pendingIntent = PendingIntent.getService(this, SERVICE_ID, getIntentAlarm(this), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager == null) return;
            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pendingIntent);
        }
    }

    /**
     * Start
     *
     * @param pContext
     */
    public static Intent getIntentStart(Context pContext) {
        return getActionIntent(pContext, ACTION_START);
    }

    /**
     * Alarm
     *
     * @param pContext
     */
    public static Intent getIntentAlarm(Context pContext) {
        return getActionIntent(pContext, ACTION_ALARM);
    }

    /**
     * EndStart
     *
     * @param pContext
     */
    public static Intent getIntentEndStart(Context pContext) {
        return getActionIntent(pContext, ACTION_END_START);
    }

    /**
     * Service Intent
     *
     * @param pContext
     * @param pAction
     * @return
     */
    private static Intent getActionIntent(Context pContext, String pAction) {
        Intent intent = new Intent(pContext, AliveService.class);
        intent.setAction(pAction);
        return intent;
    }
}
