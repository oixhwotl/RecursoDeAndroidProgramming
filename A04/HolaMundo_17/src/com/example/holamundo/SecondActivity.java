package com.example.holamundo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SecondActivity extends Activity
{
	Button mButton2;
	View.OnClickListener mButton2ClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			Toast.makeText(getApplication(), "button cliquee", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getApplication(), MainActivity.class);
			startActivity(intent);
		}
	};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		mButton2 = (Button)findViewById(R.id.button2);
		mButton2.setOnClickListener(mButton2ClickListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
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
