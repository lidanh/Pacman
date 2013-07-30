package pacman.model;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import pacman.controllers.GameEngine;

/**
 *  Stationary Object
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class StationaryObject extends JLabel {
	private static final long serialVersionUID = 8873369615267229329L;
	public static final Dimension BLOCK_SIZE = new Dimension(24,24);
	
	public StationaryObject(ImageIcon icon) {
		setIcon(icon);
		setPreferredSize(BLOCK_SIZE);
	}
	
	/**
	 * Handles collisions between the stationary object and pacman (kind of visitor pattern)
	 * @param pacman the pacman object to be collided
	 * @param engine the game engine that handles the collision
	 */
	public void collideWith(Pacman pacman, GameEngine engine) { }
	
	/**
	 * Determines if the stationary object can be collided with the given controllable object
	 * for most of the stationary object every controllable object can be collided, but you can
	 * override this method and configure it differently for each controllable object type.
	 * @param object controllable object
	 * @return true if can be collided
	 */
	public boolean isCollidableWith(ControllableObject object) {
		return true;
	}
}
