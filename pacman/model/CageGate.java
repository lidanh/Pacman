package pacman.model;

import java.awt.Color;

import javax.swing.border.MatteBorder;

/**
 *  Cage gate
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class CageGate extends StationaryObject {
	private static final long serialVersionUID = 8538341383276233949L;

	/**
	 * Creates a new Cage Gate
	 */
	public CageGate() {
		super(null);
		setOpaque(false);
		setBorder(new MatteBorder(5, 0, 5, 0, new Color(255,171,255)));
	}
	
	/**
	 * Monsters can collide with the cage gate (if it's not trapped)
	 * but pacman cannot collide with this object
	 */
	@Override
	public boolean isCollidableWith(ControllableObject object) {
		if (object instanceof Monster && !((Monster)object).isTrapped()) {
			return true;
		}
		else
			return false;
	}
}
