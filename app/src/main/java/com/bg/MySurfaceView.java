package com.bg;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MySurfaceView extends SurfaceView implements Callback, Runnable, ContactListener {
	private Thread th;
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint paint;
	private boolean flag;
	// ----添加一个物理世界---->>
	private final float RATE = 30;// 屏幕到现实世界的比例 30px：1m;
	private World world;// 声明一个物理世界对象
	private AABB aabb;// 声明一个物理世界的范围对象
	private Vec2 gravity;// 声明一个重力向量对象
	private float timeStep = 1f / 60f;// 物理世界模拟的的频率
	private int iterations = 10;// 迭代值，迭代越大模拟越精确，但性能越低
	// 声明小球的Body，便于后续对小球进行操作
	private Body bodyBall;
	//声明胜利与失败的body，用于判定游戏的胜负
	private Body lostBody1, lostBody2, lostBody3,lostBody4,lostBody5,winBody;
	// 声明屏幕宽高
	private int screenW, screenH;
	// 声明游戏状态
	private final int GAMESTATE_MENU = 0;
	private final int GAMESTATE_HELP = 1;
	private final int GAMESTATE_GAMEING = 2;
	private int gameState = GAMESTATE_MENU;
	// 为了游戏暂停时，失败，胜利能继续可能到游戏中的状态，所以并没有将其写成一个状态
	private boolean gameIsPause, gameIsLost, gameIsWin;
	// Body图片资源
	private Bitmap bmpH, bmpS, bmpSh, bmpSs, bmpBall;
	// 菜单、按钮、游戏背景图片资源
	private Bitmap bmpMenu_help, bmpMenu_play, bmpMenu_exit, bmpMenu_resume, bmpMenu_replay, bmp_menubg, bmp_gamebg, bmpMenuBack, bmp_smallbg, bmpMenu_menu,
			bmp_helpbg, bmpBody_lost, bmpBody_win, bmpWinbg, bmpLostbg;
	// 创建按钮
	private HButton hbHelp, hbPlay, hbExit, hbResume, hbReplay, hbBack, hbMenu;

	//声明一个传感器管理器
	private SensorManager sm;
	//声明一个传感器
	private Sensor sensor;
	//声明一个传感器监听器
	private SensorEventListener mySensorListener;
	private float gravX=0,gravY=0;

	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public MySurfaceView(Context context) {
		super(context);
		this.setKeepScreenOn(true);
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		// --添加一个物理世界--->>
		aabb = new AABB();
		gravity = new Vec2(0, 0);
		aabb.lowerBound.set(-100, -100);
		aabb.upperBound.set(100, 100);
		world = new World(aabb, gravity, true);

		//获取传感器管理类实例
		sm = (SensorManager) MainActivity.main.getSystemService(Service.SENSOR_SERVICE);
		//实例一个重力传感器实例
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mySensorListener = new SensorEventListener() {
			@Override
			//传感器获取值发生改变时在响应此函数
			public void onSensorChanged(SensorEvent event) {
				gravX = event.values[0];
				gravY = event.values[1];

				switch (gameState) {
					case GAMESTATE_MENU:
						break;
					case GAMESTATE_HELP:
						break;
					case GAMESTATE_GAMEING:
						// 游戏没有暂停、失败、胜利
						if (!gameIsPause && !gameIsLost && !gameIsWin) {
							if (gravX < 0)
								bodyBall.applyForce(new Vec2(150,0),bodyBall.getPosition());
							else if (gravX > 0)
								bodyBall.applyForce(new Vec2(-150, 0), bodyBall.getPosition());
							if (gravY < 0)
								bodyBall.applyForce(new Vec2(0, -150), bodyBall.getPosition());
							else if (gravY > 0)
								bodyBall.applyForce(new Vec2(0, 150), bodyBall.getPosition());

						}
				}


			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		//为传感器注册监听器
		sm.registerListener(mySensorListener,sensor,SensorManager.SENSOR_DELAY_GAME);

		// ---实例化Body图片资源
		bmpH = BitmapFactory.decodeResource(getResources(), R.drawable.h);
		bmpS = BitmapFactory.decodeResource(getResources(), R.drawable.s);
		bmpSh = BitmapFactory.decodeResource(getResources(), R.drawable.sh);
		bmpSs = BitmapFactory.decodeResource(getResources(), R.drawable.ss);
		bmpBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
		// 实例菜单、按钮、游戏背景图片资源
		bmpMenu_help = BitmapFactory.decodeResource(getResources(), R.drawable.menu_help);
		bmpMenu_play = BitmapFactory.decodeResource(getResources(), R.drawable.menu_play);
		bmpMenu_exit = BitmapFactory.decodeResource(getResources(), R.drawable.menu_exit);
		bmpMenu_resume = BitmapFactory.decodeResource(getResources(), R.drawable.menu_resume);
		bmpMenu_replay = BitmapFactory.decodeResource(getResources(), R.drawable.menu_replay);
		bmp_menubg = BitmapFactory.decodeResource(getResources(), R.drawable.menu_bg);
		bmp_gamebg = BitmapFactory.decodeResource(getResources(), R.drawable.game_bg);
		bmpMenuBack = BitmapFactory.decodeResource(getResources(), R.drawable.menu_back);
		bmp_smallbg = BitmapFactory.decodeResource(getResources(), R.drawable.smallbg);
		bmpMenu_menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu_menu);
		bmp_helpbg = BitmapFactory.decodeResource(getResources(), R.drawable.helpbg);
		bmpBody_lost = BitmapFactory.decodeResource(getResources(), R.drawable.lostbody);
		bmpBody_win = BitmapFactory.decodeResource(getResources(), R.drawable.winbody);
		bmpWinbg = BitmapFactory.decodeResource(getResources(), R.drawable.gamewin);
		bmpLostbg = BitmapFactory.decodeResource(getResources(), R.drawable.gamelost);
	}
	//SurfaceView创建
	public void surfaceCreated(SurfaceHolder holder) {
		//防止Home按键导致游戏重置
		if (gameState == GAMESTATE_MENU) {
			screenW = this.getWidth();
			screenH = this.getHeight();
			// 实例化按钮
			hbPlay = new HButton(bmpMenu_play, screenW / 2 - bmpMenu_help.getWidth() / 2, 150);
			hbHelp = new HButton(bmpMenu_help, hbPlay.getX(), hbPlay.getY() + 50);
			hbExit = new HButton(bmpMenu_exit, hbPlay.getX(), hbHelp.getY() + 50);
			hbBack = new HButton(bmpMenuBack, 0, screenH - bmpMenu_help.getHeight());
			hbResume = new HButton(bmpMenu_resume, screenW / 2 - bmpMenu_help.getWidth() / 2, 200);
			hbReplay = new HButton(bmpMenu_replay, hbResume.getX(), hbResume.getY() + 50);
			hbMenu = new HButton(bmpMenu_menu, hbResume.getX(), hbReplay.getY() + 50);
			// 创建主角小球
			bodyBall = createCircle(bmpBall, bmpH.getHeight(), bmpH.getHeight(), bmpBall.getWidth() / 2, 5);
			//创建胜负Body
			lostBody1 = createCircle(bmpBody_lost, screenW - bmpH.getHeight() - bmpBody_lost.getWidth(), bmpH.getHeight(), bmpBody_lost.getWidth() / 2, 0);
			lostBody2 = createCircle(bmpBody_lost, bmpH.getHeight(), screenH - bmpH.getHeight() - bmpBody_lost.getHeight(), bmpBody_lost.getWidth() / 2, 0);
			lostBody3 = createCircle(bmpBody_lost, screenW - bmpH.getHeight() - bmpBody_lost.getWidth(), bmpH.getHeight()+170, bmpBody_lost.getWidth() / 2, 0);
			lostBody4 = createCircle(bmpBody_lost, screenW - bmpSh.getWidth()+10, bmpH.getHeight()+170, bmpBody_lost.getWidth() / 2, 0);
			lostBody5 = createCircle(bmpBody_lost, getWidth()-bmpBody_lost.getWidth()-102, screenH - bmpH.getHeight() - bmpBody_lost.getHeight(), bmpBody_lost.getWidth() / 2, 0);

			winBody = createCircle(bmpBody_win, screenW - bmpH.getHeight() - bmpBody_win.getWidth(), screenH - bmpH.getHeight() - bmpBody_win.getHeight(),
					bmpBody_win.getWidth() / 2, 0);
			//设置传感器，发生碰撞，但无碰撞效果
			lostBody1.getShapeList().m_isSensor = true;
			lostBody2.getShapeList().m_isSensor = true;
			lostBody3.getShapeList().m_isSensor = true;
			lostBody4.getShapeList().m_isSensor = true;
			lostBody5.getShapeList().m_isSensor = true;
			winBody.getShapeList().m_isSensor = true;
			// 创建边界
			createRect(bmpH, 0, 0, bmpH.getWidth(), bmpH.getHeight(), 0);// 上
			createRect(bmpH, 0, getHeight() - bmpH.getHeight(), bmpH.getWidth(), bmpH.getHeight(), 0);// 下
			createRect(bmpS, 0, 0, bmpS.getWidth(), bmpS.getHeight(), 0);// 左
			createRect(bmpS, getWidth() - bmpS.getWidth(), 0, bmpS.getWidth(), bmpS.getHeight(), 0);// 右
			// -----创建障碍物
			createRect(bmpSh, 0, 90, bmpSh.getWidth(), bmpSh.getHeight(), 0);
			createRect(bmpSh, 110, 170, bmpSh.getWidth(), bmpSh.getHeight(), 0);
			createRect(bmpSs, 110, 170, bmpSs.getWidth(), bmpSs.getHeight(), 0);
			createRect(bmpSs, 110, getHeight()-bmpSs.getHeight()-100, bmpSs.getWidth(), bmpSs.getHeight(), 0);
			createRect(bmpSs, getWidth() - 102, screenH - bmpSs.getHeight(), bmpSs.getWidth(), bmpSs.getHeight(), 0);
			createRect(bmpSs, getWidth() - 102, 255, bmpSs.getWidth(), bmpSs.getHeight(), 0);
			//绑定监听器
			world.setContactListener(this);
		}
		flag = true;
		th = new Thread(this);
		th.start();
	}

	//在物理世界中添加矩形Body
	public Body createRect(Bitmap bmp, float x, float y, float w, float h, float density) {
		PolygonDef pd = new PolygonDef();
		pd.density = density;
		pd.friction = 0.8f;
		pd.restitution = 0.3f;
		pd.setAsBox(w / 2 / RATE, h / 2 / RATE);
		BodyDef bd = new BodyDef();
		bd.position.set((x + w / 2) / RATE, (y + h / 2) / RATE);
		Body body = world.createBody(bd);
		body.m_userData = new MyRect(bmp, x, y);
		body.createShape(pd);
		body.setMassFromShapes();
		return body;
	}

	//在物理世界中添加圆形Body
	public Body createCircle(Bitmap bmp, float x, float y, float r, float density) {
		CircleDef cd = new CircleDef();
		cd.density = density;
		cd.friction = 0.8f;
		cd.restitution = 0.3f;
		cd.radius = r / RATE;
		BodyDef bd = new BodyDef();
		bd.position.set((x + r) / RATE, (y + r) / RATE);
		Body body = world.createBody(bd);
		body.m_userData = new MyCircle(bmp, x, y, r);
		body.createShape(cd);
		body.setMassFromShapes();
		//放置小球进入休眠，导致改变重力方向，小球无法移动
		body.allowSleeping(false);
		return body;
	}

	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.BLACK);
			switch (gameState) {
			case GAMESTATE_MENU:
				//绘制主菜单背景
				//canvas.drawBitmap(bmp_menubg, 0, 0, paint);
				canvas.drawColor(Color.WHITE);
				//绘制Play按钮
				hbPlay.draw(canvas, paint);
				//绘制Help按钮
				hbHelp.draw(canvas, paint);
				//绘制Exit按钮
				hbExit.draw(canvas, paint);
				break;
			case GAMESTATE_HELP:
				//绘制帮助界面背景
				//canvas.drawBitmap(bmp_helpbg, 0, 0, paint);
				canvas.drawColor(Color.WHITE);
				//绘制Back按钮
				hbBack.draw(canvas, paint);
				break;
			case GAMESTATE_GAMEING:
				//绘制游戏背景
				//canvas.drawBitmap(bmp_gamebg, 0, 0, paint);
				canvas.drawColor(Color.WHITE);
				//遍历物理世界中所有存在的Body;
				Body body = world.getBodyList();
				for (int i = 1; i < world.getBodyCount(); i++) {
					if ((body.m_userData) instanceof MyRect) {
						MyRect rect = (MyRect) (body.m_userData);
						rect.drawMyRect(canvas, paint);
					} else if ((body.m_userData) instanceof MyCircle) {
						MyCircle mcc = (MyCircle) (body.m_userData);
						mcc.drawArc(canvas, paint);
					}
					body = body.m_next;
				}
				//当游戏暂停或失败或成功时
				if (gameIsPause || gameIsLost || gameIsWin) {
					// 当游戏暂停或失败或成功时画一个半透明黑色矩形，突出界面
					Paint paintB = new Paint();
					paintB.setAlpha(0x77);
					canvas.drawRect(0, 0, screenW, screenH, paintB);
				}
				// 游戏暂停
				if (gameIsPause) {
					//绘制暂停背景
					//canvas.drawBitmap(bmp_smallbg, screenW / 2 - bmp_smallbg.getWidth() / 2, screenH / 2 - bmp_smallbg.getHeight() / 2, paint);
					paint.setStyle(Style.FILL);
					paint.setColor(Color.WHITE);
					canvas.drawRect(screenW / 2 - bmp_smallbg.getWidth() / 2,screenH / 2 - bmp_smallbg.getHeight()/2,screenW / 2 + bmp_smallbg.getWidth() / 2,screenH / 2 + bmp_smallbg.getHeight()/2,paint);
					//绘制Resume按钮
					hbResume.draw(canvas, paint);
					//绘制Replay按钮
					hbReplay.draw(canvas, paint);
					//绘制Menu按钮
					hbMenu.draw(canvas, paint);
				} else
				//游戏失败
				if (gameIsLost) {
					//绘制游戏背景
					canvas.drawBitmap(bmpLostbg, screenW / 2 - bmpLostbg.getWidth() / 2, screenH / 2 - bmpLostbg.getHeight() / 2, paint);
					//绘制Replay按钮
					hbReplay.draw(canvas, paint);
					//绘制Menu按钮
					hbMenu.draw(canvas, paint);
				} else
				//游戏胜利
				if (gameIsWin) {
					//绘制失败背景
					canvas.drawBitmap(bmpWinbg, screenW / 2 - bmpWinbg.getWidth() / 2, screenH / 2 - bmpWinbg.getHeight() / 2, paint);
					//绘制Replay按钮
					hbReplay.draw(canvas, paint);
					//绘制Menu按钮
					hbMenu.draw(canvas, paint);
				}

				break;
			}
		} catch (Exception e) {
			Log.e("Himi", "myDraw is Error!");
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 触屏按键事件处理
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (gameState) {
		case GAMESTATE_MENU:
			//判断Play按钮是否被点击
			if (hbPlay.isPressed(event)) {
				//Play按钮被点击开始游戏
				gameState = GAMESTATE_GAMEING;
				//判断Help按钮是否被点击
			} else if (hbHelp.isPressed(event)) {
				//Play按钮被点击进入游戏游戏帮助界面
				gameState = GAMESTATE_HELP;
				//判断Exit按钮是否被点击
			} else if (hbExit.isPressed(event)) {
				//Exit按钮被点击调用退出函数
				MainActivity.main.exit();
			}
			break;
		case GAMESTATE_HELP:
			//判断Back按钮是否被点击
			if (hbBack.isPressed(event)) {
				//Back按钮被点击进入主菜单界面
				gameState = GAMESTATE_MENU;
			}
			break;
		case GAMESTATE_GAMEING:
			// 游戏暂停(因为游戏失败和游戏胜利中按钮处理事件一样，所以就不需要重写)
			if (gameIsPause || gameIsLost || gameIsWin) {
				if (hbResume.isPressed(event)) {
					gameIsPause = false;
				} else if (hbReplay.isPressed(event)) {
					//因为在重置前小球可能拥有力，所以重置游戏要先使用putToSleep()方法
					//让其Body进入睡眠，并让Body停止模拟，速度置为0
					bodyBall.putToSleep();
					// 然后对小球的坐标进行重置
					bodyBall.setXForm(new Vec2((bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE, (bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE),
							0);
					//并且设置默认重力方向为向下
					world.setGravity(new Vec2(0, 0));
					//唤醒小球
					bodyBall.wakeUp();
					//游戏暂停、胜利、失败条件还原默认false
					gameIsPause = false;
					gameIsLost = false;
					gameIsWin = false;
				} else if (hbMenu.isPressed(event)) {
					//因为在重置前小球可能拥有力，所以重置游戏要先使用putToSleep()方法
					//让其Body进入睡眠，并让Body停止模拟，速度置为0
					bodyBall.putToSleep();
					// 然后对小球的坐标进行重置
					bodyBall.setXForm(new Vec2((bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE, (bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE),
							0);
					//并且设置默认重力方向为向下
					world.setGravity(new Vec2(0, 0));
					//唤醒小球
					bodyBall.wakeUp();
					//重置游戏状态为主菜单
					gameState = GAMESTATE_MENU;
					//游戏暂停、胜利、失败条件还原默认false
					gameIsPause = false;
					gameIsLost = false;
					gameIsWin = false;
				}
			}
			break;
		}
		return true;
	}

	/**
	 * 实体键盘按键处理
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 当前游戏状态不处于正在游戏中时，屏蔽“返回”实体按键,避免程序进入后台;
		if (keyCode == KeyEvent.KEYCODE_BACK && gameState != GAMESTATE_GAMEING) {
			return true;
		}
		switch (gameState) {
		case GAMESTATE_MENU:
			break;
		case GAMESTATE_HELP:
			break;
		case GAMESTATE_GAMEING:
			// 游戏没有暂停、失败、胜利
			if (!gameIsPause && !gameIsLost && !gameIsWin) {
				//如果返回键被按下
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					//进入游戏暂停界面
					gameIsPause = true;
				}
			}
			//屏蔽“返回”实体按键,避免程序进入后台;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}




	//游戏逻辑函数
	public void Logic() {
		switch (gameState) {
		case GAMESTATE_MENU:
			break;
		case GAMESTATE_HELP:
			break;
		case GAMESTATE_GAMEING:
			// 游戏没有暂停
			if (!gameIsPause && !gameIsLost && !gameIsWin) {
				// --开始模拟物理世界--->>
				world.step(timeStep, iterations);
				Body body = world.getBodyList();
				for (int i = 1; i < world.getBodyCount(); i++) {
					if ((body.m_userData) instanceof MyRect) {
						MyRect rect = (MyRect) (body.m_userData);
						rect.setX(body.getPosition().x * RATE - rect.getW() / 2);
						rect.setY(body.getPosition().y * RATE - rect.getH() / 2);
					} else if ((body.m_userData) instanceof MyCircle) {
						MyCircle mcc = (MyCircle) (body.m_userData);
						mcc.setX(body.getPosition().x * RATE - mcc.getR());
						mcc.setY(body.getPosition().y * RATE - mcc.getR());
						mcc.setAngle((float) (body.getAngle() * 180 / Math.PI));
					}
					body = body.m_next;
				}
			}
			break;
		}
	}

	public void run() {
		while (flag) {
			myDraw();
			Logic();
			try {
				Thread.sleep((long) timeStep * 1000);
			} catch (Exception ex) {
				Log.e("Wyf", "Thread is Error!");
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}

	@Override
	public void add(ContactPoint point) {
		//当前游戏状态为进行游戏时
		if (gameState == GAMESTATE_GAMEING) {
			//游戏没有进入暂停、失败、胜利界面
			if (!gameIsPause && !gameIsLost && !gameIsWin) {
				//判定主角小球是否与失败小球1发生碰撞
				if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody1) {
					//游戏失败
					gameIsLost = true;
					//判定主角小球是否与失败小球发生碰撞
				} else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody2) {
					//游戏失败
					gameIsLost = true;

				} else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody3) {
					//游戏失败
					gameIsLost = true;

				}else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody4) {
					//游戏失败
					gameIsLost = true;

				}else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody5) {
					//游戏失败
					gameIsLost = true;

				}else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == winBody) {
					//游戏胜利
					gameIsWin = true;
				}
			}
		}
	}

	@Override
	public void persist(ContactPoint point) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(ContactPoint point) {
		// TODO Auto-generated method stub

	}

	@Override
	public void result(ContactResult point) {
		// TODO Auto-generated method stub

	}

}
