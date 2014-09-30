package com.example.services_01;

import com.example.services_01.RandomNumberService.LocalBinder;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
		
		boolean mIsBound = false;
		RandomNumberService mBoundService;
		
		private ServiceConnection mConnection = new ServiceConnection()
		{
			@Override
			public void onServiceConnected (ComponentName className,
					IBinder service)
			{
				Log.v(TAG, "onServiceConnected()");
				
				LocalBinder binder = (LocalBinder) service;
				mBoundService = binder.getService();
				
				mIsBound = true;
				mButtonBind.setEnabled(false);
				mButtonCallService.setEnabled(true);
			}
			
			@Override
			public void onServiceDisconnected (ComponentName arg0)
			{
				Log.v(TAG, "onServiceDisconnected()");
				
				mIsBound = false;
				mButtonBind.setEnabled(true);
				mButtonCallService.setEnabled(false);
			}
		};
		
		boolean mIsDownloading = false;
		boolean mIsReceiving = false;
		
		Button mButtonBind, mButtonCallService, mButtonDownload;
		View.OnClickListener mButtonBindClickListener =
			new View.OnClickListener()
			{
				@Override
				public void onClick (View v)
				{
					Log.v(TAG, "Button Bind onClick()");
					if (mIsBound == false)
					{
						Intent intent =
							new Intent(getActivity(), RandomNumberService.class);
						getActivity().bindService(intent, mConnection,
								Context.BIND_AUTO_CREATE);
					}
					else
					{
						Toast.makeText(getActivity(),
								"El servicio ya está presa", Toast.LENGTH_SHORT)
								.show();
					}
				}
			};
		
		View.OnClickListener mButtonCallServiceClickListener =
			new View.OnClickListener()
			{
				@Override
				public void onClick (View v)
				{
					Log.v(TAG, "Button Call Service onClick()");
					if (mIsBound == true)
					{
						int num = mBoundService.getRandomNumber();
						mTextViewRandomNumber.setText("" + num);
					}
					else
					{
						Toast.makeText(getActivity(),
								"El servicio no está obligado",
								Toast.LENGTH_SHORT).show();
					}
				}
			};
		
		View.OnClickListener mButtonDownloadClickListener =
			new View.OnClickListener()
			{
				@Override
				public void onClick (View v)
				{
					Log.v(TAG, "Button Download onClick()");
					if (mIsDownloading == false)
					{
						// Data
						String url = mEditTextUrl.getText().toString();
						String filename =
							mEditTextFileName.getText().toString();
						
						// Call Service
						Intent intent =
							new Intent(getActivity(),
									DownloadService.class);
						intent.putExtra(DownloadService.URL_KEY, url);
						intent.putExtra(DownloadService.FILENAME_KEY, filename);
						getActivity().startService(intent);
						
						// UI
						mIsDownloading = true;
						mButtonDownload.setEnabled(false);
						
						Toast.makeText(getActivity(), "Iniciando el servicio",
								Toast.LENGTH_SHORT).show();
					}
				}
			};
		
		BroadcastReceiver mReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive (Context arg0, Intent arg1)
			{
				int progressValue =
					arg1.getIntExtra(DownloadService.PROGRESS_KEY, -1);
				int resultValue =
					arg1.getIntExtra(DownloadService.RESULT_KEY,
							Activity.RESULT_CANCELED);
				if (progressValue == -1)
				{
					// result
					// UI
					mIsDownloading = false;
					mButtonDownload.setEnabled(true);
					mProgressBarDownload1.setVisibility(View.INVISIBLE);
					mProgressBarDownload2.setVisibility(View.INVISIBLE);
					
					// getActivity().unregisterReceiver(mReceiver);
					// mIsReceiving = false;
					
					Toast.makeText(getActivity(),
							"El descarga completado con result:" + resultValue,
							Toast.LENGTH_SHORT)
							.show();
					Log.v(TAG, "Downloading is completed with result:"
							+ resultValue);
					
				}
				else
				{
					// progress
					// UI
					if (progressValue == DownloadService.PROGRESS_INDETERMINATE)
					{
						if (mProgressBarDownload2.getVisibility() == View.INVISIBLE)
						{
							mProgressBarDownload2.setVisibility(View.VISIBLE);
						}
					}
					else
					{
						if (mProgressBarDownload1.getVisibility() == View.INVISIBLE)
						{
							mProgressBarDownload1.setVisibility(View.VISIBLE);
						}
						
						mProgressBarDownload1.setProgress(progressValue);
						Log.v(TAG, "receives progress:" + progressValue);
					}
				}
				
			}
		};
		EditText mEditTextFileName, mEditTextUrl;
		ProgressBar mProgressBarDownload1, mProgressBarDownload2;
		TextView mTextViewRandomNumber;
		
		public PlaceholderFragment ()
		{
		}
		
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView =
				inflater.inflate(R.layout.fragment_main, container, false);
			
			mEditTextUrl = (EditText) rootView.findViewById(R.id.edittext_url);
			
			mEditTextFileName =
				(EditText) rootView.findViewById(R.id.edittext_filename);
			
			mButtonDownload =
				(Button) rootView.findViewById(R.id.button_download);
			mButtonDownload.setOnClickListener(mButtonDownloadClickListener);
			mButtonDownload.setEnabled(!mIsDownloading);
			
			mProgressBarDownload1 =
				(ProgressBar) rootView.findViewById(R.id.progressbar_download1);
			mProgressBarDownload1.setVisibility(View.INVISIBLE);
			
			mProgressBarDownload2 =
				(ProgressBar) rootView.findViewById(R.id.progressbar_download2);
			mProgressBarDownload2.setVisibility(View.INVISIBLE);
			
			mButtonBind = (Button) rootView.findViewById(R.id.button_bind);
			mButtonBind.setOnClickListener(mButtonBindClickListener);
			mButtonBind.setEnabled(!mIsBound);
			
			mButtonCallService =
				(Button) rootView.findViewById(R.id.button_callservice);
			mButtonCallService
					.setOnClickListener(mButtonCallServiceClickListener);
			mButtonCallService.setEnabled(mIsBound);
			
			mTextViewRandomNumber =
				(TextView) rootView.findViewById(R.id.textview_number);
			
			LinearLayout linearLayout =
				(LinearLayout) rootView.findViewById(R.id.linearlayout_root);
			linearLayout.setOnClickListener(mHideSoftKeyboard);
			
			// Register BroadcastReceiver
			if (mIsReceiving == false)
			{
				getActivity().registerReceiver(
						mReceiver,
						new IntentFilter(
								DownloadService.NOTIFICATION_KEY));
				mIsReceiving = true;
			}
			
			return rootView;
		}
		
		@Override
		public void onDestroyView ()
		{
			
			// boolean mIsBound = false;
			if (mIsBound == true)
			{
				getActivity().unbindService(mConnection);
				mIsBound = false;
			}
			
			mIsDownloading = false;
			
			if (mIsReceiving == true)
			{
				getActivity().unregisterReceiver(mReceiver);
				mIsReceiving = false;
			}
			
			super.onDestroyView();
		}
		
		@Override
		public void onPause ()
		{
			InputMethodManager inputMethodManager =
				(InputMethodManager) getActivity().getSystemService(
						Activity.INPUT_METHOD_SERVICE);
			if (inputMethodManager.isActive())
			{
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(), 0);
			}
			super.onPause();
		}
		
		View.OnClickListener mHideSoftKeyboard = new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				InputMethodManager inputMethodManager =
					(InputMethodManager) getActivity().getSystemService(
							Activity.INPUT_METHOD_SERVICE);
				if (inputMethodManager.isActive())
				{
					inputMethodManager.hideSoftInputFromWindow(getActivity()
							.getCurrentFocus().getWindowToken(), 0);
				}
			}
		};
	}
	
}
