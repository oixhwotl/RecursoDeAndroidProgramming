package com.example.dosjuegos;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button01 = (Button) findViewById(R.id.btn_goto_juego_primero);
		button01.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(getApplication(), JuegoPrimero.class);
				startActivity(intent);
			}
		});
		
		Button button02 = (Button) findViewById(R.id.btn_goto_juego_segundo);
		button02.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(getApplication(), JuegoSegundo.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
