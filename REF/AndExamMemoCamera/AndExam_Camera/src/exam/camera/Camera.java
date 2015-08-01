package exam.camera;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class Camera extends Activity {
	TextView txtStatus;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		txtStatus = (TextView)findViewById(R.id.txtstatus);
		Intent intent = getIntent();
		String caller = intent.getStringExtra("caller");
		if (caller == null) {
			txtStatus.setText("단독 실행중입니다.");
		} else {
			txtStatus.setText(caller + "에서 호출하였습니다.");
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }
    
	public void mOnClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btnreview:
			intent = new Intent(Camera.this, Review.class);
			intent.setData(Uri.parse("방금 찍은 사진"));
			startActivity(intent);
			break;
		case R.id.btnreviewprev:
			v.postDelayed(new Runnable() {
				public void run() {
					Intent intent = new Intent(Camera.this, Review.class);
					intent.setData(Uri.parse("전에 찍었던 사진"));
		    		//intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}, 5000);
			break;
		case R.id.btnshare:
			intent = new Intent(Camera.this, Share.class);
			startActivity(intent);
			break;
		case R.id.btnquit:
			finish();
			System.exit(0);
			break;
		}
	}

	public void onNewIntent (Intent intent) {
		Toast.makeText(this, "Camera receive new intent", 1).show();
	}    
}
