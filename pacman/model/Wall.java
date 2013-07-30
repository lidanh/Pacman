package pacman.model;

import java.awt.Color;

/**
 *  Game wall
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class Wall extends StationaryObject {
	private static final long serialVersionUID = -1931280758910189974L;

	/**
	 * Creates a new wall
	 */
	public Wall() {
		super(null);
		setOpaque(true);
		setBackground(new Color(87,87,255));
	}
	
	/**
	 * Cannot collide with any controllable object
	 */
	@Override
	public boolean isCollidableWith(ControllableObject object) {
		return false;
	}
}
