package com.example.ejemploconsqlite_01;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity
{
	private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Con SQLite");
		actionBar.setHomeButtonEnabled(true);
		
		if (savedInstanceState == null)
		{
			getFragmentManager()
					.beginTransaction()
					.add(R.id.container,
							new ListViewFragment())
					.commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed ()
	{
		getFragmentManager().popBackStack();
		// super.onBackPressed();
	}
	
}
