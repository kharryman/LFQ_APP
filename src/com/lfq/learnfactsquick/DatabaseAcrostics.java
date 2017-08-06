package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

public class DatabaseAcrostics extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "lfq_acrostics.db";
	private static final int DATABASE_VERSION = 1;
	Context myContext;
	private static String status;
	private String url;
	private com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases loader_main;
	private com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases loader_acrostics;
	private com.lfq.learnfactsquick.EditTables.doLoadDatabases loader_tables;
	private com.lfq.learnfactsquick.EditUsers.doLoadDatabases loader_users;
	private com.lfq.learnfactsquick.EditNewWords.doLoadDatabases loader_edit_newwords;
	private com.lfq.learnfactsquick.NewWords.doLoadDatabases loader_newwords;
	private com.lfq.learnfactsquick.ShowAcrosticsTables.doLoadDatabases loader_show_acrostics;
	private com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases loader_sync;


	private enum activity {
		ACROSTICS, EDIT_NEWWORDS, MAIN, NEWWORDS, SHOW_ACROSTICS, SYNC, TABLES, USERS
	};

	private activity act;
	private ContentValues cv;
		
	private static DatabaseAcrostics sInstance;
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.EditNewWords.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.NewWords.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.ShowAcrosticsTables.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.EditTables.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseAcrostics getInstance(Context context,
			com.lfq.learnfactsquick.EditUsers.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseAcrostics(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}

	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_main = myLoader;
		myContext = context;
		status = "";
		cv = new ContentValues();
		act = activity.MAIN;
	}
	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_acrostics = myLoader;
		myContext = context;
		status = "";
		cv = new ContentValues();
		act = activity.ACROSTICS;
	}

	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.EditNewWords.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_edit_newwords = myLoader;
		myContext = context;
		status = "";
		cv = new ContentValues();
		act = activity.EDIT_NEWWORDS;
	}

	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.NewWords.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_newwords = myLoader;
		myContext = context;
		status = "";
		act = activity.ACROSTICS;
		cv = new ContentValues();
		act = activity.NEWWORDS;
	}

	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.ShowAcrosticsTables.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_show_acrostics = myLoader;
		myContext = context;
		status = "";
		cv = new ContentValues();
		act = activity.SHOW_ACROSTICS;
	}

	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_sync = myLoader;
		myContext = context;
		status = "";
		cv = new ContentValues();
		act = activity.SYNC;
	}

	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.EditTables.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_tables = myLoader;
		myContext = context;
		status = "";
		cv = new ContentValues();
		act = activity.TABLES;
	}

	public DatabaseAcrostics(Context context,
			com.lfq.learnfactsquick.EditUsers.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_users = myLoader;
		myContext = context;
		status = "";
		cv = new ContentValues();
		act = activity.USERS;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		System.out.println("LOADING ACROSTICS DATABASE");
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_lfq_database.php";
		String table = "", text = "";		
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("database", "acrostics"));
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
				System.out.println(json.getString("DEBUG") + ", count=" + ct_sqls);
				table = json.getString("Table" + ct_tables);
				for (int j = 0; j < ct_sqls; j++) {
					String sql_get = json.getString("sql" + j);
					if (!sql_get.contains(table)) {						
						table = json.getString("Table" + ct_tables);
						System.out.println("table="+table + ct_tables);
						ct_tables++;
						text = "Loading table " + table + "...";						
						switch (act) {
						case ACROSTICS:
							loader_acrostics.doProgress(text);
							break;
						case EDIT_NEWWORDS:
							loader_edit_newwords.doProgress(text);
							break;
						case MAIN:
							loader_main.doProgress(text);
							break;
						case NEWWORDS:
							loader_newwords.doProgress(text);
							break;
						case SHOW_ACROSTICS:
							loader_show_acrostics.doProgress(text);
							break;
						case SYNC:
							loader_sync.doProgress(text);
							break;
						case TABLES:
							loader_tables.doProgress(text);
							break;
						case USERS:
							loader_users.doProgress(text);
							break;
						default:
							break;
						}

					}
					if (sql_get != null && !sql_get.equals("")
							&& !sql_get.equals("null")) {
						database.execSQL(sql_get);
					}
					if (!json.getString("Image" + j).equals("***NO_IMAGE***")){
						cv.clear();
						byte[] imageInByte = Base64.decode(json.getString("Image" + j),
								0);
						cv.put("Image", imageInByte);
						database.update(table, cv, "Name=?", new String[]{json.getString("Name" + j)});
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
		//text += OldSynchronize.setDatabaseDate("DATE_ACR_SYNCED");		
		switch (act) {
		case ACROSTICS:
			loader_acrostics.doProgress(text);
			break;
		case EDIT_NEWWORDS:
			loader_edit_newwords.doProgress(text);
			break;
		case MAIN:
			loader_main.doProgress(text);
			break;
		case NEWWORDS:
			loader_newwords.doProgress(text);
			break;
		case SHOW_ACROSTICS:
			loader_show_acrostics.doProgress(text);
			break;
		case SYNC:
			loader_sync.doProgress(text);
			break;
		case TABLES:
			loader_tables.doProgress(text);
			break;
		case USERS:
			loader_users.doProgress(text);
			break;
		default:
			break;
		}

	}


	public String getStatus() {
		return status;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}

