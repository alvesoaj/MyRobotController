package com.zerokol;

import com.zerokol.views.JoystickView;
import com.zerokol.views.JoystickView.OnJoystickMoveListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyRobotControllerActivity extends Activity {
	private TextView angleTextView;
	private TextView powerTextView;
	private TextView directionTextView;
	private JoystickView joystick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		angleTextView = (TextView) findViewById(R.id.angleTextView);
		powerTextView = (TextView) findViewById(R.id.powerTextView);
		directionTextView = (TextView) findViewById(R.id.directionTextView);
		joystick = (JoystickView) findViewById(R.id.joystickView);

		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(int angle, int power, int direction) {
				// TODO Auto-generated method stub
				angleTextView.setText(" " + String.valueOf(angle) + "°");
				powerTextView.setText(" " + String.valueOf(power) + "%");
				switch (direction) {
				case JoystickView.FRONT:
					directionTextView.setText(R.string.front_lab);
					break;
				case JoystickView.FRONT_RIGHT:
					directionTextView.setText(R.string.front_right_lab);
					break;
				case JoystickView.RIGHT:
					directionTextView.setText(R.string.right_lab);
					break;
				case JoystickView.RIGHT_BOTTOM:
					directionTextView.setText(R.string.right_bottom_lab);
					break;
				case JoystickView.BOTTOM:
					directionTextView.setText(R.string.bottom_lab);
					break;
				case JoystickView.BOTTOM_LEFT:
					directionTextView.setText(R.string.bottom_left_lab);
					break;
				case JoystickView.LEFT:
					directionTextView.setText(R.string.left_lab);
					break;
				case JoystickView.LEFT_FRONT:
					directionTextView.setText(R.string.left_front_lab);
					break;
				default:
					directionTextView.setText(R.string.center_lab);
				}
			}
		}, JoystickView.DEFAULT_LOOP_INTERVAL);
	}
}