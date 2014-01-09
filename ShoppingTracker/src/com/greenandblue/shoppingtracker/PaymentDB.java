package com.greenandblue.shoppingtracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author Tal
 * class for managing SQLite DB and its tables
 */
public class PaymentDB {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_PAYMENT = "payment_type";
	public static final String KEY_PRODUCT = "product_type";
	public static final String KEY_NAME = "product_name";
	public static final String KEY_SUM = "sum";
	public static final String KEY_REMARK = "remark";
	public static final String KEY_DATE = "date";
	
	public static final String DB_NAME = "ShoppingDB";
	public static final String DB_TABLE = "PaymentsTable";
	public static final int DB_VERSION = 1;
	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	
	private DBHelper paymentHelper;
	private final Context myContext;
	private SQLiteDatabase paymentDatabase;
	
	/**
	 * This is helper for managing DB: create and upgrade
	 */
	private static class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DB_TABLE + " (" + 
			KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_PAYMENT + " TEXT NOT NULL, " + 
					KEY_PRODUCT + " TEXT NOT NULL, " +
					KEY_NAME + " TEXT NOT NULL, " +
					KEY_SUM + " INTEGER NOT NULL, " + 
					KEY_REMARK + " TEXT NOT NULL, " +
					KEY_DATE + " INTEGER NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
		}
	}
	
	/**
	 * constructor
	 */
	public PaymentDB(Context c)
	{
		myContext = c;
	}
	
	/**
	 * this is for writable DB opening
	 * @return DB
	 * @throws SQLException
	 */
	public PaymentDB open() throws SQLException
	{
		paymentHelper = new DBHelper(myContext);
		paymentDatabase = paymentHelper.getWritableDatabase();
		Log.i("MYBDTAG", "db opened");
		return this;
	}
	
	/**
	 * This closes open DB
	 */
	public void close()
	{
		paymentHelper.close();
		Log.i("MYBDTAG", "db closed");
	}
	
	/**
	 * This puts table values in a new row
	 */
	public long createEntry(String payment, String product, String name, int sum,
			String remark, long date) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_PAYMENT, payment);
		cv.put(KEY_PRODUCT, product);
		cv.put(KEY_NAME, name);
		cv.put(KEY_SUM, sum);
		cv.put(KEY_REMARK, remark);
		cv.put(KEY_DATE, date);
		return paymentDatabase.insert(DB_TABLE, null, cv);
		
	}
	
	/**
	 * This is to get all entries in the table
	 * used as help function during application debug
	 * @return all table as a String
	 */
	public String getData() {
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_NAME, KEY_PRODUCT, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, null, null, null, null, null);
		String result = "";
		Calendar cal;
		int iRow = cursor.getColumnIndex(KEY_ROWID);
		int iPay = cursor.getColumnIndex(KEY_PAYMENT);
		int iProduct = cursor.getColumnIndex(KEY_PRODUCT);
		int iName = cursor.getColumnIndex(KEY_NAME);
		int iSum = cursor.getColumnIndex(KEY_SUM);
		int iRemark = cursor.getColumnIndex(KEY_REMARK);
		int iDate = cursor.getColumnIndex(KEY_DATE);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			long lng = cursor.getLong(iDate);
			cal = Calendar.getInstance();
	        cal.setTimeInMillis(lng);
			result = result + cursor.getString(iRow) + "| " + cursor.getString(iPay)+ "| "+ cursor.getString(iProduct) + "| " + cursor.getString(iName)+"| "+ cursor.getInt(iSum) + "| " + cursor.getString(iRemark) + "| " + formatter.format(cal.getTime()) +"<br>";
		}
		return result;
	}
	
	/**
	 * get payment column value
	 * @param l
	 * @return payment
	 */
	public String getPayment(long l) {
		// TODO Auto-generated method stub
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_ROWID+"="+l, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			String payment = cursor.getString(1);
			Log.i("MYBDTAG", payment);
			return payment;
		}
		return null;
	}
	/**
	 * get product column value
	 * @param l
	 * @return product
	 */
	public String getProduct(long l) {
		// TODO Auto-generated method stub
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_ROWID+"="+l, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			String product = cursor.getString(2);
			Log.i("MYBDTAG", product);
			return product;
		}
		return null;
	}
	
	/**
	 * get name column value
	 * @param l
	 * @return name
	 */
	public String getName(long l) {
		// TODO Auto-generated method stub
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_ROWID+"="+l, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			String name = cursor.getString(3);
			Log.i("MYBDTAG", name);
			return name;
		}
		return null;
	}
	/**
	 * get sum column value
	 * @param l
	 * @return sum
	 */
	public int getSum(long l) {
		// TODO Auto-generated method stub
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_ROWID+"="+l, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			int sum = cursor.getInt(4);
			Log.i("MYBDTAG", "" +sum);
			return sum;
		}
		return 0;
	}
	
	/**
	 * get remark column value
	 * @param l
	 * @return remark
	 */
	public String getRemark(long l) {
		// TODO Auto-generated method stub
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_ROWID+"="+l, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			String remark = cursor.getString(5);
			Log.i("MYBDTAG", remark);
			return remark;
		}
		return null;
	}
	
	/**
	 * get date column value as milliseconds
	 * @param l
	 * @return date
	 */
	public long getDate(long l) {
		// TODO Auto-generated method stub
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_ROWID+"="+l, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			long date = cursor.getLong(6);
			return date;
		}
		return 0;
	}
	
	/**
	 * this is to update table entry
	 * @param lng
	 * @param payment
	 * @param product
	 * @param name
	 * @param sum
	 * @param remark
	 */
	public void updateEntry(long lng, String payment, String product, String name,
			int sum, String remark) {
		// TODO Auto-generated method stub
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(KEY_PAYMENT, payment);
		cvUpdate.put(KEY_PRODUCT, product);
		cvUpdate.put(KEY_NAME, name);
		cvUpdate.put(KEY_SUM, sum);
		cvUpdate.put(KEY_REMARK, remark);
		paymentDatabase.update(DB_TABLE, cvUpdate, KEY_ROWID+"="+lng, null);
	}
	
	/**
	 * this is to delete table entry
	 * help method, used during debug process only
	 * @param lDel
	 */
	public void deleteEntry(long lDel) {
		// TODO Auto-generated method stub
		paymentDatabase.delete(DB_TABLE, KEY_ROWID + "=" + lDel, null);
	}
	
	/**
	 * this is to get all entries in the table for current month
	 * as String with HTML tags, used by jquery functionality
	 * @return entries as a String
	 */
	public String lastMonthPayments()
	{
		String result = "<ul id='listXXX' data-role='listview' data-inset='true'>";
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    long startOfMonth = cal.getTimeInMillis();
	    String sql = "SELECT "+ KEY_ROWID + ", " + KEY_PAYMENT+", " + KEY_PRODUCT+ ", " + KEY_NAME + ", " + KEY_SUM + ", " + KEY_REMARK+", "+ KEY_DATE + " FROM " + DB_TABLE;
	    sql+= " WHERE " + KEY_DATE + ">"+ startOfMonth;
	    Cursor cursor = paymentDatabase.rawQuery(sql, null);
	    
	    int iRow = cursor.getColumnIndex(KEY_ROWID);
	    int iPayment = cursor.getColumnIndex(KEY_PAYMENT);
		int iProduct = cursor.getColumnIndex(KEY_PRODUCT);
		int iName = cursor.getColumnIndex(KEY_NAME);
		int iSum = cursor.getColumnIndex(KEY_SUM);
		int iRemark = cursor.getColumnIndex(KEY_REMARK);
		int iDate = cursor.getColumnIndex(KEY_DATE);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			result = result + "<li data-role='list-divider'>" + formatter.format(cursor.getLong(iDate)) + "</li>";
			result+= "<li><a href='#editchoice' id = '" + cursor.getString(iRow) + "'>";
			result+= "<h4>" + cursor.getString(iProduct) + ", " + cursor.getString(iName) +"</h4>";
			result+= "<p><strong> " + cursor.getString(iPayment) + ", sum: " + cursor.getString(iSum) + "</strong></p>";
			result+= "<p>Remarks: " + cursor.getString(iRemark) + "</p></a></li>";
		}
		result+="</ul>";
		return result;
	}
	
	/**
	 * this is to get sum of sum values for current month entries
	 * @return sum as int
	 */
	public int lastMonthSum()
	{
		int sum = 0;
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    long startOfMonth = cal.getTimeInMillis();
	    String sql = "SELECT SUM(" + KEY_SUM + ") FROM " + DB_TABLE + " WHERE " + KEY_DATE + ">"+ startOfMonth;
	    Cursor cursor = paymentDatabase.rawQuery(sql, null);
	    if(cursor.moveToFirst()) {
	    	sum = cursor.getInt(0);
	    	}
		
		return sum;
	}
	
	/**
	 * this is to get sums grouped by product for current month
	 * used by js functionality of Highcharts
	 * @return string of sums
	 */
	public String monthlySums()
	{
		String str = "";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		long monthAgo = cal.getTimeInMillis();
		
		String sql = "SELECT " + KEY_PRODUCT + ", SUM(" + KEY_SUM + ") FROM " + DB_TABLE; 
		sql += " WHERE " + KEY_DATE + ">" + monthAgo + " GROUP BY " + KEY_PRODUCT;
		Cursor cursor = paymentDatabase.rawQuery(sql, null);
		int iSum = cursor.getColumnIndex("SUM(" + KEY_SUM + ")");
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			str+= cursor.getInt(iSum);
			str+="|";
		}
		str = str.substring(0, str.length()-1);
		return str;
	}
	
	/**
	 * this is to get productss grouped by product for current month
	 * used by js functionality of Highcharts
	 * @return string of products
	 */
	public String monthlyProducts()
	{
		String str = "";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		long monthAgo = cal.getTimeInMillis();
		
		String sql = "SELECT " + KEY_PRODUCT + ", SUM(" + KEY_SUM + ") FROM " + DB_TABLE;
		sql += " WHERE " + KEY_DATE + ">" + monthAgo + " GROUP BY " + KEY_PRODUCT;;
		Cursor cursor = paymentDatabase.rawQuery(sql, null);
		int iProduct = cursor.getColumnIndex(KEY_PRODUCT);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			str+= cursor.getString(iProduct); 
			str+="|";
		}
		str = str.substring(0, str.length()-1);
		return str;
	}

	
}

