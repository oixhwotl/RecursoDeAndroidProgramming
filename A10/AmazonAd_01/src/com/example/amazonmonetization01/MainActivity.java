package com.example.amazonmonetization01;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DefaultAdListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setAdView();
	}

	private AdLayout mAdView; // The ad view used to load and display the ad.

	private void setAdView() {
		// For debugging purposes enable logging, but disable for production
		// builds.
		AdRegistration.enableLogging(true);
		// For debugging purposes flag all ad requests as tests, but set to
		// false for production builds.
		AdRegistration.enableTesting(false);

		mAdView = (AdLayout) findViewById(R.id.ad_view);
		mAdView.setListener(new SampleAdListener());

		try {
			AdRegistration.setAppKey("2137135c958845a5986f84392b9e3a2c");
		} catch (final Exception e) {
			Log.e(TAG, "Exception thrown: " + e.toString());
			return;
		}
		loadAd();
	}

	/**
	 * Load a new ad.
	 */
	public void loadAd() {
		// Load an ad with default ad targeting.
		mAdView.loadAd();

		// Note: You can choose to provide additional targeting information to
		// modify how your ads
		// are targeted to your users. This is done via an AdTargetingOptions
		// parameter that's passed
		// to the loadAd call. See an example below:
		//
		// final AdTargetingOptions adOptions = new AdTargetingOptions();
		// adOptions.enableGeoLocation(true);
		// adOptions.setAge(25);
		// this.adView.loadAd(adOptions);
	}

	/**
	 * This class is for an event listener that tracks ad lifecycle events. It
	 * extends DefaultAdListener, so you can override only the methods that you
	 * need.
	 */
	class SampleAdListener extends DefaultAdListener {
		/**
		 * This event is called once an ad loads successfully.
		 */
		@Override
		public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
			Log.i(TAG, adProperties.getAdType().toString()
					+ " ad loaded successfully.");
		}

		/**
		 * This event is called if an ad fails to load.
		 */
		@Override
		public void onAdFailedToLoad(final Ad ad, final AdError error) {
			Log.w(TAG, "Ad failed to load. Code: " + error.getCode()
					+ ", Message: " + error.getMessage());
		}

		/**
		 * This event is called after a rich media ad expands.
		 */
		@Override
		public void onAdExpanded(final Ad ad) {
			Log.i(TAG, "Ad expanded.");
			// You may want to pause your activity here.
		}

		/**
		 * This event is called after a rich media ad has collapsed from an
		 * expanded state.
		 */
		@Override
		public void onAdCollapsed(final Ad ad) {
			Log.i(TAG, "Ad collapsed.");
			// Resume your activity here, if it was paused in onAdExpanded.
		}
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
