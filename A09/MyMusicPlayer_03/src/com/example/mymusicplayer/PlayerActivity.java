package com.example.mymusicplayer;

import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PlayerActivity extends Activity
{
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player, menu);
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
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			MediaPlayer.OnCompletionListener
	{
		
		MediaPlayer mMediaPlayer;
		boolean isPlaying; // Guardar estado de MediaPlayer, esta tocando?
		
		ImageButton mButtonPlayPause;
		View.OnClickListener mButtonPlayPauseClickListener =
			new View.OnClickListener()
			{
				@Override
				public void onClick (View v)
				{
					if (isPlaying)
					{ // Si esta tocando
						mMediaPlayer.pause(); // pausarlo
					}
					else
					{ // Si no,
						mMediaPlayer.start(); // iniciarlo
					}
					// actualizar las imagenes de el butón
					updatePlayPauseButtonImage(mMediaPlayer.isPlaying());
				}
			};
		
		ImageButton mButtonStop;
		View.OnClickListener mButtonStopClickListener =
			new View.OnClickListener()
			{
				@Override
				public void onClick (View v)
				{
					if (isPlaying)
					{
						mMediaPlayer.pause();
					}
					mMediaPlayer.seekTo(0);
					updatePlayPauseButtonImage(false);
				}
			};
		
		static final int DEFAULT_SKIP_TIME = 10000;
		ImageButton mButtonRewind;
		View.OnClickListener mButtonRewindClickListener =
			new View.OnClickListener()
			{
				
				@Override
				public void onClick (View v)
				{
					int currentPosition = mMediaPlayer.getCurrentPosition();
					if (currentPosition > 0)
					{
						int newPosition =
							(currentPosition > DEFAULT_SKIP_TIME) ?
									currentPosition - DEFAULT_SKIP_TIME
									: 0;
						mMediaPlayer.seekTo(newPosition);
					}
					
					if (isPlaying)
					{
						mMediaPlayer.start();
					}
				}
			};
		
		ImageButton mButtonFastForward;
		View.OnClickListener mButtonFastForwardClickListener =
			new View.OnClickListener()
			{
				@Override
				public void onClick (View v)
				{
					int duration = mMediaPlayer.getDuration();
					int currentPosition = mMediaPlayer.getCurrentPosition();
					
					if (currentPosition < duration)
					{
						int newPosition =
							(currentPosition + DEFAULT_SKIP_TIME < duration) ?
									currentPosition + DEFAULT_SKIP_TIME
									: duration;
						mMediaPlayer.seekTo(newPosition);
					}
					
					if (isPlaying)
					{
						mMediaPlayer.start();
					}
				}
			};
		
		ToggleButton mToggleButtonReplay;
		CompoundButton.OnCheckedChangeListener mToggleButtonReplayCheckedChangeListener =
			new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged (CompoundButton buttonView,
						boolean isChecked)
				{
					mMediaPlayer.setLooping(isChecked);
					updateToggleButton(isChecked);
				}
			};
		
		TextView mTextViewProgress, mTextViewDuration;
		
		SeekBar mSeekBarProgress;
		SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
			new SeekBar.OnSeekBarChangeListener()
			{
				
				@Override
				public void onStopTrackingTouch (SeekBar seekBar)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch (SeekBar seekBar)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged (SeekBar seekBar, int progress,
						boolean fromUser)
				{
					mTextViewProgress.setText(msecToString(progress));
				}
			};
		
		public PlaceholderFragment ()
		{
		}
		
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView =
				inflater.inflate(R.layout.fragment_player, container, false);
			
			setupUI(rootView);
			
			initializeMediaPlayer();
			
			initializeUI();
			
			return rootView;
		}
		
		private void setupUI (View rootView)
		{
			// conectar el butón de layout con el objeto de código en Java
			mButtonPlayPause =
				(ImageButton) rootView.findViewById(R.id.buttonPlayPause);
			// registrar oyente de clic por el butón
			mButtonPlayPause.setOnClickListener(mButtonPlayPauseClickListener);
			
			mButtonStop = (ImageButton) rootView.findViewById(R.id.buttonStop);
			mButtonStop.setOnClickListener(mButtonStopClickListener);
			
			mButtonRewind =
				(ImageButton) rootView.findViewById(R.id.buttonRewind);
			mButtonRewind.setOnClickListener(mButtonRewindClickListener);
			
			mButtonFastForward =
				(ImageButton) rootView.findViewById(R.id.buttonFastForward);
			mButtonFastForward
					.setOnClickListener(mButtonFastForwardClickListener);
			
			mToggleButtonReplay =
				(ToggleButton) rootView.findViewById(R.id.buttonReplay);
			mToggleButtonReplay
					.setOnCheckedChangeListener(mToggleButtonReplayCheckedChangeListener);
			
			mTextViewProgress =
				(TextView) rootView.findViewById(R.id.textviewProgress);
			mTextViewDuration =
				(TextView) rootView.findViewById(R.id.textviewDuration);
			
			mSeekBarProgress =
				(SeekBar) rootView.findViewById(R.id.seekbarProgress);
			mSeekBarProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
		}
		
		private void initializeMediaPlayer ()
		{
			try
			{
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				Uri mediaFileUri =
					Uri.parse("android.resource://"
							+ getActivity().getPackageName() + "/"
							+ R.raw.preview_temandoflores);
				mMediaPlayer.setDataSource(getActivity(), mediaFileUri);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				
				mMediaPlayer.pause();
				// actualizar la imagen inicialmente
				updatePlayPauseButtonImage(mMediaPlayer.isPlaying());
				
				// registrar este clasa para MediaPlayer.OnCompletionListener
				mMediaPlayer.setOnCompletionListener(this);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		private void initializeUI ()
		{
			updateToggleButton(mMediaPlayer.isLooping());
			
			int durationInMsec = mMediaPlayer.getDuration();
			int currentPositionInMsec = mMediaPlayer.getCurrentPosition();
			
			mTextViewDuration.setText(msecToString(durationInMsec));
			// mTextViewProgress.setText(msecToString(currentPositionInMsec));
			
			mSeekBarProgress.setMax(durationInMsec);
			mSeekBarProgress.setProgress(currentPositionInMsec);
		}
		
		private String msecToString (int aMsec)
		{
			int inSec = aMsec / 1000;
			int min = inSec / 60;
			int sec = inSec - (min * 60);
			
			return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
		}
		
		private void updateToggleButton (boolean isChecked)
		{
			mToggleButtonReplay.setChecked(isChecked);
			float newAlpha = (isChecked) ? 1.0f : 0.5f;
			mToggleButtonReplay.setAlpha(newAlpha);
		}
		
		@Override
		public void onDestroyView ()
		{
			// método contador de onCreateView ()
			// para destruir los recursos como MediaPlayer
			if (mMediaPlayer != null)
			{
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			super.onDestroyView();
		}
		
		private void updatePlayPauseButtonImage (boolean aIsPlaying)
		{
			// para actualizar las imagenes de el butón
			isPlaying = aIsPlaying;
			if (isPlaying)
			{ // si esta tocando
			  // mostrar la imagen de pausar
				mButtonPlayPause.setImageResource(R.drawable.ic_action_pause);
				
			}
			else
			{ // si no
			  // mostrar la imagen de iniciar
				mButtonPlayPause.setImageResource(R.drawable.ic_action_play);
			}
		}
		
		// MediaPlayer.OnCompletionListener
		@Override
		public void onCompletion (MediaPlayer mp)
		{
			
			updatePlayPauseButtonImage(false);
		}
		
	}
	
}
