package com.example.amazoniap_01;

import java.util.HashSet;
import java.util.Set;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private MyAmazonIapManager mMyAmazonIapManager;

	private Button mButton;
	private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			final RequestId requestId = PurchasingService.purchase(MySku.LEVEL2
					.getSku());
			Log.v(TAG, "OnClickListener: requestId:" + requestId);
		}
	};
	private TextView mTextView;

	private Handler mGuiThreadHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButton = (Button) findViewById(R.id.button_buylevel2);
		mButton.setOnClickListener(mButtonClickListener);
		mButton.setEnabled(false);
		mTextView = (TextView) findViewById(R.id.textview_level2enabled);
		mTextView.setTextColor(Color.GRAY);
		mTextView.setBackgroundColor(Color.WHITE);

		mGuiThreadHandler = new Handler();

		setupIapOnCreate();

	}

	private void enableLevel2InView() {
		mGuiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(R.string.level2_enabled);
				mTextView.setTextColor(Color.BLUE);
				mTextView.setBackgroundColor(Color.YELLOW);
			}
		});
	}

	private void disableLevel2InView() {
		mGuiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(R.string.level2_disabled);
				mTextView.setTextColor(Color.GRAY);
				mTextView.setBackgroundColor(Color.WHITE);
			}
		});
	}

	public void showMessage(final String aMessage) {
		Toast.makeText(MainActivity.this, aMessage, Toast.LENGTH_LONG).show();
	}

	public void setLevel2Availability(final boolean aProductAvailable,
			final boolean aUserAlreadyPurchased) {
		if (aProductAvailable) {
			if (aUserAlreadyPurchased) {
				enableLevel2InView();
				mButton.setEnabled(false);
			} else {
				disableLevel2InView();
				mButton.setEnabled(true);
			}
		} else {
			disableLevel2InView();
			mButton.setEnabled(false);
		}
	}

	private void setupIapOnCreate() {
		mMyAmazonIapManager = new MyAmazonIapManager(this);
		final MyAmazonPurchasingListener myAmazonPurchasingListener = new MyAmazonPurchasingListener(
				mMyAmazonIapManager);
		Log.v(TAG, "setupIapOnCreate()");
		PurchasingService.registerListener(this.getApplicationContext(),
				myAmazonPurchasingListener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.v(TAG, "onStart()");
		final Set<String> productSkus = new HashSet<String>();
		for (final MySku mySku : MySku.values()) {
			productSkus.add(mySku.getSku());
		}
		PurchasingService.getProductData(productSkus);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume: call getUserData");
		PurchasingService.getUserData();

		Log.d(TAG, "onResume: getPurchaseUpdates");
		PurchasingService.getPurchaseUpdates(false);
	}

	class MyAmazonPurchasingListener implements PurchasingListener {
		private static final String TAG = "MyAmazonPurchasingListener";
		private final MyAmazonIapManager mMyAmazonIapManager2;

		public MyAmazonPurchasingListener(
				final MyAmazonIapManager aMyAmazonIapManager) {
			this.mMyAmazonIapManager2 = aMyAmazonIapManager;
		}

		@Override
		public void onProductDataResponse(ProductDataResponse arg0) {
			final ProductDataResponse.RequestStatus status = arg0
					.getRequestStatus();
			Log.v(TAG, "onProductDataResponse() status:" + status);

			switch (status) {
			case SUCCESSFUL:
				final Set<String> unavailableSkus = arg0.getUnavailableSkus();
				Log.v(TAG,
						"... SUCCESSFUL unavailableSku:"
								+ unavailableSkus.size());
				mMyAmazonIapManager2.enablePurchaseForSkus(arg0
						.getProductData());
				mMyAmazonIapManager2.disablePurchaseForSkus(arg0
						.getUnavailableSkus());
				mMyAmazonIapManager2.refreshUI();
				break;
			case FAILED:
			case NOT_SUPPORTED:
				Log.v(TAG, "... FAILED/NOT_SUPPORTED");
				mMyAmazonIapManager2.disableAllPurchases();
				break;
			}

		}

		@Override
		public void onPurchaseResponse(PurchaseResponse arg0) {
			final String requestId = arg0.getRequestId().toString();
			final String userId = arg0.getUserData().getUserId();
			final PurchaseResponse.RequestStatus status = arg0
					.getRequestStatus();

			Log.v(TAG, "onPurchaseResonse() requestId:" + requestId
					+ " userId:" + userId + " purchaseRequestSTatus:" + status);

			switch (status) {
			case SUCCESSFUL:
				final Receipt receipt = arg0.getReceipt();
				mMyAmazonIapManager2.updateAmazonUserData(arg0.getUserData()
						.getUserId(), arg0.getUserData().getMarketplace());
				Log.v(TAG, "... SUCCESSFUL");
				mMyAmazonIapManager2.handleReceipt(arg0.getRequestId()
						.toString(), receipt, arg0.getUserData());
				mMyAmazonIapManager2.refreshUI();
				break;

			case ALREADY_PURCHASED:
				Log.v(TAG, "... ALREADY_PURCHASED");
				break;

			case INVALID_SKU:
				Log.v(TAG, "... INVALID_SKU");
				final Set<String> unavailableSkus = new HashSet<String>();
				unavailableSkus.add(arg0.getReceipt().getSku());
				mMyAmazonIapManager2.disablePurchaseForSkus(unavailableSkus);
				break;

			case FAILED:
			case NOT_SUPPORTED:
				Log.v(TAG, "... FAILED/NOT_SUPPORTED");
				mMyAmazonIapManager2.purchaseFailed(arg0.getReceipt().getSku());
				break;
			}

		}

		@Override
		public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse arg0) {
			Log.v(TAG,
					"onPurchaseUpdateResponse() requestId:"
							+ arg0.getRequestId());
			final PurchaseUpdatesResponse.RequestStatus status = arg0
					.getRequestStatus();

			switch (status) {
			case SUCCESSFUL:
				Log.v(TAG, "... SUCCESSFUL");
				mMyAmazonIapManager2.updateAmazonUserData(arg0.getUserData()
						.getUserId(), arg0.getUserData().getMarketplace());
				for (final Receipt receipt : arg0.getReceipts()) {
					mMyAmazonIapManager2.handleReceipt(arg0.getRequestId()
							.toString(), receipt, arg0.getUserData());
				}
				if (arg0.hasMore()) {
					PurchasingService.getPurchaseUpdates(false);
				}
				mMyAmazonIapManager2.refreshUI();
				break;
			case FAILED:
			case NOT_SUPPORTED:
				Log.v(TAG, "... FAILED/NOT_SUPPORTED");
				mMyAmazonIapManager2.disableAllPurchases();
				break;
			}

		}

		@Override
		public void onUserDataResponse(UserDataResponse arg0) {
			Log.v(TAG, "onUserDataResonse() requestId:" + arg0.getRequestId()
					+ " requestStatus:" + arg0.getRequestStatus());

			final UserDataResponse.RequestStatus status = arg0
					.getRequestStatus();
			switch (status) {
			case SUCCESSFUL:
				Log.v(TAG, "... SUCCESSFUL userId:"
						+ arg0.getUserData().getUserId() + " marketplace:"
						+ arg0.getUserData().getMarketplace());
				mMyAmazonIapManager2.updateAmazonUserData(arg0.getUserData()
						.getUserId(), arg0.getUserData().getMarketplace());
				;
				break;
			case FAILED:
			case NOT_SUPPORTED:
				Log.v(TAG, "... FAILED/NOT_SUPPORTED status:" + status);
				mMyAmazonIapManager2.updateAmazonUserData(null, null);
				break;
			}
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