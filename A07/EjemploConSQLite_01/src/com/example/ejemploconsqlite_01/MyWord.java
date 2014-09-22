package com.example.ejemploconsqlite_01;

import android.content.ContentValues;

public class MyWord
{
	public int _id;
	public String word;
	
	public ContentValues toContentValues ()
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteOpenHelper.COLUMN_ID, _id);
		contentValues.put(MySQLiteOpenHelper.COLUMN_WORD, word);
		return contentValues;
	}
	
	public ContentValues idToContentValues ()
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteOpenHelper.COLUMN_ID, _id);
		return contentValues;
	}
	
	public String idToWhereClause ()
	{
		return MySQLiteOpenHelper.COLUMN_ID + "=" + _id;
	}
	
	public ContentValues wordToContentValues ()
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteOpenHelper.COLUMN_WORD, word);
		return contentValues;
	}
	
	public String wordToWhereClause ()
	{
		return MySQLiteOpenHelper.COLUMN_WORD + "='" + word + "'";
	}
	
	public String toString ()
	{
		return _id + ": " + word;
	}
	
	public String toStringForDebug ()
	{
		return "[id:" + _id + ", word:" + word + "]";
	}
	
	static MyWord getInstanceFromContentValues (ContentValues aContentValues)
	{
		MyWord myWord = new MyWord();
		myWord._id = aContentValues.getAsInteger(MySQLiteOpenHelper.COLUMN_ID);
		myWord.word =
			aContentValues.getAsString(MySQLiteOpenHelper.COLUMN_WORD);
		return myWord;
	}
	
	static MyWord getInstanceWithWord (String aWord)
	{
		MyWord myWord = new MyWord();
		myWord._id = 0;
		myWord.word = aWord;
		return myWord;
	}
}
