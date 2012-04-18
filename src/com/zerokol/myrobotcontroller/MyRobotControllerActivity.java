package com.zerokol.myrobotcontroller;

import com.zerokol.views.JoystickView;
import com.zerokol.views.JoystickView.OnJoystickMoveListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MyRobotControllerActivity extends Activity {
	// Constants
	private final int MANAGE_BLUETOOTH = 0;
	// Variables
	private TextView angleTextView;
	private TextView powerTextView;
	private TextView directionTextView;
	private JoystickView joystick;
	@SuppressWarnings("unused")
	private BluetoothAdapter bluetooth;
	private Resources myResources;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		myResources = getResources();

		angleTextView = (TextView) findViewById(R.id.angleTextView);
		powerTextView = (TextView) findViewById(R.id.powerTextView);
		directionTextView = (TextView) findViewById(R.id.directionTextView);
		joystick = (JoystickView) findViewById(R.id.joystickView);

		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(int angle, int power, int direction) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MANAGE_BLUETOOTH, 0,
				myResources.getText(R.string.settings_lab)).setIcon(
				R.drawable.settings_icon);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MANAGE_BLUETOOTH:
			Intent intent = new Intent(MyRobotControllerActivity.this,
					BluetoothManageActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}
}