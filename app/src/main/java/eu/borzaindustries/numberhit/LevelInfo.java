package eu.borzaindustries.numberhit;

class LevelInfo {
	public int number;
	public double myTime;
	public double bestTime;
	public int position;
	public double averageTime;

	public LevelInfo() {
		position = -1;
	}

	public String getPosition() {
		if (position == -1)
			return "N/A";
		return EnterScoreResult.getOrdinal((position));
	}
	
	public String getBestTime() {
		if (bestTime == 0)
			return "N/A";
		return String.valueOf(bestTime);
	}
	
	public String getAvgTime() {
		if (averageTime == 0)
			return "N/A";
		return String.valueOf(averageTime);
	}

	public String getMyTime() {
		if (myTime == 0)
			return "N/A";
		return String.valueOf(myTime);
	}

	// gets number from 0 to 1000 about ratio of my time to best time
	public int getRatioTo1000() {
		if(myTime==0)
			return 0;
		return (int) ((bestTime / myTime) * 1000);
	}
}
