package com.example.lenovo.helloworld;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lenovo.helloworld.alive.AliveService;
import com.example.lenovo.helloworld.alive.GrayService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LSH on 2018/3/27.
 * description：测试用MainActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //请求权限的返回码
    private final static int PERMISSION_REQUEST_CODE = 0x8001;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.install).setOnClickListener(this);
        findViewById(R.id.process).setOnClickListener(this);
        findViewById(R.id.wifi).setOnClickListener(this);
        findViewById(R.id.root).setOnClickListener(this);
//        startService(AliveService.getIntentStart(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.install:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    installApp();
                }
//                silentInstallApp();
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
//                SystemManager.getInstance().execRootCommand("cat /data/misc/wifi/wpa_supplicant.conf");
//                SystemManager.getInstance().execRootCommand("pm install -r /sdcard/Download/app-release.apk");
//                SystemManager.getInstance().execCommand("cat /sys/class/net/wlan0/address");
                break;
        }
    }

    /**
     * 普通的安装
     */
    private void installApp() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "app-release.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkUri = FileProvider.getUriForFile(this, this.getPackageName() + ".FileProvider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installApp();
                } else {
                    Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
