package eu.borzaindustries.numberhit;

import java.util.ArrayList;
import java.util.Random;

public class HitData {
	long startTime;
	public int s = 0;
	public int l = 0;
	ArrayList<OneHit> hits;

	public HitData() {
		hits = new ArrayList<HitData.OneHit>();
	}

	public void add(float x, float y, float size, long t) {
		if (startTime == 0)
			startTime = t;
		long elapsed = t - startTime;
		OneHit h = new OneHit();
		h.x = (int) x;
		h.y = (int) y;
		h.c = (int) elapsed;
		hits.add(h);
	}

	public int checksum() {
		int sum = s;
		s ^= l;
		// now the right checksum
		for (int i = hits.size() - 1; i >= 0; i--) {
			OneHit h = hits.get(i);
			int old = sum;
			if (i % 2 == 0)
				sum -= (h.x * h.y * 4);
			else
				sum += (h.x * h.y * 7);
			sum += (int) h.c;
			sum ^= old;
			sum += (int) Math.sqrt(sum);
			sum += 359;
		}
		return sum;
	}

	//checksum
	private int getStuff() {
		// calculate bogus number
		Random rnd = new Random();
		int bogus = rnd.nextInt(100000);
		bogus *= 10;
		// xor level and start time
		int st = (int) Math.sqrt(startTime);
		int temp = (int) st ^ l * 17;
		temp ^= s + 51;
		bogus += temp % 10;
		return bogus;
	}

	//serialize
	public String getStringStuff() {
		Random rnd = new Random();
		char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f' };
		StringBuilder sb = new StringBuilder();
		sb.append(getStuff());
		sb.append(chars[rnd.nextInt(chars.length)]);
		int st = (int) Math.sqrt(startTime);
		sb.append(st);
		sb.append(chars[rnd.nextInt(chars.length)]);
		return sb.toString();
	}
	class OneHit {
		public int x;
		public int y;
		public int c; // time
	}
}
