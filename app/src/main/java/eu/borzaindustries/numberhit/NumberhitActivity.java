package eu.borzaindustries.numberhit;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
//import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NumberhitActivity extends Activity {
	private Button timeButton;
	PriorityQueue q;
	int count;
	int w = 480;
	int h = 800;
	int currentNumber = 1;
	boolean runTimer = true;

	Handler handler;
	long startTime, now, time;

	Typeface tf_lcd;
	Typeface tf_common;
	private HitData hitData;
	
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Util.FullScreen(this);
		count = getIntent().getExtras().getInt(Util.LEVEL);
		setContentView(R.layout.main);
		ScoreManager sm = new ScoreManager(this);
		isSoundOn = sm.getSoundState();
		tf_lcd = Typeface.createFromAsset(getAssets(), "fonts/lcd.otf");
		tf_common = Typeface.createFromAsset(getAssets(),
				"fonts/shrewsbury.otf");
		hitData = new HitData();
		// get proper screen size
		q = new PriorityQueue();
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		w = display.getWidth();
		h = display.getHeight();

		// generate stuffs
		q.push(new Btn(0, 0, w, h));
		// do some steps
		for (int i = 0; i < count; i++)
			step();

		// now prepare strings into the rects
		LinkedList<Btn> copy = q.copyList();
		for (int i = 1; i <= count; i++) {
			int digits = i < 10 ? 1 : 2;
			// find item with largest font
			Btn largest = findWithLargestFontSize(copy, digits);
			copy.remove(largest);
			largest.fontSize = largest.getLargestFont(digits);
			largest.text = i + "";
			largest.number = i;
			//Log.d("LOL", i + ": font size=" + largest.fontSize);
		}

		// now do some stuff
		RelativeLayout ll = (RelativeLayout) findViewById(R.id.ll1);
		// ll.setOnTouchListener(mainTouchListener);
		LinkedList<Btn> anotherCopy = q.copyList();
		// for (int i = 0; i < anotherCopy.size(); i++) {
		for (Btn b : anotherCopy) {
			Button btn = new Button(this);
			boolean special = b.text == null;

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					b.w, b.h);
			lp.leftMargin = b.x;
			lp.topMargin = b.y;
			btn.setLayoutParams(lp);
			btn.setPadding(0, 0, 0, 0);
			ll.addView(btn);
			if (special) {
				btn.setBackgroundResource(R.drawable.time_btn_bg);
				btn.setTextColor(0xff000000);
				// 5 digits for 99.99
				btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, b.getLargestFont(5));
				btn.setText("0.00");
				timeButton = btn;
				btn.setTypeface(tf_lcd);			
			} else {
				btn.setBackgroundDrawable(getRandomDrawable());
				btn.setTextColor(0xffffffff);
				btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, b.fontSize);
				btn.setText(b.text);
				btn.setTypeface(tf_common);
				btn.setOnTouchListener(buttonTouchListener);
				// btn.setOnClickListener(gameButtonListener);
				btn.setTag(b.number);
			}
		}
		// start the "timer"
//		startTimer();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		startTimer();
	}

	private void startTimer() {
		handler = new Handler();
		startTime = System.currentTimeMillis();
		handler.post(updater);
	}

	private float lastX, lastY, lastSize;
	int lastTouchedNumber = -1;

	private OnTouchListener buttonTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int myNumber = ((Integer) v.getTag()).intValue();
			if (myNumber == lastTouchedNumber)
				return false;
			lastTouchedNumber = myNumber;
			if (myNumber == currentNumber) {
				v.setOnTouchListener(null);
				v.setVisibility(View.INVISIBLE);
				currentNumber++;
				lastX = event.getX();
				lastY = event.getY();
				lastSize = event.getSize();
				addHitData();
				checkForWin();
				// play sound
			    playSound();
			} else {
				// add penalty time
				startTime -= 250;
			}
			return false;
		}
	};

	MediaPlayer mp;
	private boolean isSoundOn;

	private void playSound() {
		if(!isSoundOn)
			return;
		try {
			if (mp == null)
				mp = MediaPlayer.create(NumberhitActivity.this, R.raw.pop);
			mp.start();
		} catch (Exception e) {
		}
	}

	protected void addHitData() {
		// lastX,lastY,lastSize,current time
		hitData.add(lastX, lastY, lastSize, System.currentTimeMillis());
	}

	private Drawable getRandomDrawable() {
		GradientDrawable gd = new GradientDrawable(Orientation.TL_BR,
				new int[] { getRandomStartColor(), getRandomEndColor() });
		gd.setCornerRadius(7);
		gd.setStroke(2, 0x900458A9);
		return gd;
	}

	private int getRandomStartColor() {
		// should be 0xffRRGGBB
		int r = Util.random.nextInt(20);
		int g = Util.random.nextInt(90) + 40;
		int b = Util.random.nextInt(180) + 60;
		return 0xff000000 + (r << 16) + (g << 8) + b;
	}

	private int getRandomEndColor() {
		int r = Util.random.nextInt(20);
		int g = Util.random.nextInt(55) + 40;
		int b = Util.random.nextInt(180) + 50;
		return 0xff000000 + (r << 16) + (g << 8) + b;
	}

	private DialogInterface submitDialog;

	protected void checkForWin() {
		if (currentNumber > count) {
			// check checksum
			// cancel stuff
			runTimer = false;
			AlertDialog.Builder b = new Builder(this);
			hitData.s = (int)time;
			hitData.l = count;
			//Log.d("TAG", "showing time:" + time + " as " + formatTime());
			b.setTitle("Level done!")
					.setMessage("It took you " + formatTime() + " seconds")
					.setPositiveButton("Submit",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									NumberhitActivity.this.submitDialog = dialog;
									// send the score somewhere
									PostScoreTask task = new PostScoreTask(
											NumberhitActivity.this);
									task.execute();
								}
							}).show();
		}
	}

	class PostScoreTask extends
			GenericSpinningWheelTask<Integer, Integer, Integer> {
		// int place;
		EnterScoreResult temp;

		public PostScoreTask(Context context) {
			super(context);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			temp = ScoreAdapter.enterScore(NumberhitActivity.this, (int) time,
					count,hitData.getStringStuff());
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			String msg = null;
			if (temp == null) {
				msg = "Cannot submit score, check your connection!";
			} else
				msg = temp.toString();
			Toast.makeText(NumberhitActivity.this, msg, Toast.LENGTH_SHORT)
					.show();
			submitDialog.dismiss();
			setResult(count);
			finish();
		}
	}

	String getOrdinal(int number) {
		if (number % 100 > 10 && number % 100 < 14)
			return number + "th";
		else if (number % 10 == 1)
			return number + "st";
		else if (number % 10 == 2)
			return number + "nd";
		else if (number % 10 == 3)
			return number + "rd";
		else
			return number + "th";
	}

	final Runnable updater = new Runnable() {
		@Override
		public void run() {
			if (!runTimer)
				return;
			now = System.currentTimeMillis();
			time = now - startTime;
			// format to string
			long s = time / 1000;
			long ms = (time % 1000) / 10;
			if (ms < 10)
				timeButton.setText(s + ".0" + ms);
			else
				timeButton.setText(s + "." + ms);
			if (runTimer)
				handler.postDelayed(this, 37);
		}
	};

	private String formatTime() {
		long s = time / 1000;
		long ms = (time % 1000) / 10;
		if (ms < 10)
			return s + ".0" + ms;
		else
			return s + "." + ms;
	}

	private Btn findWithLargestFontSize(LinkedList<Btn> list, int digits) {
		if (list.size() == 0)
			return null;
		Btn largest = list.peek();
		for (Btn b : list) {
			if (b.getLargestFont(digits) > largest.getLargestFont(digits))
				largest = b;
		}
		return largest;
	}

	private void step() {
		Btn largest = q.pop();
		Pair<Btn, Btn> p = largest.divide();
		q.push(p.first);
		q.push(p.second);
	}
}