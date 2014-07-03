package com.example.alarmandvibrate_01;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	private static final String TAG = "Activity";
	
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
	
	public static class AlarmReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive (Context arg0, Intent arg1)
		{
			String msg = "Ya, Vibrando";
			Toast.makeText(arg0, msg, Toast.LENGTH_LONG)
					.show();
			Log.v(TAG, msg);
			
			Vibrator vibrator =
				(Vibrator) arg0.getSystemService(
						Context.VIBRATOR_SERVICE);
			vibrator.vibrate(2000);
		}
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		
		public PlaceholderFragment ()
		{
		}
		
		private static final String TAG = "Fragment";
		
		TimePicker mTimePicker1;
		Button mButton1;
		View.OnClickListener mButton1ClickListener = new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				Calendar nowTime = Calendar.getInstance();
				
				int hour = mTimePicker1.getCurrentHour();
				int minute = mTimePicker1.getCurrentMinute();
				
				Calendar setTime = Calendar.getInstance();
				setTime.add(Calendar.MINUTE, minute);
				setTime.add(Calendar.HOUR_OF_DAY, hour);
				
				String msg =
					String.format(
							"Ahora: %02d:%02d\nA�adir: %02d:%02d\nA�adido: %02d:%02d",
							nowTime.get(Calendar.HOUR_OF_DAY),
							nowTime.get(Calendar.MINUTE),
							hour, minute,
							setTime.get(Calendar.HOUR_OF_DAY),
							setTime.get(Calendar.MINUTE));
				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
				Log.v(TAG, msg);
				
				Intent intent =
					new Intent(getActivity(), MainActivity.AlarmReceiver.class);
				PendingIntent pendingIntent =
					PendingIntent.getBroadcast(getActivity(), 1, intent, 0);
				
				AlarmManager alarmManager =
					(AlarmManager) getActivity()
							.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis()
								+ (3600 * hour + 60 * minute) * 1000,
						pendingIntent);
			}
		};
		
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView =
				inflater.inflate(R.layout.fragment_main, container, false);
			
			mTimePicker1 = (TimePicker) rootView.findViewById(R.id.timepicker1);
			
			mTimePicker1.setIs24HourView(true);
			mTimePicker1.setCurrentHour(0);
			mTimePicker1.setCurrentMinute(5);
			
			mButton1 = (Button) rootView.findViewById(R.id.button1);
			mButton1.setOnClickListener(mButton1ClickListener);
			
			return rootView;
		}
	}
	
}
