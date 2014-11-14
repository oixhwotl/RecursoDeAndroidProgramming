package com.example.mymusicplayer1_01;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

public class MusicPlayerService extends Service implements
		MediaPlayer.OnCompletionListener {
	private static final String TAG = "MusicPlayerService";

	public static final String ARGUMENT_PLAYER = "ARGUMENT_PLAYER";

	private Context mContext;

	private MediaPlayer mMediaPlayer;
	private MediaPlayer.OnCompletionListener mOnCompletionListener;

	private String mMediaId;
	private String mMediaArtist;
	private String mMediaTitle;
	private String mMediaData;
	private String mMediaDisplayName;
	private String mMediaDuration;

	private Bundle mBundle;
	private boolean mIsPlaying;
	private boolean mIsLooping;
	private int mTotalDuration;
	private int mCurrentPosition;
	private int mAudioSessionId;

	private SharedPreferences mPreferences;
	private boolean mPrefAutomaticPlay = false;
	private int mPrefSkipTime = 5000;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "onCreate()");
		mContext = getApplicationContext();

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		createMediaPlayer();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy()");
		if (mMediaPlayer != null) {
			try {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				mMediaPlayer.release();
				mMediaPlayer = null;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand() action:" + intent.getAction());

		// return super.onStartCommand(intent, flags, startId);
		return Service.START_NOT_STICKY;
	}

	private void createMediaPlayer() {
		try {
			Log.v(TAG, "createMediaPlayer()");

			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();

				// setVolumeControlStream(AudioManager.STREAM_MUSIC);

				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setOnCompletionListener(this);

				mAudioSessionId = mMediaPlayer.getAudioSessionId();
				Log.d(TAG, "... audio session ID: " + mAudioSessionId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	public void setDataSource(Bundle aBundle) {
		try {
			Log.v(TAG, "setDataSource()");
			if (mMediaPlayer == null) {
				createMediaPlayer();
			}
			if (aBundle == null) {
				return;
			}

			String id = (String) aBundle.getString(MediaStore.Audio.Media._ID);
			if ((mMediaId != null) && (id.compareTo(mMediaId) == 0)) {
				return;
			} else {
				Log.v(TAG, "... mMediaPlayer.reset() ");
				mMediaPlayer.reset();
			}
			mMediaId = id;
			mMediaArtist = (String) aBundle
					.getString(MediaStore.Audio.Media.ARTIST);
			mMediaTitle = (String) aBundle
					.getString(MediaStore.Audio.Media.TITLE);
			mMediaData = (String) aBundle
					.getString(MediaStore.Audio.Media.DATA);
			mMediaDisplayName = (String) aBundle
					.getString(MediaStore.Audio.Media.DISPLAY_NAME);
			mMediaDuration = (String) aBundle
					.getString(MediaStore.Audio.Media.DURATION);

			Log.v(TAG, "setDataSource() " + mMediaId + " : "
					+ mMediaDisplayName + " : " + mMediaTitle + " : "
					+ mMediaArtist);

			Uri mediaUri = Uri.parse(mMediaData);
			mMediaPlayer.setDataSource(mContext, mediaUri);

			Log.v(TAG, "... prepare(), start()");
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.pause();

			mTotalDuration = Integer.parseInt(mMediaDuration);
			Log.v(TAG, "... getDuration " + mTotalDuration);

			mIsPlaying = mMediaPlayer.isPlaying();
			mIsLooping = mMediaPlayer.isLooping();

			loadPrefAutomaticPlay();
			if (mPrefAutomaticPlay == false) {
				Log.v(TAG, "... AutomaticPlay off --> pause()");
			} else {
				Log.v(TAG, "... AutomaticPlay on");
				start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		mBundle = aBundle;
	}

	public void start() {
		Log.v(TAG, "start()");
		if (mIsPlaying == true) {
			return;
		}
		try {
			mMediaPlayer.start();
			mIsPlaying = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		Log.v(TAG, "pause()");
		if (mIsPlaying != true) {
			return;
		}
		try {
			mMediaPlayer.pause();
			mIsPlaying = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void seekTo(int aMsec) {
		Log.v(TAG, "seekTo(" + aMsec + ")");
		try {
			mMediaPlayer.seekTo(aMsec);
			if (mIsPlaying == true) {
				mMediaPlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int skip(boolean aForward) {
		Log.v(TAG, "skip(" + (aForward ? "FastForward" : "Rewind") + ")");
		loadPrefSkipTime();

		int destPos;
		if (aForward) {
			destPos = mCurrentPosition + mPrefSkipTime;
			destPos = (destPos > mTotalDuration ? mTotalDuration : destPos);
		} else {
			destPos = mCurrentPosition - mPrefSkipTime;
			destPos = (destPos < 0 ? 0 : destPos);
		}
		mMediaPlayer.seekTo(destPos);
		if (mIsPlaying) {
			mMediaPlayer.start();
		}

		return destPos;
	}

	public int getCurrentPosition() {
		mCurrentPosition = mMediaPlayer.getCurrentPosition();
		return mCurrentPosition;
	}

	public int getDuration() {
		return mTotalDuration;
	}

	public int getAudioSessionId() {
		return mAudioSessionId;
	}

	public String getTitle() {
		return mMediaTitle;
	}

	public String getDisplayName() {
		return mMediaDisplayName;
	}

	public void setLooping(boolean aEnableLooping) {
		mMediaPlayer.setLooping(aEnableLooping);
		mIsLooping = aEnableLooping;
	}

	public boolean isLooping() {
		return mIsLooping;
	}

	public boolean isPlaying() {
		return mIsPlaying;
	}

	public void setOnCompletionListener(
			MediaPlayer.OnCompletionListener aOnCompletionListener) {
		if (mOnCompletionListener != aOnCompletionListener) {
			Log.v(TAG, "setOnCompletionListener()");
			mOnCompletionListener = aOnCompletionListener;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.v(TAG, "onCompletion()");

		mIsPlaying = false;

		if (mOnCompletionListener != null) {
			mOnCompletionListener.onCompletion(mp);
		}
	}

	public final IBinder mLocalBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		MusicPlayerService getService() {
			return MusicPlayerService.this;
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(TAG, "onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.v(TAG, "onBind()");
		return mLocalBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.v(TAG, "onRebind()");
		super.onRebind(intent);
	}

}
