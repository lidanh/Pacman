package pacman.main;

import javax.swing.SwingUtilities;

import pacman.controllers.GameEngine;

/**
 *  Pacman Game
 *  This game was an assignment for Object Oriented Software Design course, Ben Gurion University, July 2013
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class PacmanMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// run the game application using the game engine object
		SwingUtilities.invokeLater(new GameEngine());
	}

}
