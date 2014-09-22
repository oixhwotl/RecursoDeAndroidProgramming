package com.example.receiveallbroadcasts_01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver
{
	private static final String TAG = "MyBroadcastReceiver";
	
	@Override
	public void onReceive (Context arg0, Intent arg1)
	{
		if (arg1.getAction().compareTo("com.example.MY_CUSTOM_INTENT") == 0)
		{
			String state = arg1.getStringExtra("MSG");
			Toast toast = Toast.makeText(
					arg0,
					"Recibió INTENT personalizado: " + arg1.getAction() + " "
							+ state, Toast.LENGTH_LONG);
			toast.show();
			
			Log.v(TAG, "onReceive " + arg1.getAction() + " " + state);
			
		}
		else
		{
			String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);
			
			Toast toast = Toast.makeText(
					arg0,
					"Recibió una intención prevista: " + arg1.getAction() + " "
							+ state, Toast.LENGTH_LONG);
			toast.show();
			
			Log.v(TAG, "onReceive " + arg1.getAction() + " " + state);
		}
	}
}
