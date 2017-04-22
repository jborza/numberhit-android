package eu.borzaindustries.numberhit;

import java.util.ArrayList;
import java.util.HashMap;

public class StartData{
	public int bestTotalScore;
	public int myTotalScore;
	public int myTotalPosition;
	public ArrayList<ScoreInfo> scores;
	public HashMap<Integer, Integer> bestScores; //key: board, value:score
	public StartData(){
		bestScores = new HashMap<Integer, Integer>();
		scores = new ArrayList<ScoreInfo>();
	}
}