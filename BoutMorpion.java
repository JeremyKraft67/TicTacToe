import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

import javax.swing.JButton;

/**
 * Draw a Button with possibility to add a cross/ circle. <br>
 * Circle: player 1 owns the case <br>
 * Cross: player 2 owns the case
 * 
 * @author jeremy
 *
 */
public class BoutMorpion extends JButton {
	private boolean player; // true: player 1, false: player 2
	private boolean clicked; // true if the case is already owned
	private int i, j; // position of the Button

	public BoutMorpion(int i, int j) {
		super();
		this.setMinimumSize(this.getSize());
		this.clicked = false;
		this.i = i;
		this.j = j;
	}

	/**
	 * Draw the Circle/ Cross atop the Button Use a boolean to define if player
	 * 1 owns the case or player 2. <br>
	 * Circle: player 1 (true) owns the case <br>
	 * Cross: player 2 (false) owns the case
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.cyan);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (clicked == true) { // the case has no markings by default
			if (player == true) { // for player 1
				g2d.setColor(Color.MAGENTA);
				g2d.setStroke(new BasicStroke(5));
				g2d.drawOval(this.getWidth() / 4, this.getHeight() / 4, this.getWidth() / 2, this.getHeight() / 2);
			} else { // for player 2
				g2d.setColor(Color.DARK_GRAY);
				g2d.setStroke(new BasicStroke(5));
				g2d.drawLine(this.getWidth() / 4, this.getHeight() / 4, 3 * this.getWidth() / 4,
						3 * this.getHeight() / 4);
				g2d.drawLine(this.getWidth() / 4, 3 * this.getHeight() / 4, 3 * this.getWidth() / 4,
						this.getHeight() / 4);
			}
		}
	}

	/**
	 * Define which player pushes the Button.
	 * 
	 * @param player
	 *            <br>
	 *            true: player 1, false player 2
	 */
	public void setPlayer(boolean player) {
		this.player = player;
	}

	public boolean getPlayer() {
		return player;
	}
	
	public boolean getClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}


}