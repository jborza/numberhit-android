package eu.borzaindustries.numberhit;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class GenericSpinningWheelTask<Params,Progress,Result> extends AsyncTask<Params, Progress, Result> {
	Dialog dialog;
	Context context;

	public GenericSpinningWheelTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		dialog = new Dialog(context,
				android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_loading);
		ImageView iv_spinner = (ImageView) dialog
				.findViewById(R.id.spinner);
		RotateAnimation anim = new RotateAnimation(0f, 350f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(700);
		iv_spinner.startAnimation(anim);
		dialog.show();
	}

	@Override
	protected Result doInBackground(Params... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Result result) {
		dialog.dismiss();
	}
}