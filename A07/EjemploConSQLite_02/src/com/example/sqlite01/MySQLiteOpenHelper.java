package com.example.sqlite01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper
{
	public static final String TABLE_NAME="MYWORDS";
	public static final String COLUMN_ID="_ID";
	public static final String COLUMN_WORD="WORD";
	public static final String TAG="MySQLiteOpenHelper";
	private static final String DATABASE_FILE_NAME=TABLE_NAME+".db";
	private static final int DATABASE_VERSION=1;
	
	private static final String SQL_CREATE_TABLE="create table "+TABLE_NAME+
			"("+COLUMN_ID+" integer primary key autoincrement, "+COLUMN_WORD+" text not null);";
	
	private static final String SQL_INSERT_FIRST_ROW="insert into "+TABLE_NAME+"("+COLUMN_WORD+")values ('word');";
	
	private static final String SQL_DROP_TABLE="drop table if exists "+ TABLE_NAME+";";
	
	public MySQLiteOpenHelper (Context context)
	{
		super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate (SQLiteDatabase arg0)
	{
		arg0.execSQL(SQL_CREATE_TABLE);
		arg0.execSQL(SQL_INSERT_FIRST_ROW);
		
	}
	
	@Override
	public void onUpgrade (SQLiteDatabase arg0, int arg1, int arg2)
	{
		
		arg0.execSQL(SQL_DROP_TABLE);
		onCreate(arg0);
	}
	
}
