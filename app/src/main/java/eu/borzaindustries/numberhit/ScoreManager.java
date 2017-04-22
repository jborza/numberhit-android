package eu.borzaindustries.numberhit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ScoreManager {
	public static final String SETTINGS = "SETTINGS";
	public static final String SOUND = "SOUND";
	private Context context;

	public ScoreManager(Context context) {
		this.context = context;
	}

	public boolean getSoundState() {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		return prefs.getBoolean(SOUND, true);
	}

	public void setSoundState(boolean sound) {
		Editor editor = context.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE).edit();
		editor.putBoolean(SOUND, sound);
		editor.commit();
	}
}