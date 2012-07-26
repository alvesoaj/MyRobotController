package com.zerokol.myrobotcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class BluetoothManageActivity extends Activity {
	// Constants
	private final int MANAGE_BLUETOOTH = 0;
	private final int SCAN_DEVICES = 1;
	// Variables
	private String label = "MyRobotController";
	private ListView bluetoothDevicesListView;
	private Resources myResources;
	private BluetoothAdapter bluetooth;
	private BluetoothDevice device;
	private String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
	private String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
	private ArrayList<String> bluetoothDeviceNames = new ArrayList<String>();
	private ArrayAdapter<String> aa;
	private ArrayList<BluetoothDevice> remoteDevices = new ArrayList<BluetoothDevice>();
	private AdapterContextMenuInfo acmi = null;
	private BluetoothSocket socket;
	// private UUID uuid =
	// UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
	private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private BroadcastReceiver discoveryResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice remoteDevice;
			remoteDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (!remoteDevices.contains(remoteDevice)) {
				remoteDevices.add(remoteDevice);
				String remoteDeviceName = remoteDevice.getName();
				if (remoteDeviceName == "") {
					remoteDeviceName = remoteDevice.getAddress();
				}
				Log.w(label, "Found: " + remoteDevice.getName() + " | "
						+ remoteDevice.getAddress());
				Toast.makeText(
						getApplicationContext(),
						myResources.getText(R.string.bluetooth_discovered_lab)
								+ " " + remoteDeviceName, Toast.LENGTH_SHORT)
						.show();
				bluetoothDeviceNames.add(0, remoteDeviceName);
				aa.notifyDataSetChanged();
			}
		}
	};

	private BroadcastReceiver discoveryMonitor = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (dStarted.equals(intent.getAction())) {
				Toast.makeText(getApplicationContext(),
						R.string.bluetooth_dis_start_lab, Toast.LENGTH_SHORT)
						.show();
			} else if (dFinished.equals(intent.getAction())) {
				Toast.makeText(getApplicationContext(),
						R.string.bluetooth_dis_comp_lab, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bluetooth_manage);

		myResources = getResources();

		bluetoothDevicesListView = (ListView) findViewById(R.id.bluetoothDevicesListView);

		bluetooth = BluetoothAdapter.getDefaultAdapter();

		String toastText;
		if (bluetooth.isEnabled()) {
			toastText = ((String) myResources
					.getText(R.string.bluetooth_on_lab))
					+ " "
					+ bluetooth.getName() + " : " + bluetooth.getAddress();
		} else {
			toastText = (String) myResources
					.getText(R.string.bluetooth_off_lab);
		}
		Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

		registerReceiver(discoveryMonitor, new IntentFilter(dStarted));
		registerReceiver(discoveryMonitor, new IntentFilter(dFinished));

		registerReceiver(discoveryResult, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));

		Log.w(label, String.valueOf(bluetoothDeviceNames.size()));

		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, bluetoothDeviceNames);

		bluetoothDevicesListView.setAdapter(aa);

		bluetoothDevicesListView
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						acmi = (AdapterContextMenuInfo) menuInfo;
						menu.add(0, 0, 0, R.string.select_device_lab);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MANAGE_BLUETOOTH, 0,
				myResources.getText(R.string.turn_bluetooth_lab)).setIcon(
				R.drawable.bluetooth);
		menu.add(0, SCAN_DEVICES, 0,
				myResources.getText(R.string.scan_devices_lab)).setIcon(
				R.drawable.scan_robot);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MANAGE_BLUETOOTH:
			if (bluetooth.isEnabled()) {
				bluetooth.disable();
			} else {
				bluetooth.enable();
			}
			return true;
		case SCAN_DEVICES:
			if (!bluetooth.isDiscovering()) {
				// Limpando a linta de estações encontradas previamente
				remoteDevices.clear();
				bluetooth.startDiscovery();
			} else {
				bluetooth.cancelDiscovery();
			}
			break;
		}
		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String name = bluetoothDeviceNames.get(acmi.position);
		Iterator<BluetoothDevice> iterator = remoteDevices.iterator();
		while (iterator.hasNext()) {
			BluetoothDevice element = iterator.next();
			if (name.equals(element.getName())
					|| name.equals(element.getAddress())) {
				device = element;
				Log.w(label,
						"Device: " + device.getName() + " | "
								+ device.getAddress());
				break;
			}
		}

		if (device != null) {
			AsyncTask<Integer, Void, Void> connectTask = new AsyncTask<Integer, Void, Void>() {
				@Override
				protected Void doInBackground(Integer... params) {
					try {
						socket = device.createRfcommSocketToServiceRecord(uuid);
						socket.connect();
						sendMessage("You rock guy!");
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

		return true;
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(discoveryMonitor);
		unregisterReceiver(discoveryResult);
		super.onDestroy();
	}

	private void sendMessage(String message){
		OutputStream outStream;
		try {
			outStream = socket.getOutputStream();
			// Add a stop character.
			byte[] byteArray = (message + " ").getBytes();
			byteArray[byteArray.length - 1] = 0;
			outStream.write(byteArray);
		} catch (IOException e) { }
	}

	@SuppressWarnings("unused")
	private String listenForMessage(){
		String result = "";
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		try {
			InputStream instream = socket.getInputStream();
			int bytesRead = -1;
			while (true) {
				bytesRead = instream.read(buffer);
				if (bytesRead != -1) {
					while ((bytesRead == bufferSize) && (buffer[bufferSize-1] != 0)){
						result = result + new String(buffer, 0, bytesRead);
						bytesRead = instream.read(buffer);
					}
					result = result + new String(buffer, 0, bytesRead - 1);
					return result;
				}
			}
		} catch (IOException e) {}
		return result;
	}
}
