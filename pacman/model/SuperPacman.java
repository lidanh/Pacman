package pacman.model;

/**
 *  Super Pacman
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class SuperPacman extends Pacman {
	private static final long serialVersionUID = -7976610103418441027L;
	
	/**
	 * Creates a new Super Pacman with a given collisions map
	 * @param map collisions map
	 */
	public SuperPacman(Map map) {
		super(map);
		
		// set pacman velocity to 8
		setVelocity(8);
	}
	
	/**
	 * Super pacman can eat weak monster (Visitor Pattern)
	 */
	@Override
	public boolean eats(WeakMonster monster) {
		return true;
	}

	/**
	 * Super pacman can eat strong monster (Visitor Pattern)
	 */
	@Override
	public boolean eats(StrongMonster monster) {
		return true;
	}
}
