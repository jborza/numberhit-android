package eu.borzaindustries.numberhit;

import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	// show listview with level, score
	int LEVEL_COUNT = 64;

	ArrayList<LevelInfo> levels;
	LevelAdapter adapter;
	int lastLevel = -1;
	int REQUEST_PLAY = 0;
	Typeface tf_common;
	StartData data;
	static int index = 0;
	static int top;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.FullScreen(this);
		getListView().setPersistentDrawingCache(
				ViewGroup.PERSISTENT_SCROLLING_CACHE);
		getListView().setAlwaysDrawnWithCacheEnabled(true);
		setContentView(R.layout.menu);
		tf_common = Typeface.createFromAsset(getAssets(),
				"fonts/pressstart2p.ttf");
		makeAdapter();
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// save scroll pos
				index = getListView().getFirstVisiblePosition();
				View v = getListView().getChildAt(0);
				top = (v == null) ? 0 : v.getTop();
				// get shit from adapter on click
				LevelInfo info = adapter.getItem(position);
				Intent intent = new Intent(MainActivity.this,
						NumberhitActivity.class);
				intent.putExtra(Util.LEVEL, info.number);
				startActivityForResult(intent, REQUEST_PLAY);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PLAY)
			lastLevel = resultCode;
	}

	@Override
	protected void onResume() {
		super.onResume();
		reload();
	}

	void reload() {
		ReloadScoresTask task = new ReloadScoresTask(this);
		task.execute();
	}

	class ReloadScoresTask extends
			GenericSpinningWheelTask<Integer, Integer, Integer> {

		public ReloadScoresTask(Context context) {
			super(context);
			this.context = context;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			return initHighScores() ? RESULT_OK : RESULT_CANCELED;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			getListView().setAdapter(adapter);
			showTotal();
			//restore old position
			getListView().setSelectionFromTop(index, top);
			if (result == RESULT_CANCELED) {
				Toast.makeText(MainActivity.this, "Cannot get high scores!",
						Toast.LENGTH_LONG).show();
				((TextView) findViewById(R.id.log)).setText("YOU ARE OFFLINE!");
			}
		}
	}

	private void showTotal() {
		// set total
		if (data == null)
			return;
		String fmt = "Total place: %s Time: %s";
		((TextView) findViewById(R.id.log)).setText(String.format(fmt,
				EnterScoreResult.getOrdinal(data.myTotalPosition),
				formatTime(data.myTotalScore)));
	}

	private String formatTime(int millis) {
		int ms = millis % 1000;
		int temp = millis / 1000;
		int s = temp % 60;
		temp /= 60;
		int m = temp % 60;
		temp /= 60;
		int h = temp;
		if (h == 0)
			return String.format("%02d:%02d.%03d", m, s, ms);
		else
			return String.format("%d:%02d:%02d.%03d", h, m, s, ms);
	}

	private boolean initHighScores() {
		// fetch http://numscores.appspot.com/start?dev=DEVICEID
		data = ScoreAdapter.doStart(this);
		if (data == null) {
			return false;
		}
		ArrayList<ScoreInfo> scores = data.scores;
		for (ScoreInfo s : scores) {
			if (s.board == 0)
				continue; // skip special board
			int idx = s.board - 1;
			if (s.board > levels.size())
				continue;
			LevelInfo li = levels.get(idx);
			li.myTime = s.value / 1000.0; // ms to s
			li.position = s.position;
		}
		for (Integer board : data.bestScores.keySet()) {
			if (board == 0) // skip special board
				continue;
			int idx = board - 1;
			LevelInfo li = levels.get(idx);
			li.bestTime = data.bestScores.get(board) / 1000.0; // ms to s
		}
		return true;
	}

	private void makeAdapter() {
		levels = new ArrayList<LevelInfo>();
		for (int i = 1; i <= LEVEL_COUNT; i++) {
			LevelInfo inf = new LevelInfo();
			inf.number = i;
			levels.add(inf);
		}
		adapter = new LevelAdapter(this, R.layout.result_row, 0, levels);
	}

	class LevelAdapter extends ArrayAdapter<LevelInfo> {
		private Context context;
		private int resource;

		public LevelAdapter(Context context, int resource,
				int textViewResourceId, List<LevelInfo> objects) {
			super(context, resource, textViewResourceId, objects);
			this.context = context;
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LevelInfo item = getItem(position);
			View view;
			if (convertView == null)
				view = View.inflate(context, resource, null);
			else
				view = convertView;
			// todo highlight the last played row
			if (item.number == lastLevel)
				view.setBackgroundResource(R.drawable.level_bg_highlight);
			else
				view.setBackgroundResource(R.drawable.level_bg);
			TextView t1 = (TextView) view.findViewById(R.id.level);
			t1.setTypeface(tf_common);
			TextView tvMy = ((TextView) view.findViewById(R.id.my));
			tvMy.setTypeface(tf_common);
			TextView tvPos = ((TextView) view.findViewById(R.id.position));
			tvPos.setTypeface(tf_common);
			TextView tvBest = ((TextView) view.findViewById(R.id.best));
			tvBest.setTypeface(tf_common);
			((TextView) view.findViewById(R.id.my_label))
					.setTypeface(tf_common);
			((TextView) view.findViewById(R.id.best_label))
					.setTypeface(tf_common);
			((TextView) view.findViewById(R.id.position_label))
					.setTypeface(tf_common);
			t1.setText(item.number + "");
			tvMy.setText(String.valueOf(item.getMyTime()));
			tvPos.setText(String.valueOf(item.getPosition()));
			tvBest.setText(String.valueOf(item.bestTime));
			((ProgressBar) view.findViewById(R.id.progress)).setProgress(item
					.getRatioTo1000());
			return view;
		}
	}
}