package com.example.mymusicplayer1_01;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MusicPlayerActivity extends Activity implements
		MediaPlayer.OnCompletionListener
{
	private static final String TAG = "MusicPlayerActivity";
	
	private MediaPlayer mMediaPlayer;
	private boolean mIsPlaying;
	
	private ImageButton mButton1;
	private View.OnClickListener mButton1ClickListener =
		new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				try
				{
					if (mIsPlaying)
					{
						mMediaPlayer.pause();
						mIsPlaying = false;
					}
					else
					{
						mMediaPlayer.start();
						mIsPlaying = true;
					}
					updateButtonImage();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	
	private void updateButtonImage ()
	{
		if (mIsPlaying == true)
		{
			mButton1.setImageResource(R.drawable.ic_action_pause);
		}
		else
		{
			mButton1.setImageResource(R.drawable.ic_action_play);
		}
	}
	
	@Override
	public void onCompletion (MediaPlayer mp)
	{
		mIsPlaying = false;
		updateButtonImage();
		try
		{
			mMediaPlayer.start();
			mMediaPlayer.pause();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);
		
		try
		{
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			Uri mediaFileUri = Uri.parse("android.resource://"
					+ MusicPlayerActivity.this.getPackageName() + "/"
					+ R.raw.preview_temandoflores);
			mMediaPlayer.setDataSource(MusicPlayerActivity.this, mediaFileUri);
			mMediaPlayer.setOnCompletionListener(this);
			
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.pause();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		mIsPlaying = false;
		
		mButton1 = (ImageButton) findViewById(R.id.button1);
		mButton1.setOnClickListener(mButton1ClickListener);
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
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
