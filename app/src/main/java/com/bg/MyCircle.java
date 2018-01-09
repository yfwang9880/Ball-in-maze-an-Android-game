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
	// 圆形的宽高与半径
	float x, y, r, angle;
	//圆形Body图片资源
	private Bitmap bmp;
	/**
	 * 圆形图片构造函数
	 * @param bmp 资源图片
	 * @param x	圆形图形X坐标
	 * @param y	圆形图形Y坐标
	 * @param r	圆形图形半径
	 */
	public MyCircle(Bitmap bmp, float x, float y, float r) {
		this.bmp = bmp;
		this.x = x;
		this.y = y;
		this.r = r;
	}
	/**
	 * 圆形图形绘制函数
	 * @param canvas 画布实例
	 * @param paint	画笔实例
	 */
	public void drawArc(Canvas canvas, Paint paint) {
		canvas.save();
		canvas.rotate(angle, x + r, y + r);
		canvas.drawBitmap(bmp, x, y, paint);
		canvas.restore();
	}
	// 设置圆形图形的X坐标
	public void setX(float x) {
		this.x = x;
	}
	//设置圆形图形的Y坐标
	public void setY(float y) {
		this.y = y;
	}
	//设置圆形图形的角度
	public void setAngle(float angle) {
		this.angle = angle;
	}

	// 获得圆形图形的半径
	public float getR() {
		return r;
	}
}
