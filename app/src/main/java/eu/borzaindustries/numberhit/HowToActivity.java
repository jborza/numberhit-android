package eu.borzaindustries.numberhit;

import android.app.Activity;
import android.os.Bundle;

public class HowToActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.FullScreen(this);
		setContentView(R.layout.howto);
	}
}
