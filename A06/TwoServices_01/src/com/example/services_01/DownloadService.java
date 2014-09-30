package com.example.services_01;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class DownloadService extends IntentService
{
	private static final String TAG = "DownloadService";
	
	public static final String URL_KEY = "URLPATH";
	public static final String FILENAME_KEY = "FILENAME";
	public static final String RESULT_KEY = "RESULT";
	public static final String PROGRESS_KEY = "PROGRESS";
	public static final int PROGRESS_INDETERMINATE = -123;
	public static final String NOTIFICATION_KEY = "com.example.services_01";
	
	public DownloadService ()
	{
		super(TAG);
	}
	
	public DownloadService (String name)
	{
		super(name);
	}
	
	@Override
	protected void onHandleIntent (Intent arg0)
	{
		Log.v(TAG, "onHandleIntent" + arg0.toString());
		
		String url = arg0.getStringExtra(URL_KEY);
		String fileName = arg0.getStringExtra(FILENAME_KEY);
		
		Log.v(TAG, "onHandleIntent, url=" + url + ", filename=" + fileName);
		
		int result = downloadWithUrlAndFileName(url, fileName);
		broadcastProgress(100, 100);
		broadcastResult(result);
	}
	
	private int downloadWithUrlAndFileName (String aUrlString,
			String aFileName)
	{
		Log.v(TAG, "downloadWithUrlAndFileName(" + aUrlString + ", "
				+ aFileName + ")");
		try
		{
			// set the download URL, a url that points to a file on the internet
			// this is the file to be downloaded
			URL url = new URL(aUrlString);
			
			// create the new connection
			HttpURLConnection urlConnection =
				(HttpURLConnection) url.openConnection();
			
			// set up some things on the connection
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			
			// and connect!
			urlConnection.connect();
			
			// set the path where we want to save the file
			// in this case, going to save it on the root directory of the
			// sd card.
			File sdCardRoot = Environment.getExternalStorageDirectory();
			
			File parentDir = new File(sdCardRoot, "/" +
					Environment.DIRECTORY_DOWNLOADS);
			// File parentDir = Environment.getDownloadCacheDirectory();
			if (!parentDir.exists())
			{
				parentDir.mkdirs();
			}
			
			// create a new file, specifying the path, and the filename
			// which we want to save the file as.
			File file = new File(parentDir, "/" + aFileName);
			
			Log.v(TAG, "file path:" + file.getAbsolutePath());
			
			try
			{
				if (!file.exists())
				{
					file.delete();
				}
				file.createNewFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// this will be used to write the downloaded data into the file we
			// created
			FileOutputStream fileOutput = new FileOutputStream(file);
			
			// this will be used in reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();
			
			// this is the total size of the file
			int totalSize = urlConnection.getContentLength();
			Log.v(TAG, "totalSize:" + totalSize);
			if (totalSize < 0)
			{
				broadcastProgress(0, totalSize);
			}
			
			// variable to store total downloaded bytes
			int downloadedSize = 0;
			
			// create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0; // used to store a temporary size of the
								  // buffer
			
			// now, read through the input buffer and write the contents to the
			// file
			Log.v(TAG, "start downloading");
			while ((bufferLength = inputStream.read(buffer)) > 0)
			{
				// add the data in the buffer to the file in the file output
				// stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);
				
				// add up the size so we know how much is downloaded
				downloadedSize += bufferLength;
				if (totalSize > 0)
				{
					// this is where you would do something to report the
					// prgress,
					// like this maybe
					broadcastProgress(downloadedSize, totalSize);
				}
				else
				{
					/*
					 * Log.v(TAG, "downloading " + bufferLength +
					 * ", cmulatively " + downloadedSize);
					 */
				}
				
			}
			Log.v(TAG, "finish downloading");
			
			// close the input stream when done
			inputStream.close();
			
			// close the output stream when done
			fileOutput.close();
			
			return Activity.RESULT_OK;
		} // catch some possible errors...
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return Activity.RESULT_CANCELED;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return Activity.RESULT_CANCELED;
		}
	}
	
	@Override
	public void onDestroy ()
	{
		Log.v(TAG, "onDestroy()");
		super.onDestroy();
	}
	
	private void broadcastResult (int aResult)
	{
		Log.v(TAG, "broadcastResult:" + aResult);
		
		Intent intent = new Intent(NOTIFICATION_KEY);
		intent.putExtra(RESULT_KEY, aResult);
		sendBroadcast(intent);
	}
	
	private void broadcastProgress (int aDownloadedSize, int aTotalSize)
	{
		Intent intent = new Intent(NOTIFICATION_KEY);
		if (aTotalSize > 0)
		{
			double percent = (aDownloadedSize * 100) / aTotalSize;
			Log.v(TAG, "broadcastProgress:" + percent);
			intent.putExtra(PROGRESS_KEY, (int) percent);
		}
		else
		{
			intent.putExtra(PROGRESS_KEY, PROGRESS_INDETERMINATE);
		}
		sendBroadcast(intent);
	}
}
