package com.example.navbuttons;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 主界面 - 控制服务的启动和停止
 */
public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnCheckPerm = findViewById(R.id.btnCheckPerm);

        btnStart.setOnClickListener(v -> {
            if (checkOverlayPermission()) {
                startService(new Intent(this, NavService.class));
                Toast.makeText(this, "虚拟按键已启动", Toast.LENGTH_SHORT).show();
            } else {
                requestOverlayPermission();
            }
        });

        btnStop.setOnClickListener(v -> {
            stopService(new Intent(this, NavService.class));
            Toast.makeText(this, "虚拟按键已关闭", Toast.LENGTH_SHORT).show();
        });

        btnCheckPerm.setOnClickListener(v -> {
            if (checkOverlayPermission()) {
                Toast.makeText(this, "已有悬浮窗权限 ✓", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "暂无悬浮窗权限 ✗", Toast.LENGTH_SHORT).show();
                requestOverlayPermission();
            }
        });
    }

    private boolean checkOverlayPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Settings.canDrawOverlays(this)
                : true;
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(this, "请在设置中允许悬浮窗权限", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (checkOverlayPermission()) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
