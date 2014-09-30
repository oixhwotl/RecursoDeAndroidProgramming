package com.example.custombroadcast02;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
	Button mButton1,mButton2;
	EditText editText1;
	View.OnClickListener button1OnClickListener=new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			if(isRegistered==false){
				registerBroadcastReceiver();
				//mButton1.setText(R.string.button_text_unregister);
				mButton1.setText("unregister");
				
			}
			else{
				unregisterBroadcastReceiver();
				//mButton1.setText(R.string.button_text_register);
				mButton1.setText("register");
			}
			
		}
	};
	View.OnClickListener button2OnClickListener=new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			Intent intent=new Intent(MY_CUSTOM_INTENT_NAME);
			intent.putExtra("MSG", editText1.getText().toString());
			sendBroadcast(intent);
			//Toast.makeText(getApplication(), ""+editText1.getText().toString() , Toast.LENGTH_SHORT).show();
			
		}
	};
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mButton1=(Button)findViewById(R.id.button1);
		mButton2=(Button)findViewById(R.id.button2);
		editText1=(EditText)findViewById(R.id.editText1);
		mButton1.setOnClickListener(button1OnClickListener);
		mButton2.setOnClickListener(button2OnClickListener);
	}

	private static final String TAG = "MainActivity";
	public static final String MY_CUSTOM_INTENT_NAME=
			"com.example.custombroadcast02.MY_CUSTOM_INTENT_NAME";
	BroadcastReceiver myBroadcastReceiver =new BroadcastReceiver()
	{
		
		@Override
		public void onReceive (Context context, Intent intent)
		{
			String msg=intent.getStringExtra("MSG");
			Toast toast= Toast.makeText(context, "Recibio un intent previsto"+intent.getAction()+msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0,0);
			toast.show();
			Log.v(TAG, "onReceive "+intent.getAction()+" "+msg);
		}
	};
	private boolean isRegistered = false;
	private void registerBroadcastReceiver(){
		if(isRegistered == false){
			registerReceiver(myBroadcastReceiver, new IntentFilter(MY_CUSTOM_INTENT_NAME));
			isRegistered=true;
		}
	}
	private void unregisterBroadcastReceiver() {
		if(isRegistered == true){
			unregisterReceiver(myBroadcastReceiver);
			isRegistered=false;
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
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
