package exam.camera;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class Review extends Activity {
	TextView txtReview;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review);

		txtReview = (TextView)findViewById(R.id.txtreview);
		Intent intent = getIntent();
		txtReview.setText(intent.getData().toString());
	}

	public void onNewIntent (Intent intent) {
		txtReview.setText(intent.getData().toString());
	}

	public void mOnClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btnshare:
			intent = new Intent(Review.this, Share.class);
			startActivity(intent);
			break;
		}
	}
}