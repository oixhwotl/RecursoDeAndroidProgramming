package com.example.dosjuegos;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class JuegoPrimeroActivity extends Activity
{
	EditText mEditTextNumero;
	Button mButtonNumero;
	TextView mTextViewNumero;
	
	int mNumero;
	
	View.OnClickListener mClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			String editTextAnswer = mEditTextNumero.getText().toString();
			int answer = Integer.parseInt(editTextAnswer);
			if (answer == mNumero)
			{
				mTextViewNumero.setText("Feliz, correcto");
			}
			else if (answer < mNumero)
			{
				mTextViewNumero.setText("No. Es mas grande");
			}
			else
			{
				mTextViewNumero.setText("No. Es mas pequena");
			}
			mEditTextNumero.setText("");
		}
	};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_juego_primero);
		
		mNumero = 1 + (int)(Math.random()*100); 
		Log.v("PRIMERO", "random number is " + mNumero );
		
		mEditTextNumero = (EditText)findViewById(R.id.edittext_numero);
		mButtonNumero = (Button) findViewById(R.id.button_numero);
		mTextViewNumero = (TextView) findViewById(R.id.textview_numero);
		mButtonNumero.setOnClickListener(mClickListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.juego_primero, menu);
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
