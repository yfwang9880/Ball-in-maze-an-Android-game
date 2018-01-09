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
public class MyCircle {
	// Բ�εĿ����뾶
	float x, y, r, angle;
	//Բ��BodyͼƬ��Դ
	private Bitmap bmp;
	/**
	 * Բ��ͼƬ���캯��
	 * @param bmp ��ԴͼƬ
	 * @param x	Բ��ͼ��X����
	 * @param y	Բ��ͼ��Y����
	 * @param r	Բ��ͼ�ΰ뾶
	 */
	public MyCircle(Bitmap bmp, float x, float y, float r) {
		this.bmp = bmp;
		this.x = x;
		this.y = y;
		this.r = r;
	}
	/**
	 * Բ��ͼ�λ��ƺ���
	 * @param canvas ����ʵ��
	 * @param paint	����ʵ��
	 */
	public void drawArc(Canvas canvas, Paint paint) {
		canvas.save();
		canvas.rotate(angle, x + r, y + r);
		canvas.drawBitmap(bmp, x, y, paint);
		canvas.restore();
	}
	// ����Բ��ͼ�ε�X����
	public void setX(float x) {
		this.x = x;
	}
	//����Բ��ͼ�ε�Y����
	public void setY(float y) {
		this.y = y;
	}
	//����Բ��ͼ�εĽǶ�
	public void setAngle(float angle) {
		this.angle = angle;
	}

	// ���Բ��ͼ�εİ뾶
	public float getR() {
		return r;
	}
}
