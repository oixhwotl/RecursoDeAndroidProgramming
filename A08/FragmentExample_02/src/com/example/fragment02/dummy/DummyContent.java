package com.example.fragment02.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent
{
	
	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
	
	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP =
		new HashMap<String, DummyItem>();

	private static final String mTitle[]={
		"Donut","Eclair",
		"Froyo", "Ginger Bread",
		"HoneyComb","IceCream Sandwich",
		"Jelly Bean","Kitkat",
		"Lollipop"};
	private static final String mImages[]={
		"android16donut","android20eclair",
		"android22froyo","android23gingerbread",
		"android30honeycomb","android40icreamsandwitch",
		"android41jellybean","android44kitkat",
		"android50lollipop"};
	
	static
	{
		int i, sizeOfArray = mTitle.length;
		for(i=0; i < sizeOfArray;i++){
			DummyItem dItem = new DummyItem(mTitle[i], mTitle[i], mImages[i]);
			addItem(dItem);
		}
	}
	
	private static void addItem (DummyItem item)
	{
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}
	
	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem
	{
		public String id;
		public String content;
		public String image;
		
		public DummyItem (String id, String content, String image)
		{
			this.id = id;
			this.content = content;
			this.image = image;
		}
		
		@Override
		public String toString ()
		{
			return content;
		}
	}
}
