package com.example.ejemploconsqlite_01;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyDatabaseConnect
{
	private static String TAG = "MyDatabaseConnect";
	
	private SQLiteDatabase mDatabase;
	private MySQLiteOpenHelper mOpenHelper;
	private String[] mAllColumns =
		{ MySQLiteOpenHelper.COLUMN_ID, MySQLiteOpenHelper.COLUMN_WORD };
	
	public MyDatabaseConnect (Context aContext)
	{
		Log.v(TAG, "MyDatabaseConnect()");
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
		Log.v(TAG, "open()");
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
		Log.v(TAG, "close()");
		mDatabase.close();
		mOpenHelper.close();
	}
	
	public int insertMyWord (MyWord aMyWord)
	{
		long rowId;
		ContentValues contentValues = aMyWord.wordToContentValues();
		
		Log.v(TAG, "insert(" + aMyWord.word + ")");
		
		rowId =
			mDatabase
					.insert(MySQLiteOpenHelper.TABLE_NAME, null, contentValues);
		
		String whereClause = "rowid = " + rowId;
		
		Cursor cursor = mDatabase.query(MySQLiteOpenHelper.TABLE_NAME,
				mAllColumns, whereClause, null, null, null, null);
		
		boolean isMovedToFirst = cursor.moveToFirst();
		if (isMovedToFirst == false)
		{
			return (int) rowId;
		}
		
		while (!cursor.isAfterLast())
		{
			ContentValues contentValuesA = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(cursor, contentValuesA);
			
			MyWord myWord = MyWord.getInstanceFromContentValues(contentValuesA);
			rowId = myWord._id;
			
			cursor.moveToNext();
		}
		
		cursor.close();
		
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
		Log.v(TAG, "delete( " + whereClause + ")");
		
		int rowCount =
			mDatabase.delete(MySQLiteOpenHelper.TABLE_NAME, whereClause, null);
		return (rowCount > 0);
	}
	
	public boolean updateMyWord (MyWord aMyWord)
	{
		Log.v(TAG,
				"update( to:" + aMyWord.word + ", where:"
						+ aMyWord.idToWhereClause() + ")");
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
				
				if (aMyWord.word != null)
				{
					whereClause += "OR " + aMyWord.wordToWhereClause();
				}
			}
			else if (aMyWord.word != null)
			{
				whereClause = aMyWord.wordToWhereClause();
			}
		}
		
		Log.v(TAG, "select(where:" + whereClause + ")");
		
		Cursor cursor = mDatabase.query(MySQLiteOpenHelper.TABLE_NAME,
				mAllColumns, whereClause, null, null, null, null);
		
		boolean isMovedToFirst = cursor.moveToFirst();
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
		
		Log.v(TAG, "returns " + arrayList.size() + " rows");
		return arrayList;
	}
}
