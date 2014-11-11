package com.example.mymusicplayer1_01;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerActivity extends Activity implements
		MediaPlayer.OnCompletionListener
{
	private static final String TAG = "MusicPlayerActivity";
	
	public static final String ARGUMENT_PLAYER = "ARGUMENT_PLAYER";
	
	private final Uri DEFAULT_MEDIAFILE_URI = Uri
			.parse("android.resource://com.example.mymusicplayer1_01/"
					+ R.raw.preview_temandoflores);
	
	private MediaPlayer mMediaPlayer;
	private boolean mMediaPlayerIsPlaying;
	private boolean mMediaPlayerIsLooping;
	private int mMediaPlayerTotalDuration;
	private int mMediaPlayerCurrentPosition;
	private int mMediaPlayerAudioSessionId;
	
	private TextView mTextViewTitle, mTextViewTimeProgress, mTextViewTimeTotal;
	
	private void updateTextViewTimeProgress (boolean aIsCurrentPositionUpdated)
	{
		if (aIsCurrentPositionUpdated == false)
		{
			mMediaPlayerCurrentPosition = mMediaPlayer.getCurrentPosition();
		}
		
		mTextViewTimeProgress
				.setText(timeToString(mMediaPlayerCurrentPosition));
	}
	
	private ImageButton mButtonPlayPause, mButtonStop, mButtonRewind,
			mButtonFastForward, mButtonReplay;
	private View.OnClickListener mButtonPlayPauseClickListener =
		new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				mMediaPlayerIsPlaying = mMediaPlayer.isPlaying();
				try
				{
					if (mMediaPlayerIsPlaying)
					{
						Log.v(TAG, "Pause clicked > Pause");
						mMediaPlayer.pause();
						updatePlayPauseButtonImage(false);
						
						cancelUpdateTimeProgressAsyncTask();
						
					}
					else
					{
						Log.v(TAG, "Play clicked > Start");
						mMediaPlayer.start();
						updatePlayPauseButtonImage(false);
						
						createUpdateTimeProgressAsyncTask();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	
	private void updatePlayPauseButtonImage (boolean aIsPlayingUpdated)
	{
		if (aIsPlayingUpdated == false)
		{
			mMediaPlayerIsPlaying = mMediaPlayer.isPlaying();
		}
		if (mMediaPlayerIsPlaying == true)
		{
			mButtonPlayPause.setImageResource(R.drawable.ic_action_pause);
		}
		else
		{
			mButtonPlayPause.setImageResource(R.drawable.ic_action_play);
		}
	}
	
	private View.OnClickListener mButtonStopClickListener =
		new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				mMediaPlayerIsPlaying = mMediaPlayer.isPlaying();
				try
				{
					Log.v(TAG, "Stop clicked > Stop playing");
					if (mMediaPlayerIsPlaying)
					{
						Log.v(TAG, "... MediaPlayer.pause()");
						mMediaPlayer.pause();
						updatePlayPauseButtonImage(false);
						
						cancelUpdateTimeProgressAsyncTask();
						
					}
					else
					{
						Log.v(TAG, "Stop clicked > Stop");
					}
					
					mMediaPlayer.seekTo(0);
					updateTextViewTimeProgress(false);
					updateSeekBar(true);
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	
	private View.OnClickListener mButtonReplayClickListener =
		new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				mMediaPlayerIsLooping = mMediaPlayer.isLooping();
				mMediaPlayerIsLooping = !mMediaPlayerIsLooping;
				if (mMediaPlayerIsLooping)
				{
					Log.v(TAG, "Replay clicked > Looping");
				}
				else
				{
					Log.v(TAG, "Replay clicked > not looping");
				}
				mMediaPlayer.setLooping(mMediaPlayerIsLooping);
				updateReplayButtonImage(false);
			}
		};
	
	private void updateReplayButtonImage (boolean aIsLoopingUpdated)
	{
		if (aIsLoopingUpdated == false)
		{
			mMediaPlayerIsLooping = mMediaPlayer.isLooping();
		}
		if (mMediaPlayerIsLooping == true)
		{
			mButtonReplay.setImageResource(R.drawable.ic_action_replay_clicked);
		}
		else
		{
			mButtonReplay.setImageResource(R.drawable.ic_action_replay);
		}
	}
	
	private static final int DEFUALT_SKIP_UNIT = 5000;
	private int mSkipUnit = DEFUALT_SKIP_UNIT;
	private View.OnClickListener mButtonSkipClickListener =
		new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				try
				{
					boolean wasPlaying = mMediaPlayer.isPlaying();
					int playOffset = mMediaPlayer.getCurrentPosition();
					int destOffset = 0;
					
					if (v.getId() == R.id.player_button_fast_forward)
					{
						destOffset = playOffset + mSkipUnit;
						if (destOffset > mMediaPlayerTotalDuration)
						{
							destOffset = mMediaPlayerTotalDuration;
						}
						Log.v(TAG, "Fast Forward clicked > Forwarding to "
								+ destOffset);
					}
					else
					{ // v.getId() == R.id.player_button_rewind
						destOffset = playOffset - mSkipUnit;
						if (destOffset < 0)
						{
							destOffset = 0;
						}
						Log.v(TAG, "Rewind clicked > Rewinding to "
								+ destOffset);
					}
					mMediaPlayer.seekTo(destOffset);
					if (wasPlaying == true)
					{
						mMediaPlayer.start();
					}
					else
					{
						updateTextViewTimeProgress(false);
						updateSeekBar(true);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
			}
		};
	
	private SeekBar mSeekBar;
	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
		new SeekBar.OnSeekBarChangeListener()
		{
			
			@Override
			public void onStopTrackingTouch (SeekBar seekBar)
			{
				Log.v(TAG, "onStopTrackingTouch " + seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch (SeekBar seekBar)
			{
				Log.v(TAG, "onStartTrackingTouch " + seekBar.getProgress());
			}
			
			@Override
			public void onProgressChanged (SeekBar seekBar, int progress,
					boolean fromUser)
			{
				if (fromUser == true)
				{
					mMediaPlayer.seekTo(progress);
					if (mMediaPlayerIsPlaying == true)
					{
						mMediaPlayer.start();
					}
					updateTextViewTimeProgress(false);
				}
			}
		};
	
	private void updateSeekBar (boolean aIsCurrentPositionUpdated)
	{
		if (aIsCurrentPositionUpdated == false)
		{
			mMediaPlayerCurrentPosition = mMediaPlayer.getCurrentPosition();
		}
		mSeekBar.setProgress(mMediaPlayerCurrentPosition);
	}
	
	@Override
	public void onCompletion (MediaPlayer mp)
	{
		try
		{
			Log.v(TAG, "onCompletion()");
			mMediaPlayer.start();
			Log.v(TAG, "... MediaPlayer.pause()");
			mMediaPlayer.pause();
			
			updatePlayPauseButtonImage(false);
			cancelUpdateTimeProgressAsyncTask();
			
			updateTextViewTimeProgress(false);
			updateSeekBar(true);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void initializeMediaPlayer ()
	{
		try
		{
			Log.v(TAG, "initializeMediaPlayer()");
			
			if (mMediaPlayer == null)
			{
				mMediaPlayer = new MediaPlayer();
				
				setVolumeControlStream(AudioManager.STREAM_MUSIC);
				
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setOnCompletionListener(this);
				
				mMediaPlayerAudioSessionId = mMediaPlayer.getAudioSessionId();
				Log.d(TAG, "... audio session ID: "
						+ mMediaPlayerAudioSessionId);
			}
			
			Uri mediaFileUri = DEFAULT_MEDIAFILE_URI;
			mMediaPlayer.setDataSource(MusicPlayerActivity.this,
					mediaFileUri);
			
			Log.v(TAG, "... prepare, start, pause");
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.pause();
			
			mMediaPlayerTotalDuration = mMediaPlayer.getDuration();
			Log.v(TAG, "... getDuration " + mMediaPlayerTotalDuration);
			
			mMediaPlayerIsPlaying = mMediaPlayer.isPlaying();
			mMediaPlayerIsLooping = mMediaPlayer.isLooping();
			
			mTextViewTimeProgress.setText(R.string.string_initial_time);
			mTextViewTimeTotal.setText(timeToString(mMediaPlayerTotalDuration));
			mTextViewTitle.setText("Te Mando Flores");
			
			Log.v(TAG, "... SeekBar setMax, setProgress");
			mSeekBar.setMax(mMediaPlayerTotalDuration);
			mSeekBar.setProgress(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void changeMediaPlayer (Bundle aBundle)
	{
		try
		{
			String audioId =
				(String) aBundle.getString(MediaStore.Audio.Media._ID);
			String audioArtist =
				(String) aBundle.getString(MediaStore.Audio.Media.ARTIST);
			String audioTitle =
				(String) aBundle.getString(MediaStore.Audio.Media.TITLE);
			String audioData =
				(String) aBundle.getString(MediaStore.Audio.Media.DATA);
			String audioDisplayName =
				(String) aBundle.getString(MediaStore.Audio.Media.DISPLAY_NAME);
			String audioDuration =
				(String) aBundle.getString(MediaStore.Audio.Media.DURATION);
			
			Log.v(TAG, "changeMediaPlayer() " + audioDisplayName);
			
			Log.v(TAG, "... mMediaPlayer.reset() ");
			mMediaPlayer.reset();
			
			Uri mediaUri = Uri.parse(audioData);
			mMediaPlayer.setDataSource(MusicPlayerActivity.this, mediaUri);
			
			Log.v(TAG, "... prepare, start, pause");
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.pause();
			
			mMediaPlayerTotalDuration = Integer.parseInt(audioDuration);
			Log.v(TAG, "... getDuration " + mMediaPlayerTotalDuration);
			
			if (mMediaPlayer.isLooping() == true)
			{
				mMediaPlayer.setLooping(false);
			}
			
			mMediaPlayerIsPlaying = mMediaPlayer.isPlaying();
			mMediaPlayerIsLooping = mMediaPlayer.isLooping();
			
			mTextViewTimeProgress.setText(R.string.string_initial_time);
			mTextViewTimeTotal.setText(timeToString(mMediaPlayerTotalDuration));
			mTextViewTitle.setText(audioDisplayName);
			
			Log.v(TAG, "... SeekBar setMax, setProgress");
			mSeekBar.setMax(mMediaPlayerTotalDuration);
			mSeekBar.setProgress(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void updateMediaPlayerUIStatus ()
	{
		try
		{
			Log.v(TAG, "updateMediaPlayerStatus()");
			
			Log.v(TAG, "... getCurrentPosition, isPlaying, isLooping");
			mMediaPlayerCurrentPosition = mMediaPlayer.getCurrentPosition();
			mMediaPlayerIsPlaying = mMediaPlayer.isPlaying();
			mMediaPlayerIsLooping = mMediaPlayer.isLooping();
			
			Log.v(TAG, "... update SkipUnit");
			SharedPreferences sharedPreference =
				this.getPreferences(Context.MODE_PRIVATE);
			mSkipUnit =
				sharedPreference.getInt(
						getString(R.string.pref_skip_second_key),
						DEFUALT_SKIP_UNIT);
			
			Log.v(TAG, "... updateTextViewTimeProgress, updateSeekBar");
			updateTextViewTimeProgress(true);
			updateSeekBar(true);
			
			Log.v(TAG,
					"... updatePlayPauseButtonImage, updateReplayButtonImage");
			updatePlayPauseButtonImage(true);
			updateReplayButtonImage(true);
			
			Log.v(TAG, "... asynctask");
			if (mMediaPlayerIsPlaying)
			{
				createUpdateTimeProgressAsyncTask();
			}
			else
			{
				cancelUpdateTimeProgressAsyncTask();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String timeToString (int aMSecond)
	{
		int aSecond, hours, remainder, mins, secs;
		aSecond = aMSecond / 1000;
		if (aSecond >= 3600)
		{
			hours = (int) aSecond / 3600;
			remainder = (int) aSecond - hours * 3600;
		}
		else
		{
			hours = 0;
			remainder = (int) aSecond;
		}
		if (remainder >= 60)
		{
			mins = (int) remainder / 60;
			secs = (int) remainder - mins * 60;
		}
		else
		{
			mins = 0;
			secs = remainder;
		}
		
		// int[] ints = {hours , mins , secs};
		// Log.v(TAG, "timeToString " + aSecond + " to " + hours + ":" + mins
		// + ":" + secs);
		return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,
				mins, secs);
	}
	
	@Override
	protected void onPause ()
	{
		Log.v(TAG, "onPause(), cancelUpdateTimeProgressAsyncTask()");
		
		cancelUpdateTimeProgressAsyncTask();
		
		super.onPause();
	}
	
	@Override
	protected void onNewIntent (Intent intent)
	{
		Log.v(TAG, "onNewIntent()");
		
		Bundle bundle = intent.getBundleExtra(ARGUMENT_PLAYER);
		if (bundle != null)
		{
			String audioDisplayName =
				(String) bundle.getString(MediaStore.Audio.Media.DISPLAY_NAME);
			Log.v(TAG, "... bundle DISPLAY_NAME:" + audioDisplayName);
			String currentTitle = mTextViewTitle.getText().toString();
			Log.v(TAG, "... title:" + currentTitle);
			
			if (currentTitle.compareTo(audioDisplayName) != 0)
			{
				if (mMediaPlayerIsPlaying == true)
				{
					Log.v(TAG, "... MediaPlayer.pause()");
					mMediaPlayer.pause();
				}
				changeMediaPlayer(bundle);
			}
		}
		
		// updateMediaPlayerUIStatus();
	}
	
	@Override
	protected void onResume ()
	{
		Log.v(TAG, "onResume()");
		super.onResume();
		
		updateMediaPlayerUIStatus();
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);
		
		Log.v(TAG, "onCreate()");
		mTextViewTitle = (TextView) findViewById(R.id.player_textview_title);
		mTextViewTimeProgress =
			(TextView) findViewById(R.id.player_textview_time_progress);
		mTextViewTimeTotal =
			(TextView) findViewById(R.id.player_textview_time_total);
		
		mButtonPlayPause =
			(ImageButton) findViewById(R.id.player_button_play_pause);
		mButtonPlayPause.setOnClickListener(mButtonPlayPauseClickListener);
		
		mButtonStop = (ImageButton) findViewById(R.id.player_button_stop);
		mButtonStop.setOnClickListener(mButtonStopClickListener);
		
		mButtonFastForward =
			(ImageButton) findViewById(R.id.player_button_fast_forward);
		mButtonFastForward.setOnClickListener(mButtonSkipClickListener);
		
		mButtonRewind = (ImageButton) findViewById(R.id.player_button_rewind);
		mButtonRewind.setOnClickListener(mButtonSkipClickListener);
		
		mButtonReplay = (ImageButton) findViewById(R.id.player_button_replay);
		mButtonReplay.setOnClickListener(mButtonReplayClickListener);
		
		mSeekBar = (SeekBar) findViewById(R.id.player_seekbar_progress);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		
		Log.v(TAG, "...initializeMediaPlayer, updateMediaPlayerStatus");
		initializeMediaPlayer();
		Intent intent = getIntent();
		if (intent != null)
		{
			onNewIntent(intent);
		}
		updateMediaPlayerUIStatus();
		
		Log.v(TAG,
				"... mLinearLayoutStub, setupVisualFxAndUI, setupEqualizerFxAndUI");
		mLinearLayoutStub =
			(LinearLayout) findViewById(R.id.player_linearlayout_stub);
		
		setupEqualizerFxAndUI();
	}
	
	@Override
	protected void onDestroy ()
	{
		if (mMediaPlayer != null)
		{
			try
			{
				if (mMediaPlayer.isPlaying())
				{
					mMediaPlayer.stop();
					
					cancelUpdateTimeProgressAsyncTask();
				}
			}
			catch (Exception e)
			{
				// Do nothing
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_player, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_list)
		{
			Intent intent = new Intent(this, MusicListActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.action_settings)
		{
			Intent intent = new Intent(this, MusicPreferenceActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	UpdateTimeProgressAsyncTask mUpdateTimeProgressAsyncTask;
	
	private void createUpdateTimeProgressAsyncTask ()
	{
		if (mUpdateTimeProgressAsyncTask == null)
		{
			Log.v(TAG, "createUpdateTimeProgressAsyncTask");
			mUpdateTimeProgressAsyncTask = new UpdateTimeProgressAsyncTask();
			mUpdateTimeProgressAsyncTask.execute(MusicPlayerActivity.this,
					null, null);
		}
	}
	
	private void cancelUpdateTimeProgressAsyncTask ()
	{
		if (mUpdateTimeProgressAsyncTask != null)
		{
			Log.v(TAG, "cancelUpdateTimeProgressAsyncTask");
			mUpdateTimeProgressAsyncTask.cancel(true);
			mUpdateTimeProgressAsyncTask = null;
		}
	}
	
	class UpdateTimeProgressAsyncTask extends
			AsyncTask<MusicPlayerActivity, Void, Void>
	{
		
		MusicPlayerActivity mActivity;
		
		@Override
		protected Void doInBackground (MusicPlayerActivity... params)
		{
			mActivity = params[0];
			int i = 0;
			
			while (!isCancelled())
			{
				i++;
				if (i % 10000 == 0)
				{
					publishProgress();
					i = 0;
				}
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate (Void... values)
		{
			if (!isCancelled())
			{
				// Log.v(TAG, "onProgressUpdate()");
				if (mMediaPlayer.isPlaying() == true)
				{
					updateTextViewTimeProgress(false);
					updateSeekBar(true);
				}
				else
				{
					updatePlayPauseButtonImage(false);
					cancelUpdateTimeProgressAsyncTask();
				}
			}
			super.onProgressUpdate(values);
		}
		
	}
	
	private Equalizer mEqualizer;
	private LinearLayout mLinearLayoutStub;
	
	private void setupEqualizerFxAndUI ()
	{
		// Create the Equalizer object (an AudioEffect subclass) and attach it
		// to our media player,
		// with a default priority (0).
		mEqualizer = new Equalizer(0, mMediaPlayerAudioSessionId);
		mEqualizer.setEnabled(true);
		
		TextView eqTextView = new TextView(this);
		eqTextView.setText("Equalizer:");
		mLinearLayoutStub.addView(eqTextView);
		
		short bands = mEqualizer.getNumberOfBands();
		
		final short minEQLevel = mEqualizer.getBandLevelRange()[0];
		final short maxEQLevel = mEqualizer.getBandLevelRange()[1];
		
		for (short i = 0; i < bands; i++)
		{
			final short band = i;
			
			TextView freqTextView = new TextView(this);
			freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
			freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000)
					+ " Hz");
			mLinearLayoutStub.addView(freqTextView);
			
			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);
			
			TextView minDbTextView = new TextView(this);
			minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			minDbTextView.setText((minEQLevel / 100) + " dB");
			
			TextView maxDbTextView = new TextView(this);
			maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			maxDbTextView.setText((maxEQLevel / 100) + " dB");
			
			LinearLayout.LayoutParams layoutParams =
				new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.weight = 1;
			SeekBar bar = new SeekBar(this);
			bar.setLayoutParams(layoutParams);
			bar.setMax(maxEQLevel - minEQLevel);
			bar.setProgress(mEqualizer.getBandLevel(band));
			
			bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
			{
				public void onProgressChanged (SeekBar seekBar, int progress,
						boolean fromUser)
				{
					mEqualizer.setBandLevel(band,
							(short) (progress + minEQLevel));
				}
				
				public void onStartTrackingTouch (SeekBar seekBar)
				{
				}
				
				public void onStopTrackingTouch (SeekBar seekBar)
				{
				}
			});
			
			row.addView(minDbTextView);
			row.addView(bar);
			row.addView(maxDbTextView);
			
			mLinearLayoutStub.addView(row);
		}
	}
}
