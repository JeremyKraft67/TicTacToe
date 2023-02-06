/**
 * Check if a player has won. <br>
 * A player can win by rowing three of his markings along:<br>
 * - a line <br>
 * - a row <br>
 * - a diagonal <br>
 * 
 * @author jeremy
 * @see BoutMorpion
 */
public class BoardCheck{

	private BoutMorpion[][] butBoard = new BoutMorpion[3][3];
	private boolean win;

	/**
	 * Copy the GUI to make the operations
	 * 
	 * @param gui The original GUI
	 * @param i the line
	 * @param j the column
	 * @see GUI
	 */
	public BoardCheck(GUI gui, int i, int j) {
		this.butBoard = gui.getButBoard();
		this.win = checkAll(i, j);
	}

	/**
	 * Return the result of the check
	 * 
	 * @return True if wins, else false
	 */
	public boolean getWin() {
		return this.win;
	}

	/**
	 * Check if the Player wins
	 * 
	 * @param i
	 *            the line number
	 * @param j
	 *            the row number
	 * @return true if wins, else false
	 */
	private boolean checkAll(int i, int j) {
		if (checkHorizontalLine(i) || checkVerticalLine(j))
			return true;
		else if (checkDiagonals())
			return true;
		else
			return false;
	}

	/**
	 * Check if the player wins with the Vertical line. <br>
	 * All cases must be clicked and all cases must belong to either player.
	 * 
	 * @param j
	 *            the line
	 * @return true if wins, else false
	 */
	private boolean checkVerticalLine(Integer j) {
		if (((butBoard[0][j].getClicked() && butBoard[1][j].getClicked() && butBoard[2][j].getClicked())
				&& ((butBoard[0][j].getPlayer() && butBoard[1][j].getPlayer() && butBoard[2][j].getPlayer())
						|| (!butBoard[0][j].getPlayer() && !butBoard[1][j].getPlayer()
								&& !butBoard[2][j].getPlayer()))))
			return true;
		else
			return false;
	}

	/**
	 * Check if the player wins with the Horizontal line. <br>
	 * All cases must be clicked and all cases must belong to either player.
	 * 
	 * @param i the line
	 * @return true if wins, else false
	 */
	private boolean checkHorizontalLine(Integer i) {
				if ((butBoard[i][0].getClicked() && butBoard[i][1].getClicked() && butBoard[i][2].getClicked())
				&& ((butBoard[i][0].getPlayer() && butBoard[i][1].getPlayer() && butBoard[i][2].getPlayer())
						|| (!butBoard[i][0].getPlayer() && !butBoard[i][1].getPlayer() && !butBoard[i][2].getPlayer())))
			return true;
		else
			return false;
	}

	/**
	 * Check if the player wins with the Diagonals line. <br>
	 * All cases must be clicked and all cases must belong to either player.
	 * @return true if win, false otherwise
	 */
	private boolean checkDiagonals() {
		// first diagonal
		if ((butBoard[0][0].getClicked() && butBoard[1][1].getClicked() && butBoard[2][2].getClicked())
				&& ((butBoard[0][0].getPlayer() && butBoard[1][1].getPlayer() && butBoard[2][2].getPlayer())
						|| (!butBoard[0][0].getPlayer() && !butBoard[1][1].getPlayer() && !butBoard[2][2].getPlayer())))
			return true;
		// second diagonal
		else if ((butBoard[0][2].getClicked() && butBoard[1][1].getClicked() && butBoard[2][0].getClicked())
				&& ((butBoard[0][2].getPlayer() && butBoard[1][1].getPlayer() && butBoard[2][0].getPlayer())
						|| (!butBoard[0][2].getPlayer() && !butBoard[1][1].getPlayer() && !butBoard[2][0].getPlayer())))

			return true;
		else
			return false;
	}

}
