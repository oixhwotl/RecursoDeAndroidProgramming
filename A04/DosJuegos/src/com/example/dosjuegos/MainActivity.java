package com.example.dosjuegos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
	Button mButton1, mButton2;
	View.OnClickListener mButton1ClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick (View v)
		{
			Intent intent = new Intent(getApplication(), JuegoPrimeroActivity.class);
			startActivity(intent);
			
		}
	};
	
	View.OnClickListener mButton2ClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick (View v)
		{
			Intent intent = new Intent(getApplication(), JuegoSegundoActivity.class);
			startActivity(intent);
			
		}
	};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton1 = (Button)findViewById(R.id.buttonMain1);
		mButton2 = (Button)findViewById(R.id.buttonMain2);
		mButton1.setOnClickListener(mButton1ClickListener);
		mButton2.setOnClickListener(mButton2ClickListener);
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
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
