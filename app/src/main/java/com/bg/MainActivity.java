package com.bg;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	//����һ�����ࣨ����ʹ�ã�
	public static MainActivity main;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ʵ������
		main = this;
		//����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//ȥ��Ӧ�ó������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//��������
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//��ʾ�Զ���MySurfaceViewʵ��
		setContentView(new MySurfaceView(this));
	}
	//���һ�������˳�����
	public void exit() {
		//�˳�Ӧ�ó���
		System.exit(0);
	}
}
