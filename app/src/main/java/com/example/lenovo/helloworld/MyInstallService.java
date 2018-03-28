package com.example.lenovo.helloworld;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
/**
 * Created by LSH on 2018/3/27.
 * description：无障碍服务——自动安装
 */
public class MyInstallService extends AccessibilityService {
    private static final String TAG = "MyInstallService";
    private static final String CONSTANCE = "packageinstaller";
    public static volatile MyInstallService installService;

    public static MyInstallService getInstance() {
        if (installService == null) {
            synchronized (MyInstallService.class) {
                if (installService == null) {
                    installService = new MyInstallService();
                }
            }
        }
        return installService;
    }

    private Context mContext;
    private AccessibilityManager mAccessibilityManager;

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @Override
    protected void onServiceConnected() {

        Log.i(TAG, "onServiceConnected: ");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "onAccessibilityEvent: " + event.getPackageName());
        //获取安装程序的包名,统一转换成小写规范不同手机厂商
        String packageName = event.getPackageName().toString().toLowerCase();
        //判断type一致，当前程序为安装程序
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && packageName.endsWith(CONSTANCE)) {
            final AccessibilityNodeInfo nodeInfo = findViewByText("安装", true);
            if (nodeInfo != null) {
                Log.e(TAG, "onAccessibilityEvent: " + nodeInfo.getText());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performViewClick(nodeInfo);
                    }
                }, 5000);

            }
        } else if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == event.getEventType() && packageName.endsWith(CONSTANCE)) {
            final AccessibilityNodeInfo nodeInfo1 = findViewByText("打开", true);
            if (nodeInfo1 != null) {
                performViewClick(nodeInfo1);
            }
        }
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text) {
        return findViewByText(text, false);
    }

    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }

    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    private boolean checkAccessibilityEnabled(String serviceName) {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 前往开启辅助服务界面
     */
    public void goAccess() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void onInterrupt() {

    }
}
