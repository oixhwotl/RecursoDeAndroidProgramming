package com.example.sqlite01;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailViewActivity extends Activity
{
	
	MyWord mMyWord = new MyWord();
	
	boolean mIsEditing = false;
	
	private MyDatabaseConnect mDatabaseConnect;
	
	private TextView mTextViewId;
	private EditText mEditTextWord;
	private View.OnTouchListener mEditTextWordTouchListener =
		new View.OnTouchListener()
		{
			@Override
			public boolean onTouch (View v, MotionEvent event)
			{
				startEditingMyWord();
				return false;
			}
		};
	
	private Button mButtonSave;
	private Button mButtonCancel;
	private LinearLayout mLinearLayout;
	private Menu mMenu;
	
	InputMethodManager mInputMethodManager;
	
	private Button.OnClickListener mButtonSaveListener =
		new Button.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				mMyWord.word = mEditTextWord.getText().toString();
				
				if (mMyWord._id == MainActivity.INDEX_FOR_INSERT)
				{
					insertMyWord();
					mTextViewId.setText("" + mMyWord._id);
				}
				else
				{
					updateMyWord();
				}
				finishEditingMyWord();
			}
		};
	
	private Button.OnClickListener mButtonCancelListener =
		new Button.OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				mEditTextWord.setText(mMyWord.word);
				
				finishEditingMyWord();
			}
		};
	
	private void startEditingMyWord ()
	{
		mIsEditing = true;
		
		mMenu.findItem(R.id.actionbar_action_edit).setEnabled(false);
		mMenu.findItem(R.id.actionbar_action_delete).setEnabled(false);
		
		mLinearLayout.setVisibility(View.VISIBLE);
		
		mEditTextWord.setCursorVisible(true);
		
		if (mInputMethodManager == null)
		{
			mInputMethodManager =
				(InputMethodManager) this.getSystemService(
						Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.showSoftInput(mEditTextWord, 0);
	}
	
	private void finishEditingMyWord ()
	{
		if (mMenu != null)
		{
			mMenu.findItem(R.id.actionbar_action_edit).setEnabled(true);
			mMenu.findItem(R.id.actionbar_action_delete).setEnabled(true);
		}
		mLinearLayout.setVisibility(View.INVISIBLE);
		
		mEditTextWord.setCursorVisible(false);
		
		if (mInputMethodManager == null)
		{
			mInputMethodManager =
				(InputMethodManager) this.getSystemService(
						Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.hideSoftInputFromWindow(
				mEditTextWord.getWindowToken(), 0);
		
		mIsEditing = false;
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_view);
		
		mDatabaseConnect = MyDatabaseConnect.getOpenedDatabase(this);
		
		mTextViewId = (TextView) findViewById(R.id.textview_id);
		mEditTextWord =
			(EditText) findViewById(R.id.edittext_word);
		mEditTextWord.setOnTouchListener(mEditTextWordTouchListener);
		
		mButtonSave = (Button) findViewById(R.id.button_save);
		mButtonSave.setOnClickListener(mButtonSaveListener);
		
		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mButtonCancel.setOnClickListener(mButtonCancelListener);
		
		mLinearLayout =
			(LinearLayout) findViewById(R.id.linearlayout_for_buttons);
	}
	
	@Override
	protected void onResume ()
	{
		Intent intent = getIntent();
		int index =
			intent.getIntExtra(MainActivity.ARGUMENT_INDEX,
					MainActivity.INDEX_FOR_INSERT);
		
		mMyWord._id = index;
		
		if (index == MainActivity.INDEX_FOR_INSERT)
		{
			mTextViewId.setText("NEW");
			// mEditTextWord.setEnabled(true);
			
			mLinearLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			ArrayList<MyWord> tempList =
				mDatabaseConnect.selectMyWords(mMyWord);
			mMyWord.word = tempList.get(0).word;
			
			mTextViewId.setText("" + (mMyWord._id));
			mEditTextWord.setText(mMyWord.word);
			mEditTextWord.setEnabled(false);
			
			mLinearLayout.setVisibility(View.INVISIBLE);
			
			finishEditingMyWord();
		}
		
		super.onResume();
	}
	
	@Override
	public void onBackPressed ()
	{
		if (mIsEditing == true)
		{
			finishEditingMyWord();
		}
		
		super.onBackPressed();
		
	}
	
	@Override
	protected void onDestroy ()
	{
		MyDatabaseConnect.closeDatabase(mDatabaseConnect);
		mDatabaseConnect = null;
		
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mMenu = menu;
		
		menu.findItem(R.id.actionbar_action_new).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_NEVER);
		menu.findItem(R.id.actionbar_action_edit).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.actionbar_action_delete).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.actionbar_action_new)
		{
			return true;
		}
		else if (id == R.id.actionbar_action_edit)
		{
			
			startEditingMyWord();
			return true;
		}
		else if (id == R.id.actionbar_action_delete)
		{
			
			deleteMyWord();
			onBackPressed();
			return true;
		}
		else if (id == android.R.id.home)
		{
			
			onBackPressed();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void insertMyWord ()
	{
		if (mMyWord._id != MainActivity.INDEX_FOR_INSERT)
		{
			return;
		}
		
		int index = mDatabaseConnect.insertMyWord(mMyWord);
		mMyWord._id = index;
	}
	
	private void deleteMyWord ()
	{
		if (mMyWord._id == MainActivity.INDEX_FOR_INSERT)
		{
			return;
		}
		
		mDatabaseConnect.deleteMyWord(mMyWord);
	}
	
	private void updateMyWord ()
	{
		if (mMyWord._id == MainActivity.INDEX_FOR_INSERT)
		{
			return;
		}
		
		mDatabaseConnect.updateMyWord(mMyWord);
	}
	
}
