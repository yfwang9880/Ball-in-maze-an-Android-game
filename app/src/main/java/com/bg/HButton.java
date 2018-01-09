package com.bg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class HButton {
	//��ťxy��������
	private int x, y, w, h;
	//��ť��ԴͼƬ
	private Bitmap bmp;
	/**
	 * ��ť���캯��
	 * @param bmp ��ťͼƬ��Դ
	 * @param x	��ťX����
	 * @param y	��ťY����
	 */
	public HButton(Bitmap bmp, int x, int y) {
		this.x = x;
		this.y = y;
		this.bmp = bmp;
		this.w = bmp.getWidth();
		this.h = bmp.getHeight();
	}
	/**
	 * ���ư�ť
	 * @param canvas  ����ʵ��
	 * @param paint	����ʵ��
	 */
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bmp, x, y, paint);
	}
	/**
	 * �ж���ť�Ƿ񱻵��
	 * @param event �����¼�����
	 */
	public boolean isPressed(MotionEvent event) {
		//�ж��û��Ƿ�����Ļ
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//ͨ����ť�������봥������������ж���ť�Ƿ񱻵��
			if (event.getX() <= x + w && event.getX() >= x) {
				if (event.getY() <= y + h && event.getY() >= y) {
					return true;
				}
			}
		}
		//û�е������false
		return false;
	}
	//��ȡ��ť�Ŀ�
	public int getW() {
		return w;
	}
	//��ȡ��ť�ĸ�
	public int getH() {
		return h;
	}
	//��ȡ��ť��X����
	public int getX() {
		return x;
	}
	//���ð�ť��X����
	public void setX(int x) {
		this.x = x;
	}
	//��ȡ��ť��Y����
	public int getY() {
		return y;
	}
	//���ð�ť��Y����
	public void setY(int y) {
		this.y = y;
	}
}
