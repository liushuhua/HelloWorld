package com.example.lenovo.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
        findViewById(R.id.root).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.install:
                silentInstallApp();
                break;
            case R.id.process:
                Intent grayIntent = new Intent(getApplicationContext(), GrayService.class);
                startService(grayIntent);
                break;

            case R.id.wifi:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.root:
//                CommandHandler.getInstance().execRootCommand("cat /data/misc/wifi/wpa_supplicant.conf");
//                CommandHandler.getInstance().execRootCommand("pm install -r /sdcard/Download/app-release.apk");
//                CommandHandler.getInstance().execCommand("cat /sys/class/net/wlan0/address");
                break;
        }
    }

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
            if ("success".equals(result.toLowerCase())) {
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
}
