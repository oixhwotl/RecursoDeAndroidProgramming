package com.example.mymusicplayer;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
	public static class PlaceholderFragment extends Fragment {

		MediaPlayer mMediaPlayer;

		Button mButton1;
		View.OnClickListener mButton1ClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isPlaying = mMediaPlayer.isPlaying();

				Toast.makeText(getActivity(),
						"Botón de clic, " + (isPlaying ? "Pausar" : "Initiar"),
						Toast.LENGTH_SHORT).show();
				if (isPlaying) {
					mMediaPlayer.pause();

				} else {
					mMediaPlayer.start();

				}
			}
		};

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_player,
					container, false);

			mButton1 = (Button) rootView.findViewById(R.id.button1);
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
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rootView;
		}
	}

}
