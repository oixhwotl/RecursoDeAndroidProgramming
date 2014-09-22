package com.example.ejemploconsqlite_01;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ejemploconsqlite_01.DetailViewFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListViewFragment extends Fragment
{
	private static final String TAG = "ListViewFragment";
	private ListView mListView;
	private ArrayList<MyWord> mMyWordList;
	
	private MyDatabaseConnect mDatabaseConnect;
	
	public static final String ARGUMENT_INDEX = "INDEX";
	public static final int INDEX_FOR_INSERT = -1;
	
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		Log.v(TAG, "onCreate(Bundle)");
		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
	{
		Log.v(TAG, "onCreateOptionsMenu(Menu, MenuInflater)");
		
		inflater.inflate(R.menu.actionbar_list, menu);
		
		menu.findItem(R.id.actionbar_action_new).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.findItem(R.id.actionbar_action_edit).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_NEVER);
		menu.findItem(R.id.actionbar_action_delete).setShowAsActionFlags(
				MenuItem.SHOW_AS_ACTION_NEVER);
		
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
			DetailViewFragment detailViewFragment =
				new DetailViewFragment();
			Bundle args = new Bundle();
			args.putInt(ListViewFragment.ARGUMENT_INDEX,
					ListViewFragment.INDEX_FOR_INSERT);
			detailViewFragment.setArguments(args);
			
			getFragmentManager()
					.beginTransaction()
					.addToBackStack(TAG)
					.replace(R.id.container,
							detailViewFragment)
					.commit();
			/*
			 * Toast.makeText(getActivity(), "New selected", Toast.LENGTH_SHORT)
			 * .show();
			 */
			return true;
		}
		else if (id == R.id.actionbar_action_edit)
		{
			Toast.makeText(getActivity(), TAG + ": Edit selected",
					Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		else if (id == R.id.actionbar_action_delete)
		{
			Toast.makeText(getActivity(), TAG + " Delete selected",
					Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public ListViewFragment ()
	{
	}
	
	private AdapterView.OnItemClickListener mOnClickListener =
		new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick (AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				DetailViewFragment detailViewFragment =
					new DetailViewFragment();
				Bundle args = new Bundle();
				args.putInt(ListViewFragment.ARGUMENT_INDEX,
						mMyWordList.get(arg2)._id);
				detailViewFragment.setArguments(args);
				
				getFragmentManager()
						.beginTransaction()
						.addToBackStack(TAG)
						.replace(R.id.container, detailViewFragment)
						.commit();
			}
		};
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		Log.v(TAG, "onCreateView()");
		mDatabaseConnect = MyDatabaseConnect.getOpenedDatabase(getActivity());
		
		View rootView =
			inflater.inflate(R.layout.fragment_list, container, false);
		
		mListView = (ListView) rootView.findViewById(R.id.listview01);
		
		mMyWordList = mDatabaseConnect.selectMyWords(null);
		if (mMyWordList.size() > 0)
		{
			ArrayAdapter<MyWord> adapter =
				new ArrayAdapter<MyWord>(getActivity(),
						android.R.layout.simple_list_item_1, mMyWordList);
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(mOnClickListener);
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
	
}
