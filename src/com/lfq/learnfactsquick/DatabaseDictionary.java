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

public class DatabaseDictionary extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "lfq_dictionary.db";
	private static final int DATABASE_VERSION = 1;
	SQLiteDatabase db;
	Context myContext;
	private String url;
	private com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases loader_main;
	private com.lfq.learnfactsquick.AnagramGenerator.doLoadDatabases loader_anagram;
	private com.lfq.learnfactsquick.Dictionary.doLoadDatabases loader_dictionary;
	private com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases loader_acrostics;
	private com.lfq.learnfactsquick.EditDictionary.doLoadDatabases loader_edit_dictionary;
	private com.lfq.learnfactsquick.MajorSystemGenerator.doLoadDatabases loader_major;
	private com.lfq.learnfactsquick.MnemonicGenerator.doLoadDatabases loader_mne_generator;
	private com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases loader_sync;
	private com.lfq.learnfactsquick.Timeline.doLoadDatabases loader_timeline;

	private enum activity {
		ANAGRAM, DICTIONARY, EDIT_ACROSTICS, EDIT_DICTIONARY, MAIN, MAJOR, MNE_GENERATOR, SYNC, TIMELINE
	};

	private activity act;	

	private static DatabaseDictionary sInstance;
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.AnagramGenerator.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.Dictionary.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.EditDictionary.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.MajorSystemGenerator.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.MnemonicGenerator.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	public static synchronized DatabaseDictionary getInstance(Context context,
			com.lfq.learnfactsquick.Timeline.doLoadDatabases myLoader) {
		if (sInstance == null) {
			sInstance = new DatabaseDictionary(context.getApplicationContext(),
					myLoader);
		}
		return sInstance;
	}
	
	public DatabaseDictionary(Context context,
			com.lfq.learnfactsquick.MainLfqActivity.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_main = myLoader;
		myContext = context;		
		act = activity.MAIN;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.AnagramGenerator.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_anagram = myLoader;
		myContext = context;
		act = activity.ANAGRAM;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.Dictionary.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_dictionary = myLoader;
		myContext = context;
		act = activity.DICTIONARY;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.EditAcrostics.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_acrostics = myLoader;
		myContext = context;
		act = activity.EDIT_ACROSTICS;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.EditDictionary.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_edit_dictionary = myLoader;
		myContext = context;
		act = activity.EDIT_DICTIONARY;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.MajorSystemGenerator.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_major = myLoader;
		myContext = context;
		act = activity.MAJOR;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.MnemonicGenerator.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_mne_generator = myLoader;
		myContext = context;
		act = activity.MNE_GENERATOR;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.OldSynchronize.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_sync = myLoader;
		myContext = context;
		act = activity.SYNC;
	}
	public DatabaseDictionary(Context context, com.lfq.learnfactsquick.Timeline.doLoadDatabases myLoader) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		loader_timeline = myLoader;
		myContext = context;
		act = activity.TIMELINE;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_lfq_database.php";
		String text = "";
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("database", "dictionary"));
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
				text = "Loading words .";
				switch (act) {
				case ANAGRAM:
					loader_anagram.doProgress(text);
					break;
				case DICTIONARY:
					loader_dictionary.doProgress(text);
					break;
				case EDIT_ACROSTICS:
					loader_acrostics.doProgress(text);
					break;
				case EDIT_DICTIONARY:
					loader_edit_dictionary.doProgress(text);
					break;
				case MAIN:
					loader_main.doProgress(text);
					break;
				case MAJOR:
					loader_major.doProgress(text);
					break;
				case MNE_GENERATOR:
					loader_mne_generator.doProgress(text);
					break;
				case SYNC:
					loader_sync.doProgress(text);
					break;
				case TIMELINE:
					loader_timeline.doProgress(text);
					break;
				default:
					break;
				}
				for (int j = 0; j < ct_sqls; j++) {
					String sql_get = json.getString("sql" + j);															
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
		text += OldSynchronize.setDatabaseDate("DATE_DIC_SYNCED");
		switch (act) {
		case ANAGRAM:
			loader_anagram.doProgress(text);
			break;
		case DICTIONARY:
			loader_dictionary.doProgress(text);
			break;
		case EDIT_ACROSTICS:
			loader_acrostics.doProgress(text);
			break;
		case EDIT_DICTIONARY:
			loader_edit_dictionary.doProgress(text);
			break;
		case MAIN:
			loader_main.doProgress(text);
			break;
		case MAJOR:
			loader_major.doProgress(text);
			break;
		case MNE_GENERATOR:
			loader_mne_generator.doProgress(text);
			break;
		case SYNC:
			loader_sync.doProgress(text);
			break;
		case TIMELINE:
			loader_timeline.doProgress(text);
			break;
		default:
			break;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
