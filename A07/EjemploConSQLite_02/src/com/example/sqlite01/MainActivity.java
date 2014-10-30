package com.example.sqlite01;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity
{
	private ListView mListView;
	private ArrayList<MyWord> mMyWordList;
	
	private MyDatabaseConnect mDatabaseConnect;
	
	public static final String ARGUMENT_INDEX = "INDEX";
	public static final int INDEX_FOR_INSERT = -1;
	
	private AdapterView.OnItemClickListener mOnClickListener =
		new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick (AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				Intent intent =
					new Intent(getApplicationContext(),
							DetailViewActivity.class);
				intent.putExtra(MainActivity.ARGUMENT_INDEX,
						mMyWordList.get(arg2)._id);
				startActivity(intent);
				
			}
		};
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDatabaseConnect = MyDatabaseConnect.getOpenedDatabase(this);
		
		mListView = (ListView) findViewById(R.id.listview1);
	}
	
	@Override
	protected void onResume ()
	{
		
		mMyWordList = mDatabaseConnect.selectMyWords(null);
		if (mMyWordList.size() > 0)
		{
			ArrayAdapter<MyWord> adapter =
				new ArrayAdapter<MyWord>(this,
						android.R.layout.simple_list_item_1, mMyWordList);
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(mOnClickListener);
		}
		super.onResume();
	}
	
	@Override
	public void onDestroy ()
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
		menu.findItem(R.id.actionbar_action_new).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.actionbar_action_edit).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_NEVER);
		menu.findItem(R.id.actionbar_action_delete).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_NEVER);
		
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
			
			Intent intent =
				new Intent(getApplicationContext(), DetailViewActivity.class);
			intent.putExtra(MainActivity.ARGUMENT_INDEX,
					MainActivity.INDEX_FOR_INSERT);
			startActivity(intent);
			
			return true;
		}
		else if (id == R.id.actionbar_action_edit)
		{
			
			return true;
		}
		else if (id == R.id.actionbar_action_delete)
		{
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
