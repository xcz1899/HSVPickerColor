
package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.test.HSVColorPicker.OnColorListener;

public class MainActivity extends Activity implements OnColorListener {
	HSVColorPicker mHSVColorPicker;
	public final static String TAG = "color";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHSVColorPicker = (HSVColorPicker) findViewById(R.id.hSVColorPicker);
		mHSVColorPicker.setOnColorListener(this);
		mHSVColorPicker.setmHue(200f);
		mHSVColorPicker.setmSaturation(0.5f);
		mHSVColorPicker.setmValue(0.5f);
	}

	@Override
	public void onChangeHS(float h, float s) {
		Log.i(TAG, "h="+h+"/s="+s);
	}

	@Override
	public void onStartHS(float h, float s) {
		Log.i(TAG, "h="+h+"/s="+s);

	}

	@Override
	public void onStartV(float v) {
		Log.i(TAG, "v="+v);

	}

	@Override
	public void onStopHS(float h, float s) {
		Log.i(TAG, "h="+h+"/s="+s);

	}

	@Override
	public void onStopV(float v) {
		Log.i(TAG, "v="+v);

	}

	@Override
	public void onChangeV(float v) {
		Log.i(TAG, "v="+v);

	}
}
