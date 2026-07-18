#!/bin/bash
# Termux 安装和运行脚本
# 在 Termux 终端中粘贴运行

echo "📱 虚拟导航按键 - Termux 安装脚本"
echo ""

# 安装 termux-api
echo "1/3 安装 termux-api..."
pkg install -y termux-api

# 安装 python-tk
echo "2/3 安装 Python Tkinter..."
pkg install -y python tkinter

# 运行
echo "3/3 启动虚拟导航按键..."
echo ""
python3 nav_termux.py
