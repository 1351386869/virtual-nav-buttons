# Git 初始化并推送 GitHub Actions 自动打包

## 1. 初始化 Git 仓库

在项目目录运行：
```bash
cd C:\Users\Administrator\clawd\pool_b\projects\android-nav-buttons
git init
git add .
git commit -m "Initial commit - Virtual Navigation Buttons"
```

## 2. 创建 GitHub 仓库

1. 打开 https://github.com/new
2. 仓库名随便填（比如 `virtual-nav-buttons`）
3. 设为 **Public**（公开）
4. 不要勾选 "Initialize with README"
5. 点 Create repository

## 3. 推送代码

GitHub 会给你一个地址，类似：
```
https://github.com/你的用户名/virtual-nav-buttons.git
```

在你的项目目录运行：
```bash
git remote add origin https://github.com/你的用户名/virtual-nav-buttons.git
git branch -M main
git push -u origin main
```

## 4. 触发自动打包

推送成功后，GitHub Actions 会自动开始编译。

你也可以手动触发：
1. 打开仓库页面
2. 点 "Actions" 标签
3. 点 "Build APK" 工作流
4. 点 "Run workflow" → 选 main 分支 → Run workflow

## 5. 下载 APK

编译完成后：
1. 点 "Actions" → 选最新的 workflow run
2. 左侧找到 "artifacts" 区域
3. 点击 `app-debug-apk.zip` 下载
4. 解压后得到 `app-debug.apk`
5. 复制到手机安装即可

## 注意事项

- 编译大概需要 5-10 分钟（首次要下载依赖）
- APK 会在 GitHub 保留 30 天
- 全程免费，不需要自己的电脑
- 每次推送代码都会自动重新编译
