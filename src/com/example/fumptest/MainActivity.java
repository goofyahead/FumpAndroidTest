package com.example.fumptest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

	private static final String TAG = MainActivity.class.getName();
	private static String SERVER_URL = "http://my-fump.herokuapp.com/api/fump";
	private static final float DIFF = (float) 2;
	private SensorManager sensorManager;
	private Sensor mSensor;
	private TextView timeStamp, xText, yText, zText;
	float xLast;
	float yLast;
	float zLast;
	private long timeStampSec;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		timeStamp = (TextView) findViewById(R.id.timeStamp);
		xText = (TextView) findViewById(R.id.xValue);
		yText = (TextView) findViewById(R.id.Yvalue);
		zText = (TextView) findViewById(R.id.zvALUE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			getAccelerometer(event);
		}
	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		// if (x > DIFF) {
		// xText.setText("" + x);
		// yText.setText("" + y);
		// zText.setText("" + z);
		// timeStamp.setText("" + System.currentTimeMillis());
		// Toast.makeText(this, "Fumped! :)", Toast.LENGTH_SHORT).show();
		// }
		timeStampSec = System.currentTimeMillis();
		if (x - xLast > DIFF) {
			xText.setText("" + (x - xLast));
			yText.setText("" + (y - yLast));
			zText.setText("" + (z - zLast));
			timeStamp.setText("" + timeStampSec);
			Toast.makeText(this, "Fumped! :)", Toast.LENGTH_SHORT).show();
			new FumpServer().execute();
		}

		xLast = x;
		yLast = y;
		zLast = z;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManager
				.registerListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	private class FumpServer extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String data = "{ \"timestamp\" : " + timeStampSec
					+ ", \"id\" : \"android deveice\" }";
			byte[] postData = null;
			try {
				postData = data.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			InputStream is = null;
			try {
				URL url = new URL(SERVER_URL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Content-Length",
						Integer.toString(postData.length));
				conn.setUseCaches(false);

				OutputStream out;
				out = conn.getOutputStream();
				out.write(postData);
				out.close();

				int responseCode = conn.getResponseCode();
				Log.d(TAG, "response code: " + responseCode);

				is = conn.getInputStream();

				// Convert the InputStream into a string
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
				String response = new String(total);
				Log.d(TAG, "response is: " + response);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
	}
}
