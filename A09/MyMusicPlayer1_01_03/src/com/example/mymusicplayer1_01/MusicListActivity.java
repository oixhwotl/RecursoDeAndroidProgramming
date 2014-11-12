package com.example.mymusicplayer1_01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.example.mymusicplayer1_01.MusicPlayerActivity.UpdateTimeProgressAsyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class MusicListActivity extends Activity
{
	private static final String TAG = "MusicListActivity";
	
	private ListView mListView;
	private AdapterView.OnItemClickListener mOnItemClickListener =
		new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick (AdapterView<?> aAdapterView, View aView,
					int aPosition, long aId)
			{
				Log.v(TAG, "onItemClick " + aPosition + " " + aId);
				
				HashMap<String, String> hashMap =
					(HashMap<String, String>) mFileList.get(aPosition);
				
				Bundle bundle = new Bundle();
				bundle.putString(MediaStore.Audio.Media._ID,
						(String) hashMap.get(MediaStore.Audio.Media._ID));
				bundle.putString(MediaStore.Audio.Media.ARTIST,
						(String) hashMap.get(MediaStore.Audio.Media.ARTIST));
				bundle.putString(MediaStore.Audio.Media.TITLE,
						(String) hashMap.get(MediaStore.Audio.Media.TITLE));
				bundle.putString(MediaStore.Audio.Media.DATA,
						(String) hashMap.get(MediaStore.Audio.Media.DATA));
				String displayName =
					(String) hashMap.get(MediaStore.Audio.Media.DISPLAY_NAME);
				Log.v(TAG, "... " + displayName);
				bundle.putString(MediaStore.Audio.Media.DISPLAY_NAME,
						displayName);
				bundle.putString(MediaStore.Audio.Media.DURATION,
						(String) hashMap.get(MediaStore.Audio.Media.DURATION));
				
				Intent intent =
					new Intent(MusicListActivity.this,
							MusicPlayerActivity.class);
				intent.putExtra(MusicPlayerActivity.ARGUMENT_PLAYER, bundle);
				startActivity(intent);
				
				/*
				 * HashMap<String, String> hashMap = (HashMap<String, String>)
				 * mFileList.get(aPosition); String mediaFilePath = (String)
				 * hashMap.get(PROJECTION[PROJECTION_DATA]); try { MediaPlayer
				 * mediaPlayer = new MediaPlayer();
				 * setVolumeControlStream(AudioManager.STREAM_MUSIC);
				 * mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				 * mediaPlayer.setDataSource(mediaFilePath);
				 * mediaPlayer.prepare(); mediaPlayer.start(); } catch
				 * (Exception e) { e.printStackTrace(); }
				 */
			}
		};
	
	private ArrayList<HashMap<String, String>> mFileList;
	
	private static final String[] PROJECTION =
		{ MediaStore.Audio.Media._ID, // 0
				MediaStore.Audio.Media.ARTIST, // 1
				MediaStore.Audio.Media.TITLE, // 2
				MediaStore.Audio.Media.DATA, // 3
				MediaStore.Audio.Media.DISPLAY_NAME,// 4
				MediaStore.Audio.Media.ALBUM_ID, // 5
				MediaStore.Audio.Media.DURATION // 6
	};
	
	private static final int PROJECTION_ID = 0;
	private static final int PROJECTION_ARTIST = 1;
	private static final int PROJECTION_TITLE = 2;
	private static final int PROJECTION_DATA = 3;
	private static final int PROJECTION_DISPLAY_NAME = 4;
	private static final int PROJECTION_ALBUM_ID = 5;
	private static final int PROJECTION_DURATION = 6;
	
	private static final String SELECTION = MediaStore.Audio.Media.IS_MUSIC
			+ " != 0";
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);
		
		mListView = (ListView) findViewById(R.id.list_listview_musiclist);
		mListView.setOnItemClickListener(mOnItemClickListener);
		
		createLoadMusicListAsyncTask(null);
	}
	
	public class MusicListArrayAdapter extends
			ArrayAdapter<HashMap<String, String>>
	{
		private static final String TAG = "MusicListArrayAdapter";
		
		private Context mContext;
		private ArrayList<HashMap<String, String>> mValues;
		
		public MusicListArrayAdapter (Context aContext,
				ArrayList<HashMap<String, String>> aValues)
		{
			super(aContext, R.layout.activity_music_list_item, aValues);
			
			Log.v(TAG, "MusicListArrayAdapter");
			mContext = aContext;
			mValues = aValues;
		}
		
		public class ViewHolder
		{
			public TextView mTextViewTitle;
			public TextView mTextViewArtist;
			public TextView mTextViewDuration;
			public TextView mTextViewId;
		}
		
		@Override
		public View getView (int aPosition, View aContentView, ViewGroup aParent)
		{
			View itemView = aContentView;
			
			if (itemView == null)
			{
				Log.v(TAG, "getView()");
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				itemView =
					inflater.inflate(R.layout.activity_music_list_item,
							aParent, false);
				
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mTextViewTitle = (TextView) itemView
						.findViewById(R.id.listitem_textview_title);
				viewHolder.mTextViewArtist = (TextView) itemView
						.findViewById(R.id.listitem_textview_artist);
				viewHolder.mTextViewDuration = (TextView) itemView
						.findViewById(R.id.listitem_textview_duration);
				viewHolder.mTextViewId = (TextView) itemView
						.findViewById(R.id.listitem_textview_id);
				itemView.setTag(viewHolder);
			}
			
			ViewHolder vHolder = (ViewHolder) itemView.getTag();
			
			HashMap<String, String> hashMap = (HashMap<String, String>) mValues
					.get(aPosition);
			String title =
				(String) hashMap.get(PROJECTION[PROJECTION_DISPLAY_NAME]);
			String artist = (String) hashMap.get(PROJECTION[PROJECTION_ARTIST]);
			String duration =
				(String) hashMap.get(PROJECTION[PROJECTION_DURATION]);
			String id = (String) hashMap.get(PROJECTION[PROJECTION_ID]);
			
			vHolder.mTextViewTitle.setText(title);
			vHolder.mTextViewArtist.setText(artist);
			vHolder.mTextViewDuration.setText(MusicPlayerActivity
					.timeToString(Integer.parseInt(duration)));
			vHolder.mTextViewId.setText(id);
			
			return itemView;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_list, menu);
		
		initializeSearchAlertDialog();
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_player)
		{
			Intent intent = new Intent(this, MusicPlayerActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.action_settings)
		{
			Intent intent = new Intent(this, MusicPreferenceActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.action_search)
		{
			mSearchAlertDialog.show();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private LoadMusicListAsyncTask mLoadMusicListAsyncTask;
	
	private void createLoadMusicListAsyncTask (String aWhereData)
	{
		if (mLoadMusicListAsyncTask == null)
		{
			cancelLoadMusicListAsyncTask();
		}
		Log.v(TAG, "createLoadMusicListAsyncTask");
		mLoadMusicListAsyncTask = new LoadMusicListAsyncTask();
		mLoadMusicListAsyncTask.execute(aWhereData);
	}
	
	private void cancelLoadMusicListAsyncTask ()
	{
		if (mLoadMusicListAsyncTask != null)
		{
			Log.v(TAG, "cancelLoadMusicListAsyncTask");
			mLoadMusicListAsyncTask.cancel(true);
			mLoadMusicListAsyncTask = null;
		}
	}
	
	private class LoadMusicListAsyncTask extends
			AsyncTask<String, Void, Cursor>
	{
		private static final String TAG = "LoadMusicFilesAsyncTask";
		
		@Override
		protected Cursor doInBackground (String... args)
		{
			String selectionFinal = SELECTION;
			Log.v(TAG, "doInBackground() " + args.length);
			
			if ((args.length > 0) && (args[0] != null))
			{
				String whereData = args[0];
				Log.v(TAG, "... where with " + whereData);
				String additionalWhereClause =
					" \"" + MediaStore.Audio.Media.TITLE + "\" LIKE '"
							+ whereData + "%' OR " +
							" \"" + MediaStore.Audio.Media.ARTIST + "\" LIKE '"
							+ whereData + "%' OR " +
							" \"" + MediaStore.Audio.Media.DISPLAY_NAME
							+ "\" LIKE '" + whereData + "^'";
				
				selectionFinal = String.format(Locale.getDefault(),
						"%s AND (%s) ", SELECTION, additionalWhereClause);
			}
			
			// http://developer.android.com/reference/android/provider/MediaStore.Audio.Media.html
			Cursor cursor = getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,
					selectionFinal, null, null);
			return cursor;
		}
		
		@Override
		protected void onPostExecute (Cursor cursor)
		{
			Log.v(TAG, "onPostExecute()");
			Log.v(TAG, "... cursor.getCount()" + cursor.getCount());
			
			mFileList = new ArrayList<HashMap<String, String>>();
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				do
				{
					HashMap<String, String> hashMap =
						new HashMap<String, String>();
					for (int i = 0; i < PROJECTION.length; i++)
					{
						String key = PROJECTION[i];
						String value =
							cursor.getString(cursor.getColumnIndex(key));
						hashMap.put(key, value);
					}
					mFileList.add(hashMap);
					Log.v(TAG,
							"... ID:"
									+ hashMap.get(PROJECTION[PROJECTION_ID])
									+ " "
									+ "TITLE:"
									+ hashMap.get(PROJECTION[PROJECTION_TITLE])
									+ " "
									+ "DISPLAY_NAME:"
									+ hashMap
											.get(PROJECTION[PROJECTION_DISPLAY_NAME]));
				}
				while (cursor.moveToNext());
			}
			
			cursor.close();
			
			MusicListArrayAdapter arrayAdapter =
				new MusicListArrayAdapter(MusicListActivity.this,
						mFileList);
			mListView.setAdapter(arrayAdapter);
			
		}
		
	}
	
	private AlertDialog mSearchAlertDialog;
	private EditText mSearchAlertDialogEditText;
	
	private void initializeSearchAlertDialog ()
	{
		Log.v(TAG, "initializeSearchAlertDialog()");
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Search Music");
		alert.setMessage("Input Search Text");
		
		mSearchAlertDialogEditText = new EditText(this);
		alert.setView(mSearchAlertDialogEditText);
		
		alert.setPositiveButton(R.string.searchdialog_search,
				new DialogInterface.OnClickListener()
				{
					public void onClick (DialogInterface dialog, int whichButton)
					{
						String result =
							mSearchAlertDialogEditText.getText().toString();
						Log.v(TAG, "... OK " + result);
						
						createLoadMusicListAsyncTask(result);
					}
				});
		alert.setNegativeButton(R.string.searchdialog_cancel,
				new DialogInterface.OnClickListener()
				{
					public void onClick (DialogInterface dialog, int whichButton)
					{
						Log.v(TAG, "... cancel ");
						dialog.cancel();
					}
				});
		alert.setNeutralButton(R.string.searchdialog_clear,
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick (DialogInterface dialog, int which)
					{
						Log.v(TAG, "... clear ");
						mSearchAlertDialogEditText.setText("");
						createLoadMusicListAsyncTask(null);
					}
				});
		mSearchAlertDialog = alert.create();
	}
}
