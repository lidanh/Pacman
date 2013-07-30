package pacman.model;

/**
 *  [Visitor Pattern]
 *  Represents an object that can eat eatable object (i.e. Pacman)
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public interface Eater {
	/**
	 * determines if pacman (eater) can eat a weak monster
	 * @param monster weak monster
	 * @return true if yes (mighty pacman & super pacman), false if not
	 */
	public boolean eats(WeakMonster monster);
	/**
	 * determines if pacman (eater) can eat a strong monster
	 * @param monster strong monster
	 * @return true if yes (super pacman), false if not
	 */
	public boolean eats(StrongMonster monster);
}