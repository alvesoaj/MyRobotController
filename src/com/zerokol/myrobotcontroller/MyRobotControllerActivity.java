package com.zerokol.myrobotcontroller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import com.zerokol.views.joystickview.JoystickView;
import com.zerokol.views.joystickview.JoystickView.OnJoystickMoveListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MyRobotControllerActivity extends Activity {
	// Constants
	private final int MANAGE_BLUETOOTH = 0;
	// Variables
	private String label = "MyRobotController";
	private TextView angleTextView;
	private TextView powerTextView;
	private TextView directionTextView;
	private JoystickView joystick;
	private BluetoothAdapter bluetooth;
	private Resources myResources;
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private Timer timer = new Timer();
	private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private int angleTemp;
	private int powerTemp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_robot_controller);

		myResources = getResources();

		angleTextView = (TextView) findViewById(R.id.angleTextView);
		powerTextView = (TextView) findViewById(R.id.powerTextView);
		directionTextView = (TextView) findViewById(R.id.directionTextView);
		joystick = (JoystickView) findViewById(R.id.joystickView);

		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(int angle, int power, int direction) {
				angleTemp = angle;
				powerTemp = power;
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
		});

		bluetooth = BluetoothAdapter.getDefaultAdapter();

		if (getIntent().hasExtra("address")) {
			device = bluetooth.getRemoteDevice(getIntent().getStringExtra(
					"address"));
			Log.w(label,
					"Device: " + device.getName() + " | " + device.getAddress());
			AsyncTask<Integer, Void, Void> connectTask = new AsyncTask<Integer, Void, Void>() {
				@Override
				protected Void doInBackground(Integer... params) {
					try {
						socket = device.createRfcommSocketToServiceRecord(uuid);
						socket.connect();
						sendMessage("You Rock Guy!");
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								sendInfos();
							}
						}, 0, 1000);
					} catch (IOException e) {
						Log.d(label, e.getMessage());
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
				}
			};
			connectTask.execute();
		}

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

	private void sendInfos() {
		sendMessage(String.valueOf(angleTemp) + "," + String.valueOf(powerTemp)
				+ "\n");
	}

	private void sendMessage(String message) {
		OutputStream outStream;
		try {
			outStream = socket.getOutputStream();
			// Add a stop character.
			byte[] byteArray = (message + " ").getBytes();
			byteArray[byteArray.length - 1] = 0;
			outStream.write(byteArray);
		} catch (IOException e) {
		}
	}
}