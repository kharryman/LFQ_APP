package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseMnemonics extends SQLiteOpenHelper {
	public static final String COLUMN_ID = "_id";	
	private static final String DATABASE_NAME = "lfq_mnemonics.db";
	private static final int DATABASE_VERSION = 1;
	Context myContext;
	private String url;
	private com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases loader_main;
	private com.lfq.learnfactsquick.EditMnemonics.doLoadDatabases loader_edit_mnemonics;
	private com.lfq.learnfactsquick.Mnemonics.doLoadDatabases loader_mnemonics;	
	private com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases loader_sync;	

	private enum activity {
		EDIT_MNEMONICS, MAIN, MNEMONICS, SYNC
	};

	private activity act;
	
	private static DatabaseMnemonics sInstance;
	public static synchronized DatabaseMnemonics getInstance(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMnemonics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseMnemonics getInstance(Context context,
			com.lfq.learnfactsquick.EditMnemonics.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMnemonics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseMnemonics getInstance(Context context,
			com.lfq.learnfactsquick.Mnemonics.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMnemonics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseMnemonics getInstance(Context context,
			com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseMnemonics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public DatabaseMnemonics(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_main = myLoader;
		myContext = context;		
		act = activity.MAIN;
	}
	public DatabaseMnemonics(Context context, com.lfq.learnfactsquick.EditMnemonics.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_edit_mnemonics = myLoader;
		myContext = context;
		act = activity.EDIT_MNEMONICS;
	}
	public DatabaseMnemonics(Context context, com.lfq.learnfactsquick.Mnemonics.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_mnemonics = myLoader;
		myContext = context;
		act = activity.MNEMONICS;
	}
	public DatabaseMnemonics(Context context, com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_sync = myLoader;
		myContext = context;
		act = activity.SYNC;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_lfq_database.php";
		String table = "", text = "";
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("database", "mnemonics"));
		args.add(new BasicNameValuePair("is_new_tables", "yes"));
		args.add(new BasicNameValuePair("start_index", null));
		int ct_sqls = 0;
		int ct_tables = 0;
		JSONObject json = null;
		boolean is_continue = false;
		do {
			ct_tables = 0;
			try {
				json = OldSynchronize.makeHttpRequest(url, "POST", args);
				if (json == null) {
					return;
				}
				ct_sqls = json.getInt("Count");
				System.out.println(json.getString("DEBUG") + ", count="
						+ ct_sqls);
				table = json.getString("Table" + ct_tables);
				for (int j = 0; j < ct_sqls; j++) {
					String sql_get = json.getString("sql" + j);
					if (!sql_get.contains(table)) {
						table = json.getString("Table" + ct_tables);
						System.out.println("table=" + table + ct_tables);
						ct_tables++;
						text = "Loading table " + table + "...";
						switch (act) {						
						case EDIT_MNEMONICS:
							loader_edit_mnemonics.doProgress(text);
							break;
						case MAIN:
							loader_main.doProgress(text);
							break;
						case MNEMONICS:
							loader_mnemonics.doProgress(text);
							break;
						case SYNC:
							loader_sync.doProgress(text);
							break;
						default:
							break;
						}
					}
					if (sql_get != null && !sql_get.equals("")
							&& !sql_get.equals("null")) {
						database.execSQL(sql_get);
					}
				}
				is_continue = json.getBoolean("CONTINUE");
				if (is_continue) {
					args.set(
							2,
							new BasicNameValuePair("start_index", json
									.getString("Start_Index")));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} while (is_continue);

		ct_tables++;
		text = "ALL COMPLETE!!!    LOADED " + ct_tables + " TABLES!";
		text += OldSynchronize.setDatabaseDate("DATE_MNE_SYNCED");
		switch (act) {						
		case EDIT_MNEMONICS:
			loader_edit_mnemonics.doProgress(text);
			break;
		case MAIN:
			loader_main.doProgress(text);
			break;
		case MNEMONICS:
			loader_mnemonics.doProgress(text);
			break;
		case SYNC:
			loader_sync.doProgress(text);
			break;
		default:
			break;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
