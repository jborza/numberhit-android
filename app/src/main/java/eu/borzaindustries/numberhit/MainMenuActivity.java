package eu.borzaindustries.numberhit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MainMenuActivity extends Activity {
	CheckBox soundBox;
	ScoreManager sm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.FullScreen(this);
		setContentView(R.layout.main_menu);
		// typeface
		setTypeface();
		findViewById(R.id.play).setOnClickListener(listener);
		findViewById(R.id.high_scores).setOnClickListener(listener);
		findViewById(R.id.how_to).setOnClickListener(listener);
		findViewById(R.id.feedback).setOnClickListener(listener);
		sm = new ScoreManager(this);
		soundBox  = (CheckBox)findViewById(R.id.sound);
		soundBox.setChecked(sm.getSoundState());
		soundBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sm.setSoundState(isChecked);
			}
		});
	}

	private void startAct(Class<?> c) {
		Intent intent = new Intent(getBaseContext(), c);
		startActivity(intent);
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.feedback:
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "borzaindustries@gmail.com" });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Feedback for " + getString(R.string.app_name));
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				break;
			case R.id.how_to:
				startAct(HowToActivity.class);
				break;
			case R.id.high_scores:
				startHighScores();
				break;
			case R.id.play:
				startAct(MainActivity.class);
				break;
			}
		}
	};

	private void startHighScores() {
		 startAct(HighScoreActivity.class);
	}

	private void setTypeface() {
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/pressstart2p.ttf");
		((TextView) findViewById(R.id.title)).setTypeface(tf);
		((TextView) findViewById(R.id.play)).setTypeface(tf);
		((TextView) findViewById(R.id.high_scores)).setTypeface(tf);
		((TextView) findViewById(R.id.how_to)).setTypeface(tf);
		((TextView) findViewById(R.id.feedback)).setTypeface(tf);
		((CheckBox) findViewById(R.id.sound)).setTypeface(tf);
	}
}
