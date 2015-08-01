package exam.memo;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

public class Memo extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.memo, menu);
		return true;
	}

	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.btnedit:
			startActivity(new Intent(Memo.this, Edit.class));
			break;
		case R.id.btnsetting:
			startActivity(new Intent(Memo.this, Setting.class));
			break;
		case R.id.btnquit:
			finish();
			System.exit(0);
			break;
		}
	}
}
