package com.taptopay.paypal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

import com.paypal.android.MEP.PayPalResultDelegate;

public class ResultDelegate implements PayPalResultDelegate, Serializable {

	private static final long serialVersionUID = 10001L;
	public static final String TAG = ResultDelegate.class.getName();
	private static String SERVER_URL = "http://10.11.20.186:5000/api/result";
	private String result;

	/**
	 * Notification that the payment has been completed successfully.
	 * 
	 * @param payKey			the pay key for the payment
	 * @param paymentStatus		the status of the transaction
	 */
	public void onPaymentSucceeded(String payKey, String paymentStatus) {
		Log.d("RESULT", "SUCCESSS!!!");
		result = "OK";
		new FumpServerResult().execute();
		SimpleDemo.resultTitle = "SUCCESS";
		SimpleDemo.resultInfo = "You have successfully completed your transaction.";
		SimpleDemo.resultExtra = "Key: " + payKey;
	}

	
	/**
	 * Notification that the payment has failed.
	 * 
	 * @param paymentStatus		the status of the transaction
	 * @param correlationID		the correlationID for the transaction failure
	 * @param payKey			the pay key for the payment
	 * @param errorID			the ID of the error that occurred
	 * @param errorMessage		the error message for the error that occurred
	 */
	public void onPaymentFailed(String paymentStatus, String correlationID,
			String payKey, String errorID, String errorMessage) {
		result = "NOK";
		new FumpServerResult().execute();
		SimpleDemo.resultTitle = "FAILURE";
		SimpleDemo.resultInfo = errorMessage;
		SimpleDemo.resultExtra = "Error ID: " + errorID + "\nCorrelation ID: "
				+ correlationID + "\nPay Key: " + payKey;
	}

	/**
	 * Notification that the payment was canceled.
	 * 
	 * @param paymentStatus		the status of the transaction
	 */
	public void onPaymentCanceled(String paymentStatus) {
		result = "NOK";
		new FumpServerResult().execute();
		SimpleDemo.resultTitle = "CANCELED";
		SimpleDemo.resultInfo = "The transaction has been cancelled.";
		SimpleDemo.resultExtra = "";
	}
	
public class FumpServerResult extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String data = "{ \"result\" : \"" + result
					+ "\", \"id\" : \"Alex\" }";
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