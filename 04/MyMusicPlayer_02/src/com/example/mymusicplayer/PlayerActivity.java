package com.example.mymusicplayer;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class PlayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			MediaPlayer.OnCompletionListener {

		MediaPlayer mMediaPlayer;
		boolean isPlaying; // Guardar estado de MediaPlayer, esta tocando?

		Button mButton1;
		View.OnClickListener mButton1ClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						getActivity(), // !Toast!
						"Botón de clic, " + (isPlaying ? "Pausar" : "Iniciar"),
						Toast.LENGTH_SHORT).show();
				if (isPlaying) { // Si esta tocando
					mMediaPlayer.pause(); // pausarlo
				} else { // Si no,
					mMediaPlayer.start(); // iniciarlo
				}
				updateButtonImage(mMediaPlayer.isPlaying()); // actualizar las
																// imagenes de
																// el butón
			}
		};

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_player,
					container, false);

			// conectar el butón de layout con el objeto de código en Java
			mButton1 = (Button) rootView.findViewById(R.id.buttonPlayPause);
			// registrar oyente de clic por el butón
			mButton1.setOnClickListener(mButton1ClickListener);

			try {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				Uri mediaFileUri = Uri.parse("android.resource://"
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
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rootView;
		}

		@Override
		public void onDestroyView() {
			// método contador de onCreateView ()
			// para destruir los recursos como MediaPlayer
			if (mMediaPlayer != null) {
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			super.onDestroyView();
		}

		private void updateButtonImage(boolean aIsPlaying) {
			// para actualizar las imagenes de el butón
			isPlaying = aIsPlaying;
			if (isPlaying) { // si esta tocando
				// mostrar la imagen de pausar
				mButton1.setBackgroundResource(R.drawable.ic_action_pause);
			} else { // si no
				// mostrar la imagen de iniciar
				mButton1.setBackgroundResource(R.drawable.ic_action_play);
			}
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			// implementar MediaPlayer.OnCompletionListener
			updateButtonImage(false);
		}

	}

}
