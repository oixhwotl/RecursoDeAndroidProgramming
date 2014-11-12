package com.example.mymusicplayer1_01;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class MusicPreferenceActivity extends PreferenceActivity
{
	@Override
	protected void onCreate (final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			getFragmentManager().beginTransaction()
					.replace(
							android.R.id.content, new MyPreferenceFragment()
					)
					.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static class MyPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate (final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_screen);
		}
	}
}
