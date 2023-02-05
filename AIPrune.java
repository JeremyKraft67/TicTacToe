
import java.util.ArrayList;
import java.util.List;

/**
 * Operate an AI for computer. <br>
 * Algorithm used is Prune Minimax.
 * 
 * @author jeremy
 * @see BoutMorpion
 */
public class AIPrune {
	final int val1 = 1; // if only 1 consecutive player's case
	final int val2 = 10; // if 2 consecutive player's case
	final int val3 = 10000; // if 3 consecutive player's case, player wins
	final int VALMAX = 100 * val3;
	private int DEPTH_MAX; // maximum number of turns
	private BoutMorpion[][] butBoard = new BoutMorpion[3][3];
	private boolean currentPlayer;
	static int numRun; // number of alphabeta run
	private int timerCount;
	private int[] bestMove = new int[2]; // best possible move
	// on cree un plateau simule car l'interface gere mal les clones du plateau
	private boolean[][] boolBoard = new boolean[3][3]; // boutons deja visites
	private boolean[][] boolPlayerBoard = new boolean[3][3]; // stocke les

	/**
	 * Copy the current test of the Game
	 * 
	 * @param gui
	 *            GUI to copy
	 */
	public AIPrune(GUI gui) {
		this.butBoard = gui.getButBoard();
		this.DEPTH_MAX = gui.getLevelAI();
		
	}

	/**
	 * Lance l'evaluation
	 * 
	 * @return (x, y): coordonnees du mouvement optimal
	 */
	public int[] initAlphabeta(int compt) {
		int buff = 0;
		long begin, finish;
		
		System.out.println("Niveau AI " + DEPTH_MAX);
		// initialize best move
		bestMove[0] = this.findPossibility().get(0)[0];
		bestMove[1] = this.findPossibility().get(0)[1];
		begin = System.nanoTime();
		buff = alphabeta(DEPTH_MAX, -VALMAX, VALMAX, true); // AI is
		// player
		// 2
		finish = System.nanoTime();
		System.out.println(numRun + " runs " + "d' Alphabeta, temps: " + Math.round((finish - begin)/1000) + " μs");
		System.out.println("Best move: (" + bestMove[0] + ", " + bestMove[1] + "); " + "Score: " + buff);

		return this.bestMove;
	}

	/**
	 * Algorithm Pruning Minimax. <br>
	 * La fonction enregistre le mouvement optimale 'bestMove', et retourne son
	 * gain. <br>
	 * La fonction utilise deux booleens pour simuler les coups suivant sur le
	 * plateau. Ceci est necessaire car utiliser des clones du plateau reel
	 * genere des bugs.
	 * 
	 * @param board
	 *            Board tel que present dans l'interface graphique
	 * @param depth
	 *            nombre de coups a calculer a l'avance
	 * @param alpha
	 *            sert de cut-off bas (pour la maximisation)
	 * @param beta
	 *            sert de cut-off haut (pour la minimisation)
	 * @param maximize
	 *            joueur qui commence
	 * @return valeur maximale de gain
	 */
	private int alphabeta(int depth, int alpha, int beta, boolean maximize) {
		int buffAlpha;
		numRun++;

		if (depth == 0 || this.findPossibility().isEmpty()) {
			return evaluateBoard(); // current evaluation of the board
		}

		// maximize player
		else if (maximize) {
			for (int[] coord : this.findPossibility()) {

				this.currentPlayer = false; // player 2
				// declare the case selected for current player
				boolBoard[coord[0]][coord[1]] = true;
				boolPlayerBoard[coord[0]][coord[1]] = this.currentPlayer;

				// check if player 2 wins immediately
				if (evaluateBoard() >= 0.8 * val3) { // player 2 wins
					if (depth == DEPTH_MAX) { // only for immediate win
						bestMove[0] = coord[0];
						bestMove[1] = coord[1];
					}
					// unselect the case
					boolBoard[coord[0]][coord[1]] = false;
					boolPlayerBoard[coord[0]][coord[1]] = false;
					// alpha = VALMAX * (1 - (5 / 100) * (DEPTH_MAX - depth));
					// // diminution
					alpha = VALMAX; // de
					// 5% a chaque
					// tour
					break;
				}

				// keep in memory the best possible move
				buffAlpha = alphabeta(depth - 1, alpha, beta, !maximize);
				if ((alpha < buffAlpha) && (depth == DEPTH_MAX)) {
					bestMove[0] = coord[0];
					bestMove[1] = coord[1];
				}

				// Evaluation du noeud
				alpha = Math.max(alpha, buffAlpha);
				// unselect the case
				boolBoard[coord[0]][coord[1]] = false;
				boolPlayerBoard[coord[0]][coord[1]] = false;

				if (beta < alpha) {
					break; // cut-off
				}

			}

			return alpha;

			// minimize player
		} else {
			for (int[] coord : this.findPossibility()) {
				this.currentPlayer = true; // player 1
				// declare the case selected for current player
				boolBoard[coord[0]][coord[1]] = true;
				boolPlayerBoard[coord[0]][coord[1]] = this.currentPlayer;
				// check if player 1 wins immediately
				if (evaluateBoard() <= -0.8 * val3) { // player 1 wins
					// unselect the case
					boolBoard[coord[0]][coord[1]] = false;
					boolPlayerBoard[coord[0]][coord[1]] = false;
					// beta = -VALMAX * (1 - (5 / 100) * (DEPTH_MAX - depth));
					// // diminution
					beta = -VALMAX; // de
					// 5% a chaque
					// tour
					break;
				}

				// Evaluation du noeud
				beta = Math.min(beta, alphabeta(depth - 1, alpha, beta, !maximize));
				// unselect the case
				boolBoard[coord[0]][coord[1]] = false;
				boolPlayerBoard[coord[0]][coord[1]] = false;

				if (beta < alpha) {
					break; // (* α cut-off *)
				}
			}
			return beta;

		}
	}

	/**
	 * Evaluate the current board. <br>
	 * Player 1 : positive score <br>
	 * Player 2 : negative score
	 * 
	 * @return total evaluation of the current Board
	 */
	private int evaluateBoard() {
		int total = 0;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (boolBoard[i][j] && !boolPlayerBoard[i][j]) // player 2
																// simulated
																// case
					total += evaluateMove(i, j, false); // player 2 maximizes
				else if (boolBoard[i][j] && boolPlayerBoard[i][j]) { // player 1
					total -= evaluateMove(i, j, true);
				}
			}
		}
		return total;
	}

	/**
	 * Give the evaluation of the move.
	 * 
	 * @param i
	 *            coordinate x
	 * @param j
	 *            coordinate
	 * @param player
	 *            Current player
	 * @return Evaluation of the move: <br>
	 *         Evaluation = horizontal line + vertical line
	 */
	private int evaluateMove(Integer i, Integer j, boolean player) {
		int totalHor = 0, totalVert = 0, totalDiag1 = 0, totalDiag2 = 0, total = 0;
		// check vertical
		for (int l = 0; l < 3; l++)
			// if cases are clicked (or simulated) and current player (or
			// simulated) owns the cases
			if ((butBoard[i][l].getClicked() && butBoard[i][l].getPlayer() == player)
					|| (boolBoard[i][l] && boolPlayerBoard[i][l] == player))
				totalVert++;
		// check horizontal
		for (int k = 0; k < 3; k++) {
			// if cases are clicked (or simulated) and current player (or
			// simulated) owns the cases
			if ((butBoard[k][j].getClicked() && butBoard[k][j].getPlayer() == player)
					|| (boolBoard[k][j] && boolPlayerBoard[k][j] == player)) {
				totalHor++;

			}
		}
		if (i == j) // check if belong to 1st diagonal
			// check 1st diagonal
			for (int l = 0; l < 3; l++)
				// if cases are clicked (or simulated) and current player (or
				// simulated) owns the cases
				if ((butBoard[l][l].getClicked() && butBoard[l][l].getPlayer() == player)
						|| (boolBoard[l][l] && boolPlayerBoard[l][l] == player))
					totalDiag1++;
		// check 2nd diagonal
		if (i == 2 - j) // check if belong to 2nd diagonal
			for (int l = 0; l < 3; l++)
				// if cases are clicked (or simulated) and current player (or
				// simulated) owns the cases
				if ((butBoard[2 - l][l].getClicked() && butBoard[2 - l][l].getPlayer() == player)
						|| (boolBoard[2 - l][l] && boolPlayerBoard[2 - l][l] == player))
					totalDiag2++;

		// total
		total = evaluateComb(totalHor) + evaluateComb(totalVert) + evaluateComb(totalDiag1) + evaluateComb(totalDiag2);
		return total;
	}

	/**
	 * Give the value of the combination. Used internally by the evaluateMove
	 * function.
	 * 
	 * @param total
	 *            number of case owned by the player
	 * @return Value of the combination
	 */
	private int evaluateComb(int total) {
		int eval = 0;
		switch (total) {
		case 0:
			eval = 0; // aucune case
			break;
		case 1:
			eval = this.val1; // premiere case de la ligne
			break;
		case 2:
			eval = this.val2; // on en aligne 2
			break;
		case 3:
			eval = this.val3; // on gagne
			break;
		}
		return eval;
	}

	/**
	 * List all possibilies for next move.
	 * 
	 * @return List of coordinates (x, y)
	 */
	List<int[]> findPossibility() {
		List<int[]> possMove = new ArrayList<int[]>();
		int[] coord;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				coord = new int[2];
				if (!butBoard[i][j].getClicked() && !boolBoard[i][j]) {
					coord[0] = i;
					coord[1] = j;
					possMove.add(coord);
				}
			}
		return possMove;
	}
	

	/**
	 * Main function used for tests
	 * 
	 * @param args
	 *            Null
	 */
	public static void main(String[] args) {
		System.out.println("Lancement test");
		GUI gui = new GUI();
		gui.GUIVisible(true);
	}

}
