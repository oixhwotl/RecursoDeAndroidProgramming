package com.example.alarmandvibrate02;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity
{
	static final String TAG="MainActivity";
	Button mButton1;
	TimePicker timePicker;
	View.OnClickListener mButton1OnClickListener=new View.OnClickListener()
	
	{
		
		@Override
		public void onClick (View v)
		{
			Calendar nowTime=Calendar.getInstance();
			int hour=timePicker.getCurrentHour();
			int minute=timePicker.getCurrentMinute();
			Calendar setTime = Calendar.getInstance();
			setTime.add(Calendar.MINUTE,minute);
			setTime.add(Calendar.HOUR_OF_DAY,hour);
			String msg= String.format("Ahora: %02d:%02d\nAnadir: %02d:%02d\nAnadido: %02d:%02d",
					nowTime.get(Calendar.HOUR_OF_DAY),
					nowTime.get(Calendar.MINUTE),
					hour, minute,
					setTime.get(Calendar.HOUR_OF_DAY),
					setTime.get(Calendar.MINUTE));
			Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
			Log.v(TAG, msg);
			
			Intent intent=new Intent(getApplication(), MainActivity.AlarmReceiver.class);
			PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
			
			AlarmManager alarmManager =(AlarmManager)getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
			System.currentTimeMillis()+(3600*hour+60*minute)*1000,pendingIntent);
			
			
		}
	};
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton1=(Button)findViewById(R.id.button1);
		timePicker=(TimePicker)findViewById(R.id.timePicker1);
		mButton1.setOnClickListener(mButton1OnClickListener);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(0);
		timePicker.setCurrentMinute(5);
	}
	
	
	public static class AlarmReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive (Context context, Intent intent)
		{
			String msg="Ya vibrando";
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			Log.v(TAG, msg);
			Vibrator vibrator=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(2000);
			
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
