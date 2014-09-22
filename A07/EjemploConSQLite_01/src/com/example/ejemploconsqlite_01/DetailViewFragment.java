package com.example.ejemploconsqlite_01;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailViewFragment extends Fragment
{
	private static final String TAG = "DetailViewFragment";
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
				
				if (mMyWord._id == ListViewFragment.INDEX_FOR_INSERT)
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
				(InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.showSoftInput(mEditTextWord, 0);
	}
	
	private void finishEditingMyWord ()
	{
		mMenu.findItem(R.id.actionbar_action_edit).setEnabled(true);
		mMenu.findItem(R.id.actionbar_action_delete).setEnabled(true);
		
		mLinearLayout.setVisibility(View.INVISIBLE);
		
		mEditTextWord.setCursorVisible(false);
		
		if (mInputMethodManager == null)
		{
			mInputMethodManager =
				(InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.hideSoftInputFromWindow(
				mEditTextWord.getWindowToken(), 0);
		
		mIsEditing = false;
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		Log.v(TAG, "onCreate(Bundle)");
		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		// ActionBar actionBar = getActionBar();
		
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
	{
		Log.v(TAG, "onCreateOptionsMenu(Menu, MenuInflater)");
		
		inflater.inflate(R.menu.actionbar_list, menu);
		mMenu = menu;
		
		menu.findItem(R.id.actionbar_action_new).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_NEVER);
		menu.findItem(R.id.actionbar_action_edit).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.actionbar_action_delete).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		super.onCreateOptionsMenu(menu, inflater);
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
			Toast.makeText(getActivity(), TAG + ": New selected",
					Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		else if (id == R.id.actionbar_action_edit)
		{
			Toast.makeText(getActivity(), TAG + ": Edit selected",
					Toast.LENGTH_SHORT)
					.show();
			
			startEditingMyWord();
			return true;
		}
		else if (id == R.id.actionbar_action_delete)
		{
			Toast.makeText(getActivity(), TAG + " Delete selected",
					Toast.LENGTH_SHORT)
					.show();
			
			deleteMyWord();
			onBackPressed();
			return true;
		}
		else if (id == android.R.id.home)
		{
			Toast.makeText(getActivity(), TAG + " Home selected",
					Toast.LENGTH_SHORT)
					.show();
			
			onBackPressed();
			return true;
		}
		else
		{
			Log.v(TAG, item.toString() + " is selected");
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public DetailViewFragment ()
	{
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		Log.v(TAG, "onCreateView()");
		mDatabaseConnect = MyDatabaseConnect.getOpenedDatabase(getActivity());
		
		View rootView =
			inflater.inflate(R.layout.fragment_detail, container, false);
		
		mTextViewId = (TextView) rootView.findViewById(R.id.textview_id);
		mEditTextWord =
			(EditText) rootView.findViewById(R.id.edittext_word);
		mEditTextWord.setOnTouchListener(mEditTextWordTouchListener);
		
		mButtonSave = (Button) rootView.findViewById(R.id.button_save);
		mButtonSave.setOnClickListener(mButtonSaveListener);
		
		mButtonCancel = (Button) rootView.findViewById(R.id.button_cancel);
		mButtonCancel.setOnClickListener(mButtonCancelListener);
		
		mLinearLayout =
			(LinearLayout) rootView
					.findViewById(R.id.linearlayout_for_buttons);
		
		Bundle args = getArguments();
		int index =
			args.getInt(ListViewFragment.ARGUMENT_INDEX,
					ListViewFragment.INDEX_FOR_INSERT);
		
		mMyWord._id = index;
		
		if (index == ListViewFragment.INDEX_FOR_INSERT)
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
			// mEditTextWord.setEnabled(true);
			
			mLinearLayout.setVisibility(View.INVISIBLE);
		}
		
		return rootView;
	}
	
	@Override
	public void onDestroyView ()
	{
		MyDatabaseConnect.closeDatabase(mDatabaseConnect);
		mDatabaseConnect = null;
		
		super.onDestroyView();
	}
	
	public void onBackPressed ()
	{
		if (mIsEditing == true)
		{
			finishEditingMyWord();
		}
		else
		{
			getFragmentManager().popBackStack();
		}
		// super.onBackPressed();
	}
	
	private void insertMyWord ()
	{
		if (mMyWord._id != ListViewFragment.INDEX_FOR_INSERT)
		{
			return;
		}
		
		int index = mDatabaseConnect.insertMyWord(mMyWord);
		mMyWord._id = index;
	}
	
	private void deleteMyWord ()
	{
		if (mMyWord._id == ListViewFragment.INDEX_FOR_INSERT)
		{
			return;
		}
		
		mDatabaseConnect.deleteMyWord(mMyWord);
	}
	
	private void updateMyWord ()
	{
		if (mMyWord._id == ListViewFragment.INDEX_FOR_INSERT)
		{
			return;
		}
		
		mDatabaseConnect.updateMyWord(mMyWord);
	}
	
}
