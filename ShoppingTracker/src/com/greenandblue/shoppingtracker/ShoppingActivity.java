package com.greenandblue.shoppingtracker;


import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * main activity of application which uses WebView for UI
 * @author Tal
 *
 */
public class ShoppingActivity extends Activity {
	
	private static final String HTML_ROOT = "file:///android_asset/www/";
	private WebView webView = null;
	private JavascriptAdapter adapter;
	private SharedPreferences sp;
	private PaymentDB info;
	
	/**
	 * onCreate - uses WebView and JS interface via JavascriptAdapter class object
	 */
	@SuppressLint("SetJavaScriptEnabled") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = PreferenceManager.getDefaultSharedPreferences(ShoppingActivity.this);
		info = new PaymentDB(ShoppingActivity.this);
		adapter = new JavascriptAdapter();
		webView = new WebView(this); 
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true); 
        webView.addJavascriptInterface(adapter, "ob");
        webView.loadUrl(HTML_ROOT + "menu.html");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping, menu);
		return true; 
	}
	
	/**
	 * this is to limit hardware back button functionality to WEbView only
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // Check if the key event was the Back button and if there's history
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	        webView.goBack();
	        return true;
	    }
	    // If it wasn't the Back key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * object from this class is used by js interface to run methods of this class
	 * @author Tal
	 *
	 */
	class JavascriptAdapter 
	{
		/**
		 * creates new entry in DB
		 * @param payment
		 * @param product
		 * @param name
		 * @param sum
		 * @param remark
		 */
		public void createEntry(String payment, String product, String name, int sum, String remark)
		{
			try
			{
				Calendar rightNow = Calendar.getInstance();
				long date = rightNow.getTimeInMillis();
				info.open();
				info.createEntry(payment, product.trim(), name, sum, remark, date);
				info.close();
				Log.i("SQLtag", "ENTRY ADDED");
				startService(new Intent(ShoppingActivity.this, LimitNotificationService.class)); 
			}
			catch(Exception e)
			{
				String error = e.toString();
				e.printStackTrace();
				Dialog d = new Dialog(ShoppingActivity.this);
				d.setTitle("Payment not added, try later");
				TextView tv = new TextView(ShoppingActivity.this);
				tv.setText(error);
				d.setContentView(tv);
				d.show();
			}
		}
		
		/**
		 * updates the entry in DB
		 * @param id
		 * @param payment
		 * @param product
		 * @param name
		 * @param sum
		 * @param remark
		 */
		public void updateDBEntry(long id, String payment, String product, String name, int sum, String remark)
		{
			try{
				info.open();
				info.updateEntry(id, payment, product.trim(), name, sum, remark);
				info.close();
				Log.i("SQLtag", "ENTRY updated");
			}
			catch(Exception e)
			{
				String error = e.toString();
				e.printStackTrace();
				Dialog d = new Dialog(ShoppingActivity.this);
				d.setTitle("Update failed, try later");
				TextView tv = new TextView(ShoppingActivity.this);
				tv.setText(error);
				d.setContentView(tv);
				d.show();
			}
		}

		/**
		 * works in jquery/js functionality
		 * @return all table entries represented as String
		 */
		public String showData() throws Exception
		{
			info.open();
			String data = info.getData();
			info.close();
			return data;
			
		}
		
		/**
		 * works in jquery/js functionality
		 * @return current month entries as a string with HTML tags
		 */
		public String lastPaymentsAsList() throws Exception
		{
			//PaymentDB info = new PaymentDB(ShoppingActivity.this);
			info.open();
			String dataStr = info.lastMonthPayments();
			info.close();
			return dataStr;
		}
		
		/**
		 * works in jquery/js functionality for entry update
		 * @param row
		 * @return entry as JSON object
		 * @throws JSONException
		 */
		public JSONObject getEntry(int row) throws JSONException
		{
			Log.i("MYDBTAGGGG", "In method getEntry");
			long id = (long)row;
			JSONObject entry = new JSONObject();

			info.open();
			String retPayment = info.getPayment(id);
			String retProduct = info.getProduct(id);
			String retName = info.getName(id);
			int retSum = info.getSum(id);
			String retRemark = info.getRemark(id);
			info.close();

			entry.put("id", new Integer(row));
			entry.put("payment", retPayment);
			entry.put("product", retProduct);
			entry.put("name", retName);
			entry.put("sum", new Integer(retSum));
			entry.put("remark", retRemark);

			return entry;
		}
		
		/**
		 * works in jquery/js functionality for limit budget settings
		 * @return limit as int
		 */
		public int loadPrefs()
		{
			int limit = sp.getInt("LIMIT", 0);
			
			return limit;
		}
		
		/**
		 * saves new budget limit value
		 * works in jquery/js functionality
		 * @param key
		 * @param value
		 */
		public void savePrefs(String key, int value)
		{
			Editor edit = sp.edit();
			edit.putInt(key, value);
			edit.commit();
			startService(new Intent(ShoppingActivity.this, LimitNotificationService.class));
		}
		
		/**
		 * works in highcharts/js functionality
		 * @return sums by product type as a string
		 */
		public String sumStatistics() throws Exception
		{
			info.open();
			String data = info.monthlySums();
			info.close();

			return data;
		}

		/**
		 * works in highcharts/js functionality
		 * @return product types as a string
		 */
		public String prodStatistics() throws Exception
		{
			info.open();
			String data = info.monthlyProducts();
			info.close();

			return data;
		}
		
	}

}
