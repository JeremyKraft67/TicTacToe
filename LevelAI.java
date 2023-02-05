
/**
 * Define IA difficulty.
 * Enable a randomized difficulty.
 * @author jeremy
 *
 */

public enum LevelAI {
	FACILE(1), MOYEN(2), DIFFICILE(3), EXTREME(8);

	private int difficulte;

	LevelAI(int difficulte) {
		this.difficulte = difficulte;
	}

	public int getDifficulte() {
		return difficulte;
	}

	/**
	 * Get a random mix of difficulty.
	 * Create artificial mistake for IA. 
	 * 
	 * @return randomized difficulty <br>
	 * 
	 * <br> Facile: <br>
	 * 50% Facile <br>
	 * 50% Moyen <br>
	 * 
	 * <br> Moyen: <br>
	 * 25% Facile <br>
	 * 50% Moyen <br>
	 * 25% Difficile <br>
	 *
	 *<br> Difficile: <br>
	 * 50% Moyen <br>
	 * 40% Difficile <br>
	 * 10% Extreme <br> 
	 * 
	 * <br> Extreme: <br>
	 * 10% Moyen <br>
	 * 50% Difficile <br>
	 * 40% Extreme <br> 
	 */
	public int getRandomizedDifficulty() {
		double rd = Math.random();
		int randDiff = 0;

		switch (difficulte) {
		
		case 1: // facile
			if(rd <= 0.5)
				randDiff = 1;
			else if(rd > 0.5)
				randDiff = 2;
			break;
		case 2: // moyen
			if(rd <= 0.25)
				randDiff = 1;
			else if (rd > 0.25 && rd <= 0.75)
				randDiff = 2;
			else
				randDiff = 3;
			break;
		case 3: // difficile
			if(rd <= 0.50)
				randDiff = 2;
			else if(rd > 0.50 && rd <= 0.9)
				randDiff = 3;
			else 
				randDiff = 8;
			break;
		case 8: // extreme
			if(rd <= 0.10)
				randDiff = 2;
			else if(rd > 0.10 && rd <= 0.60)
				randDiff = 3;
			else 
				randDiff = 8;
			break;
		}
		return randDiff;
	}

}