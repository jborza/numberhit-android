package eu.borzaindustries.numberhit;

import android.util.Pair;

@SuppressWarnings("rawtypes")
public class Btn implements Comparable {
	public int x = 0;
	public int y = 0;
	public int w = 0;
	public int h = 0;
	public String text;
	public int fontSize;
	public int number;
	// consts
	static double maxSplit = 0.63;
	static double minSplit = 0.37;
	static double aspect_min = 0.9;
	static double aspect_max = 1.6;
	static int margin = 4;
	double fontAspectRatio = 0.7;
	static double fontRatio = 0.7;

	public Btn(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public double area() {
		return w * h;
	}

	static double getSplitRatio() {
		return Util.random.nextDouble() * (maxSplit - minSplit) + minSplit;
	}

	double getAspectRatio() {
		return (double) w / (double) h;
	}

	int getLargestFont(int digits) {
		double min = Math.min(h - margin * 2, (w - margin * 2)
				/ (fontAspectRatio * digits));
		return (int) (min * fontRatio);
	}

	// check whether we want to divide horizontally (false if vertically)
	boolean shouldDivideHorizontally() {
		// if aspect > aspect_max always divide horizontally
		// if(aspect < aspect_min always divide vertically
		// else decide at random
		if (getAspectRatio() > aspect_max)
			return true;
		else if (getAspectRatio() < aspect_min)
			return false;
		else
			return Util.random.nextBoolean();
	}

	public Pair<Btn, Btn> divide() {
		Btn r1, r2;
		if (shouldDivideHorizontally()) {
			int splitw = (int) (w * getSplitRatio());
			r1 = new Btn(x, y, splitw, h);
			r2 = new Btn(x + splitw, y, w - splitw, h);
		} else {// vert.
			int splith = (int) (h * getSplitRatio());
			r1 = new Btn(x, y, w, splith);
			r2 = new Btn(x, y + splith, w, h - splith);
		}
		return new Pair<Btn, Btn>(r1, r2);
	}

	@Override
	public int compareTo(Object another) {
		Btn anotherBtn = (Btn) another;
		if (area() == anotherBtn.area())
			return 0;
		else if (area() > anotherBtn.area())
			return 1;
		else
			return -1;
	}

}