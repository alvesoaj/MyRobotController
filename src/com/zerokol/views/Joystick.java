package com.zerokol.views;

import com.zerokol.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Joystick extends View implements Runnable {
	// Constants
	private final double RAD = 57.2957795;
	public final static long DEFAULT_LOOP_INTERVAL = 100;
	// Variables
	private OnJoystickMoveListener onJoystickMoveListener; // Listener
	private Thread thread = new Thread(this);
	private long loopInterval = DEFAULT_LOOP_INTERVAL;
	private int xPosition = 0; // Touch x position
	private int yPosition = 0; // Touch y position
	private double centerX = 0; // Center view x position
	private double centerY = 0; // Center view y position
	private int padding = 10;
	private int circleColor;
	private int buttonColor;
	private int buttonRadius = 30;
	private int joystickRadius = 100;

	public Joystick(Context context) {
		super(context);
	}

	public Joystick(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Joystick);
		circleColor = a.getColor(R.styleable.Joystick_circleColor, Color.RED);
		buttonColor = a.getColor(R.styleable.Joystick_buttonColor, Color.RED);
		buttonRadius = a.getInteger(R.styleable.Joystick_buttonRadius, 30);
		a.recycle();
	}

	@Override
	protected void onFinishInflate() {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// setting the measured values to resize the view to a certain width and
		// height
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
		// before measure, get the center of view
		xPosition = (int) getWidth() / 2;
		yPosition = (int) getWidth() / 2;
	}

	private int measureWidth(int measureSpec) {
		return 2 * padding + buttonRadius + 2 * joystickRadius;
	}

	private int measureHeight(int measureSpec) {
		return 2 * padding + buttonRadius + 2 * joystickRadius;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		centerX = (getWidth()) / 2;
		centerY = (getHeight()) / 2;

		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(circleColor);
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius, p);
		// dotted stroke
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius / 2, p);

		p.setColor(buttonColor);
		p.setStyle(Paint.Style.FILL);
		canvas.drawCircle(xPosition, yPosition, buttonRadius, p);
		canvas.drawCircle(xPosition, yPosition, buttonRadius / 2, p);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		xPosition = (int) event.getX();
		yPosition = (int) event.getY();
		double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
				+ (yPosition - centerY) * (yPosition - centerY));
		if (abs > joystickRadius) {
			xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
			yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
		}
		invalidate();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			xPosition = (int) centerX;
			yPosition = (int) centerY;
			onJoystickMoveListener.onValueChanged(getAngle(), getPower());
			thread.interrupt();
		}
		if (onJoystickMoveListener != null
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
			thread = new Thread(this);
			thread.start();
			onJoystickMoveListener.onValueChanged(getAngle(), getPower());
		}
		return true;
	}

	public int getAngle() {
		if (xPosition > centerX) {
			if (yPosition < centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX)) * RAD + 90);
			} else if (yPosition > centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX)) * RAD) + 90;
			} else {
				return 90;
			}
		} else if (xPosition < centerX) {
			if (yPosition < centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX)) * RAD - 90);
			} else if (yPosition > centerY) {
				return (int) (Math.atan((yPosition - centerY)
						/ (xPosition - centerX)) * RAD) - 90;
			} else {
				return 90;
			}
		} else {
			if (yPosition <= centerY) {
				return 0;
			} else {
				return 180;
			}
		}
	}

	public int getPower() {
		return (int) (100 * Math.sqrt((xPosition - centerX)
				* (xPosition - centerX) + (yPosition - centerY)
				* (yPosition - centerY)) / joystickRadius);
	}

	public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
			long repeatInterval) {
		this.onJoystickMoveListener = listener;
		this.loopInterval = repeatInterval;
	}

	public static interface OnJoystickMoveListener {
		public void onValueChanged(int angle, int power);
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			post(new Runnable() {
				public void run() {
					onJoystickMoveListener.onValueChanged(getAngle(),
							getPower());
				}
			});
			try {
				Thread.sleep(loopInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}