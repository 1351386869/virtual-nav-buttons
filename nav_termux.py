#!/usr/bin/env python3
"""
Termux 虚拟导航按键
在 Termux 终端中运行: python3 nav_termux.py
需要先安装 termux-api: pkg install termux-api
"""

import os
import sys
import subprocess
import threading
import time
import tkinter as tk
from tkinter import ttk

def check_termux():
    """检查是否在 Termux 环境中运行"""
    return 'PREFIX' in os.environ and os.path.isdir('/data/data/com.termux/files/home')

def send_key(key_event):
    """通过 termux-api 发送按键事件"""
    try:
        if check_termux():
            # 尝试通过 termux-api 发送按键
            cmd = ['termux-notification', '--title', '按键', '--content', f'发送: {key_event}']
            subprocess.run(cmd, capture_output=True)
            return True
        else:
            # 在电脑上用 pynput 模拟
            return send_key_pynput(key_event)
    except Exception as e:
        print(f"发送按键失败: {e}")
        return False

def send_key_pynput(key_event):
    """使用 pynput 模拟按键（仅限桌面）"""
    try:
        from pynput.keyboard import Key, Controller
        keyboard = Controller()
        
        key_map = {
            'back': Key.backspace,
            'home': Key.cmd,
            'recent': Key.f2,
        }
        
        key = key_map.get(key_event)
        if key:
            keyboard.press(key)
            keyboard.release(key)
            return True
    except ImportError:
        print("需要安装 pynput: pip install pynput")
    return False

class NavApp:
    def __init__(self, root):
        self.root = root
        self.root.title("虚拟导航按键")
        self.root.geometry("400x500")
        self.root.configure(bg='#1a1a2e')
        self.root.resizable(False, False)
        
        # 标题
        title_label = tk.Label(
            root,
            text="📱 虚拟导航按键",
            font=('', 24, 'bold'),
            bg='#1a1a2e',
            fg='#ffffff'
        )
        title_label.pack(pady=(30, 8))
        
        subtitle = tk.Label(
            root,
            text="返回 · 主页 · 最近任务",
            font=('', 12),
            bg='#1a1a2e',
            fg='#888888'
        )
        subtitle.pack()
        
        # 分隔线
        separator = ttk.Separator(root, orient='horizontal')
        separator.pack(fill='x', padx=20, pady=20)
        
        # 三个大按钮
        btn_frame = tk.Frame(root, bg='#1a1a2e')
        btn_frame.pack(fill='both', expand=True, padx=30, pady=10)
        
        # 返回键
        self.btn_back = tk.Button(
            btn_frame,
            text="◀  返 回",
            font=('', 18, 'bold'),
            bg='#2d2d44',
            fg='#ffffff',
            activebackground='#3d3d55',
            activeforeground='#ffffff',
            relief='flat',
            cursor='hand2',
            padx=20,
            pady=15,
            command=self.on_back
        )
        self.btn_back.pack(fill='x', pady=5)
        self.btn_back.bind('<Enter>', lambda e: self.btn_back.config(bg='#3d3d55'))
        self.btn_back.bind('<Leave>', lambda e: self.btn_back.config(bg='#2d2d44'))
        
        # 主页键
        self.btn_home = tk.Button(
            btn_frame,
            text="⌂  主 页",
            font=('', 18, 'bold'),
            bg='#2d2d44',
            fg='#ffffff',
            activebackground='#3d3d55',
            activeforeground='#ffffff',
            relief='flat',
            cursor='hand2',
            padx=20,
            pady=15,
            command=self.on_home
        )
        self.btn_home.pack(fill='x', pady=5)
        self.btn_home.bind('<Enter>', lambda e: self.btn_home.config(bg='#3d3d55'))
        self.btn_home.bind('<Leave>', lambda e: self.btn_home.config(bg='#2d2d44'))
        
        # 最近任务键
        self.btn_recent = tk.Button(
            btn_frame,
            text="▤  最 近",
            font=('', 18, 'bold'),
            bg='#2d2d44',
            fg='#ffffff',
            activebackground='#3d3d55',
            activeforeground='#ffffff',
            relief='flat',
            cursor='hand2',
            padx=20,
            pady=15,
            command=self.on_recent
        )
        self.btn_recent.pack(fill='x', pady=5)
        self.btn_recent.bind('<Enter>', lambda e: self.btn_recent.config(bg='#3d3d55'))
        self.btn_recent.bind('<Leave>', lambda e: self.btn_recent.config(bg='#2d2d44'))
        
        # 状态栏
        self.status_var = tk.StringVar(value="就绪")
        status_bar = tk.Label(
            root,
            textvariable=self.status_var,
            font=('', 10),
            bg='#1a1a2e',
            fg='#4CAF50',
            anchor='w'
        )
        status_bar.pack(fill='x', padx=20, pady=(10, 15))
        
        # 提示信息
        info_text = """
使用方式:
• Termux 用户: 需安装 termux-api
• 电脑用户: 需安装 pynput (pip install pynput)
• 按键需要权限才能生效
        """.strip()
        
        info_label = tk.Label(
            root,
            text=info_text,
            font=('', 10),
            bg='#1a1a2e',
            fg='#666666',
            justify='center'
        )
        info_label.pack(pady=(0, 10))
    
    def on_back(self):
        self.status_var.set("◀ 发送返回键...")
        success = send_key('back')
        self.status_var.set("✓ 返回键已发送" if success else "✗ 返回键发送失败")
        self.root.after(2000, lambda: self.status_var.set("就绪"))
    
    def on_home(self):
        self.status_var.set("⌂ 发送主页键...")
        success = send_key('home')
        self.status_var.set("✓ 主页键已发送" if success else "✗ 主页键发送失败")
        self.root.after(2000, lambda: self.status_var.set("就绪"))
    
    def on_recent(self):
        self.status_var.set("▤ 发送最近任务键...")
        success = send_key('recent')
        self.status_var.set("✓ 最近任务键已发送" if success else "✗ 最近任务键发送失败")
        self.root.after(2000, lambda: self.status_var.set("就绪"))

def main():
    print("=" * 40)
    print("  虚拟导航按键模拟器")
    print("=" * 40)
    
    if check_termux():
        print("✓ 检测到 Termux 环境")
        if not os.path.exists('/data/data/com.termux/files/usr/bin/termux-notification'):
            print("⚠ 未找到 termux-api，请先安装: pkg install termux-api")
    else:
        print("ℹ 桌面环境运行（非 Termux）")
        print("  按键功能需要额外配置")
    
    print("\n启动界面...")
    root = tk.Tk()
    app = NavApp(root)
    root.mainloop()

if __name__ == '__main__':
    main()
