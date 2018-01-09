package com.bg;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	//声明一个主类（便于使用）
	public static MainActivity main;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//实例本类
		main = this;
		//设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//去除应用程序标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//显示自定义MySurfaceView实例
		setContentView(new MySurfaceView(this));
	}
	//添加一个程序退出方法
	public void exit() {
		//退出应用程序
		System.exit(0);
	}
}
