package com.example.listview02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private static final String TAG="MainActivity";
	private ListView mListView;
	private AdapterView.OnItemClickListener mOnItemClickListener= new AdapterView.OnItemClickListener(){

		@Override
		public void onItemClick (AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			Toast.makeText(getApplicationContext(), "Click"+arg2, Toast.LENGTH_SHORT);
			
		}
		
	};
	public static final String KEY_TITLE="TITLE";
	public static final String KEY_IMAGE="IMAGE";
	private static String mTitle[]={
		"Donut","Eclair",
		"Froyo", "Ginger Bread",
		"HoneyComb","IceCream Sandwich",
		"Jelly Bean","Kitkat",
		"Lollipop"};
	private static String mImages[]={
		"android16donut","android20eclair",
		"android22froyo","android23gingerbread",
		"android30honeycomb","android40icreamsandwitch",
		"android41jellybean","android44kitkat",
		"android50lollipop"};
	private static ArrayList <HashMap<String,String>> mArrayList= new ArrayList<HashMap<String,String>>();
	static{
		Log.v(TAG, "initializing ArrayList");
		int sizeOfArray=mTitle.length;
		int  i;
		for(i=0; i < sizeOfArray;i++){
			HashMap<String,String>hashMap=new HashMap<String, String>();
			hashMap.put(KEY_TITLE, mTitle[i]);
			hashMap.put(KEY_IMAGE, mImages[i]);
			mArrayList.add(hashMap);
		}
	}
	public class CustomArrayAdapter extends ArrayAdapter<HashMap<String,String>>{
		public CustomArrayAdapter (Context context,ArrayList<HashMap<String, String>> aValues)
			
		
		{
			super(context, R.layout.listview_item,aValues);
			Log.v(TAG, "CustomArrayAdapter");
			mContext=context;
			mValues=aValues;
			// TODO Auto-generated constructor stub
		}
		private static final String TAG="CustomArrayAdapter";
				private Context mContext;
				
		private ArrayList<HashMap<String,String>> mValues;
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
				LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				itemView=inflater.inflate(R.layout.listview_item,null);
				ViewHolder viewHolder= new ViewHolder();
				viewHolder.textView=(TextView)itemView.findViewById(R.id.textview1);
				viewHolder.imageView=(ImageView)itemView.findViewById(R.id.imageview1);
				itemView.setTag(viewHolder);
				
			}
			ViewHolder vHolder=(ViewHolder)itemView.getTag();
			HashMap<String,String>hashMap=(HashMap<String, String>)mValues.get(position);
			vHolder.textView.setText((String)hashMap.get(KEY_TITLE));
			String imageName=(String)hashMap.get(KEY_IMAGE);
			int imageId=mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
			vHolder.imageView.setImageResource(imageId);
			
			return itemView;
		}
		
		
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		Log.v(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CustomArrayAdapter customArrayAdapter= new CustomArrayAdapter(this, mArrayList);
		mListView=(ListView)findViewById(R.id.listview1);
		mListView.setAdapter(customArrayAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		
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
