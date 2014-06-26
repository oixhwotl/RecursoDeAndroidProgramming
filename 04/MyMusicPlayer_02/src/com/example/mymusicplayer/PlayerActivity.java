package com.example.mymusicplayer;

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
					// actualizar las imagenes de el but�n
					updateButtonImage(mMediaPlayer.isPlaying());
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
					updateButtonImage(false);
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
					float newAlpha = 1.0f;
					if (!isChecked)
					{
						newAlpha = 0.5f;
					}
					mToggleButtonReplay.setAlpha(newAlpha);
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
			
			// conectar el but�n de layout con el objeto de c�digo en Java
			mButtonPlayPause =
				(ImageButton) rootView.findViewById(R.id.buttonPlayPause);
			// registrar oyente de clic por el but�n
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
				updateButtonImage(mMediaPlayer.isPlaying());
				
				// registrar este clasa para MediaPlayer.OnCompletionListener
				mMediaPlayer.setOnCompletionListener(this);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			mToggleButtonReplay.setChecked(mMediaPlayer.isLooping());
			float newAlpha = 1.0f;
			if (!mToggleButtonReplay.isChecked())
			{
				newAlpha = 0.5f;
			}
			mToggleButtonReplay.setAlpha(newAlpha);
			
			return rootView;
		}
		
		@Override
		public void onDestroyView ()
		{
			// m�todo contador de onCreateView ()
			// para destruir los recursos como MediaPlayer
			if (mMediaPlayer != null)
			{
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			super.onDestroyView();
		}
		
		private void updateButtonImage (boolean aIsPlaying)
		{
			// para actualizar las imagenes de el but�n
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
		
		@Override
		public void onCompletion (MediaPlayer mp)
		{
			// implementar MediaPlayer.OnCompletionListener
			updateButtonImage(false);
		}
		
	}
	
}
