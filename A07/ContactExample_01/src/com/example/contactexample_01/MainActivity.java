package com.example.contactexample_01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;
import android.provider.ContactsContract;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate ( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if ( savedInstanceState == null )
		{
			getFragmentManager().beginTransaction()
							.add(R.id.container, new PlaceholderFragment())
							.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu ( Menu menu )
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected ( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if ( id == R.id.action_settings )
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
		String [] mMapFrom =
		{
			"NAME", "DATA"
		};
		int [] mMapTo =
		{
			android.R.id.text1, android.R.id.text2
		};

		ListView mListView01;
		Button mButtonRefresh;
		View.OnClickListener mButtonRefreshListener = new View.OnClickListener()
		{
			@Override
			public void onClick ( View v )
			{
				readContacts();
			}
		};

		public PlaceholderFragment ( )
		{
		}

		@Override
		public View onCreateView ( LayoutInflater inflater,
						ViewGroup container, Bundle savedInstanceState )
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,
							false);
			mListView01 = (ListView) rootView.findViewById(R.id.listview_01);
			mButtonRefresh = (Button) rootView
							.findViewById(R.id.button_refresh);
			mButtonRefresh.setOnClickListener(mButtonRefreshListener);

			readContacts();

			return rootView;
		}

		public void readContacts ( )
		{

			ArrayList < HashMap < String, String >> arrayList = new ArrayList < HashMap < String, String > >();

			Uri CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
			String CONTACTS_ID_NAME = ContactsContract.Contacts._ID;
			String CONTACTS_DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
			String CONTACTS_HAS_PHONE_NUMBER_NAME = ContactsContract.Contacts.HAS_PHONE_NUMBER;

			Uri PHONENUMBER_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String PHONENUMBER_ID_NAME = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
			String PHONENUMBER_NUMBER_NAME = ContactsContract.CommonDataKinds.Phone.NUMBER;

			Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
			String EMAIL_ID_NAME = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
			String EMAIL_DATA_NAME = ContactsContract.CommonDataKinds.Email.DATA;

			ContentResolver contentResolver = getActivity()
							.getContentResolver();

			Cursor cursor = contentResolver.query(CONTACTS_CONTENT_URI, null,
							null, null, null);
			if ( cursor.getCount() > 0 )
			{
				while ( cursor.moveToNext() )
				{
					String contactId = cursor.getString(cursor
									.getColumnIndex(CONTACTS_ID_NAME));
					String displayName = cursor.getString(cursor
									.getColumnIndex(CONTACTS_DISPLAY_NAME));
					int hasPhoneNumber = Integer
									.parseInt(cursor.getString(cursor
													.getColumnIndex(CONTACTS_HAS_PHONE_NUMBER_NAME)));

					if ( hasPhoneNumber != 0 )
					{
						Cursor phoneNumberCursor = contentResolver.query(
										PHONENUMBER_CONTENT_URI, null,
										PHONENUMBER_ID_NAME + " = ? ",
										new String [ ]
										{
											contactId
										}, null);
						if ( phoneNumberCursor.getCount() > 0 )
						{
							while ( phoneNumberCursor.moveToNext() )
							{
								String phoneNumber = phoneNumberCursor
												.getString(phoneNumberCursor
																.getColumnIndex(PHONENUMBER_NUMBER_NAME));

								HashMap < String, String > item = new HashMap < String, String >();
								item.put("NAME", displayName);
								item.put("DATA", phoneNumber);
								arrayList.add(item);

							}
						}
						phoneNumberCursor.close();

						// Query and loop for every email of the contact
						Cursor emailAddrCursor = contentResolver.query(
										EMAIL_CONTENT_URI, null, EMAIL_ID_NAME
														+ " = ?",
										new String [ ]
										{
											contactId
										}, null);

						while ( emailAddrCursor.moveToNext() )
						{
							String emailAddr = emailAddrCursor
											.getString(emailAddrCursor
															.getColumnIndex(EMAIL_DATA_NAME));

							HashMap < String, String > item = new HashMap < String, String >();
							item.put("NAME", displayName);
							item.put("DATA", emailAddr);
							arrayList.add(item);

						}

						emailAddrCursor.close();
					}

				}

				SimpleAdapter adapter = new SimpleAdapter(getActivity(),
								arrayList,
								android.R.layout.simple_list_item_2, mMapFrom,
								mMapTo);
				mListView01.setAdapter(adapter);
			}
		}
	}

}
