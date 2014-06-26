package com.example.holamundo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
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
					.add(R.id.container, new PlaceholderFragment()).commit();
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
		
		EditText mEditText1;
		RadioGroup mRadioGroup1;
		CheckBox mCheckBox1;
		TextView mTextView1;
		
		Button mButton;
		View.OnClickListener mButtonClickListener = new View.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				String textVal = mEditText1.getText().toString();
				int colorVal = mRadioGroup1.getCheckedRadioButtonId();
				boolean isBoldVal = mCheckBox1.isChecked();
				
				int colorValInd = Color.BLACK;
				switch (colorVal)
				{
					case R.id.radio0:
						colorValInd = Color.RED;
						break;
					case R.id.radio1:
						colorValInd = Color.BLUE;
						break;
				}
				
				if (textVal.length() > 1)
				{
					Toast.makeText(getActivity(),
							textVal + " " + colorVal + " " + isBoldVal,
							Toast.LENGTH_SHORT).show();
					
					Intent intent = new Intent(getActivity(),
							SecondActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("TEXTVAL", textVal);
					bundle.putInt("COLORVAL", colorValInd);
					bundle.putBoolean("ISBOLDVAL", isBoldVal);
					intent.putExtra("BUNDLE", bundle);
					
					getActivity().startActivity(intent);
				}
				else
				{
					mTextView1.setText("ERROR: Llenar EditText");
				}
			}
		};
		
		public PlaceholderFragment ()
		{
		}
		
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			mButton = (Button) rootView.findViewById(R.id.button1);
			mButton.setOnClickListener(mButtonClickListener);
			
			mEditText1 = (EditText) rootView.findViewById(R.id.edittext1);
			mRadioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
			mCheckBox1 = (CheckBox) rootView.findViewById(R.id.checkBox1);
			mTextView1 = (TextView) rootView.findViewById(R.id.textview1);
			mTextView1.setText("Mensaje");
			
			return rootView;
		}
	}
}
