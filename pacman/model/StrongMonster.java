package pacman.model;

/**
 *  Strong monster (its path chosen in order to chase Pacman)
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class StrongMonster extends Monster {
	private static final long serialVersionUID = -2245577027287082503L;

	/**
	 * Creates a new strong monster for the given collision map
	 * @param map collision map
	 */
	public StrongMonster(Map map) {
		super(map);
		// low probability to choose a random path for strong monster.
		// if the value is "0" or very very low- all the strong monster will be get the same path.
		// you can change this value in order to get easier or harder game
		setRandomPathProbability(0.3f);
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
	 * strong monster does not in fear from mighty pacman
	 */
	@Override
	public void fearFromMightyPacman() {
		
	}
}