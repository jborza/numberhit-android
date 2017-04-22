package eu.borzaindustries.numberhit;

public class EnterScoreResult {
	public boolean better;
	public int oldPosition;
	public int newPosition;

	@Override
	public String toString() {
		String newOrdinal = getOrdinal(newPosition);
		if (oldPosition == newPosition) {
			if (better)
				return String
						.format("You improved your time, but still remain at %s place.",
								newOrdinal);
			else
				return String
						.format("You haven't improved, your position is %s",
								newOrdinal);
		} else {
			if (oldPosition == -1) // first placement
				return String.format("Your new position is %s", newOrdinal);
			else
				return String.format("Great! You moved from %s to %s place!",
						getOrdinal(oldPosition), newOrdinal);
		}
	}

	public static String getOrdinal(int number) {
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
}
