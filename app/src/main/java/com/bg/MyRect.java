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
	// 矩形图形的图片
	private Bitmap bmp;
	// 矩形图形的坐标
	private float x, y;
	/**
	 * 矩形图片构造函数
	 * @param bmp 资源图片
	 * @param x	矩形图形X坐标
	 * @param y	矩形图形Y坐标
	 * @param r	矩形图形半径
	 */
	public MyRect(Bitmap bmp, float x, float y) {
		this.bmp = bmp;
		this.x = x;
		this.y = y;
	}
	/**
	 * 矩形图形绘制函数
	 * @param canvas 画布实例
	 * @param paint	画笔实例
	 */
	public void drawMyRect(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bmp, x, y, paint);
	}
	// 设置矩形图形的X坐标
	public void setX(float x) {
		this.x = x;
	}
	//设置矩形图形的Y坐标
	public void setY(float y) {
		this.y = y;
	}
	//获取矩形图形的宽
	public int getW() {
		return bmp.getWidth();
	}
	//获取矩形图形的高
	public int getH() {
		return bmp.getHeight();
	}
}
