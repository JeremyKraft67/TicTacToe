import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Create the GUI. <br>
 * This is the main Class for the graphical interface. <br>
 * The GUI has a TicTacToe-shape board of 'BoutMorpion' in the Center. <br>
 * On the upper side are the names of players and their victories. <br>
 * <br>
 * Technical characteristics: <br>
 * - Player 2 can be played by IA. <br>
 * - Game can be saved/ loaded.
 * 
 * @author jeremy
 * @see BoutMorpion
 * @see AIPrune
 */
public class GUI extends JFrame {
	private JPanel panPrinc = new JPanel(), panBoard = new JPanel(), panText = new JPanel();
	private JTextField player1Field = new JTextField("Player 1"), player2Field = new JTextField("Player 2");
	private JLabel winLabel1, winLabel2;
	private JCheckBox computer = new JCheckBox("IA");
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fichier = new JMenu("Fichier"), edition = new JMenu("Edition"),
			difficulte = new JMenu("Difficulte AI");
	private JMenuItem nouveau = new JMenuItem("Nouveau"), quitter = new JMenuItem("Quitter"),
			sauvegarder = new JMenuItem("Sauvegarder"), charger = new JMenuItem("Charger"),
			annuler = new JMenuItem("Annuler coup");
	private JRadioButton aIFacile = new JRadioButton("Facile"), aIMoyen = new JRadioButton("Moyen"),
			aIDifficile = new JRadioButton("Difficile"), aIExtreme = new JRadioButton("Extreme");
	private JCheckBox random = new JCheckBox("Aleatoire");
	private ButtonGroup radButGroup = new ButtonGroup();
	private BoutMorpion[][] butBoard = new BoutMorpion[3][3];
	private boolean player, IAon, randomOn;
	private String[] nomPlayers = new String[2];
	private int compt;
	private LevelAI levelAI;
	static int win1, win2; // number of victory of each Player
	private BoutMorpion lastBut1, lastBut2; // last button pushed by player 1 or
											// 2

	public GUI() {

		// Creation de l'interface graphique
		this.setTitle("Morpion");
		this.setSize(300, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		// ajout des boutons
		panBoard.setLayout(new GridLayout(3, 3));
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				butBoard[i][j] = new BoutMorpion(i, j);
				butBoard[i][j].addActionListener(new DrawListener());
				panBoard.add(butBoard[i][j]);
			}

		// Les JTextField
		Font ftText = new Font("Arial", Font.BOLD, 16);
		player1Field.setFont(ftText);
		player2Field.setFont(ftText);
		player1Field.setBackground(Color.GREEN); // player 1 starts first
		winLabel1 = new JLabel(Integer.toString(win1));
		winLabel2 = new JLabel(Integer.toString(win2));
		panText.add(player1Field);
		panText.add(winLabel1);
		panText.add(player2Field);
		panText.add(winLabel2);

		// Creation du menu

		// Ajout des Listeners pour le menu
		nouveau.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getGUI().dispose(); // on ferme la GUI
				GUI gui = new GUI(); // on cree une nouvelle GUI
				gui.setVisible(true);
			}
		});

		quitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getGUI().dispose(); // on ferme la GUI
			}
		});
		sauvegarder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveGame(); // on sauvegarde la GUI dans un fichier
			}
		});
		charger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadGame(); // on sauvegarde la GUI dans un fichier
			}
		});

		annuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// si IA est on, on enleve les 2 derniers mouvements
				if (compt >= 1 && IAon && lastBut2.getClicked()) {
					lastBut2.setClicked(false);
					compt--;
					lastBut1.setClicked(false);
					compt--;
				}
				// si l'IA est off, on enleve juste le dernier mouvement
				else if (compt >= 1 && !IAon) {
					if (compt % 2 == 1 && lastBut1.getClicked()) { // player 1
						lastBut1.setClicked(false);
						compt--;
					} else if (compt % 2 == 0 && lastBut2.getClicked()) { // player
																			// 2
						lastBut2.setClicked(false);
						compt--;
					}
				}
			}
		});

		random.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				randomOn = !randomOn;
			}
		});

		aIFacile.addActionListener(new reglageAI());
		aIMoyen.addActionListener(new reglageAI());
		aIDifficile.addActionListener(new reglageAI());
		aIExtreme.addActionListener(new reglageAI());

		// Les boutons de reglage de l'AI

		// Regroupement des boutons
		radButGroup.add(aIFacile);
		radButGroup.add(aIMoyen);
		radButGroup.add(aIDifficile);
		radButGroup.add(aIExtreme);
		// Ajout au menu Edition
		difficulte.add(aIFacile);
		difficulte.add(aIMoyen);
		difficulte.add(aIDifficile);
		difficulte.add(aIExtreme);
		difficulte.addSeparator();
		difficulte.add(random);
		random.doClick(); // par defaut
		if (levelAI == null) // si nouvelle partie
			aIMoyen.doClick(); // par defaut

		// Affichage du menu
		menuBar.add(fichier);
		menuBar.add(edition);
		fichier.add(nouveau);
		nouveau.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		fichier.add(sauvegarder);
		fichier.add(charger);
		fichier.add(quitter);
		quitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
		edition.add(annuler);
		edition.addSeparator();
		edition.add(difficulte);
		annuler.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
		this.setJMenuBar(menuBar);

		// Ajout du bouton IA
		computer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (IAon) {
					IAon = false; // on arrete l'IA
					player2Field.setEnabled(true);
				} else {
					IAon = true; // on lance l'IA
					player2Field.setEnabled(false);
					if (compt % 2 == 1) // seulement si c'est au tour du joueur
										// 2
						lanceIA();
				}
			}
		});
		computer.doClick();
		panText.add(computer);

		// on dessine le panneau principal
		panPrinc.setLayout(new BorderLayout());
		panPrinc.add(panBoard, BorderLayout.CENTER);
		panPrinc.add(panText, BorderLayout.SOUTH);
		this.setContentPane(panPrinc);
	}

	public void GUIVisible(boolean visible) {
		this.setVisible(visible);
	}

	/**
	 * Action realisee quand on presse un bouton. <br>
	 * Dessine un rond ou une croix selon le joueur, et incremete le compteur de
	 * tours. <br>
	 * Verifie si le joueur a gagne. Si c'est le cas, on affiche un message de
	 * fin. <br>
	 * On propose enfin de rejouer.
	 * 
	 * @author jeremy
	 *
	 */
	class DrawListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			BoardCheck check;

			if (((BoutMorpion) e.getSource()).getClicked() == false) {
				((BoutMorpion) e.getSource()).setClicked(true);
				if (compt % 2 == 0) // player 1
					lastBut1 = (BoutMorpion) e.getSource();
				else // player 2
					lastBut2 = (BoutMorpion) e.getSource();

				// Check whose player's turn it is
				if (compt % 2 == 0)
					player = true;
				else
					player = false;
				((BoutMorpion) e.getSource()).setPlayer(player);
				compt++; // on incremente le compteur de tours
				check = new BoardCheck(getGUI(), ((BoutMorpion) e.getSource()).getI(),
						((BoutMorpion) e.getSource()).getJ());
				if (check.getWin() == true)
					showMessage();

				// check if draw
				if (compt == 9) {
					compt = -1; // prevent AI to plays next
					showMessage();
				}

				player = !player; // On change de joueur
				// Montre a quel joueur est le tour
				if (player) {
					player1Field.setBackground(Color.green);
					player2Field.setBackground(Color.WHITE);
				} else {
					player1Field.setBackground(Color.white);
					player2Field.setBackground(Color.green);
				}

				if (IAon)
					lanceIA();
			}
		}
	}

	private void lanceIA() {
		AIPrune ai;
		int[] AIMove = new int[2]; // AI's move
		BoardCheck check;

		if (!player && compt != -1) { // compt = -1 if draw
			ai = new AIPrune(getGUI());
			AIMove = ai.initAlphabeta(compt);
			// dessine le mouvement de l'AI
			butBoard[AIMove[0]][AIMove[1]].setPlayer(player);
			butBoard[AIMove[0]][AIMove[1]].setClicked(true);
			lastBut2 = butBoard[AIMove[0]][AIMove[1]];
			butBoard[AIMove[0]][AIMove[1]].repaint();

			// check si l'IA gagne
			check = new BoardCheck(getGUI(), AIMove[0], AIMove[1]);
			if (check.getWin() == true)
				showMessage();

			// montre que le tour est au joueur 1
			player1Field.setBackground(Color.green);
			player2Field.setBackground(Color.WHITE);
			player = true; // tour au joueur 1
			compt++; // on incremente le compteur de tours
		}

	}

	/**
	 * Getter for the Board
	 * 
	 * @return Board of Buttons
	 */
	public BoutMorpion[][] getButBoard() {
		return this.butBoard;
	}

	// use for internal listener only
	private GUI getGUI() {
		return this;
	}

	public boolean getPlayer() {
		return this.player;
	}

	private void showMessage() {
		JOptionPane message = new JOptionPane();
		JOptionPane msgFin = new JOptionPane();
		GUI gui;
		JTextField txtField = null;
		String txt = "";
		int option;

		if (compt != -1) { // no draw

			// play the Applause sound
			playApplause();
			// Congratulation message and
			// ask if the player wants to reset the game
			if (player) {
				txtField = player1Field;
				win1++;
			} else {
				txtField = player2Field;
				win2++;
			}
			txt = "Felicitations, " + txtField.getText() + " gagne!" + "\n" + "Rejouez?";
		} else // draw
			txt = "Match nul!" + "\n" + "Rejouez?";

		// show a Congratulation message and propose to play again
		option = msgFin.showConfirmDialog(null, txt, "Partie Terminee", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (option == JOptionPane.OK_OPTION) {
			// getGUI().setVisible(false);
			compt = -1; // evite d'avoir le message 'match nul' si on
						// termine a compt = 9
			getGUI().setVisible(false);
			// cree un nouveau jeu
			gui = new GUI();
			gui.levelAI = this.levelAI;
			// Selectionne le niveau d'AI
			if (gui.levelAI == LevelAI.DIFFICILE) // difficile preselectionne
				gui.aIDifficile.doClick();
			else if (levelAI == LevelAI.FACILE) // facile preselctionne
				gui.aIFacile.doClick();
			else if (levelAI == LevelAI.MOYEN) // default
				gui.aIMoyen.doClick();
			else if (levelAI == LevelAI.EXTREME) // extreme preselctionne
				gui.aIExtreme.doClick();
			gui.randomOn = this.randomOn;
			gui.random.setSelected(this.random.isSelected());
			gui.player1Field.setText(getGUI().player1Field.getText());
			gui.player2Field.setText(getGUI().player2Field.getText());
			if (!this.IAon) // desactive l'IA si auparavant desactivee
				gui.computer.doClick();
			gui.setVisible(true);
		} else
			getGUI().dispose();
		compt = -1; // evite d'avoir le message 'match nul' si on
					// termine a compt = 9
	}

	/**
	 * Play an Audio clip located at 'Ma Musique'
	 */
	private void playApplause() {			
			ClassLoader classLoader = GUI.class.getClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream("Applause.wav");
			try {
			  Clip clip = AudioSystem.getClip();
			  AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
			  DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
			  clip = (Clip) AudioSystem.getLine(info);
			  clip.open(ais);
			  clip.start();
			} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
			  System.err.println("ERROR: Playing sound has failed");
			  e.printStackTrace();
			}
			
		
}

	/**
	 * Charge un jeu. <br>
	 * La GUI a charger se trouve dans le repertoire courant.
	 */
	private void loadGame() {
		JFileChooser fc = new JFileChooser();
		File file = null;
		gameData buff;

		// on modifie le look de la boite de dialogue
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			System.out.println("Look & Feel du systeme mon trouve");
			e1.printStackTrace();
		}
		// Look and Feel of Dialog Box
		SwingUtilities.updateComponentTreeUI(fc);

		// cree la boite de dialogue
		fc.setCurrentDirectory(new java.io.File("./Sauvegarde")); // start at
																	// application
		// current directory

		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) { // si fichier
														// selectionne
			file = fc.getSelectedFile();

			// Lecture des donnees
			try (InputStream fis = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
					ObjectInputStream reader = new ObjectInputStream(fis)) {
				buff = (gameData) reader.readObject();
				buff.loadData(getGUI());
				// on redessine le Board
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++)
						this.butBoard[i][j].repaint();
				System.out.println("Chargement fini");

			} catch (NoSuchFileException e) {
				// TODO Auto-generated catch block
				System.out.println("Fichier inexistant");
			} catch (EOFException e) {
				// TODO Auto-generated catch block
				System.out.println("Fichier vide");

			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// si pas de fichier selectionne
		else
			System.out.println("Sauvegarde annulee");

		// reset Look and Feel of the GUI
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Default Look & Feel non trouve");
		}
		// reset Look and Feel of the GUI to Defaults
		SwingUtilities.updateComponentTreeUI(getGUI().getContentPane());
		SwingUtilities.updateComponentTreeUI(getGUI().getJMenuBar());
	}

	/**
	 * Cree un fichier de sauvegarde du jeu. <br>
	 * Stocke la GUI dans un fichier ajoute au repertoire courant.
	 */
	private void saveGame() {
		JFileChooser fc = new JFileChooser();
		File file = null;
		gameData game = new gameData();
		int returnVal;

		// on modifie le look de la boite de dialogue
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			System.out.println("Look & Feel du systeme mon trouve");
			e1.printStackTrace();
		}
		// Look and Feel of Dialog Box
		SwingUtilities.updateComponentTreeUI(fc);

		game.saveData(this);
		// cree la boite de dialogue
		fc.setCurrentDirectory(new java.io.File("./Sauvegarde")); // start at
																	// application
		// current directory
		returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) { // si fichier
														// selectionne
			file = fc.getSelectedFile();
			// Ecriture des donnees
			try (OutputStream fos = Files.newOutputStream(file.toPath());
					ObjectOutputStream writer = new ObjectOutputStream(fos)) {
				writer.writeObject(game);
				System.out.println("Sauvegarde terminee");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Probleme lors de la sauvegarde");
			}
		}
		// si pas de fichier selectionne
		else
			System.out.println("Sauvegarde annulee");

		// reset Look and Feel of the GUI
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Default Look & Feel non trouve");
		}
		// reset Look and Feel of the GUI to Defaults
		SwingUtilities.updateComponentTreeUI(getGUI().getContentPane());
		SwingUtilities.updateComponentTreeUI(getGUI().getJMenuBar());
	}

	/**
	 * Create a class to store all game data.
	 * 
	 * @author jeremy
	 *
	 */
	private class gameData implements Serializable {
		private boolean[][] boutClicked = new boolean[3][3];
		private boolean[][] boutPlayer = new boolean[3][3];
		private boolean player, IAon, computerSelected;
		private int compt, win1, win2;
		private String[] nomPlayers = new String[2];
		private String player1FieldText = new String();
		private String player2FieldText = new String();
		private Color TextField1BackgroundColor, TextField2BackgroundColor;

		/**
		 * gameData class constructor.
		 */
		private gameData() {
		}

		/**
		 * Store game information in class gameData.
		 * 
		 * @param gui
		 *            The game.
		 */
		private void saveData(GUI gui) {
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++) {
					this.boutClicked[i][j] = gui.butBoard[i][j].getClicked();
					this.boutPlayer[i][j] = gui.butBoard[i][j].getPlayer();
				}
			this.player = gui.player;
			this.compt = gui.compt;
			this.nomPlayers = gui.nomPlayers;
			this.IAon = gui.IAon;
			this.player1FieldText = gui.player1Field.getText();
			this.player2FieldText = gui.player2Field.getText();
			this.win1 = gui.win1;
			this.win2 = gui.win2;
			this.computerSelected = gui.computer.isSelected();
			this.TextField1BackgroundColor = gui.player1Field.getBackground();
			this.TextField2BackgroundColor = gui.player2Field.getBackground();

		}

		/**
		 * Load game information in class gameData.
		 * 
		 * @param gui
		 *            The GUI to copy the data into.
		 */
		private void loadData(GUI gui) {
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++) {
					gui.butBoard[i][j].setClicked(this.boutClicked[i][j]);
					gui.butBoard[i][j].setPlayer(this.boutPlayer[i][j]);
				}
			gui.player = this.player;
			gui.compt = this.compt;
			gui.nomPlayers = this.nomPlayers;
			gui.IAon = this.IAon;
			gui.player1Field.setText(this.player1FieldText);
			gui.player2Field.setText(this.player2FieldText);
			gui.win1 = this.win1;
			gui.win2 = this.win2;
			gui.winLabel1.setText(Integer.toString(win1));
			gui.winLabel2.setText(Integer.toString(win2));
			gui.computer.setSelected(this.computerSelected);
			gui.player1Field.setBackground(this.TextField1BackgroundColor);
			gui.player2Field.setBackground(this.TextField2BackgroundColor);
		}

	} // end gameData

	/**
	 * Manage AI difficulty level
	 * 
	 * @author jeremy
	 *
	 */
	class reglageAI implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (((JRadioButton) e.getSource()) == aIFacile)
				levelAI = LevelAI.FACILE;
			else if (((JRadioButton) e.getSource()) == aIMoyen) {
				levelAI = LevelAI.MOYEN;
			} else if (((JRadioButton) e.getSource()) == aIDifficile)
				levelAI = LevelAI.DIFFICILE;
			else if (((JRadioButton) e.getSource()) == aIExtreme)
				levelAI = LevelAI.EXTREME;

		}

	}

	/**
	 * Getter pour difficulte normale
	 * 
	 * @return levelAI
	 */
	public int getLevelAI() {
		if (randomOn)
			return this.levelAI.getRandomizedDifficulty();
		else
			return this.levelAI.getDifficulte();
	}

} // end GUI.java
