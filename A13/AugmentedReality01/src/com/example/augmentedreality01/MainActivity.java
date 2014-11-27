package com.example.augmentedreality01;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private boolean mIsInPreview = false;

	private SensorManager mSensorManager;
	private static final float[] ZEROS3F = { 0.0f, 0.0f, 0.0f };

	private Sensor mAccelerometer;
	private float[] mAccData = new float[3];
	private boolean mIsAccDataReady = false;
	private boolean mIsAccAvailable = false;

	private TextView mTextViewAccX;
	private TextView mTextViewAccY;
	private TextView mTextViewAccZ;

	private Sensor mMagnetometer;
	private float[] mMagData = new float[3];
	private boolean mIsMagDataReady = false;
	private boolean mIsMagAvailable = false;

	private TextView mTextViewMagX;
	private TextView mTextViewMagY;
	private TextView mTextViewMagZ;

	private float[] mRotationMatrix = new float[9];
	private float[] mOrientation = new float[3];

	private LocationManager mLocationManager;
	private TextView mTextViewGpsLa;
	private TextView mTextViewGpsLo;
	private TextView mTextViewGpsAl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview1);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(mSurfaceHolderCallback);

		initTextViews();

		initSensorManager();
		initLocationManager();
	}

	private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.v(TAG, "SurfaceHolder.Callback surfaceCreated()");
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
			} catch (Exception e) {
				Log.e(TAG, "Exception in setPreviewDisplay()");
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.v(TAG, "SurfaceHolder.Callback surfaceChanged()");

			Camera.Parameters params = mCamera.getParameters();

			Camera.Size size = getBestPreviewSize(width, height, params);

			if (size != null) {
				params.setPreviewSize(size.width, size.height);
				mCamera.setParameters(params);
				mCamera.startPreview();
				mIsInPreview = true;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.v(TAG, "SurfaceHolder.Callback surfaceDestroyed()");
			stopCamera();
		}
	};

	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location arg0) {
			Log.v(TAG, "LocationListener onLocationChanged()");
			setLocationViews(arg0);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			Log.v(TAG, "LocationListener onProviderDisabled() " + arg0);
		}

		@Override
		public void onProviderEnabled(String arg0) {
			Log.v(TAG, "LocationListener onProviderEnabled() " + arg0);
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			Log.v(TAG, "LocationListener onStatusChanged() " + arg0);
		}

	};

	private void startCamera() {
		if (mCamera == null) {
			Log.v(TAG, "startCamera() ");
			mCamera = Camera.open();
		}
	}

	private void stopCamera() {
		if (mCamera == null) {
			return;
		}
		Log.v(TAG, "stopCamera() ");
		if (mIsInPreview) {
			mCamera.stopPreview();
			mIsInPreview = false;
		}
		mCamera.release();
		mCamera = null;
	}

	private void initTextViews() {
		if (mTextViewAccX != null) {
			return;
		}
		Log.v(TAG, "initTextViews() ");

		mTextViewAccX = (TextView) findViewById(R.id.textview_acc_x);
		mTextViewAccY = (TextView) findViewById(R.id.textview_acc_y);
		mTextViewAccZ = (TextView) findViewById(R.id.textview_acc_z);

		mTextViewMagX = (TextView) findViewById(R.id.textview_mag_x);
		mTextViewMagY = (TextView) findViewById(R.id.textview_mag_y);
		mTextViewMagZ = (TextView) findViewById(R.id.textview_mag_z);

		mTextViewGpsLa = (TextView) findViewById(R.id.textview_gps_la);
		mTextViewGpsLo = (TextView) findViewById(R.id.textview_gps_lo);
		mTextViewGpsAl = (TextView) findViewById(R.id.textview_gps_al);
	}

	private void setSensorViews() {
		// Log.d(TAG, "setSensorViews() ");
		mTextViewAccX.setText(String.valueOf(mAccData[0]));
		mTextViewAccY.setText(String.valueOf(mAccData[1]));
		mTextViewAccZ.setText(String.valueOf(mAccData[2]));

		mTextViewMagX.setText(String.valueOf(mMagData[0]));
		mTextViewMagY.setText(String.valueOf(mMagData[1]));
		mTextViewMagZ.setText(String.valueOf(mMagData[2]));

	}

	private void setLocationViews(Location aLocation) {
		// Log.d(TAG, "setLocationViews() ");
		mTextViewGpsLa.setText(String.valueOf(aLocation.getLatitude()));
		mTextViewGpsLo.setText(String.valueOf(aLocation.getLongitude()));
		mTextViewGpsAl.setText(String.valueOf(aLocation.getAltitude()));
	}

	private void initSensorManager() {
		if (mSensorManager != null) {
			return;
		}
		Log.v(TAG, "initSensorManager() ");
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

	}

	private void initLocationManager() {
		if (mLocationManager != null) {
			return;
		}
		Log.v(TAG, "initLocationManager() ");

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// criteria.setAltitudeRequired(false);
		// criteria.setBearingRequired(false);
		// criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);

		String bestProvider = mLocationManager.getBestProvider(criteria, true);

		Log.v(TAG, "Best provider: " + bestProvider);

		mLocationManager.requestLocationUpdates(bestProvider, 50, 0,
				mLocationListener);
	}

	private SensorEventListener mSensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			Log.v(TAG, "SensorEventListener onAccuracyChanged() ");
		}

		@Override
		public void onSensorChanged(SensorEvent arg0) {
			// Log.d(TAG, "SensorEventListener onSensorChanged() ");

			if (arg0.sensor == mAccelerometer) {
				System.arraycopy(arg0.values, 0, mAccData, 0,
						arg0.values.length);
				mIsAccDataReady = true;
			} else if (arg0.sensor == mMagnetometer) {
				System.arraycopy(arg0.values, 0, mMagData, 0,
						arg0.values.length);
				mIsMagDataReady = true;
			}

			setSensorViews();
			
			if (mIsAccDataReady && mIsMagDataReady) {
				SensorManager.getRotationMatrix(mRotationMatrix, null,
						mAccData, mMagData);
				SensorManager.getOrientation(mRotationMatrix, mOrientation);
				Log.v(TAG, String.format("Orientation: %f, %f, %f",
						mOrientation[0], mOrientation[1], mOrientation[2]));

				System.arraycopy(ZEROS3F, 0, mAccData, 0, ZEROS3F.length);
			}

		}

	};

	private void registerSensors() {
		Log.v(TAG, "registerSensors() ");
		if (mIsAccAvailable == false) {
			mIsAccAvailable = mSensorManager.registerListener(
					mSensorEventListener, mAccelerometer,
					SensorManager.SENSOR_DELAY_NORMAL);
			System.arraycopy(ZEROS3F, 0, mAccData, 0, ZEROS3F.length);
		}
		if (mIsMagAvailable == false) {
			mIsMagAvailable = mSensorManager.registerListener(
					mSensorEventListener, mMagnetometer,
					SensorManager.SENSOR_DELAY_NORMAL);
			System.arraycopy(ZEROS3F, 0, mMagData, 0, ZEROS3F.length);
		}
	}

	private void unregisterSensors() {
		Log.v(TAG, "unregisterSensors() ");
		if (mIsAccAvailable) {
			mSensorManager.unregisterListener(mSensorEventListener,
					mAccelerometer);
			mIsAccAvailable = false;
			mIsAccDataReady = false;
		}
		if (mIsMagAvailable) {
			mSensorManager.unregisterListener(mSensorEventListener,
					mMagnetometer);
			mIsMagAvailable = false;
			mIsMagDataReady = false;
		}
	}

	@Override
	protected void onPause() {
		Log.v(TAG, "onPause() ");
		stopCamera();
		unregisterSensors();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onPause() ");
		super.onResume();
		registerSensors();
		startCamera();
	}

	private Camera.Size getBestPreviewSize(int aMaxWidth, int aMaxHeight,
			Camera.Parameters aParams) {
		Camera.Size result = null;
		int resultArea = 0;

		for (Camera.Size size : aParams.getSupportedPreviewSizes()) {
			if ((size.width > aMaxWidth) || (size.height > aMaxHeight)) {
				continue;
			}
			if (result == null) {
				result = size;
				resultArea = size.width * size.height;
			} else {
				int newArea = size.width * size.height;
				if (newArea > resultArea) {
					result = size;
					resultArea = newArea;
				}
			}
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
