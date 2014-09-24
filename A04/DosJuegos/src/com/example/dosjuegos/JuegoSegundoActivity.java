package com.example.dosjuegos;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class JuegoSegundoActivity extends Activity
{
	
	View.OnClickListener b=new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			if (v.getId()==R.id.button_numero1){
				Toast.makeText(getApplication(), "correcto", Toast.LENGTH_SHORT).show();
				
			}
			else
			{
				Toast.makeText(getApplication(), "incorrecto", Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	Button b1,b2,b3,b4;
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_juego_segundo);
		b1=(Button)findViewById(R.id.button_numero1);
		b2=(Button)findViewById(R.id.button_numero2);
		b3=(Button)findViewById(R.id.button_numero3);
		b4=(Button)findViewById(R.id.button_numero4);
		b1.setOnClickListener(b);
		b2.setOnClickListener(b);
		b3.setOnClickListener(b);
		b4.setOnClickListener(b);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.juego_segundo, menu);
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
