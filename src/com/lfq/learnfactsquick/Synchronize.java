package com.lfq.learnfactsquick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

@SuppressLint("ClickableViewAccessibility")
public class Synchronize {
	private static String text, autosync_result;
	JSONArray response = null;
	private static InputStream is = null;
	private static JSONObject jObj = null;
	private static String json_str = "";
	private static Cursor c = null;
	private static String username = "";
	private static List<NameValuePair> params = new ArrayList<NameValuePair>();

	private static List<String[]> to_list = new ArrayList<String[]>(),
			lfq_conflicts = new ArrayList<String[]>(),
			app_conflicts = new ArrayList<String[]>();

	private static String url = "";

	ArrayList<HashMap<String, String>> row_params;

	private static ContentValues cv = new ContentValues();
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	private static JSONObject json;
	private static TelephonyManager telephonyManager = (TelephonyManager) LfqApp
			.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
	//private static String device_id = "";
	private static String results = "";

	public Synchronize(Context context) {
	}


	public static void doSyncTo(
			com.lfq.learnfactsquick.MainLfqActivity.doSyncTo loader) {		
		c = MainLfqActivity.getSyncDb().rawQuery("SELECT * FROM `sync_table`", null);
		if (!isConnected()) {
			loader.doProgress("NOT CONNECTED." + c.getCount()
					+ " UPDATES NOT SYNCED.");
			return;
		}
		text = "";
		autosync_result = "";
		// PASS SYNC TO QUERIES:
		try {
			int count_to = 0;
			params.clear();
			if (c.moveToFirst()) {
				do {
					params.add(new BasicNameValuePair("SQL" + count_to, c
							.getString(c.getColumnIndex("SQL"))));
					params.add(new BasicNameValuePair("ID" + count_to, c
							.getString(c.getColumnIndex("_id"))));
					params.add(new BasicNameValuePair("DB" + count_to, c
							.getString(c.getColumnIndex("DB"))));
					params.add(new BasicNameValuePair("Username" + count_to, c
							.getString(c.getColumnIndex("Username"))));
					params.add(new BasicNameValuePair("Password" + count_to, c
							.getString(c.getColumnIndex("Password"))));
					params.add(new BasicNameValuePair("Is_Image" + count_to, c
							.getString(c.getColumnIndex("Is_Image"))));
					if (c.getString(c.getColumnIndex("Is_Image")) != null) {
						if (c.getString(c.getColumnIndex("Is_Image")).equals(
								"yes")) {
							params.add(new BasicNameValuePair("Image" + count_to, Base64
									.encodeToString(c.getBlob(c
											.getColumnIndex("Image")), 0)));
						}
					}
					params.add(new BasicNameValuePair("Table_name" + count_to,
							c.getString(c.getColumnIndex("Table_name"))));
					params.add(new BasicNameValuePair("Name" + count_to, c
							.getString(c.getColumnIndex("Name"))));
					//params.add(new BasicNameValuePair("Device_Id" + count_to, c.getString(c.getColumnIndex("Device_Id"))));
					// sql,user,password,is_image,image,table,name,device_id
					String image_string="";
					if (c.getString(c.getColumnIndex("Is_Image")).equals("yes")){
						image_string = Base64.encodeToString(c.getBlob(c.getColumnIndex("Image")),0);
					}
					to_list.add(new String[] {
							c.getString(c.getColumnIndex("SQL")),
							c.getString(c.getColumnIndex("Username")),
							c.getString(c.getColumnIndex("Password")),
							c.getString(c.getColumnIndex("Is_Image")),
							image_string,
							c.getString(c.getColumnIndex("Table_name")),
							c.getString(c.getColumnIndex("Name")),
							//c.getString(c.getColumnIndex("Device_Id"))
							});
					count_to++;
				} while (c.moveToNext());
			}
			c.close();
			params.add(new BasicNameValuePair("Count_To_Queries", String
					.valueOf(count_to)));
			//device_id = telephonyManager.getDeviceId();
			//params.add(new BasicNameValuePair("Device_Id", device_id));
			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_to.php";
			json = makeHttpRequest(url, "POST", params);
			if (json == null) {
				loader.doProgress("SERVER NOT RESPONDING.");
			}
			System.out.println(json.toString());
			System.out.println("count_to=" + count_to);
			for (int i = 0; i < count_to; i++) {
				System.out.println("results" + i + "="
						+ json.getString("to_results" + i));
			}
			for (int i = 0; i < count_to; i++) {
				int j = i + 1;
				loader.doProgress(j + ")" + json.getString("to_results" + i)
						+ ":" + to_list.get(i)[0]);				
			    MainLfqActivity.getSyncDb().delete("sync_table", null, null);
				
			}
			//loader.doProgress(json.getString("DEBUG"));

		} catch (JSONException e) {
			e.printStackTrace();
			loader.doProgress("...FAILED CONNECTION.");
		}
	}

	public static void doSyncFrom(
			com.lfq.learnfactsquick.MainLfqActivity.doSyncFrom loader) {
		// --------------------------------------------------
		// DO FROM LFQ QUERIES:
		int count_from;
		results = "";
		try {			
			//device_id = telephonyManager.getDeviceId();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//params.add(new BasicNameValuePair("Device_Id", device_id));
			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from.php";
			json = makeHttpRequest(url, "POST", params);
			if (json == null) {
				loader.doProgress("SERVER NOT RESPONDING.");
			}
			count_from = json.getInt("count_from");
			results = "";
			loader.doProgress(count_from+" QUERIES.");
			c = null;			
			//System.out.println(json.toString());
			String db, id, is_image, imageString, query, table, name, action, user;
			// sync_db, acr_db, alp_db, dictionary_db, events_db, misc_db, mne_db, nw_db, num_db, users_db;
			Boolean is_acr=false, is_dic=false, is_events=false, is_misc=false, is_mne=false, is_nw=false, is_num=false, is_users=false;
			Boolean done = false;
			for (int i = 0; i < count_from; i++) {
				done =false;
				db = json.getString("DB" + i);				
				MainLfqActivity.setDatabase(db);
				id = json.getString("ID" + i);
				is_image = json.getString("Is_Image" + i);
				imageString = json.getString("Image" + i);
				query = json.getString("SQL" + i);				
				table = json.getString("Table" + i);
				name = json.getString("Name" + i);
				action = json.getString("Action" + i);
				user = json.getString("User" + i);
				for (String[] str : to_list) {
					if (Arrays.asList(str).contains(table)
							&& Arrays.asList(str).contains(name)) {
						app_conflicts.add(str);
					}
				}
				lfq_conflicts.add(new String[] { db, id, query, is_image,
						imageString, table, name, action, user });				
				if (is_image.equals("false")) {// NOT IMAGE:
					try {
						MainLfqActivity.getDatabase().execSQL(query);
						results = (i + 1) + ". SYNCED: " + query + ". ";
						done = true;
					} catch (Exception e) {
						results = (i + 1) + ". NOT SYNCED " + query + "."
								+ e.getMessage();
					}
					results += "<br />";
				} else {// IS IMAGE? -> INSERT:
					imageString = json.getString("Image" + i);
					byte[] imageInByte = Base64.decode(imageString, 0);
					cv.clear();
					cv.put("Image", imageInByte);
					cv.put("Has_Image", "yes");
					MainLfqActivity.getDatabase().update(table, cv, "Name=?", new String[] { name });
					results = "Updated " + table + ", " + name + ". ";
					done = true;
				}
				if (done==true){
					if (db.equals("acr_db") && is_acr==false){
						setDatabaseDate("DATE_ACR_SYNCED");
						is_acr=true;
					}
					if (db.equals("dictionary_db") && is_dic==false){
						setDatabaseDate("DATE_DIC_SYNCED");
						is_dic=true;
					}
					if (db.equals("events_db") && is_events==false){
						setDatabaseDate("DATE_EVT_SYNCED");
						is_events=true;
					}
					if (db.equals("misc_db") && is_misc==false){
						setDatabaseDate("DATE_MSC_SYNCED");
						is_misc=true;
					}
					if (db.equals("mne_db") && is_mne==false){
						setDatabaseDate("DATE_MNE_SYNCED");
						is_mne=true;
					}
					if (db.equals("nw_db") && is_nw==false){
						setDatabaseDate("DATE_NWS_SYNCED");
						is_nw=true;
					}
					if (db.equals("num_db") && is_num==false){
						setDatabaseDate("DATE_NUM_SYNCED");
						is_num=true;
					}
					if (db.equals("users_db") && is_users==false){
						setDatabaseDate("DATE_USR_SYNCED");
						is_users=true;
					}
				}
				//System.out.println(results);
				loader.doProgress(results);
			}// END FOR LOOP
				// RESET DATABASES SYNCED DATES:			

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

		public static int getFromCount(Boolean is_report, String which_loader) {
		int ret = 0;		
		if (!isConnected()) {
			if (is_report) {				
				if (which_loader=="db"){					
					MainLfqActivity.getDoLoadDatabases().doProgress("NOT CONNECTED." + c.getCount()
							+ " UPDATES NOT SYNCED.");
				}
				if (which_loader=="to"){
					MainLfqActivity.getDoSyncTo().doProgress("NOT CONNECTED." + c.getCount()
							+ " UPDATES NOT SYNCED.");
				}
			}
			return ret;
		}

		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			TelephonyManager telephonyManager = (TelephonyManager) LfqApp
					.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
			//device_id = telephonyManager.getDeviceId();
			//params.add(new BasicNameValuePair("Device_Id", device_id));
			url = "http://www.learnfactsquick.com/lfq_app_php/get_sync_from_count.php";
			JSONObject json_from_count = makeHttpRequest(url, "POST", params);
			if (is_report) {
				if (json_from_count == null) {
					if (which_loader=="sync_db"){
						MainLfqActivity.getDoLoadDatabases().doProgress("SERVER NOT RESPONDING.");
					}
					if (which_loader=="sync_to"){
						MainLfqActivity.getDoSyncTo().doProgress("SERVER NOT RESPONDING.");
					}
					
				}
			}
			if (json_from_count != null){
			   ret = json_from_count.getInt("COUNT");
			}
			//loader.doProgress(json_from_count.getString("DEBUG"));
		} catch (JSONException e) {
			e.printStackTrace();
			if (is_report) {
				if (which_loader=="sync_db"){
					MainLfqActivity.getDoLoadDatabases().doProgress("...FAILED CONNECTION.");
				}
				if (which_loader=="sync_to"){
					MainLfqActivity.getDoSyncTo().doProgress("...FAILED CONNECTION.");
				}
				
			}
		}		
		return ret;
	}

	public static int getConflictCount() {
		return lfq_conflicts.size();
	}

	public static void doSyncConflicts(
			com.lfq.learnfactsquick.MainLfqActivity.doSyncConflicts loader) {
		// ----------------------------------------------
		// RETURN THE CONFLICTS OF TO AND FROM:

		for (String[] str : lfq_conflicts) {
			loader.doProgress(Helpers.arrToString(str, "@"));
		}
		for (String[] str : app_conflicts) {
			loader.doProgress(Helpers.arrToString(str, "@"));
		}
	}

	public static String autoSync(String sql, String db, String action,
			String table, String name, boolean is_image, byte[] image) {
		System.out.println("AUTOSYNC CALLED");
		text = "";
		autosync_result = "";
		TelephonyManager telephonyManager = (TelephonyManager) LfqApp
				.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		//device_id = telephonyManager.getDeviceId();
		SharedPreferences sharedPref = LfqApp.getInstance()
				.getSharedPreferences(
						LfqApp.getInstance().getString(
								R.string.preference_file_key),
						Context.MODE_PRIVATE);		
		String is_image_str = "no";
		username = "";
		if (Helpers.getLoginStatus() == true) {
			username = Helpers.getUsername();
		}
		if (is_image == true) {
			is_image_str = "yes";
		} else {
			is_image_str = "no";
		}
		MainLfqActivity.getSyncDb().delete("sync_table", "Action=? AND Table_name=? AND Name=?",
				new String[] { action, table, name });
		if (sharedPref.getBoolean("AUTO SYNC", false) == false
				|| !isConnected()) {
			cv.clear();
			cv.put("SQL", sql);
			cv.put("DB", db);
			cv.put("Action", action);
			cv.put("Username", username);
			cv.put("Table_name", table);
			cv.put("Name", name);
			//cv.put("Device_Id", device_id);
			cv.put("Is_Image", is_image_str);
			cv.put("Image", image);
			// sync_db, acr_db, alp_db, dictionary_db, events_db, misc_db,
			// mne_db, newwords_db, numbers_db, users_db
			System.out.println("INSERTING TO SYNC DB");
			MainLfqActivity.getSyncDb().insert("sync_table", null, cv);
			return "UPDATED SYNC TABLE.";
		}
		try {
			// FIRST CHECK FOR CONFLICTS:
			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("DB", db));
//			params.add(new BasicNameValuePair("Action", action));
//			params.add(new BasicNameValuePair("Table", table));
//			params.add(new BasicNameValuePair("Name", name));
//			params.add(new BasicNameValuePair("Device_Id", device_id));
//			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_check_conflict.php";
//			json = makeHttpRequest(url, "POST", params);
//			if (json.getBoolean("IS_CONFLICT")) {
//				cv.put("SQL", sql);
//				cv.put("DB", db);
//				cv.put("Username", username);
//				cv.put("Table_name", table);
//				cv.put("Name", name);
//				cv.put("Device_Id", device_id);
//				MainLfqActivity.getSyncDb().insert("sync_table", null, cv);
//				return "HAS CONFLICT, WILL RESOLVE AT NEXT APP RUN";
//			}
			// -------------------------------            
			params.clear();
			System.out.println("sql="+sql);
			params.add(new BasicNameValuePair("SQL", sql));
			params.add(new BasicNameValuePair("User", username));			
			//params.add(new BasicNameValuePair("Device_Id", device_id));
			params.add(new BasicNameValuePair("Is_Image", is_image_str));
			if (is_image == true) {
				params.add(new BasicNameValuePair("table", table));
				params.add(new BasicNameValuePair("DB", db));
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("image", Base64
						.encodeToString(image, 0)));				
			}
			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_app_auto.php";
			json = makeHttpRequest(url, "POST", params);
			if (json == null) {
				return "";
			}
			autosync_result = json.getString("result");
			String debug = json.getString("DEBUG");
			text += " SYNC TO LFQ " + autosync_result + ".";
			if (sql != "") {
				text += "mySQL = " + sql + ". ";
			}
			text += debug;
		} catch (JSONException e) {
			e.printStackTrace();
			text += "...FAILED CONNECTION.";
		}
		return text;
	}

	public static boolean isConnected() {
		boolean ret = true;
		if (!Helpers.isNetworkAvailable()) {
			ret = false;
		}
		return ret;
	}

	public static JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {
		// Making HTTP request
		try {
			// check for request method
			if (method == "POST") {
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				httpResponse.addHeader("Cache-Control", "no-cache");
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} else if (method == "GET") {
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");

			}
			is.close();
			json_str = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json_str);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;
	}

	static public String setDatabaseDate(String date_database_synced) {
		if (!isConnected()) {
			return "NOT CONNECTED";
		}
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_database_date.php";
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		TelephonyManager telephonyManager = (TelephonyManager) LfqApp
				.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		//device_id = telephonyManager.getDeviceId();
		//args.add(new BasicNameValuePair("device_id", device_id));
		args.add(new BasicNameValuePair("date_database_synced",
				date_database_synced));// EG: DATE_ACR_SYNCHED
		String ret = "";
		try {
			JSONObject json = OldSynchronize.makeHttpRequest(url, "POST", args);
			if (json == null) {
				return "RETURNED NULL";
			}
			ret = json.getString("result");
		} catch (JSONException e) {
			return e.getMessage();
		}
		return ret;
	}

	public static List<Boolean> updateLfqUsers(String action, String username,
			String password, String new_password, String question, String answer) {
		url = "http://www.learnfactsquick.com/lfq_app_php/update_lfq_users.php";
		List<Boolean> upd_results = new ArrayList<Boolean>();
		upd_results.add(false);// add false to is_connected
		upd_results.add(false);// add is_username_exists
		upd_results.add(false);// add is_success
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("action", action));
		args.add(new BasicNameValuePair("username", username));
		args.add(new BasicNameValuePair("password", password));
		if (action.equals("update_password")) {
			args.add(new BasicNameValuePair("new_password", new_password));
			args.add(new BasicNameValuePair("question", question));
			args.add(new BasicNameValuePair("answer", answer));
		}
		try {
			if (!isConnected()) {
				return upd_results;
			}
			JSONObject json = makeHttpRequest(url, "POST", args);
			if (json == null) {
				return upd_results;
			}
			upd_results.set(0, true);// set is_connected and not_null to true
			if (json.getBoolean("username_exists")) {
				upd_results.set(1, true);
				return upd_results;
			}
			if (json.getBoolean("result")) {
				upd_results.set(1, true);
				return upd_results;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return upd_results;
	}

	public static List<Integer> updateAppUsers(String username, Context context) {
		url = "http://www.learnfactsquick.com/lfq_app_php/update_app_users.php";
		List<Integer> upd_results = new ArrayList<Integer>();
		upd_results.add(0);// initialize is_connected to false
		upd_results.add(0);// initialize is_username_exists to false
		upd_results.add(0);// initialize is_updated to false
		upd_results.add(0);// initialize count_queries to 0
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", username));
		try {
			if (!isConnected()) {
				return upd_results;
			}
			JSONObject json = makeHttpRequest(url, "POST", args);
			if (json == null) {
				return upd_results;
			}
			upd_results.set(0, 1);// set is_connected and not_null to true
			if (!json.getBoolean("username_exists")) {
				return upd_results;
			}
			upd_results.set(1, 1);// set is_username_exists to true
			if (!json.getBoolean("is_updated")) {
				return upd_results;
			}
			upd_results.set(2, 1);// set is_updated to true
			int count_queries = json.getInt("Count");
			upd_results.set(3, count_queries);
			for (int i = 0; i < count_queries; i++) {
				MainLfqActivity.getUsersDb().execSQL(json.getString("Query" + i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return upd_results;
	}

}