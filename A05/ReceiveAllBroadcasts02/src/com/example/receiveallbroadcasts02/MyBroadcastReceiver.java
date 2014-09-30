package com.example.receiveallbroadcasts02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver
{
	private static String TAG="MyBRoadcastReceiver";
	@Override
	public void onReceive (Context arg0, Intent arg1)
	{
		String state=arg1.getStringExtra(TelephonyManager.EXTRA_STATE);
		Toast.makeText(arg0,
				"Recibio una intencion prevista"+arg1.getAction()+""+state,
				Toast.LENGTH_LONG).show();
		Log.v(TAG,"onReceive"+arg1.getAction()+""+state);
	}
	
}
