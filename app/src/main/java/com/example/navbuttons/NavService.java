package com.example.navbuttons;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import androidx.core.app.NotificationCompat;

/**
 * 悬浮导航按键服务
 * 在屏幕底部显示返回/主页/最近三个虚拟按键
 */
public class NavService extends Service {

    private static final String CHANNEL_ID = "nav_buttons_channel";
    private WindowManager windowManager;
    private ViewGroup buttonContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingButtons();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatingButtons();
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
                .setContentText("点击按键模拟导航操作")
                .setSmallIcon(android.R.drawable.ic_menu_rotate)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, notification);
        }
    }

    private void showFloatingButtons() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 创建按钮容器
        buttonContainer = new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft = 0;
                int width = (r - l) / 3;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() == View.GONE) continue;
                    child.layout(childLeft, 0, childLeft + width, b - t);
                    childLeft += width;
                }
            }
        };

        // 窗口参数 - 固定在屏幕底部
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int buttonHeight = (int) (screenWidth * 0.08f); // 高度为屏幕宽度的8%
        final int buttonWidth = screenWidth / 3;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                screenWidth,
                buttonHeight,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                android.graphics.PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM;
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;

        windowManager.addView(buttonContainer, params);

        // 创建返回键
        Button btnBack = createButton("← 返回", Color.parseColor("#333333"), Color.WHITE);
        btnBack.setOnClickListener(v -> simulateBack());
        buttonContainer.addView(btnBack);

        // 创建主页键
        Button btnHome = createButton("⌂ 主页", Color.parseColor("#333333"), Color.WHITE);
        btnHome.setOnClickListener(v -> simulateHome());
        buttonContainer.addView(btnHome);

        // 创建最近任务键
        Button btnRecent = createButton("☰ 最近", Color.parseColor("#333333"), Color.WHITE);
        btnRecent.setOnClickListener(v -> simulateRecent());
        buttonContainer.addView(btnRecent);
    }

    private Button createButton(String text, int bgColor, int textColor) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(bgColor);
        button.setTextColor(textColor);
        button.setTextSize(14);
        button.setPadding(0, 0, 0, 0);
        button.setAllCaps(false);
        button.setAlpha(0.85f);
        return button;
    }

    private void removeFloatingButtons() {
        if (windowManager != null && buttonContainer != null) {
            windowManager.removeView(buttonContainer);
        }
    }

    /**
     * 模拟返回键
     */
    private void simulateBack() {
        try {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            home.addCategory(Intent.CATEGORY_HOME);
            // 先回桌面再回来模拟返回效果
            startActivity(home);
            
            // 使用输入事件模拟返回键
            injectKeyEvent(android.view.KeyEvent.KEYCODE_BACK);
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
            injectKeyEvent(android.view.KeyEvent.KEYCODE_APP_SWITCH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注入键盘事件（需要系统签名或root）
     */
    private void injectKeyEvent(int keyCode) {
        try {
            long now = SystemClock.uptimeMillis();
            
            // 按下
            android.view.KeyEvent down = new android.view.KeyEvent(
                    now, now,
                    android.view.KeyEvent.ACTION_DOWN,
                    keyCode, 0,
                    0, android.view.KeyCharacterMap.VIRTUAL_KEYBOARD,
                    0, 0,
                    android.view.InputDevice.SOURCE_KEYBOARD);
            
            // 抬起
            android.view.KeyEvent up = new android.view.KeyEvent(
                    now, now,
                    android.view.KeyEvent.ACTION_UP,
                    keyCode, 0,
                    0, android.view.KeyCharacterMap.VIRTUAL_KEYBOARD,
                    0, 0,
                    android.view.InputDevice.SOURCE_KEYBOARD);

            android.view.KeyEvent.dispatch(down, this.getWindowManager(), 0);
            android.view.KeyEvent.dispatch(up, this.getWindowManager(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
