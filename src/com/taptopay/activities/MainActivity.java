package com.taptopay.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.fumptest.R;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;
import com.taptopay.paypal.ResultDelegate;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

	private static final String TAG = MainActivity.class.getName();
	// The PayPal server to be used - can also be ENV_NONE and ENV_LIVE
	private static final int server = PayPal.ENV_SANDBOX;
	// The ID of your application that you received from PayPal
	private static final String appID = "APP-80W284485P519543T";
	// This is passed in for the startActivityForResult() android function, the
	// value used is up to you
	private static final int request = 1;

	public static final String build = "10.12.09.8053";

	protected static final int INITIALIZE_SUCCESS = 0;
	protected static final int INITIALIZE_FAILURE = 1;
	private static String SERVER_URL = "http://10.11.20.186:5000/api/fump";
	private static final float DIFF = (float) 1.5;
	private SensorManager sensorManager;
	private Sensor mSensor;
	private TextView timeStamp, xText, yText, zText;
	private Button pay;
	float xLast;
	float yLast;
	float zLast;
	private long lastTimeStamp;
	private long timeStampSec;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_show);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		pay = (Button) findViewById(R.id.PAY);
		
		pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				PayPalPayment payment = exampleSimplePayment();	
//				// Use checkout to create our Intent.
//				Intent checkoutIntent = PayPal.getInstance().checkout(payment, this, new ResultDelegate());
//				// Use the android's startActivityForResult() and pass in our Intent. This will start the library.
//		    	startActivityForResult(checkoutIntent, request);
			}
		});
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
	
	private PayPalPayment exampleSimplePayment() {
		// Create a basic PayPalPayment.
		PayPalPayment payment = new PayPalPayment();
		// Sets the currency type for this payment.
    	payment.setCurrencyType("USD");
    	// Sets the recipient for the payment. This can also be a phone number.
    	payment.setRecipient("taptopaymerchant@gmail.com");
    	// Sets the amount of the payment, not including tax and shipping amounts.
    	payment.setSubtotal(new BigDecimal("8.25"));
    	// Sets the payment type. This can be PAYMENT_TYPE_GOODS, PAYMENT_TYPE_SERVICE, PAYMENT_TYPE_PERSONAL, or PAYMENT_TYPE_NONE.
    	payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);
    	
    	// PayPalInvoiceData can contain tax and shipping amounts. It also contains an ArrayList of PayPalInvoiceItem which can
    	// be filled out. These are not required for any transaction.
    	PayPalInvoiceData invoice = new PayPalInvoiceData();
    	// Sets the tax amount.
    	invoice.setTax(new BigDecimal("1.25"));
    	// Sets the shipping amount.
    	invoice.setShipping(new BigDecimal("4.50"));
    	
    	// PayPalInvoiceItem has several parameters available to it. None of these parameters is required.
    	PayPalInvoiceItem item1 = new PayPalInvoiceItem();
    	// Sets the name of the item.
    	item1.setName("Pink Stuffed Bunny");
    	// Sets the ID. This is any ID that you would like to have associated with the item.
    	item1.setID("87239");
    	// Sets the total price which should be (quantity * unit price). The total prices of all PayPalInvoiceItem should add up
    	// to less than or equal the subtotal of the payment.
    	item1.setTotalPrice(new BigDecimal("6.00"));
    	// Sets the unit price.
    	item1.setUnitPrice(new BigDecimal("2.00"));
    	// Sets the quantity.
    	item1.setQuantity(3);
    	// Add the PayPalInvoiceItem to the PayPalInvoiceData. Alternatively, you can create an ArrayList<PayPalInvoiceItem>
    	// and pass it to the PayPalInvoiceData function setInvoiceItems().
    	invoice.getInvoiceItems().add(item1);
    	
    	// Create and add another PayPalInvoiceItem to add to the PayPalInvoiceData.
    	PayPalInvoiceItem item2 = new PayPalInvoiceItem();
    	item2.setName("Well Wishes");
    	item2.setID("56691");
    	item2.setTotalPrice(new BigDecimal("2.25"));
    	item2.setUnitPrice(new BigDecimal("0.25"));
    	item2.setQuantity(9);
    	invoice.getInvoiceItems().add(item2);
    	
    	// Sets the PayPalPayment invoice data.
    	payment.setInvoiceData(invoice);
    	// Sets the merchant name. This is the name of your Application or Company.
    	payment.setMerchantName("The Gift Store");
    	// Sets the description of the payment.
    	payment.setDescription("Quite a simple payment");
    	// Sets the Custom ID. This is any ID that you would like to have associated with the payment.
    	payment.setCustomID("8873482296");
    	// Sets the Instant Payment Notification url. This url will be hit by the PayPal server upon completion of the payment.
    	payment.setIpnUrl("http://www.exampleapp.com/ipn");
    	// Sets the memo. This memo will be part of the notification sent by PayPal to the necessary parties.
    	payment.setMemo("Hi! I'm making a memo for a simple payment.");
    	
    	return payment;
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
		if (x - xLast > DIFF && (timeStampSec - lastTimeStamp) > 1000) {
			xText.setText("" + (x - xLast));
			yText.setText("" + (y - yLast));
			zText.setText("" + (z - zLast));
			timeStamp.setText("" + timeStampSec);
			Toast.makeText(this, "Tapped! :)", Toast.LENGTH_SHORT).show();
			new FumpServer().execute();
			lastTimeStamp = timeStampSec;
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
