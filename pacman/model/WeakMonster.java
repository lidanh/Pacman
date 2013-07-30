package pacman.model;

/**
 *  Weak monster (its path chosen randomly)
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class WeakMonster extends Monster {
	private static final long serialVersionUID = 7146618458701400873L;

	/**
	 * Creates a new weak monster for the given collision map
	 * @param map collision map
	 */
	public WeakMonster(Map map) {
		super(map);
		// set the random path probability to 1-
		// which means that in each step, the monster's path chosen randomly
		setRandomPathProbability(1);
	}
	
	/**
	 * [Visitor Pattern]- determines if the monster can be eaten by the given eater
	 */
	@Override
	public boolean eatenBy(Eater eater) {
		return eater.eats(this);
	}

	/**
	 * fear from super pacman
	 */
	@Override
	public void fearFromSuperPacman() {
		setFearMode();
	}

	/**
	 * fear from mighty pacman
	 */
	@Override
	public void fearFromMightyPacman() {
		setFearMode();
	}
}
