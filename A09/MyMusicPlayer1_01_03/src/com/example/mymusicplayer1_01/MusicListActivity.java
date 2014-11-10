package com.example.mymusicplayer1_01;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MusicListActivity extends Activity
{
	private ListView mListView;
	private ArrayList<HashMap<String, String>> mFileList;
	
	private void retrieveAudioFileListFromMediaStore ()
	{
		// http://developer.android.com/reference/android/provider/MediaStore.Audio.Media.html
		String[] projection =
			{
					MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.DURATION
		};
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection,
				selection,
				null,
				null);
		if (cursor.getCount() == 0)
		{
			return;
		}
		cursor.moveToFirst();
		
		mFileList = new ArrayList<HashMap<String, String>>();
		do
		{
			HashMap<String, String> hashMap = new HashMap<String, String>();
			for (int i = 0; i < projection.length; i++)
			{
				String key = projection[i];
				String value = cursor.getString(cursor.getColumnIndex(key));
				hashMap.put(key, value);
			}
			mFileList.add(hashMap);
		}
		while (cursor.moveToNext());
		
		return;
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);
		
		mListView = (ListView) findViewById(R.id.listview_musiclist);
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
