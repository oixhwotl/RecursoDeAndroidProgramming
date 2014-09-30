package com.example.services_01;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RandomNumberService extends Service
{
	private final IBinder mBinder = new LocalBinder();
	private final Random mGenerator = new Random();
	private static final String TAG = "RandomNumberService";
	
	@Override
	public IBinder onBind (Intent intent)
	{
		Log.v(TAG, "onBind()");
		return mBinder;
	}
	
	public class LocalBinder extends Binder
	{
		RandomNumberService getService ()
		{
			Log.v(TAG, "getService()");
			return RandomNumberService.this;
		}
	}
	
	public int getRandomNumber ()
	{
		Log.v(TAG, "getRandomNumber()");
		return mGenerator.nextInt(100);
	}
	
}
