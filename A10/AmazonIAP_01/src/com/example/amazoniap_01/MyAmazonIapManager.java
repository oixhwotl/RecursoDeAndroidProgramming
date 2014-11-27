package com.example.amazoniap_01;

import java.util.Map;
import java.util.Set;

import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyAmazonIapManager {

	private static final String TAG = "MyAmazonIapManager";

	final private MainActivity mMainActivity;
	private boolean mIsLevel2ProductAvailable;
	private UserIapData mUserIapData;

	private SharedPreferences mPref;
	private SharedPreferences.Editor mPrefEditor;

	private EntitlementRecord mRecord;

	public MyAmazonIapManager(final MainActivity aMainActivity) {
		mMainActivity = aMainActivity;

		mPref = PreferenceManager.getDefaultSharedPreferences(mMainActivity);
		mPrefEditor = mPref.edit();

		mRecord = new EntitlementRecord();
	}

	public void updateAmazonUserData(final String aNewAmazonUserId,
			final String aNewAmazonMarketplace) {
		if (aNewAmazonUserId == null) {
			if (mUserIapData != null) {
				mUserIapData = null;
				refreshUI();
			}
		} else if ((mUserIapData == null)
				|| (!aNewAmazonUserId.equals(mUserIapData.getAmazonUserId()))) {
			mUserIapData = new UserIapData(aNewAmazonUserId,
					aNewAmazonMarketplace);
			refreshUI();
		}
	}

	public void enablePurchaseForSkus(final Map<String, Product> aProductData) {
		if (aProductData.containsKey(MySku.LEVEL2.getSku())) {
			mIsLevel2ProductAvailable = true;
		}
	}

	public void disablePurchaseForSkus(final Set<String> aUnavailableSkus) {
		if (aUnavailableSkus.contains(MySku.LEVEL2.toString())) {
			mIsLevel2ProductAvailable = false;
			mMainActivity.showMessage("The level2 isn't available now");
		}
	}

	public void handleReceipt(final String aRequestId, final Receipt aReceipt,
			final UserData aUserData) {
		switch (aReceipt.getProductType()) {
		case CONSUMABLE:
			break;
		case ENTITLED:
			handleEntitlementPurchase(aReceipt, aUserData);
			break;
		case SUBSCRIPTION:
			break;
		}
	}

	public void purchaseFailed(final String aSku) {
		mMainActivity.showMessage("Purchase failed!");
	}

	public UserIapData getUserIapData() {
		return mUserIapData;
	}

	public boolean getIsLevel2ProductAvailable() {
		return mIsLevel2ProductAvailable;
	}

	public void setIsLevel2ProductAvailable(
			final boolean aIsLevel2ProductAvailable) {
		mIsLevel2ProductAvailable = aIsLevel2ProductAvailable;
	}

	public void disableAllPurchases() {
		setIsLevel2ProductAvailable(false);
		refreshUI();
	}

	public void refreshUI() {
		boolean isLevel2Purchased = false;
		if (mUserIapData != null) {
			isLevel2Purchased = (mRecord.getCancelDate() == EntitlementRecord.DATE_NOT_SET);
		}
		mMainActivity.setLevel2Availability(mIsLevel2ProductAvailable,
				isLevel2Purchased);
	}

	private void grantEntitlementPurchase(final Receipt aReceipt,
			final UserData aUserData) {
		final MySku mySku = MySku.fromSku(aReceipt.getSku(),
				mUserIapData.getAmazonMarketplace());
		if (mySku != MySku.LEVEL2) {
			Log.v(TAG, "grantEntitlementPurchase() SKU not matched");
			PurchasingService.notifyFulfillment(aReceipt.getReceiptId(),
					FulfillmentResult.UNAVAILABLE);
			return;
		}
		try {
			saveEntitlementPurchase(aReceipt, aUserData.getUserId());
			PurchasingService.notifyFulfillment(aReceipt.getReceiptId(),
					FulfillmentResult.FULFILLED);
		} catch (final Throwable e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private boolean verifyReceiptFromYourService(final String aReceiptId,
			final UserData aUserData) {
		return true;
	}

	private void saveEntitlementPurchase(final Receipt aReceipt,
			final String aUserId) {
		final long purchaseDate = aReceipt.getPurchaseDate() != null ? aReceipt
				.getPurchaseDate().getTime() : EntitlementRecord.DATE_NOT_SET;

		final long cancelDate = aReceipt.isCanceled() ? aReceipt
				.getCancelDate().getTime() : EntitlementRecord.DATE_NOT_SET;
		mRecord.setPurchaseDate(purchaseDate);
		mRecord.setCancelDate(cancelDate);
	}

	private void handleEntitlementPurchase(final Receipt aReceipt,
			final UserData aUserData) {
		try {
			if (aReceipt.isCanceled()) {
				// Check whether this receipt is to revoke a entitlement
				// purchase
				revokeEntitlement(aReceipt, aUserData.getUserId());
			} else {
				// We strongly recommend that you verify the receipt
				// server-side.
				if (!verifyReceiptFromYourService(aReceipt.getReceiptId(),
						aUserData)) {
					// if the purchase cannot be verified,
					// show relevant error message to the customer.
					mMainActivity
							.showMessage("Purchase cannot be verified, please retry later.");
					return;
				}
				grantEntitlementPurchase(aReceipt, aUserData);
			}
			return;
		} catch (final Throwable e) {
			mMainActivity
					.showMessage("Purchase cannot be completed, please retry");
		}

	}

	private void revokeEntitlement(final Receipt aReceipt, final String aUserId) {
		String receiptId = aReceipt.getReceiptId();
		final EntitlementRecord record;
		if (receiptId == null) {
			record = mRecord;
			receiptId = record.getReceiptId();
		} else {
			if (mRecord.getReceiptId().equals(receiptId)) {
				record = mRecord;
			} else {
				record = null;
			}
		}
		if (record == null) {
			return;
		}
		if (record.getCancelDate() == EntitlementRecord.DATE_NOT_SET
				|| record.getCancelDate() > System.currentTimeMillis()) {
			final long cancelDate = aReceipt.getCancelDate() != null ? aReceipt
					.getCancelDate().getTime() : System.currentTimeMillis();
			mRecord.setCancelDate(cancelDate);
		} else {
			return;
		}

	}

	public class EntitlementRecord {
		public static final long DATE_NOT_SET = -1;

		public static final String KEY_RECEIPT_ID = "receiptId";
		public static final String KEY_USER_ID = "userId";
		public static final String KEY_SKU = "sku";
		public static final String KEY_PURCHASE_DATE = "purchaseDate";
		public static final String KEY_CANCEL_DATE = "cancelDate";

		public String getReceiptId() {
			return mPref.getString(KEY_RECEIPT_ID, KEY_RECEIPT_ID);
		}

		public void setReceiptId(String aReceiptId) {
			mPrefEditor.putString(KEY_RECEIPT_ID, aReceiptId);
		}

		public String getUserId() {
			return mPref.getString(KEY_USER_ID, KEY_USER_ID);
		}

		public void setUserId(String aUserId) {
			mPrefEditor.putString(KEY_USER_ID, aUserId);
		}

		public String getSku() {
			return mPref.getString(KEY_SKU, KEY_SKU);
		}

		public void setSku(String aSku) {
			mPrefEditor.putString(KEY_SKU, aSku);
		}

		public long getPurchaseDate() {
			return mPref.getLong(KEY_PURCHASE_DATE, DATE_NOT_SET);
		}

		public void setPurchaseDate(long aPurchaseDate) {
			mPrefEditor.putLong(KEY_PURCHASE_DATE, aPurchaseDate);
		}

		public long getCancelDate() {
			return mPref.getLong(KEY_CANCEL_DATE, DATE_NOT_SET);
		}

		public void setCancelDate(long aCancelDate) {
			mPrefEditor.putLong(KEY_CANCEL_DATE, aCancelDate);
		}
	}

	public class UserIapData {
		private final String mAmazonUserId;
		private final String mAmazonMarketplace;

		public String getAmazonUserId() {
			return mAmazonUserId;
		}

		public String getAmazonMarketplace() {
			return mAmazonMarketplace;
		}

		public UserIapData(final String aAmazonUserId,
				final String aAmazonMarketplace) {
			mAmazonUserId = aAmazonUserId;
			mAmazonMarketplace = aAmazonMarketplace;
		}
	}
}
