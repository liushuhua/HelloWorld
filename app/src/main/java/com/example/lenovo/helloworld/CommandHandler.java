package com.example.lenovo.helloworld;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LSH on 2018/3/29.
 * description：adb Command 处理类
 */

public class CommandHandler {
    private static final String TAG = "CommandHandler";
    public static volatile CommandHandler manager;

    public static CommandHandler getInstance() {
        if (manager == null) {
            synchronized (CommandHandler.class) {
                if (manager == null) {
                    manager = new CommandHandler();
                }
            }
        }
        return manager;
    }

    /**
     * 执行Root权限下的Command
     *
     * @param command 权限
     * @return 执行结果
     */
    public String execRootCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
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
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return "";
    }

    /**
     * 执行普通Command
     *
     * @param command 指令
     * @return 执行结果
     */
    public String execCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            InputStream inputStream = process.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            while ((read = inputStream.read()) != -1) {
                byteArrayOutputStream.write(read);
            }
            byte[] data = byteArrayOutputStream.toByteArray();
            String result = new String(data);
            Log.i(TAG, "execCommand: " + result);
            inputStream.close();
            byteArrayOutputStream.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return "";
    }
}
