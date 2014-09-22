package com.example.holamundo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class SecondActivity extends Activity
{
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
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
		
		TextView mTextView3;
		Button mButton;
		View.OnClickListener mButtonClickListener = new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				Intent intent = new Intent(getActivity(), MainActivity.class);
				getActivity().startActivity(intent);
			}
		};
		
		public PlaceholderFragment ()
		{
		}
		
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_second,
					container, false);
			
			mButton = (Button) rootView.findViewById(R.id.button2);
			mButton.setOnClickListener(mButtonClickListener);
			
			mTextView3 = (TextView) rootView.findViewById(R.id.textview3);
			
			Intent intent = getActivity().getIntent();
			if (intent != null)
			{
				Bundle bundle = intent.getBundleExtra("BUNDLE");
				String textVal = bundle.getString("TEXTVAL");
				int colorVal = bundle.getInt("COLORVAL");
				boolean isBoldVal = bundle.getBoolean("ISBOLDVAL");
				
				mTextView3.setTextColor(colorVal);
				if (isBoldVal)
				{
					SpannableString spanString = new SpannableString(textVal);
					spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
							spanString.length(), 0);
					mTextView3.setText(spanString);
				}
				else
				{
					mTextView3.setText(textVal);
				}
			}
			
			return rootView;
		}
	}
	
}
