@echo off
chcp 65001 >nul
echo ============================================
echo   虚拟导航按键 - 一键安装到手机
echo ============================================
echo.

set ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk
if not exist "%ANDROID_SDK_ROOT%" (
    set ANDROID_SDK_ROOT=%APPDATA%\Local\Android\Sdk
)

set ADB="%ANDROID_SDK_ROOT%\platform-tools\adb.exe"

echo 检查 ADB:
if exist %ADB% (
    echo [√] ADB 已找到
) else (
    echo [×] 未找到 ADB，请先安装 Android SDK
    echo     下载地址: https://developer.android.com/studio
    pause
    exit /b 1
)

echo.
echo 查找手机设备...
%ADB% devices

echo.
echo 正在安装 APK...
for %%f in (app\build\outputs\apk\debug\*.apk) do (
    echo 安装: %%f
    %ADB% install -r "%%f"
)

echo.
echo 安装完成!
echo 在手机应用列表中找到"虚拟导航"并打开
echo 授予悬浮窗权限后即可使用
echo.
pause
