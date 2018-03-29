package com.example.lenovo.helloworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.lenovo.helloworld.alive.GrayService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LSH on 2018/3/27.
 * description：测试用MainActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.install).setOnClickListener(this);
        findViewById(R.id.process).setOnClickListener(this);
        findViewById(R.id.wifi).setOnClickListener(this);
        findViewById(R.id.command).setOnClickListener(this);
        findViewById(R.id.mobile).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.install:
                installApp();
                break;
            case R.id.process:
                Intent grayIntent = new Intent(getApplicationContext(), GrayService.class);
                startService(grayIntent);
                break;
            case R.id.wifi:
                openWifi();
                break;
            case R.id.command:
//                CommandHandler.getInstance().execRootCommand("cat /data/misc/wifi/wpa_supplicant.conf");
//                CommandHandler.getInstance().execCommand("cat /sys/class/net/wlan0/address");
                break;
            case R.id.mobile:
                openMobileData();
                break;
        }
    }

    /**
     * app安装,耗时操作启动线程执行
     */
    @SuppressLint("StaticFieldLeak")
    private void installApp() {
        new AsyncTask<String, String, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                return silentInstallApp();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    Log.e(TAG, "onPostExecute: 成功");
                }
            }
        }.execute();
    }

    /**
     * root权限下的静默安装
     *
     * @return 安装状态
     */
    private boolean silentInstallApp() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"su", "-c", "pm install -r /sdcard/Download/app-release.apk"});
            process.waitFor();
            InputStream inputStream = process.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            while ((read = inputStream.read()) != -1) {
                byteArrayOutputStream.write(read);
            }
            byte[] data = byteArrayOutputStream.toByteArray();
            String result = new String(data);
            Log.i(TAG, "execRootCommand: " + result);
            inputStream.close();
            byteArrayOutputStream.close();
            if (result.toLowerCase().contains("success")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }


    /**
     * 打开Wifi开关
     */
    public void openWifi() {
        WifiManager wm = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null && !wm.isWifiEnabled()) {
            wm.setWifiEnabled(true);
        }

    }

    /**
     * 打开手机数据开关,root为前提
     */
    public void openMobileData() {
        CommandHandler.getInstance().execRootCommand("svc data enable");
    }
}
