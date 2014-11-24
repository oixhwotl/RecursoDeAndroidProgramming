package com.example.amazoniap_01;

public enum MySku {
	LEVEL2("com.amazon.sample.iap.entitlement.level2", "US");

	private final String mSku;
	private final String mAvailableMarketplace;

	public static MySku fromSku(final String aSku, final String aMarketPlace) {
		if (LEVEL2.getSku().equals(aSku)
				&& (aMarketPlace == null || LEVEL2.getAvailableMarketplace()
						.equalsIgnoreCase(aMarketPlace))) {
			return LEVEL2;
		}
		return null;
	}

	public String getSku() {
		return mSku;
	}

	public String getAvailableMarketplace() {
		return mAvailableMarketplace;
	}

	private MySku(final String aSku, final String aAvailableMarketplace) {
		this.mSku = aSku;
		this.mAvailableMarketplace = aAvailableMarketplace;
	}
}
