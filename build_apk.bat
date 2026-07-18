@echo off
echo ========================================
echo   虚拟导航按键 - APK 打包脚本
echo ========================================
echo.

REM 检查 Java
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [错误] 未找到 Java，请先安装 JDK 17+
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)

java -version 2>&1 | findstr /i "version"
echo.

REM 检查 Android SDK
set ANDROID_SDK_ROOT=%LOCALAPPDATA%\Android\Sdk
if not exist "%ANDROID_SDK_ROOT%" (
    set ANDROID_SDK_ROOT=%APPDATA%\Local\Android\Sdk
)

if not exist "%ANDROID_SDK_ROOT%\platform-tools\adb.exe" (
    echo [错误] 未找到 Android SDK
    echo 请先安装 Android Studio 或 Android SDK Command-line Tools
    echo.
    echo 或者使用以下方法手动打包:
    echo 1. 下载 Android Studio: https://developer.android.com/studio
    echo 2. 安装后 SDK 在: %APPDATA%\Local\Android\Sdk
    echo 3. 然后重新运行此脚本
    pause
    exit /b 1
)

echo [√] Java 已安装
echo [√] Android SDK 路径: %ANDROID_SDK_ROOT%
echo.

REM 进入项目目录
cd /d "%~dp0"

REM 检查 Gradle
if exist "gradlew.bat" (
    echo [1/3] 正在编译...
    call gradlew.bat assembleDebug
) else (
    echo [1/3] 使用系统 Gradle 编译...
    gradle assembleDebug
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo [错误] 编译失败
    echo.
    echo 如果编译失败，请:
    echo 1. 安装 Android Studio: https://developer.android.com/studio
    echo 2. 用 Android Studio 打开本项目
    echo 3. Build → Build Bundle(s) / APK(s) → Build APK(s)
    pause
    exit /b 1
)

echo.
echo [2/3] 编译完成!
echo.

REM 查找 APK
if exist "app\build\outputs\apk\debug\" (
    echo [3/3] APK 文件位置:
    dir /b "app\build\outputs\apk\debug\*.apk"
    echo.
    echo 完整路径: app\build\outputs\apk\debug\
    echo.
    echo 可以通过以下方式安装到手机:
    echo 1. USB 连接手机，运行: adb install -r app\build\outputs\apk\debug\*.apk
    echo 2. 或直接将 APK 文件复制到手机安装
) else (
    echo [警告] 未找到 APK 文件
    echo 编译输出可能在其他位置
)

echo.
echo ========================================
echo   打包完成!
echo ========================================
pause
