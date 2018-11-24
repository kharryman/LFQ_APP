package com.lfq.learnfactsquick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lfq.learnfactsquick.Constants.cols.acrostics;
import com.lfq.learnfactsquick.Constants.cols.sync_table;
import com.lfq.learnfactsquick.Constants.cols.user_new_words;
import com.lfq.learnfactsquick.Constants.tables;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;

@SuppressLint("ClickableViewAccessibility")
public class Synchronize {
	private static final String TAG = "Synchronize";
	private static String text, autosync_result;
	JSONArray response = null;
	private static InputStream is = null;
	private static JSONObject jObj = null;
	private static String json_str = "";
	private static Cursor c = null;
	private static String username = "";

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
	private static String device_id = "";
	private static String results = "";
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public Synchronize(Context context) {
	}

	public static void updateAcrostics(
			com.lfq.learnfactsquick.MainLfqActivity.doSyncTo loader) {
		/*
		 * if (!isConnected()) { loader.doProgress("NOT CONNECTED." +
		 * c.getCount() + " UPDATES NOT SYNCED."); return; } c =
		 * MainLfqActivity.getAcrosticsDb().rawQuery(
		 * " SELECT name FROM sqlite_master " +
		 * " WHERE type='table' ORDER BY name", null); List<String> acrtabs =
		 * new ArrayList<String>(); if (c.moveToFirst()) { do { if
		 * (!c.getString(0).equals("android_metadata") &&
		 * !c.getString(0).equals("sqlite_sequence"))
		 * acrtabs.add(c.getString(0)); } while (c.moveToNext()); } c.close();
		 * int count_to = 0; String sql_upd = ""; params.clear(); for (int i =
		 * 0; i < acrtabs.size(); i++) { c =
		 * MainLfqActivity.getAcrosticsDb().rawQuery( "SELECT " + acrostics.Name
		 * + "," + acrostics.Acrostics + " FROM " + acrtabs.get(i) + " WHERE " +
		 * acrostics.Acrostics + "<>''", null); if (c.moveToFirst()) { do {
		 * sql_upd = "UPDATE " + acrtabs.get(i) + " SET " + acrostics.Acrostics
		 * + "='" + c.getString(c.getColumnIndex(acrostics.Acrostics)) +
		 * "' WHERE " + acrostics.Name + "='" +
		 * c.getString(c.getColumnIndex(acrostics.Name)) + "'";
		 * 
		 * //System.out.println("SQL=" + sql_upd); params.add(new
		 * BasicNameValuePair(sync_table.SQL + count_to, sql_upd)); count_to++;
		 * } while (c.moveToNext()); c.close(); } }
		 * System.out.println("# acrostics =" + count_to); params.add(new
		 * BasicNameValuePair("COUNT_SQL", String .valueOf(count_to))); url =
		 * "http://www.learnfactsquick.com/lfq_app_php/udpate_app_acr.php"; json
		 * = makeHttpRequest(url, "POST", params); if (json == null) {
		 * loader.doProgress("SERVER NOT RESPONDING."); }
		 * System.out.println(json.toString()); System.out.println("count_to=" +
		 * count_to); try { System.out.println("RESPONSE COUNT_SQL=" +
		 * json.getString("COUNT_SQL")); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

	}

	public static void doInsertNW(
			com.lfq.learnfactsquick.MainLfqActivity.doSyncTo loader) {
		/*
		 * if (!isConnected()) { loader.doProgress("NOT CONNECTED." +
		 * c.getCount() + " UPDATES NOT SYNCED."); return; } DatabaseNewwords dn
		 * = new DatabaseNewwords(LfqApp.getInstance()); SQLiteDatabase nw_db =
		 * dn.getWritableDatabase(); c =
		 * nw_db.rawQuery("SELECT * FROM harryman75_savednewwords", null); int
		 * ct_sql = c.getCount(); System.out.println("# harryman75 newwords=" +
		 * ct_sql); String sql_ins = ""; int count_to = 0; params.clear();
		 * String cols[] = c.getColumnNames(); for (int i=0;i<cols.length;i++){
		 * System.out.println("cols["+i+"]=" + cols[i]); }
		 * 
		 * if (c.moveToFirst()){ do { sql_ins =
		 * "INSERT INTO user_new_words (Username, Table_name, Date, Word) VALUES('harryman75','"
		 * + c.getString(c.getColumnIndex("Table_name")) + "','" +
		 * c.getString(c.getColumnIndex("MyDate")) + "','" +
		 * c.getString(c.getColumnIndex("Word")) + "')";
		 * 
		 * //System.out.println("SQL=" + sql_ins); params.add(new
		 * BasicNameValuePair(sync_table.SQL + count_to, sql_ins)); count_to++;
		 * } while (c.moveToNext()); c.close();
		 * 
		 * params.add(new BasicNameValuePair("COUNT_SQL",
		 * String.valueOf(ct_sql))); url =
		 * "http://www.learnfactsquick.com/lfq_app_php/insert_app_nw.php"; json
		 * = makeHttpRequest(url, "POST", params); if (json == null) {
		 * loader.doProgress("SERVER NOT RESPONDING."); }
		 * System.out.println(json.toString()); System.out.println("count_to=" +
		 * count_to); try { System.out.println("RESPONSE COUNT_SQL=" +
		 * json.getString("COUNT_SQL")); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * //for (int i = 0; i < count_to; i++) { // int j = i + 1; // try { //
		 * loader.doProgress(j + ")" + json.getString("to_results" + i)); // }
		 * catch (JSONException e) { // e.printStackTrace(); // } //} }
		 */

	}

	public static void doSyncTo(
			com.lfq.learnfactsquick.MainLfqActivity.doSyncTo loader) {
		c = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT * FROM " + tables.sync_table, null);
		if (!isConnected()) {
			loader.doProgress("NOT CONNECTED." + c.getCount()
					+ " UPDATES NOT SYNCED.");
			return;
		}
		text = "";
		autosync_result = "";
		// PASS SYNC TO QUERIES:
		loader.doProgress(c.getCount() + " Sync Entries");
		// long num_del = MainLfqActivity.getMiscDb().delete(tables.sync_table,
		// null, null);
		// System.out.println("NUMBER DELETED=" + num_del);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			int count_to = 0;
			device_id = telephonyManager.getDeviceId();
			System.out.println("SYNC TO DEVICE ID = " + device_id);
			params.add(new BasicNameValuePair("Device_Id", String
					.valueOf(device_id)));
			params.add(new BasicNameValuePair("Count_To_Queries", String
					.valueOf(c.getCount())));
			if (c.moveToFirst()) {
				do {
					System.out.println("SQL="
							+ c.getString(c.getColumnIndex(sync_table.SQL)));

					params.add(new BasicNameValuePair(
							sync_table.SQL + count_to, c.getString(c
									.getColumnIndex(sync_table.SQL))));
					params.add(new BasicNameValuePair("ID" + count_to, c
							.getString(c.getColumnIndex("_id"))));
					params.add(new BasicNameValuePair(sync_table.DB + count_to,
							c.getString(c.getColumnIndex(sync_table.DB))));
					params.add(new BasicNameValuePair(sync_table.Username
							+ count_to, c.getString(c
							.getColumnIndex(sync_table.Username))));
					params.add(new BasicNameValuePair(sync_table.Password
							+ count_to, c.getString(c
							.getColumnIndex(sync_table.Password))));
					params.add(new BasicNameValuePair(sync_table.Is_Image
							+ count_to, c.getString(c
							.getColumnIndex(sync_table.Is_Image))));
					if (c.getString(c.getColumnIndex(sync_table.Is_Image)) != null) {
						if (c.getString(c.getColumnIndex(sync_table.Is_Image))
								.equals("yes")) {
							params.add(new BasicNameValuePair(sync_table.Image
									+ count_to, Base64.encodeToString(
									c.getBlob(c
											.getColumnIndex(sync_table.Image)),
									0)));
						}
					}
					params.add(new BasicNameValuePair(sync_table.Table_name
							+ count_to, c.getString(c
							.getColumnIndex(sync_table.Table_name))));
					params.add(new BasicNameValuePair(sync_table.Name
							+ count_to, c.getString(c
							.getColumnIndex(sync_table.Name))));
					String image_string = "";
					if (c.getString(c.getColumnIndex(sync_table.Is_Image))
							.equals("yes")) {
						image_string = Base64.encodeToString(
								c.getBlob(c.getColumnIndex(sync_table.Image)),
								0);
					}
					to_list.add(new String[] {
							c.getString(c.getColumnIndex(sync_table.SQL)),
							c.getString(c.getColumnIndex(sync_table.Username)),
							c.getString(c.getColumnIndex(sync_table.Password)),
							c.getString(c.getColumnIndex(sync_table.Is_Image)),
							image_string,
							c.getString(c.getColumnIndex(sync_table.Table_name)),
							c.getString(c.getColumnIndex(sync_table.Name)) });

					count_to++;

				} while (c.moveToNext());
			}
			c.close();
			if (count_to > 0) {
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
					if (json.getString("to_results" + i).equals("SUCCESS")) {
						long sql_del = MainLfqActivity.getMiscDb().delete(
								tables.sync_table,
								"_id=?",
								new String[] { json.getString("to_results_id"
										+ i) });
						if (sql_del > 0) {
							System.out.println("DELETED " + sql_del
									+ " SQL TABLE ROW(S).");
						}
					}
				}
				for (int i = 0; i < count_to; i++) {
					int j = i + 1;
					loader.doProgress(j + ")"
							+ json.getString("to_results" + i) + ":"
							+ to_list.get(i)[0]);
				}
			}
			// loader.doProgress(json.getString("DEBUG"));

		} catch (Exception e) {
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
			device_id = telephonyManager.getDeviceId();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			SharedPreferences sharedPref = LfqApp.getInstance()
					.getSharedPreferences(
							LfqApp.getInstance().getString(
									R.string.preference_file_key),
							Context.MODE_PRIVATE);
			Log.d(TAG,
					"TIME_SYNCED_FROM="
							+ sharedPref.getString("TIME_SYNCED_FROM", null));
			params.add(new BasicNameValuePair("TIME_SYNCED_FROM", sharedPref
					.getString("TIME_SYNCED_FROM", null)));
			params.add(new BasicNameValuePair("Device_Id", device_id));
			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from.php";
			json = makeHttpRequest(url, "POST", params);
			if (json == null) {
				loader.doProgress("SERVER NOT RESPONDING.");
			}
			count_from = json.getInt("count_from");
			results = "";
			loader.doProgress(count_from + " QUERIES.");
			c = null;
			// System.out.println(json.toString());
			String db, id, is_image, imageString, query, table, name, action, user;
			Boolean done = true;
			for (int i = 0; i < count_from; i++) {
				done = false;
				db = json.getString(sync_table.DB + i);
				MainLfqActivity.setDatabase(db);
				id = json.getString("ID" + i);
				is_image = json.getString(sync_table.Is_Image + i);
				imageString = json.getString(sync_table.Image + i);
				query = json.getString(sync_table.SQL + i);
				table = json.getString("Table" + i);
				name = json.getString(sync_table.Name + i);
				action = json.getString(sync_table.Action + i);
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
					} catch (Exception e) {
						results = (i + 1) + ". NOT SYNCED " + query + "."
								+ e.getMessage();
						done = false;
						Log.e(TAG,
								"NOT SYCNED!!!: " + query + "."
										+ e.getMessage());
					}
					results += "<br />";
				} else {// IS IMAGE? -> INSERT:
					imageString = json.getString(sync_table.Image + i);
					byte[] imageInByte = Base64.decode(imageString, 0);
					cv.clear();
					cv.put(sync_table.Image, imageInByte);
					cv.put("Has_Image", "yes");
					MainLfqActivity.getDatabase().update(table, cv,
							sync_table.Name + "=?", new String[] { name });
					results = "Updated " + table + ", " + name + ". ";
				}
				// System.out.println(results);
				loader.doProgress(results);
			}// END FOR LOOP
				// RESET DATABASES SYNCED DATES:
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
					.format(Calendar.getInstance().getTime());
			sharedPref.edit().putString("TIME_SYNCED_FROM", timeStamp).commit();
			Log.d(TAG, "SET TIME_SYCNED FROM=" + timeStamp);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static int getFromCount(Boolean is_report, String which_loader) {
		int ret = 0;
		if (!isConnected()) {
			if (is_report) {
				if (which_loader == "db") {
					MainLfqActivity.getDoLoadDatabases().doProgress(
							"NOT CONNECTED." + c.getCount()
									+ " UPDATES NOT SYNCED.");
				}
				if (which_loader == "to") {
					MainLfqActivity.getDoSyncTo().doProgress(
							"NOT CONNECTED." + c.getCount()
									+ " UPDATES NOT SYNCED.");
				}
			}
			return ret;
		}

		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// TelephonyManager telephonyManager = (TelephonyManager)
			// LfqApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
			device_id = telephonyManager.getDeviceId();
			// params.add(new BasicNameValuePair("Device_Id", device_id));
			url = "http://www.learnfactsquick.com/lfq_app_php/get_sync_from_count.php";
			SharedPreferences sharedPref = LfqApp.getInstance()
					.getSharedPreferences(
							LfqApp.getInstance().getString(
									R.string.preference_file_key),
							Context.MODE_PRIVATE);
			params.add(new BasicNameValuePair("TIME_SYNCED_FROM", sharedPref
					.getString("TIME_SYNCED_FROM", null)));
			JSONObject json_from_count = makeHttpRequest(url, "POST", params);
			if (is_report) {
				if (json_from_count == null) {
					if (which_loader == "sync_db") {
						MainLfqActivity.getDoLoadDatabases().doProgress(
								"SERVER NOT RESPONDING.");
					}
					if (which_loader == "sync_to") {
						MainLfqActivity.getDoSyncTo().doProgress(
								"SERVER NOT RESPONDING.");
					}

				}
			}
			if (json_from_count != null) {
				ret = json_from_count.getInt("COUNT");
			}
			// loader.doProgress(json_from_count.getString("DEBUG"));
		} catch (JSONException e) {
			e.printStackTrace();
			if (is_report) {
				if (which_loader == "sync_db") {
					MainLfqActivity.getDoLoadDatabases().doProgress(
							"...FAILED CONNECTION.");
				}
				if (which_loader == "sync_to") {
					MainLfqActivity.getDoSyncTo().doProgress(
							"...FAILED CONNECTION.");
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

	static class autoSync extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			System.out.println("AUTOSYNC CALLED");
			username = "";
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
			String text = "";
		}

		@Override
		protected String doInBackground(String... vars) {
			String is_image_str = "no";
			String sql = vars[0];
			String db = vars[1];
			String action = vars[2];
			String table = vars[3];
			String name = vars[4];
			String is_image = vars[5];
			String image = vars[6];
			String autosync_result = "";

			if (is_image.equals("TRUE")) {
				is_image_str = "yes";
			} else {
				is_image_str = "no";
			}
			MainLfqActivity.getMiscDb().delete(
					tables.sync_table,
					sync_table.Action + "=? AND " + sync_table.Table_name
							+ "=? AND " + sync_table.Name + "=?",
					new String[] { action, table, name });
			SharedPreferences sharedPref = LfqApp.getInstance()
					.getSharedPreferences(
							LfqApp.getInstance().getString(
									R.string.preference_file_key),
							Context.MODE_PRIVATE);
			if (sharedPref.getBoolean("AUTO SYNC", false) == false
					|| !isConnected()) {
				ContentValues cv = new ContentValues();
				cv.clear();
				cv.put(sync_table.SQL, sql);
				cv.put(sync_table.DB, db);
				cv.put(sync_table.Action, action);
				cv.put(sync_table.Username, username);
				cv.put(sync_table.Table_name, table);
				cv.put(sync_table.Name, name);
				cv.put(sync_table.Is_Image, is_image_str);
				cv.put(sync_table.Image, image);
				System.out.println("INSERTING TO SYNC DB");
				MainLfqActivity.getMiscDb().insert(tables.sync_table, null, cv);
				return "UPDATED SYNC TABLE.";
			}
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.clear();
				System.out.println("sql=" + sql);
				params.add(new BasicNameValuePair(sync_table.SQL, sql));
				params.add(new BasicNameValuePair("User", username));
				params.add(new BasicNameValuePair(sync_table.Is_Image,
						is_image_str));
				if (is_image.equals("TRUE")) {
					params.add(new BasicNameValuePair("table", table));
					params.add(new BasicNameValuePair(sync_table.DB, db));
					params.add(new BasicNameValuePair("name", name));
					// Base64.encodeToString(image, 0);
					params.add(new BasicNameValuePair("image", image));
				}
				String url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_app_auto.php";
				JSONObject myjson = makeHttpRequest(url, "POST", params);
				if (myjson == null) {
					return "";
				}
				System.out.println("JSON=" + gson.toJson(myjson));
				autosync_result = myjson.getString("result");
				String debug = myjson.getString("DEBUG");
				text += " SYNC TO LFQ " + autosync_result + ".";
				if (sql != "") {
					text += "mySQL = " + sql + ". ";
				}
				text += debug;
			} catch (JSONException e) {
				e.printStackTrace();
				text += "...FAILED CONNECTION.";
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

		@Override
		protected void onPostExecute(String result) {
			returnResult(text);
		}

		private String returnResult(String result) {
			return result;
		}

	}

	public static String autoSync(String sql, String db, String action,
			String table, String name, boolean is_image, byte[] image) {
		System.out.println("AUTOSYNC CALLED");
		text = "";
		autosync_result = "";
		TelephonyManager telephonyManager = (TelephonyManager) LfqApp
				.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		// device_id = telephonyManager.getDeviceId();
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
		MainLfqActivity.getMiscDb().delete(
				tables.sync_table,
				sync_table.Action + "=? AND " + sync_table.Table_name
						+ "=? AND " + sync_table.Name + "=?",
				new String[] { action, table, name });
		if (sharedPref.getBoolean("AUTO SYNC", false) == false
				|| !isConnected()) {
			cv.clear();
			cv.put(sync_table.SQL, sql);
			cv.put(sync_table.DB, db);
			cv.put(sync_table.Action, action);
			cv.put(sync_table.Username, username);
			cv.put(sync_table.Table_name, table);
			cv.put(sync_table.Name, name);
			// cv.put("Device_Id", device_id);
			cv.put(sync_table.Is_Image, is_image_str);
			cv.put(sync_table.Image, image);
			// sync_db, acr_db, alp_db, dictionary_db, events_db, misc_db,
			// mne_db, newwords_db, numbers_db, users_db
			System.out.println("INSERTING TO SYNC DB");
			MainLfqActivity.getMiscDb().insert(tables.sync_table, null, cv);
			return "UPDATED SYNC TABLE.";
		}
		try {
			// FIRST CHECK FOR CONFLICTS:
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// params.add(new BasicNameValuePair("DB", db));
			// params.add(new BasicNameValuePair("Action", action));
			// params.add(new BasicNameValuePair("Table", table));
			// params.add(new BasicNameValuePair("Name", name));
			// params.add(new BasicNameValuePair("Device_Id", device_id));
			// url =
			// "http://www.learnfactsquick.com/lfq_app_php/synchronize_check_conflict.php";
			// json = makeHttpRequest(url, "POST", params);
			// if (json.getBoolean("IS_CONFLICT")) {
			// cv.put("SQL", sql);
			// cv.put("DB", db);
			// cv.put("Username", username);
			// cv.put("Table_name", table);
			// cv.put("Name", name);
			// cv.put("Device_Id", device_id);
			// MainLfqActivity.getSyncDb().insert("sync_table", null, cv);
			// return "HAS CONFLICT, WILL RESOLVE AT NEXT APP RUN";
			// }
			// -------------------------------
			params.clear();
			System.out.println("sql=" + sql);
			params.add(new BasicNameValuePair(sync_table.SQL, sql));
			params.add(new BasicNameValuePair("User", username));
			params.add(new BasicNameValuePair(sync_table.Is_Image, is_image_str));
			if (is_image == true) {
				params.add(new BasicNameValuePair("table", table));
				params.add(new BasicNameValuePair(sync_table.DB, db));
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("image", Base64
						.encodeToString(image, 0)));
			}
			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_app_auto.php";
			JSONObject myjson = makeHttpRequest(url, "POST", params);
			if (myjson == null) {
				return "";
			}
			System.out.println("JSON=" + gson.toJson(myjson));
			autosync_result = myjson.getString("result");
			String debug = myjson.getString("DEBUG");
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
			System.out.println("JSON_STR=" + json_str);
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
				MainLfqActivity.getMiscDb()
						.execSQL(json.getString("Query" + i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return upd_results;
	}

}