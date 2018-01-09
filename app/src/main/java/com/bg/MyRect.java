/**
 * 
 */
package com.bg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Himi
 * 
 */
public class MyRect {
	// ����ͼ�ε�ͼƬ
	private Bitmap bmp;
	// ����ͼ�ε�����
	private float x, y;
	/**
	 * ����ͼƬ���캯��
	 * @param bmp ��ԴͼƬ
	 * @param x	����ͼ��X����
	 * @param y	����ͼ��Y����
	 * @param r	����ͼ�ΰ뾶
	 */
	public MyRect(Bitmap bmp, float x, float y) {
		this.bmp = bmp;
		this.x = x;
		this.y = y;
	}
	/**
	 * ����ͼ�λ��ƺ���
	 * @param canvas ����ʵ��
	 * @param paint	����ʵ��
	 */
	public void drawMyRect(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bmp, x, y, paint);
	}
	// ���þ���ͼ�ε�X����
	public void setX(float x) {
		this.x = x;
	}
	//���þ���ͼ�ε�Y����
	public void setY(float y) {
		this.y = y;
	}
	//��ȡ����ͼ�εĿ�
	public int getW() {
		return bmp.getWidth();
	}
	//��ȡ����ͼ�εĸ�
	public int getH() {
		return bmp.getHeight();
	}
}
