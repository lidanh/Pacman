package pacman.model;

/**
 *  Mighty Pacman
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class MightyPacman extends Pacman {
	private static final long serialVersionUID = 4127241523353250165L;

	/**
	 * Creates a new Mighty Pacman with a given collisions map
	 * @param map collisions map
	 */
	public MightyPacman(Map map) {
		super(map);
		
		// set pacman velocity to 6
		setVelocity(6);
	}
	
	/**
	 * Mighty pacman can eat weak monster (Visitor Pattern)
	 */
	@Override
	public boolean eats(WeakMonster monster) {
		return true;
	}

	/**
	 * Mighty pacman cannot eat weak monster (Visitor Pattern)
	 */
	@Override
	public boolean eats(StrongMonster monster) {
		return false;
	}
}
