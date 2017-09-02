package com.lfq.learnfactsquick;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSynchronize extends SQLiteOpenHelper {	
	private static final String DATABASE_NAME = "lfq_sync.db";
	private static final int DATABASE_VERSION = 1;
	Context myContext;

	private static DatabaseSynchronize sInstance;
	public static synchronized DatabaseSynchronize getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DatabaseSynchronize (context.getApplicationContext());
		}
		return sInstance;
	}
	
	public DatabaseSynchronize(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE `sync_table` (`_id` integer PRIMARY KEY AUTOINCREMENT, `Username` tinytext, `Password` tinytext, `SQL` text, `DB` tinytext, `Action` tinytext, `Table_name` tinytext, `Name` tinytext, `id` integer, `Device_Id` tinytext, `Is_Image` tinytext, `Image` blob);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
