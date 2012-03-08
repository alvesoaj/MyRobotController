package com.zerokol;


import com.zerokol.views.JoystickView;
import com.zerokol.views.JoystickView.OnJoystickMoveListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyRobotControllerActivity extends Activity {
	private TextView angleTextView;
	private TextView powerTextView;
	private JoystickView joystick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		angleTextView = (TextView) findViewById(R.id.angleTextView);
		powerTextView = (TextView) findViewById(R.id.powerTextView);
		joystick = (JoystickView) findViewById(R.id.joystickView);

		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(int angle, int power) {
				// TODO Auto-generated method stub
				angleTextView.setText(" " + String.valueOf(angle) + "°");
				powerTextView.setText(" " + String.valueOf(power) + "%");

			}
		}, JoystickView.DEFAULT_LOOP_INTERVAL);
	}
}