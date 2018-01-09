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
	// ----���һ����������---->>
	private final float RATE = 30;// ��Ļ����ʵ����ı��� 30px��1m;
	private World world;// ����һ�������������
	private AABB aabb;// ����һ����������ķ�Χ����
	private Vec2 gravity;// ����һ��������������
	private float timeStep = 1f / 60f;// ��������ģ��ĵ�Ƶ��
	private int iterations = 10;// ����ֵ������Խ��ģ��Խ��ȷ��������Խ��
	// ����С���Body�����ں�����С����в���
	private Body bodyBall;
	//����ʤ����ʧ�ܵ�body�������ж���Ϸ��ʤ��
	private Body lostBody1, lostBody2, lostBody3,lostBody4,lostBody5,winBody;
	// ������Ļ���
	private int screenW, screenH;
	// ������Ϸ״̬
	private final int GAMESTATE_MENU = 0;
	private final int GAMESTATE_HELP = 1;
	private final int GAMESTATE_GAMEING = 2;
	private int gameState = GAMESTATE_MENU;
	// Ϊ����Ϸ��ͣʱ��ʧ�ܣ�ʤ���ܼ������ܵ���Ϸ�е�״̬�����Բ�û�н���д��һ��״̬
	private boolean gameIsPause, gameIsLost, gameIsWin;
	// BodyͼƬ��Դ
	private Bitmap bmpH, bmpS, bmpSh, bmpSs, bmpBall;
	// �˵�����ť����Ϸ����ͼƬ��Դ
	private Bitmap bmpMenu_help, bmpMenu_play, bmpMenu_exit, bmpMenu_resume, bmpMenu_replay, bmp_menubg, bmp_gamebg, bmpMenuBack, bmp_smallbg, bmpMenu_menu,
			bmp_helpbg, bmpBody_lost, bmpBody_win, bmpWinbg, bmpLostbg;
	// ������ť
	private HButton hbHelp, hbPlay, hbExit, hbResume, hbReplay, hbBack, hbMenu;

	//����һ��������������
	private SensorManager sm;
	//����һ��������
	private Sensor sensor;
	//����һ��������������
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
		// --���һ����������--->>
		aabb = new AABB();
		gravity = new Vec2(0, 0);
		aabb.lowerBound.set(-100, -100);
		aabb.upperBound.set(100, 100);
		world = new World(aabb, gravity, true);

		//��ȡ������������ʵ��
		sm = (SensorManager) MainActivity.main.getSystemService(Service.SENSOR_SERVICE);
		//ʵ��һ������������ʵ��
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mySensorListener = new SensorEventListener() {
			@Override
			//��������ȡֵ�����ı�ʱ����Ӧ�˺���
			public void onSensorChanged(SensorEvent event) {
				gravX = event.values[0];
				gravY = event.values[1];

				switch (gameState) {
					case GAMESTATE_MENU:
						break;
					case GAMESTATE_HELP:
						break;
					case GAMESTATE_GAMEING:
						// ��Ϸû����ͣ��ʧ�ܡ�ʤ��
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
		//Ϊ������ע�������
		sm.registerListener(mySensorListener,sensor,SensorManager.SENSOR_DELAY_GAME);

		// ---ʵ����BodyͼƬ��Դ
		bmpH = BitmapFactory.decodeResource(getResources(), R.drawable.h);
		bmpS = BitmapFactory.decodeResource(getResources(), R.drawable.s);
		bmpSh = BitmapFactory.decodeResource(getResources(), R.drawable.sh);
		bmpSs = BitmapFactory.decodeResource(getResources(), R.drawable.ss);
		bmpBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
		// ʵ���˵�����ť����Ϸ����ͼƬ��Դ
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
	//SurfaceView����
	public void surfaceCreated(SurfaceHolder holder) {
		//��ֹHome����������Ϸ����
		if (gameState == GAMESTATE_MENU) {
			screenW = this.getWidth();
			screenH = this.getHeight();
			// ʵ������ť
			hbPlay = new HButton(bmpMenu_play, screenW / 2 - bmpMenu_help.getWidth() / 2, 150);
			hbHelp = new HButton(bmpMenu_help, hbPlay.getX(), hbPlay.getY() + 50);
			hbExit = new HButton(bmpMenu_exit, hbPlay.getX(), hbHelp.getY() + 50);
			hbBack = new HButton(bmpMenuBack, 0, screenH - bmpMenu_help.getHeight());
			hbResume = new HButton(bmpMenu_resume, screenW / 2 - bmpMenu_help.getWidth() / 2, 200);
			hbReplay = new HButton(bmpMenu_replay, hbResume.getX(), hbResume.getY() + 50);
			hbMenu = new HButton(bmpMenu_menu, hbResume.getX(), hbReplay.getY() + 50);
			// ��������С��
			bodyBall = createCircle(bmpBall, bmpH.getHeight(), bmpH.getHeight(), bmpBall.getWidth() / 2, 5);
			//����ʤ��Body
			lostBody1 = createCircle(bmpBody_lost, screenW - bmpH.getHeight() - bmpBody_lost.getWidth(), bmpH.getHeight(), bmpBody_lost.getWidth() / 2, 0);
			lostBody2 = createCircle(bmpBody_lost, bmpH.getHeight(), screenH - bmpH.getHeight() - bmpBody_lost.getHeight(), bmpBody_lost.getWidth() / 2, 0);
			lostBody3 = createCircle(bmpBody_lost, screenW - bmpH.getHeight() - bmpBody_lost.getWidth(), bmpH.getHeight()+170, bmpBody_lost.getWidth() / 2, 0);
			lostBody4 = createCircle(bmpBody_lost, screenW - bmpSh.getWidth()+10, bmpH.getHeight()+170, bmpBody_lost.getWidth() / 2, 0);
			lostBody5 = createCircle(bmpBody_lost, getWidth()-bmpBody_lost.getWidth()-102, screenH - bmpH.getHeight() - bmpBody_lost.getHeight(), bmpBody_lost.getWidth() / 2, 0);

			winBody = createCircle(bmpBody_win, screenW - bmpH.getHeight() - bmpBody_win.getWidth(), screenH - bmpH.getHeight() - bmpBody_win.getHeight(),
					bmpBody_win.getWidth() / 2, 0);
			//���ô�������������ײ��������ײЧ��
			lostBody1.getShapeList().m_isSensor = true;
			lostBody2.getShapeList().m_isSensor = true;
			lostBody3.getShapeList().m_isSensor = true;
			lostBody4.getShapeList().m_isSensor = true;
			lostBody5.getShapeList().m_isSensor = true;
			winBody.getShapeList().m_isSensor = true;
			// �����߽�
			createRect(bmpH, 0, 0, bmpH.getWidth(), bmpH.getHeight(), 0);// ��
			createRect(bmpH, 0, getHeight() - bmpH.getHeight(), bmpH.getWidth(), bmpH.getHeight(), 0);// ��
			createRect(bmpS, 0, 0, bmpS.getWidth(), bmpS.getHeight(), 0);// ��
			createRect(bmpS, getWidth() - bmpS.getWidth(), 0, bmpS.getWidth(), bmpS.getHeight(), 0);// ��
			// -----�����ϰ���
			createRect(bmpSh, 0, 90, bmpSh.getWidth(), bmpSh.getHeight(), 0);
			createRect(bmpSh, 110, 170, bmpSh.getWidth(), bmpSh.getHeight(), 0);
			createRect(bmpSs, 110, 170, bmpSs.getWidth(), bmpSs.getHeight(), 0);
			createRect(bmpSs, 110, getHeight()-bmpSs.getHeight()-100, bmpSs.getWidth(), bmpSs.getHeight(), 0);
			createRect(bmpSs, getWidth() - 102, screenH - bmpSs.getHeight(), bmpSs.getWidth(), bmpSs.getHeight(), 0);
			createRect(bmpSs, getWidth() - 102, 255, bmpSs.getWidth(), bmpSs.getHeight(), 0);
			//�󶨼�����
			world.setContactListener(this);
		}
		flag = true;
		th = new Thread(this);
		th.start();
	}

	//��������������Ӿ���Body
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

	//���������������Բ��Body
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
		//����С��������ߣ����¸ı���������С���޷��ƶ�
		body.allowSleeping(false);
		return body;
	}

	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.BLACK);
			switch (gameState) {
			case GAMESTATE_MENU:
				//�������˵�����
				//canvas.drawBitmap(bmp_menubg, 0, 0, paint);
				canvas.drawColor(Color.WHITE);
				//����Play��ť
				hbPlay.draw(canvas, paint);
				//����Help��ť
				hbHelp.draw(canvas, paint);
				//����Exit��ť
				hbExit.draw(canvas, paint);
				break;
			case GAMESTATE_HELP:
				//���ư������汳��
				//canvas.drawBitmap(bmp_helpbg, 0, 0, paint);
				canvas.drawColor(Color.WHITE);
				//����Back��ť
				hbBack.draw(canvas, paint);
				break;
			case GAMESTATE_GAMEING:
				//������Ϸ����
				//canvas.drawBitmap(bmp_gamebg, 0, 0, paint);
				canvas.drawColor(Color.WHITE);
				//�����������������д��ڵ�Body;
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
				//����Ϸ��ͣ��ʧ�ܻ�ɹ�ʱ
				if (gameIsPause || gameIsLost || gameIsWin) {
					// ����Ϸ��ͣ��ʧ�ܻ�ɹ�ʱ��һ����͸����ɫ���Σ�ͻ������
					Paint paintB = new Paint();
					paintB.setAlpha(0x77);
					canvas.drawRect(0, 0, screenW, screenH, paintB);
				}
				// ��Ϸ��ͣ
				if (gameIsPause) {
					//������ͣ����
					//canvas.drawBitmap(bmp_smallbg, screenW / 2 - bmp_smallbg.getWidth() / 2, screenH / 2 - bmp_smallbg.getHeight() / 2, paint);
					paint.setStyle(Style.FILL);
					paint.setColor(Color.WHITE);
					canvas.drawRect(screenW / 2 - bmp_smallbg.getWidth() / 2,screenH / 2 - bmp_smallbg.getHeight()/2,screenW / 2 + bmp_smallbg.getWidth() / 2,screenH / 2 + bmp_smallbg.getHeight()/2,paint);
					//����Resume��ť
					hbResume.draw(canvas, paint);
					//����Replay��ť
					hbReplay.draw(canvas, paint);
					//����Menu��ť
					hbMenu.draw(canvas, paint);
				} else
				//��Ϸʧ��
				if (gameIsLost) {
					//������Ϸ����
					canvas.drawBitmap(bmpLostbg, screenW / 2 - bmpLostbg.getWidth() / 2, screenH / 2 - bmpLostbg.getHeight() / 2, paint);
					//����Replay��ť
					hbReplay.draw(canvas, paint);
					//����Menu��ť
					hbMenu.draw(canvas, paint);
				} else
				//��Ϸʤ��
				if (gameIsWin) {
					//����ʧ�ܱ���
					canvas.drawBitmap(bmpWinbg, screenW / 2 - bmpWinbg.getWidth() / 2, screenH / 2 - bmpWinbg.getHeight() / 2, paint);
					//����Replay��ť
					hbReplay.draw(canvas, paint);
					//����Menu��ť
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
	 * ���������¼�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (gameState) {
		case GAMESTATE_MENU:
			//�ж�Play��ť�Ƿ񱻵��
			if (hbPlay.isPressed(event)) {
				//Play��ť�������ʼ��Ϸ
				gameState = GAMESTATE_GAMEING;
				//�ж�Help��ť�Ƿ񱻵��
			} else if (hbHelp.isPressed(event)) {
				//Play��ť�����������Ϸ��Ϸ��������
				gameState = GAMESTATE_HELP;
				//�ж�Exit��ť�Ƿ񱻵��
			} else if (hbExit.isPressed(event)) {
				//Exit��ť����������˳�����
				MainActivity.main.exit();
			}
			break;
		case GAMESTATE_HELP:
			//�ж�Back��ť�Ƿ񱻵��
			if (hbBack.isPressed(event)) {
				//Back��ť������������˵�����
				gameState = GAMESTATE_MENU;
			}
			break;
		case GAMESTATE_GAMEING:
			// ��Ϸ��ͣ(��Ϊ��Ϸʧ�ܺ���Ϸʤ���а�ť�����¼�һ�������ԾͲ���Ҫ��д)
			if (gameIsPause || gameIsLost || gameIsWin) {
				if (hbResume.isPressed(event)) {
					gameIsPause = false;
				} else if (hbReplay.isPressed(event)) {
					//��Ϊ������ǰС�����ӵ����������������ϷҪ��ʹ��putToSleep()����
					//����Body����˯�ߣ�����Bodyֹͣģ�⣬�ٶ���Ϊ0
					bodyBall.putToSleep();
					// Ȼ���С��������������
					bodyBall.setXForm(new Vec2((bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE, (bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE),
							0);
					//��������Ĭ����������Ϊ����
					world.setGravity(new Vec2(0, 0));
					//����С��
					bodyBall.wakeUp();
					//��Ϸ��ͣ��ʤ����ʧ��������ԭĬ��false
					gameIsPause = false;
					gameIsLost = false;
					gameIsWin = false;
				} else if (hbMenu.isPressed(event)) {
					//��Ϊ������ǰС�����ӵ����������������ϷҪ��ʹ��putToSleep()����
					//����Body����˯�ߣ�����Bodyֹͣģ�⣬�ٶ���Ϊ0
					bodyBall.putToSleep();
					// Ȼ���С��������������
					bodyBall.setXForm(new Vec2((bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE, (bmpH.getHeight() + bmpBall.getWidth() / 2 + 2) / RATE),
							0);
					//��������Ĭ����������Ϊ����
					world.setGravity(new Vec2(0, 0));
					//����С��
					bodyBall.wakeUp();
					//������Ϸ״̬Ϊ���˵�
					gameState = GAMESTATE_MENU;
					//��Ϸ��ͣ��ʤ����ʧ��������ԭĬ��false
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
	 * ʵ����̰�������
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ��ǰ��Ϸ״̬������������Ϸ��ʱ�����Ρ����ء�ʵ�尴��,�����������̨;
		if (keyCode == KeyEvent.KEYCODE_BACK && gameState != GAMESTATE_GAMEING) {
			return true;
		}
		switch (gameState) {
		case GAMESTATE_MENU:
			break;
		case GAMESTATE_HELP:
			break;
		case GAMESTATE_GAMEING:
			// ��Ϸû����ͣ��ʧ�ܡ�ʤ��
			if (!gameIsPause && !gameIsLost && !gameIsWin) {
				//������ؼ�������
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					//������Ϸ��ͣ����
					gameIsPause = true;
				}
			}
			//���Ρ����ء�ʵ�尴��,�����������̨;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}




	//��Ϸ�߼�����
	public void Logic() {
		switch (gameState) {
		case GAMESTATE_MENU:
			break;
		case GAMESTATE_HELP:
			break;
		case GAMESTATE_GAMEING:
			// ��Ϸû����ͣ
			if (!gameIsPause && !gameIsLost && !gameIsWin) {
				// --��ʼģ����������--->>
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
		//��ǰ��Ϸ״̬Ϊ������Ϸʱ
		if (gameState == GAMESTATE_GAMEING) {
			//��Ϸû�н�����ͣ��ʧ�ܡ�ʤ������
			if (!gameIsPause && !gameIsLost && !gameIsWin) {
				//�ж�����С���Ƿ���ʧ��С��1������ײ
				if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody1) {
					//��Ϸʧ��
					gameIsLost = true;
					//�ж�����С���Ƿ���ʧ��С������ײ
				} else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody2) {
					//��Ϸʧ��
					gameIsLost = true;

				} else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody3) {
					//��Ϸʧ��
					gameIsLost = true;

				}else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody4) {
					//��Ϸʧ��
					gameIsLost = true;

				}else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == lostBody5) {
					//��Ϸʧ��
					gameIsLost = true;

				}else if (point.shape1.getBody() == bodyBall && point.shape2.getBody() == winBody) {
					//��Ϸʤ��
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
