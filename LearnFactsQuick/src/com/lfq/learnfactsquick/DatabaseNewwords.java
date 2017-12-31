package com.lfq.learnfactsquick;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseNewwords extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "lfq_newwords.db";
	private static final int DATABASE_VERSION = 1;	
	Context myContext;

	public DatabaseNewwords(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}	
}
