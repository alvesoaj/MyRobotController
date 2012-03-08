package com.zerokol;


import com.zerokol.joystick.Joystick;
import com.zerokol.joystick.Joystick.OnJoystickMoveListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyRobotControllerActivity extends Activity {
	private TextView angleTextView;
	private TextView powerTextView;
	private Joystick joystick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		angleTextView = (TextView) findViewById(R.id.angleTextView);
		powerTextView = (TextView) findViewById(R.id.powerTextView);
		joystick = (Joystick) findViewById(R.id.joystick);

		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(int angle, int power) {
				// TODO Auto-generated method stub
				angleTextView.setText(" " + String.valueOf(angle) + "°");
				powerTextView.setText(" " + String.valueOf(power) + "%");

			}
		}, Joystick.DEFAULT_LOOP_INTERVAL);
	}
}