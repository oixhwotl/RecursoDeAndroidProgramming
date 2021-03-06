package com.example.fragment02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fragment02.dummy.DummyContent;
import com.example.fragment02.dummy.DummyContent.DummyItem;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment
{
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;
	
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks
	{
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected (String id);
	}
	
	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks()
	{
		@Override
		public void onItemSelected (String id)
		{
		}
	};
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemListFragment ()
	{
	}
	
	public class CustomArrayAdapter extends ArrayAdapter<DummyItem> {
		public CustomArrayAdapter (Context context, List<DummyItem> aValues)		
		{
			super(context, R.layout.listview_item, aValues);
			Log.v(TAG, "CustomArrayAdapter");
			mContext=context;
			mValues=aValues;
		}
		private static final String TAG="CustomArrayAdapter";
		private Context mContext;
				
		private List<DummyItem> mValues;
		public class ViewHolder{
			public TextView textView;
			public ImageView imageView;
		}
		@Override
		public View getView (int position, View convertView, ViewGroup parent)
		{
			Log.v(TAG, "getView()");
			View itemView=convertView;
			if(itemView==null){
				LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
				itemView = inflater.inflate(R.layout.listview_item, parent, false);
		        
				ViewHolder viewHolder= new ViewHolder();
				viewHolder.textView=(TextView)itemView.findViewById(R.id.textview1);
				viewHolder.imageView=(ImageView)itemView.findViewById(R.id.imageview1);
				itemView.setTag(viewHolder);
				
			}
			
			ViewHolder vHolder=(ViewHolder)itemView.getTag();
			DummyItem dItem = mValues.get(position);
			vHolder.textView.setText(dItem.id);
			String imageName=dItem.image;
			int imageId=mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
			vHolder.imageView.setImageResource(imageId);
			
			return itemView;
		}
		
		
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		CustomArrayAdapter customArrayAdapter= new CustomArrayAdapter(getActivity(), DummyContent.ITEMS);
		setListAdapter(customArrayAdapter);
	}
	
	@Override
	public void onViewCreated (View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
		{
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}
	
	@Override
	public void onAttach (Activity activity)
	{
		super.onAttach(activity);
		
		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks))
		{
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		
		mCallbacks = (Callbacks) activity;
	}
	
	@Override
	public void onDetach ()
	{
		super.onDetach();
		
		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}
	
	@Override
	public void onListItemClick (ListView listView, View view, int position,
			long id)
	{
		super.onListItemClick(listView, view, position, id);
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
	}
	
	@Override
	public void onSaveInstanceState (Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION)
		{
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}
	
	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick (boolean activateOnItemClick)
	{
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick
				? ListView.CHOICE_MODE_SINGLE
				: ListView.CHOICE_MODE_NONE);
	}
	
	private void setActivatedPosition (int position)
	{
		if (position == ListView.INVALID_POSITION)
		{
			getListView().setItemChecked(mActivatedPosition, false);
		}
		else
		{
			getListView().setItemChecked(position, true);
		}
		
		mActivatedPosition = position;
	}
}
