package com.zf.pulldown_listview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button btn1;
	private Button btn2;
	private Button btn3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		btn1 = (Button) findViewById(R.id.btn1);
		btn1.setOnClickListener(this);
		btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(this);
		btn3 = (Button) findViewById(R.id.btn3);
		btn3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			startActivity(new Intent(MainActivity.this, ImgArrowActivity.class));
			break;
		case R.id.btn2:
			startActivity(new Intent(MainActivity.this, ImgMatrixActivity.class));
			break;
		case R.id.btn3:
			startActivity(new Intent(MainActivity.this, ProgressChangeActivity.class));
			break;
		}
	}
}
