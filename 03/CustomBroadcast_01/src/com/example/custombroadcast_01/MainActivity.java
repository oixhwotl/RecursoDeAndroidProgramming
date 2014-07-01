package com.example.custombroadcast_01;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity
{
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment())
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
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		private static final String TAG = "Fragment";
		public static final String MY_CUSTOM_INTENT_NAME =
			"com.example.MY_CUSTOM_INTENT";
		
		BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive (Context context, Intent intent)
			{
				String msg = intent.getStringExtra("MSG");
				
				Toast toast =
					Toast.makeText(
							context,
							"Recibió una intención prevista: "
									+ intent.getAction() + " "
									+ msg, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				
				Log.v(TAG, "onReceive " + intent.getAction() + " " + msg);
				
			}
			
		};
		
		public PlaceholderFragment ()
		{
		}
		
		boolean isRegistered = false;
		Button buttonRegister, buttonBroadcast;
		
		View.OnClickListener buttonRegisterClickListener =
			new View.OnClickListener()
			{
				
				@Override
				public void onClick (View v)
				{
					if (isRegistered == false)
					{
						registerBroadcastReceiver();
						buttonRegister.setText(R.string.button_text_unregister);
					}
					else
					{
						unregisterBroadcastReceiver();
						buttonRegister.setText(R.string.button_text_register);
					}
					
				}
			};
		
		View.OnClickListener buttonBroadcastClickListener =
			new View.OnClickListener()
			{
				
				@Override
				public void onClick (View v)
				{
					Intent intent = new Intent(MY_CUSTOM_INTENT_NAME);
					intent.putExtra("MSG", editTextMessage.getText().toString());
					getActivity().sendBroadcast(intent);
				}
			};
		EditText editTextMessage;
		
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView =
				inflater.inflate(R.layout.fragment_main, container, false);
			
			buttonRegister = (Button) rootView.findViewById(R.id.button1);
			buttonRegister.setOnClickListener(buttonRegisterClickListener);
			
			buttonBroadcast = (Button) rootView.findViewById(R.id.button2);
			buttonBroadcast.setOnClickListener(buttonBroadcastClickListener);
			
			editTextMessage = (EditText) rootView.findViewById(R.id.edittext1);
			return rootView;
		}
		
		@Override
		public void onDestroyView ()
		{
			if (isRegistered == true)
			{
				unregisterBroadcastReceiver();
			}
			super.onDestroyView();
		}
		
		private void registerBroadcastReceiver ()
		{
			if (isRegistered == false)
			{
				getActivity().registerReceiver(myBroadcastReceiver,
						new IntentFilter(
								MY_CUSTOM_INTENT_NAME));
				isRegistered = true;
			}
			
		}
		
		private void unregisterBroadcastReceiver ()
		{
			if (isRegistered == true)
			{
				getActivity().unregisterReceiver(myBroadcastReceiver);
				isRegistered = false;
			}
		}
	}
	
}
