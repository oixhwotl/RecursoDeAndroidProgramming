package com.example.decoraciondetexto;

import com.example.decoraciondetexto.R.id;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TextActivity extends Activity
{
	Button botonAtras;
	TextView texto;
	View.OnClickListener vButton1ViewOnClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
		Intent intent=new Intent(getApplication(),MainActivity.class);
		startActivity(intent);
			
		}
	};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
		botonAtras=(Button)findViewById(R.id.button_1);
		texto=(TextView)findViewById(R.id.textView_1);
		botonAtras.setOnClickListener(vButton1ViewOnClickListener);
		texto.setOnClickListener(vButton1ViewOnClickListener);
		Intent intent=getIntent();
		if(intent!=null)
		{
			Bundle bundle=intent.getBundleExtra("BUNDLE");
			String textVal=bundle.getString("TEXTVAL");
			int colorVal=bundle.getInt("COLORVAL");
			boolean isBoldVal=bundle.getBoolean("ISBOLDVAL");
			
			texto.setText(textVal);
			
			
			texto.setTextColor(colorVal);
			if (isBoldVal)
			{
				texto.setTypeface(null, Typeface.BOLD);
			}
			else
			{
				texto.setTypeface(null, Typeface.NORMAL);
			}
			texto.setTextSize(24.0f);
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text, menu);
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
