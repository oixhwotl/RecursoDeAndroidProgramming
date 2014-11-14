package com.example.mymusicplayer1_01;

import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
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

import com.example.mymusicplayer1_01.MusicPlayerService;

public class MusicPlayerActivity extends Activity implements
		MediaPlayer.OnCompletionListener {
	private static final String TAG = "MusicPlayerActivity";

	private boolean mIsPlaying;
	private boolean mIsLooping;
	private int mTotalDuration;
	private int mCurrentPosition;
	private int mAudioSessionId;
	private Bundle mBundle;

	private boolean mBound = false;
	private MusicPlayerService mMediaPlayer;

	private String mTitle;
	private String mId;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName aClassName, IBinder aBinder) {
			Log.v(TAG, "ServiceConnection.onServiceConnected()");

			MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) aBinder;
			mMediaPlayer = binder.getService();
			mBound = true;

			mMediaPlayer.setOnCompletionListener(MusicPlayerActivity.this);

			mAudioSessionId = mMediaPlayer.getAudioSessionId();
			setupEqualizerFxAndUI();

			if ((mMediaPlayer.isPlaying() == false) && (mBundle != null)) {
				mMediaPlayer.setDataSource(mBundle);
				mId = mMediaPlayer.getId();
			}

			updateUI();
		}

		public void onServiceDisconnected(ComponentName aClassName) {
			Log.v(TAG, "ServiceConnection.onServiceDisconnected()");
			mBound = false;
			updateUI();
		}
	};

	private TextView mTextViewTitle, mTextViewTimeProgress, mTextViewTimeTotal;

	private void updateTextViewTimeProgress(boolean aIsCurrentPositionUpdated) {
		if (!mBound) {
			return;
		}
		if (aIsCurrentPositionUpdated == false) {
			mCurrentPosition = mMediaPlayer.getCurrentPosition();
		}

		mTextViewTimeProgress.setText(timeToString(mCurrentPosition));

	}

	private ImageButton mButtonPlayPause, mButtonStop, mButtonRewind,
			mButtonFastForward, mButtonReplay;
	private View.OnClickListener mButtonPlayPauseClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!mBound) {
				return;
			}
			mIsPlaying = mMediaPlayer.isPlaying();
			try {
				if (mIsPlaying) {
					Log.v(TAG, "Pause clicked > Pause");
					mMediaPlayer.pause();
					updatePlayPauseButtonImage(false);

					cancelUpdateTimeProgressAsyncTask();

				} else {
					Log.v(TAG, "Play clicked > Start");
					mMediaPlayer.start();
					updatePlayPauseButtonImage(false);

					createUpdateTimeProgressAsyncTask();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	private void updatePlayPauseButtonImage(boolean aIsPlayingUpdated) {
		if (!mBound) {
			return;
		}
		if (aIsPlayingUpdated == false) {
			mIsPlaying = mMediaPlayer.isPlaying();
		}
		if (mIsPlaying == true) {
			mButtonPlayPause.setImageResource(R.drawable.ic_action_pause);
		} else {
			mButtonPlayPause.setImageResource(R.drawable.ic_action_play);
		}
	}

	private View.OnClickListener mButtonStopClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!mBound) {
				return;
			}
			mIsPlaying = mMediaPlayer.isPlaying();
			try {
				Log.v(TAG, "Stop clicked > Stop playing");
				if (mIsPlaying) {
					Log.v(TAG, "... MediaPlayer.pause()");
					mMediaPlayer.pause();
					updatePlayPauseButtonImage(false);

					cancelUpdateTimeProgressAsyncTask();

				} else {
					Log.v(TAG, "Stop clicked > Stop");
				}

				mMediaPlayer.seekTo(0);
				updateTextViewTimeProgress(false);
				updateSeekBar(true);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	private View.OnClickListener mButtonReplayClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!mBound) {
				return;
			}
			mIsLooping = mMediaPlayer.isLooping();
			mIsLooping = !mIsLooping;
			if (mIsLooping) {
				Log.v(TAG, "Replay clicked > Looping");
			} else {
				Log.v(TAG, "Replay clicked > not looping");
			}
			mMediaPlayer.setLooping(mIsLooping);
			updateReplayButtonImage(false);

		}
	};

	private void updateReplayButtonImage(boolean aIsLoopingUpdated) {
		if (!mBound) {
			return;
		}
		if (aIsLoopingUpdated == false) {
			mIsLooping = mMediaPlayer.isLooping();
		}
		if (mIsLooping == true) {
			mButtonReplay.setImageResource(R.drawable.ic_action_replay_clicked);
		} else {
			mButtonReplay.setImageResource(R.drawable.ic_action_replay);
		}

	}

	private SharedPreferences mPreferences;
	private boolean mPrefAutomaticPlay = false;
	private static final int DEFUALT_SKIP_UNIT = 5000;
	private int mPrefSkipTime = DEFUALT_SKIP_UNIT;

	private void loadPrefAutomaticPlay() {
		boolean prefAutomaticPlay = (boolean) mPreferences.getBoolean(
				getResources().getString(R.string.pref_automatic_play_key),
				false);
		if (prefAutomaticPlay != mPrefAutomaticPlay) {
			mPrefAutomaticPlay = prefAutomaticPlay;
			Log.v(TAG, "loadPrefAutomaticPlay() " + mPrefAutomaticPlay);
		}
	}

	private void loadPrefSkipTime() {
		String prefSkipTimeString = (String) mPreferences
				.getString(
						getResources().getString(R.string.pref_skip_second_key),
						"5000");
		int prefSkipTime = Integer.parseInt(prefSkipTimeString);
		if (prefSkipTime != mPrefSkipTime) {
			mPrefSkipTime = prefSkipTime;
			Log.v(TAG, "loadPrefSkipTime() " + mPrefSkipTime);
		}
	}

	private View.OnClickListener mButtonSkipClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!mBound) {
				return;
			}
			try {
				boolean wasPlaying = mMediaPlayer.isPlaying();
				int destOffset = mMediaPlayer.getCurrentPosition();

				if (v.getId() == R.id.player_button_fast_forward) {
					destOffset = mMediaPlayer.skip(true);
					Log.v(TAG, "Fast Forward clicked > Forwarding to "
							+ destOffset);
				} else { // v.getId() == R.id.player_button_rewind
					destOffset = mMediaPlayer.skip(false);
					Log.v(TAG, "Rewind clicked > Rewinding to " + destOffset);
				}

				if (wasPlaying == false) {
					updateTextViewTimeProgress(false);
					updateSeekBar(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	private SeekBar mSeekBar;
	private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Log.v(TAG, "onStopTrackingTouch " + seekBar.getProgress());
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			Log.v(TAG, "onStartTrackingTouch " + seekBar.getProgress());
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (!mBound) {
				return;
			}
			if (fromUser == true) {
				mMediaPlayer.seekTo(progress);
				if (mIsPlaying == true) {
					mMediaPlayer.start();
				}
				updateTextViewTimeProgress(false);
			}
		}
	};

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (!mBound) {
			return;
		}

		Log.v(TAG, "onCompletion");

		updatePlayPauseButtonImage(false);
		cancelUpdateTimeProgressAsyncTask();

		mMediaPlayer.seekTo(0);
		mCurrentPosition = 0;

		updateSeekBar(true);
		updateTextViewTimeProgress(true);
	}

	private void updateSeekBar(boolean aIsCurrentPositionUpdated) {
		if (!mBound) {
			return;
		}
		if (aIsCurrentPositionUpdated == false) {
			mCurrentPosition = mMediaPlayer.getCurrentPosition();
		}
		mSeekBar.setProgress(mCurrentPosition);
	}

	private void bindMusicPlayerService() {
		Intent intent = new Intent(this, MusicPlayerService.class);
		startService(intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void updateUI() {

		mButtonPlayPause.setEnabled(mBound);
		mButtonStop.setEnabled(mBound);
		mButtonRewind.setEnabled(mBound);
		mButtonFastForward.setEnabled(mBound);
		mButtonReplay.setEnabled(mBound);
		mSeekBar.setEnabled(mBound);

		if (!mBound) {
			mTextViewTimeProgress.setText(R.string.string_initial_time);
			mTextViewTimeTotal.setText(R.string.string_initial_time);

			mSeekBar.setMax(100);
			mSeekBar.setProgress(0);

			return;
		}
		try {
			Log.v(TAG, "updateUI()");
			mTitle = mMediaPlayer.getTitle();
			mTextViewTitle.setText(mTitle);
			setupEqualizerFxAndUI();
			mId = mMediaPlayer.getId();

			Log.v(TAG, "... getCurrentPosition, isPlaying, isLooping");
			mCurrentPosition = mMediaPlayer.getCurrentPosition();
			mTotalDuration = mMediaPlayer.getDuration();
			mIsPlaying = mMediaPlayer.isPlaying();
			mIsLooping = mMediaPlayer.isLooping();

			Log.v(TAG, "... updateTextViewTimeProgress, updateSeekBar");
			updateTextViewTimeProgress(true);
			mTextViewTimeTotal.setText(timeToString(mTotalDuration));
			mSeekBar.setMax(mTotalDuration);
			updateSeekBar(true);

			Log.v(TAG,
					"... updatePlayPauseButtonImage, updateReplayButtonImage");
			updatePlayPauseButtonImage(true);
			updateReplayButtonImage(true);

			Log.v(TAG, "... asynctask");
			if (mIsPlaying) {
				createUpdateTimeProgressAsyncTask();
			} else {
				cancelUpdateTimeProgressAsyncTask();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String timeToString(int aMSecond) {
		int aSecond, hours, remainder, mins, secs;
		aSecond = aMSecond / 1000;
		if (aSecond >= 3600) {
			hours = (int) aSecond / 3600;
			remainder = (int) aSecond - hours * 3600;
		} else {
			hours = 0;
			remainder = (int) aSecond;
		}
		if (remainder >= 60) {
			mins = (int) remainder / 60;
			secs = (int) remainder - mins * 60;
		} else {
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
	protected void onPause() {
		Log.v(TAG, "onPause(), cancelUpdateTimeProgressAsyncTask()");

		cancelUpdateTimeProgressAsyncTask();

		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.v(TAG, "onNewIntent()");

		Bundle bundle = intent
				.getBundleExtra(MusicPlayerService.ARGUMENT_PLAYER);
		if (bundle == null) {
			return;
		}

		String id = (String) bundle.getString(MediaStore.Audio.Media._ID);
		String title = (String) bundle.getString(MediaStore.Audio.Media.TITLE);
		Log.v(TAG, "... bundle TITLE:" + title + " ID:" + id);
		if (mBound) {
			mId = mMediaPlayer.getId();
		}
		Log.v(TAG, "... current ID:" + mId);

		if ((mId == null) || (id.compareTo(mId) != 0)) {
			mBundle = bundle;
			if (mBound) {
				if (mIsPlaying == true) {
					Log.v(TAG, "... MediaPlayer.pause()");
					mMediaPlayer.pause();
				}
				Log.v(TAG, "... MediaPlayer.setDataSource()");
				mMediaPlayer.setDataSource(mBundle);
				mId = mMediaPlayer.getId();
			}
		}
		updateUI();
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume()");
		super.onResume();

		if (!mBound) {
			bindMusicPlayerService();
		} else if (mBundle != null) {
			String id = (String) mBundle.getString(MediaStore.Audio.Media._ID);
			String title = (String) mBundle
					.getString(MediaStore.Audio.Media.TITLE);
			Log.v(TAG, "... bundle TITLE:" + title + " ID:" + id);
			Log.v(TAG, "... current ID:" + mId);

			mId = mMediaPlayer.getId();
			if ((mId == null) || (id.compareTo(mId) != 0)) {
				if (mIsPlaying == true) {
					Log.v(TAG, "... MediaPlayer.pause()");
					mMediaPlayer.pause();
				}
				Log.v(TAG, "... MediaPlayer.setDataSource()");
				mMediaPlayer.setDataSource(mBundle);
				mId = mMediaPlayer.getId();
			}
		}
		updateUI();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);

		Log.v(TAG, "onCreate()");
		mTextViewTitle = (TextView) findViewById(R.id.player_textview_title);
		mTextViewTimeProgress = (TextView) findViewById(R.id.player_textview_time_progress);
		mTextViewTimeTotal = (TextView) findViewById(R.id.player_textview_time_total);

		mButtonPlayPause = (ImageButton) findViewById(R.id.player_button_play_pause);
		mButtonPlayPause.setOnClickListener(mButtonPlayPauseClickListener);

		mButtonStop = (ImageButton) findViewById(R.id.player_button_stop);
		mButtonStop.setOnClickListener(mButtonStopClickListener);

		mButtonFastForward = (ImageButton) findViewById(R.id.player_button_fast_forward);
		mButtonFastForward.setOnClickListener(mButtonSkipClickListener);

		mButtonRewind = (ImageButton) findViewById(R.id.player_button_rewind);
		mButtonRewind.setOnClickListener(mButtonSkipClickListener);

		mButtonReplay = (ImageButton) findViewById(R.id.player_button_replay);
		mButtonReplay.setOnClickListener(mButtonReplayClickListener);

		mSeekBar = (SeekBar) findViewById(R.id.player_seekbar_progress);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

		mLinearLayoutStub = (LinearLayout) findViewById(R.id.player_linearlayout_stub);

		Log.v(TAG, "...bindMusicPlayerService()");
		bindMusicPlayerService();

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		loadPrefAutomaticPlay();
		loadPrefSkipTime();

		Intent intent = getIntent();
		if (intent != null) {
			onNewIntent(intent);
		}
		updateUI();
	}

	@Override
	protected void onDestroy() {
		cancelUpdateTimeProgressAsyncTask();

		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_list) {
			Intent intent = new Intent(this, MusicListActivity.class);
			startActivity(intent);
		} else if (id == R.id.action_settings) {
			Intent intent = new Intent(this, MusicPreferenceActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	UpdateTimeProgressAsyncTask mUpdateTimeProgressAsyncTask;

	private void createUpdateTimeProgressAsyncTask() {
		if (mUpdateTimeProgressAsyncTask == null) {
			Log.v(TAG, "createUpdateTimeProgressAsyncTask");
			mUpdateTimeProgressAsyncTask = new UpdateTimeProgressAsyncTask();
			mUpdateTimeProgressAsyncTask.execute(MusicPlayerActivity.this,
					null, null);
		}
	}

	private void cancelUpdateTimeProgressAsyncTask() {
		if (mUpdateTimeProgressAsyncTask != null) {
			Log.v(TAG, "cancelUpdateTimeProgressAsyncTask");
			mUpdateTimeProgressAsyncTask.cancel(true);
			mUpdateTimeProgressAsyncTask = null;
		}
	}

	class UpdateTimeProgressAsyncTask extends
			AsyncTask<MusicPlayerActivity, Void, Void> {

		MusicPlayerActivity mActivity;

		@Override
		protected Void doInBackground(MusicPlayerActivity... params) {
			mActivity = params[0];
			int i = 0;

			while (!isCancelled()) {
				i++;
				if (i % 10000 == 0) {
					publishProgress();
					i = 0;
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			if (!isCancelled()) {
				// Log.v(TAG, "onProgressUpdate()");
				if (mMediaPlayer.isPlaying() == true) {
					updateTextViewTimeProgress(false);
					updateSeekBar(true);
				} else {
					updatePlayPauseButtonImage(false);
					cancelUpdateTimeProgressAsyncTask();
				}
			}
			super.onProgressUpdate(values);
		}

	}

	private Equalizer mEqualizer;
	private LinearLayout mLinearLayoutStub;

	private void setupEqualizerFxAndUI() {
		// Create the Equalizer object (an AudioEffect subclass) and attach it
		// to our media player,
		// with a default priority (0).
		if (!mBound) {
			return;
		}
		if (mEqualizer != null) {
			return;
		}

		mEqualizer = new Equalizer(0, mAudioSessionId);
		mEqualizer.setEnabled(true);

		TextView eqTextView = new TextView(this);
		eqTextView.setText("Equalizer:");
		mLinearLayoutStub.addView(eqTextView);

		short bands = mEqualizer.getNumberOfBands();

		final short minEQLevel = mEqualizer.getBandLevelRange()[0];
		final short maxEQLevel = mEqualizer.getBandLevelRange()[1];

		for (short i = 0; i < bands; i++) {
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

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.weight = 1;
			SeekBar bar = new SeekBar(this);
			bar.setLayoutParams(layoutParams);
			bar.setMax(maxEQLevel - minEQLevel);
			bar.setProgress(mEqualizer.getBandLevel(band));

			bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mEqualizer.setBandLevel(band,
							(short) (progress + minEQLevel));
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});

			row.addView(minDbTextView);
			row.addView(bar);
			row.addView(maxDbTextView);

			mLinearLayoutStub.addView(row);
		}
	}
}
