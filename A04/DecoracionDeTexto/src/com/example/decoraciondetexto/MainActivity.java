package com.example.decoraciondetexto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	EditText editText;
	RadioGroup radioGroup;
	CheckBox checkBox;
	Button button1;
	TextView textView;
	
	View.OnClickListener button1OnClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			//Toast.makeText(getApplication(), "Button Clicked", Toast.LENGTH_SHORT).show(); 
			//setPreviewText();
			Intent intent=new Intent(getApplication(),TextActivity.class);
			
			Bundle bundle=new Bundle();
			bundle.putString("TEXTVAL", textView.getText().toString());
			
			int color_id = radioGroup.getCheckedRadioButtonId(), color_val = Color.BLACK;
			switch (color_id)
			{
				case R.id.radiobutton_azul: color_val = Color.BLUE ;break;
				case R.id.radiobutton_negro: color_val = Color.BLACK; break;
				case R.id.radiobutton_rojo: color_val = Color.RED ; break;
			}
			
			bundle.putInt("COLORVAL",color_val);
			bundle.putBoolean("ISBOLDVAL", checkBox.isChecked());
			intent.putExtra("BUNDLE", bundle);
			
			startActivity(intent);
			
		}
	};
	RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
	{
		
		@Override
		public void onCheckedChanged (RadioGroup group, int checkedId)
		{
			Toast.makeText(getApplication(), "" + checkedId, Toast.LENGTH_SHORT).show();
			setPreviewText();
		}
	};
	
	CompoundButton.OnCheckedChangeListener checkBoxOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
	{
		
		@Override
		public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
		{
			Toast.makeText(getApplication(), "" + isChecked, Toast.LENGTH_SHORT).show();
			setPreviewText();
		}
	};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button1=(Button)findViewById(R.id.button1);
		editText=(EditText)findViewById(R.id.edittext_text);
		radioGroup=(RadioGroup)findViewById(R.id.radiogroup_color);
		textView=(TextView)findViewById(R.id.textview1);
		checkBox=(CheckBox)findViewById(R.id.checkbox_negrita);
		
		button1.setOnClickListener(button1OnClickListener);
		radioGroup.setOnCheckedChangeListener(radioGroupOnCheckedChangeListener);
		checkBox.setOnCheckedChangeListener(checkBoxOnCheckedChangeListener);	
		
	}
	
	private void setPreviewText()
	{
		textView.setText(editText.getText());
		int color_id = radioGroup.getCheckedRadioButtonId(), color_val = Color.BLACK;
		switch (color_id)
		{
			case R.id.radiobutton_azul: color_val = Color.BLUE ;break;
			case R.id.radiobutton_negro: color_val = Color.BLACK; break;
			case R.id.radiobutton_rojo: color_val = Color.RED ; break;
		}
		
		textView.setTextColor(color_val);
		if (checkBox.isChecked())
		{
			textView.setTypeface(null, Typeface.BOLD);
		}
		else
		{
			textView.setTypeface(null, Typeface.NORMAL);
		}
		textView.setTextSize(24.0f);
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
