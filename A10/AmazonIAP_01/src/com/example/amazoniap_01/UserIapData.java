package com.example.amazoniap_01;

public class UserIapData {
	private final String mAmazonUserId;
	private final String mAmazonMarketplace;
	
	public String getAmazonUserId() {
		return mAmazonUserId;
	}
	public String getAmazonMarketplace() {
		return mAmazonMarketplace;
	}
	
	public UserIapData(final String aAmazonUserId, final String aAmazonMarketplace) {
		mAmazonUserId = aAmazonUserId;
		mAmazonMarketplace = aAmazonMarketplace;
	}
}
