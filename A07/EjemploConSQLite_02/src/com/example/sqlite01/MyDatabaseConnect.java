package com.example.sqlite01;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class MyDatabaseConnect
{
	private static String TAGA = "MyDatabaseConnect";
	private SQLiteDatabase mDatabase;
	private MySQLiteOpenHelper mOpenHelper;
	private String[] mAllColumns =
		{
				MySQLiteOpenHelper.COLUMN_ID, MySQLiteOpenHelper.COLUMN_WORD
	};
	
	public MyDatabaseConnect (Context aContext)
	{
		mOpenHelper = new MySQLiteOpenHelper(aContext);
		
	}
	
	public static MyDatabaseConnect getOpenedDatabase (Context aContext)
	{
		MyDatabaseConnect databaseConnect = new MyDatabaseConnect(aContext);
		databaseConnect.open();
		return databaseConnect;
		
	}
	
	public static void closeDatabase (MyDatabaseConnect aDatabaseConnect)
	{
		if (aDatabaseConnect == null)
		{
			return;
		}
		aDatabaseConnect.close();
		aDatabaseConnect = null;
	}
	
	public boolean open ()
	{
		boolean result = true;
		try
		{
			mDatabase = mOpenHelper.getWritableDatabase();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public void close ()
	{
		mDatabase.close();
		mOpenHelper.close();
	}
	
	public int insertMyWord (MyWord aMyWord)
	{
		long rowId;
		ContentValues contentValues = aMyWord.wordToContentValues();
		rowId =
			mDatabase
					.insert(MySQLiteOpenHelper.TABLE_NAME, null, contentValues);
		return (int) rowId;
	}
	
	public boolean deleteMyWord (MyWord aMyWord)
	{
		String whereClause;
		if (aMyWord._id == 0)
		{
			whereClause = aMyWord.wordToWhereClause();
		}
		else
		{
			whereClause = aMyWord.idToWhereClause();
		}
		int rowCount =
			mDatabase.delete(MySQLiteOpenHelper.TABLE_NAME, whereClause, null);
		return (rowCount > 0);
	}
	
	public boolean updateMyWord (MyWord aMyWord)
	{
		int rowCount =
			mDatabase.update(MySQLiteOpenHelper.TABLE_NAME,
					aMyWord.wordToContentValues(), aMyWord.idToWhereClause(),
					null);
		return (rowCount > 0);
	}
	
	public ArrayList<MyWord> selectMyWords (MyWord aMyWord)
	{
		String whereClause = null;
		ArrayList<MyWord> arrayList = new ArrayList<MyWord>();
		if (aMyWord != null)
		{
			if (aMyWord._id != 0)
			{
				whereClause = aMyWord.idToWhereClause();
				
			}
			else if (aMyWord != null)
			{
				whereClause += "OR" + aMyWord.wordToWhereClause();
			}
		}
		Cursor cursor =
			mDatabase.query(MySQLiteOpenHelper.TABLE_NAME, mAllColumns,
					whereClause, null, null, null, null);
		boolean isMovedToFirst=cursor.moveToFirst();
		if (isMovedToFirst == false)
		{
			return arrayList;
		}
		
		while (!cursor.isAfterLast())
		{
			ContentValues contentValues = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
			
			MyWord myWord = MyWord.getInstanceFromContentValues(contentValues);
			
			arrayList.add(myWord);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		
		
		return arrayList;
	}
}
