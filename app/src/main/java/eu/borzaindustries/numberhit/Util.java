package eu.borzaindustries.numberhit;

import java.util.Random;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Window;
import android.view.WindowManager;

public class Util {
	static Random random = new Random();
	protected static String LEVEL = "LEVEL";
	public static String ADDON = "";
	
	public static void FullScreen(Activity act){
		act.getWindow().setFormat(PixelFormat.RGBA_8888);
		act.requestWindowFeature(Window.FEATURE_NO_TITLE);
		act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
