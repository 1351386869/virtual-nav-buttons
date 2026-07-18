package com.example.virtualnav;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.core.app.NotificationCompat;

/**
 * 虚拟导航按键服务
 * 在屏幕底部显示三个按键：返回、主页、最近任务
 */
public class NavService extends Service {

    private static final String CHANNEL_ID = "nav_service_channel";
    private WindowManager windowManager;
    private View navBarView;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundService();
        showNavBar();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideNavBar();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "虚拟导航按键",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("显示底部虚拟导航按键");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("虚拟导航按键")
                .setContentText("运行中 - 点击关闭")
                .setSmallIcon(android.R.drawable.ic_menu_call)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, notification);
        }
    }

    private void showNavBar() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 加载布局
        navBarView = LayoutInflater.from(this).inflate(
                R.layout.nav_bar_layout, null);

        // 窗口参数 - 固定在屏幕底部
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;
        final int navHeight = (int) (screenHeight * 0.06f); // 6%屏幕高度

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                screenWidth,
                navHeight,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                android.graphics.PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM;

        windowManager.addView(navBarView, params);

        // 绑定按钮事件
        ImageButton btnBack = navBarView.findViewById(R.id.btnBack);
        ImageButton btnHome = navBarView.findViewById(R.id.btnHome);
        ImageButton btnRecent = navBarView.findViewById(R.id.btnRecent);

        btnBack.setOnClickListener(v -> simulateBack());
        btnHome.setOnClickListener(v -> simulateHome());
        btnRecent.setOnClickListener(v -> simulateRecent());

        // 点击通知关闭服务
        navBarView.findViewById(R.id.btnClose).setOnClickListener(v -> {
            stopSelf();
            finish();
        });
    }

    private void hideNavBar() {
        if (windowManager != null && navBarView != null) {
            windowManager.removeView(navBarView);
        }
    }

    /**
     * 模拟返回键
     */
    private void simulateBack() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 使用 sendKeyEvent API
                android.view.KeyEvent backEvent = new android.view.KeyEvent(
                        0, 0, android.view.KeyEvent.ACTION_DOWN,
                        android.view.KeyEvent.KEYCODE_BACK, 0, 0, 0, 0,
                        android.view.KeyEvent.FLAG_FROM_SYSTEM);
                android.view.KeyEvent backUpEvent = new android.view.KeyEvent(
                        0, 0, android.view.KeyEvent.ACTION_UP,
                        android.view.KeyEvent.KEYCODE_BACK, 0, 0, 0, 0,
                        android.view.KeyEvent.FLAG_FROM_SYSTEM);

                // 尝试通过 InputManager 发送
                android.hardware.input.InputManager inputManager =
                        android.hardware.input.InputManager.getInstance();
                if (inputManager != null) {
                    long now = SystemClock.uptimeMillis();
                    inputManager.injectInputEvent(backEvent, 0);
                    inputManager.injectInputEvent(backUpEvent, 0);
                }
            } else {
                // 低版本尝试通过反射
                injectKeyEvent(android.view.KeyEvent.KEYCODE_BACK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟主页键
     */
    private void simulateHome() {
        try {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟最近任务键
     */
    private void simulateRecent() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.hardware.input.InputManager inputManager =
                        android.hardware.input.InputManager.getInstance();
                if (inputManager != null) {
                    android.view.KeyEvent recentDown = new android.view.KeyEvent(
                            0, 0, android.view.KeyEvent.ACTION_DOWN,
                            android.view.KeyEvent.KEYCODE_APP_SWITCH, 0, 0, 0, 0,
                            android.view.KeyEvent.FLAG_FROM_SYSTEM);
                    android.view.KeyEvent recentUp = new android.view.KeyEvent(
                            0, 0, android.view.KeyEvent.ACTION_UP,
                            android.view.KeyEvent.KEYCODE_APP_SWITCH, 0, 0, 0, 0,
                            android.view.KeyEvent.FLAG_FROM_SYSTEM);
                    inputManager.injectInputEvent(recentDown, 0);
                    inputManager.injectInputEvent(recentUp, 0);
                }
            } else {
                injectKeyEvent(android.view.KeyEvent.KEYCODE_APP_SWITCH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射注入按键事件
     */
    private void injectKeyEvent(int keyCode) {
        try {
            Class<?> inputManagerClass = Class.forName("android.hardware.input.InputManager");
            java.lang.reflect.Method getInstanceMethod = inputManagerClass.getMethod("getInstance");
            Object inputManager = getInstanceMethod.invoke(null);
            java.lang.reflect.Method injectInputEventMethod = inputManagerClass.getMethod(
                    "injectInputEvent",
                    android.view.KeyEvent.class,
                    int.class);

            long now = SystemClock.uptimeMillis();

            android.view.KeyEvent down = new android.view.KeyEvent(
                    now, now,
                    android.view.KeyEvent.ACTION_DOWN,
                    keyCode, 0, 0, 0, 0,
                    android.view.KeyEvent.FLAG_FROM_SYSTEM);

            android.view.KeyEvent up = new android.view.KeyEvent(
                    now, now,
                    android.view.KeyEvent.ACTION_UP,
                    keyCode, 0, 0, 0, 0,
                    android.view.KeyEvent.FLAG_FROM_SYSTEM);

            injectInputEventMethod.invoke(inputManager, down, 0);
            injectInputEventMethod.invoke(inputManager, up, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭服务
     */
    private void finish() {
        stopForeground(true);
        stopSelf();
    }
}
