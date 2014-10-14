package com.example.ejemploconsqlite_01;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MyWordContentProvider extends ContentProvider
{
	private static final String TAG = "MyWordContentProvider";
	
	// used for the UriMacher
	private static final int URI_MYWORD = 20;
	private static final int URI_MYWORD_ID = 10;

	private static final String AUTHORITY = "com.example.ejemploconsqlite_01.contentprovider";

	private static final String BASE_PATH = "myword";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	      + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
	      + "/words";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
	      + "/word";

	private static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		mURIMatcher.addURI(AUTHORITY, BASE_PATH, URI_MYWORD);
		mURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", URI_MYWORD_ID);
	}

	// database
	private SQLiteDatabase mDatabase;

	@Override
	public int delete (Uri arg0, String arg1, String[] arg2)
	{
		int uriType = mURIMatcher.match(arg0);
	    int rowsDeleted = 0;
	    
	    switch (uriType) {
	    	case URI_MYWORD:
	    		rowsDeleted = mDatabase.delete(MySQLiteOpenHelper.TABLE_NAME,
	    				arg1, arg2);
	    		break;
	    	case URI_MYWORD_ID:
	    		String id = arg0.getLastPathSegment();
	    		MyWord myWord = new MyWord();
    			myWord._id = Integer.parseInt(id);
    			
	    		if (TextUtils.isEmpty(arg1))
	    		{
	    			rowsDeleted = mDatabase.delete(MySQLiteOpenHelper.TABLE_NAME, 
	    					myWord.idToWhereClause(), null);
	    		} else {
	    			rowsDeleted = mDatabase.delete(MySQLiteOpenHelper.TABLE_NAME, 
	    					myWord.idToWhereClause() + " and " + arg1, arg2);
	    		}
	    		default:
	    			throw new IllegalArgumentException("Unknown URI: " + arg0);
	    }

	    getContext().getContentResolver().notifyChange(arg0, null);

	    return rowsDeleted;
	}
	
	@Override
	public String getType (Uri uri)
	{
		// devolver tipo de MIME
		// pero esta bien de devolver null
		return null;
	}
	
	@Override
	public Uri insert (Uri uri, ContentValues values)
	{
		int uriType = mURIMatcher.match(uri);

	    long rowId = 0;
	    
	    switch (uriType) {
	    case URI_MYWORD:
	      rowId = mDatabase.insert(MySQLiteOpenHelper.TABLE_NAME, null, values);
	      break;
	      
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + rowId);
	}
	
	@Override
	public boolean onCreate ()
	{
		Log.v(TAG, "MyWordContentProvider()");
		MySQLiteOpenHelper openHelper = new MySQLiteOpenHelper(getContext());
		mDatabase = openHelper.getWritableDatabase();
		
		return (mDatabase != null);
	}
	
	@Override
	public Cursor query (Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		// Uisng SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // Set the table
	    queryBuilder.setTables(MySQLiteOpenHelper.TABLE_NAME);

	    int uriType = mURIMatcher.match(uri);
	    
	    switch (uriType) {
	    case URI_MYWORD:
	      break;
	      
	    case URI_MYWORD_ID:
	      // adding the ID to the original query
	      queryBuilder.appendWhere(MySQLiteOpenHelper.COLUMN_ID + "="
	          + uri.getLastPathSegment());
	      break;
	      
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    Cursor cursor = queryBuilder.query(mDatabase, projection, selection,
	        selectionArgs, null, null, sortOrder);
	    // make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

	    return cursor;
	}
	
	@Override
	public int update (Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		int uriType = mURIMatcher.match(uri);
	    int rowsUpdated = 0;
	    
	    switch (uriType) {
	    case URI_MYWORD:
	      rowsUpdated = mDatabase.update(MySQLiteOpenHelper.TABLE_NAME, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	      
	    case URI_MYWORD_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = mDatabase.update(MySQLiteOpenHelper.TABLE_NAME, 
	            values,
	            MySQLiteOpenHelper.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = mDatabase.update(MySQLiteOpenHelper.TABLE_NAME, 
	            values,
	            MySQLiteOpenHelper.COLUMN_ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	      
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
	
}
