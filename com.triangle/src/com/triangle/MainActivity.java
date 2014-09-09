package com.triangle;

import android.os.Bundle;
import android.app.Activity;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	LinearLayout main_linearLayout, main_linearLayout_button;
	private float[] humidity = new float[6];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		main_linearLayout = (LinearLayout) findViewById(R.id.main_linearLayout);
		main_linearLayout.addView(new TriangleView(this, 1, 95));

		main_linearLayout_button = (LinearLayout) findViewById(R.id.main_linearLayout_button);
		humidity[0] = 0.0f;
		humidity[1] = 100f;
		humidity[2] = 50f;
		humidity[3] = 60f;
		humidity[4] = 70f;
		humidity[5] = 80f;
		main_linearLayout_button.addView(new RotaryView(this, humidity));
	}
}
