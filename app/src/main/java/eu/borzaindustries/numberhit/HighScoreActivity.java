package eu.borzaindustries.numberhit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HighScoreActivity extends ListActivity {
	Drawable progressDrawable;
	int LEVEL_COUNT = 64;
	Typeface tf_common;
	double max = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.FullScreen(this);
		setContentView(R.layout.high);
		makeAdapter();	
		progressDrawable = getResources().getDrawable(
				R.drawable.my_progress_bar);
		tf_common = Typeface.createFromAsset(getAssets(),
				"fonts/pressstart2p.ttf");
		HighScoreLoadTask task = new HighScoreLoadTask(this);
		task.execute();
	}

	LevelAdapter adapter;
	private ArrayList<LevelInfo> levels;

	private void makeAdapter() {
		levels = new ArrayList<LevelInfo>();
		for (int i = 1; i <= LEVEL_COUNT; i++) {
			LevelInfo inf = new LevelInfo();
			inf.number = i;
			levels.add(inf);
		}
		adapter = new LevelAdapter(this, R.layout.high_score_row, 0, levels);
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
			ProgressBar pb = (ProgressBar) view.findViewById(R.id.progress);
			pb.setMax(1000);
			double progress = item.bestTime / max;
			pb.setProgress((int) (progress * 1000));
			((TextView) view.findViewById(R.id.level)).setText(String
					.valueOf(item.number));
			((TextView) view.findViewById(R.id.level)).setTypeface(tf_common);
			((TextView) view.findViewById(R.id.best)).setTypeface(tf_common);
			((TextView) view.findViewById(R.id.best)).setText("best:"
					+ item.getBestTime());
			((TextView) view.findViewById(R.id.avg)).setTypeface(tf_common);
			((TextView) view.findViewById(R.id.avg)).setText("avg:"
					+ item.getAvgTime());
			return view;
		}
	}

	class HighScoreLoadTask extends GenericSpinningWheelTask<Integer,Integer,Integer>
	{
		Dialog dialog;

		public HighScoreLoadTask(Context context) {
			super(context);
		}

		@Override 
		protected Integer doInBackground(Integer... params) {
			String url = "http://borzaindustries.eu/scores/ws.php?action=json";
			String json = ScoreAdapter.downloadStringFromUrl(url);
			try {
				JSONArray ja = new JSONArray(json);
				for (int i = 0; i < ja.length(); i++) {
					JSONArray a = ja.getJSONArray(i);
					int level = a.getInt(0);
					LevelInfo inf = levels.get(level - 1);
					double best = a.getDouble(1);
					inf.bestTime = ((int) best) / 1000.0;
					double avg = a.getDouble(2);
					inf.averageTime = ((int) avg) / 1000.0;
					if (inf.bestTime > max)
						max = inf.bestTime;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			getListView().setAdapter(adapter);
		}
	}
}