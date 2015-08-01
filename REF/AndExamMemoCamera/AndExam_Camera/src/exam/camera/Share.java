package exam.camera;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;

public class Share extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
	}

	public void mOnClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btnreview:
			intent = new Intent(Share.this, Review.class);
			intent.setData(Uri.parse("방금 찍은 사진"));
			startActivity(intent);
			break;
		}
	}
}