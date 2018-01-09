package com.bg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class HButton {
	//按钮xy坐标与宽高
	private int x, y, w, h;
	//按钮资源图片
	private Bitmap bmp;
	/**
	 * 按钮构造函数
	 * @param bmp 按钮图片资源
	 * @param x	按钮X坐标
	 * @param y	按钮Y坐标
	 */
	public HButton(Bitmap bmp, int x, int y) {
		this.x = x;
		this.y = y;
		this.bmp = bmp;
		this.w = bmp.getWidth();
		this.h = bmp.getHeight();
	}
	/**
	 * 绘制按钮
	 * @param canvas  画布实例
	 * @param paint	画笔实例
	 */
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bmp, x, y, paint);
	}
	/**
	 * 判定按钮是否被点击
	 * @param event 触屏事件参数
	 */
	public boolean isPressed(MotionEvent event) {
		//判定用户是否点击屏幕
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//通过按钮坐标宽高与触屏的坐标进行判定按钮是否被点击
			if (event.getX() <= x + w && event.getX() >= x) {
				if (event.getY() <= y + h && event.getY() >= y) {
					return true;
				}
			}
		}
		//没有点击返回false
		return false;
	}
	//获取按钮的宽
	public int getW() {
		return w;
	}
	//获取按钮的高
	public int getH() {
		return h;
	}
	//获取按钮的X坐标
	public int getX() {
		return x;
	}
	//设置按钮的X坐标
	public void setX(int x) {
		this.x = x;
	}
	//获取按钮的Y坐标
	public int getY() {
		return y;
	}
	//设置按钮的Y坐标
	public void setY(int y) {
		this.y = y;
	}
}
