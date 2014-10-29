package com.example.notificationexample_01;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity
{
	NotificationManager mNotificationManager;
	static final int NOTIFICATION_ID = 100;
	int mNotificationCount = 0;
	
	EditText mEditTextSubject, mEditTextBody;
	Button mButtonNotify, mButtonCancel;
	
	View.OnClickListener mButtonNotifyClickListener =
		new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				String subject = mEditTextSubject.getText().toString();
				String body = mEditTextBody.getText().toString();
				
				Intent intent =
					new Intent(getApplicationContext(), MainActivity.class);
				PendingIntent pendingIntent =
					PendingIntent.getActivity(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
				
				NotificationCompat.Builder notificationBuilder
				= new NotificationCompat.Builder(getApplicationContext())
						.setContentTitle(subject)
						.setContentText(body)
						.setSmallIcon(R.drawable.ic_launcher)
						.setNumber(++mNotificationCount)
						.setContentIntent(pendingIntent);
				
				mNotificationManager.notify(NOTIFICATION_ID,
						notificationBuilder.build());
			}
		};
	
	View.OnClickListener mButtonCancelClickListener =
		new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				mNotificationManager.cancel(NOTIFICATION_ID);
			}
		};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mEditTextSubject = (EditText) findViewById(R.id.edittext_subject);
		mEditTextBody = (EditText) findViewById(R.id.edittext_body);
		
		mButtonNotify = (Button) findViewById(R.id.button_notify);
		mButtonNotify.setOnClickListener(mButtonNotifyClickListener);
		
		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mButtonCancel.setOnClickListener(mButtonCancelClickListener);
		
		mNotificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
