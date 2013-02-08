package com.example.fumptest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
	private static final float DIFF = 2;
	private SensorManager sensorManager;
	private Sensor mSensor;
	private TextView timeStamp,xText,yText,zText;
	float xLast;
	float yLast;
	float zLast;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		
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
		
//		if (x > DIFF) {
//			xText.setText("" + x);
//			yText.setText("" + y);
//			zText.setText("" + z);
//			timeStamp.setText("" + System.currentTimeMillis());
//			Toast.makeText(this, "Fumped! :)", Toast.LENGTH_SHORT).show();
//		}
		
		if (x - xLast > DIFF) {
			xText.setText("" + (x - xLast));
			yText.setText("" + (y - yLast));
			zText.setText("" + (z - zLast));
			timeStamp.setText("" + System.currentTimeMillis());
			Toast.makeText(this, "Fumped! :)", Toast.LENGTH_SHORT).show();
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
	    sensorManager.registerListener(this,
	        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
	        SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  @Override
	  protected void onPause() {
	    // unregister listener
	    super.onPause();
	    sensorManager.unregisterListener(this);
	  }
}
