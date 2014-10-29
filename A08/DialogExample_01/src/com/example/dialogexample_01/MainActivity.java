package com.example.dialogexample_01;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity
{
	EditText mEditTextTitle, mEditTextContent;
	
	Button mButton1, mButton2;
	View.OnClickListener mButton1ClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			// 1. Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder =
				new AlertDialog.Builder(MainActivity.this);
			
			// 2. Chain together various setter methods to set the dialog
			// characteristics
			String title = mEditTextTitle.getText().toString();
			String content = mEditTextContent.getText().toString();
			builder.setMessage(content).setTitle(title);
			
			// Add the buttons
			builder.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener()
					{
						// User confirm the dialog
						public void onClick (DialogInterface dialog, int id)
						{
							Intent intent =
								new Intent(MainActivity.this, YesActivity.class);
							startActivity(intent);
						}
					});
			builder.setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener()
					{
						public void onClick (DialogInterface dialog, int id)
						{
							Intent intent =
								new Intent(MainActivity.this, NoActivity.class);
							startActivity(intent);
						}
					});
			
			// 3. Get the AlertDialog from create()
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	};
	View.OnClickListener mButton2ClickListener = new View.OnClickListener()
	{
		
		@Override
		public void onClick (View v)
		{
			// Create custom dialog object
			final Dialog dialog = new Dialog(MainActivity.this);
			// Include dialog.xml file
			dialog.setContentView(R.layout.dialog_image_with_button);
			// Set dialog title
			dialog.setTitle("Dialog Propio");
			
			// set values for custom dialog components - text, image and button
			TextView text =
				(TextView) dialog.findViewById(R.id.dialog_textview);
			text.setText("Un ejemplo de dialog propio");
			ImageView image =
				(ImageView) dialog.findViewById(R.id.dialog_imageview);
			image.setImageResource(R.drawable.ic_launcher);
			
			Button declineButton =
					(Button) dialog.findViewById(R.id.dialog_button);
				// if decline button is clicked, close the custom dialog
				declineButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick (View v)
					{
						// Close dialog
						dialog.dismiss();
					}
				});

			// call show()
			dialog.show();
			
			
		}
	};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mEditTextTitle = (EditText) findViewById(R.id.edittext_title);
		mEditTextContent = (EditText) findViewById(R.id.edittext_content);
		
		mButton1 = (Button) findViewById(R.id.button1);
		mButton1.setOnClickListener(mButton1ClickListener);
		
		mButton2 = (Button) findViewById(R.id.button2);
		mButton2.setOnClickListener(mButton2ClickListener);
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
