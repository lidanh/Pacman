package pacman.model;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import pacman.ai.Path;
import pacman.views.utils.AssetsManager;

/**
 *  A Monster
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public abstract class Monster extends ControllableObject implements Eatable {
	private static final long serialVersionUID = -2278066974451795606L;
	/* monster image chosen randomly */
	BufferedImage _image;
	/* is in fear mode (special stage) */
	boolean _inFear = false;
	/* current path */
	private Path _path;
	private int _nextStep;
	private boolean _requestNewPath;
	/* probability for getting a randomized path */
	private float _randomPathProbability = 0.5f;
	/* is monster trapped in the cage */
	private boolean _isTrapped = true;
	private long _releaseTime = System.currentTimeMillis();

	/**
	 * Creates a new monster for the given collisions map
	 * @param map collisions map
	 */
	public Monster(Map map) {
		super(map);

		// set animation speed
		setFPS(5);
		
		// initialize monster
		initializeMonster();
	}

	/**
	 * Initialize new monster on game start
	 */
	public void initializeMonster() {
		// Choose monster image randomly (for making the game harder!!!)
		try {
			_image = ImageIO.read(AssetsManager.getResource(Monster.class, ((int)Math.floor(Math.random() * 4) + 1) + ".png"));
			setSpriteImage(_image);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set the position to the cage position of the given map
		setPosition(getCollisionMap().getCagePosition().x, getCollisionMap().getCagePosition().y);

		// set the initial direction
		setDirection(Direction.RIGHT);

		_requestNewPath = true;
	}

	/**
	 * Check if the monster requests a new path (for example finish the current path and reached the target)
	 * @return true if needs new path, false if not
	 */
	public boolean requestNewPath() {
		return _requestNewPath;
	}

	/**
	 * Change random path probability. the value must be valid: between 0 and 1.
	 * @param probability
	 */
	protected void setRandomPathProbability(float probability) {
		if (probability > 0 && probability <= 1)
			_randomPathProbability = probability;
	}

	/**
	 * Get random path probability
	 * @return probability for randomize a path
	 */
	public float getRandomPathProbability() {
		return _randomPathProbability;
	}

	/**
	 * Change monster's path
	 * @param path
	 */
	public void setPath(Path path) {
		_path = path;
		_nextStep = 1;
		_requestNewPath = false;

		if (path == null)
			_requestNewPath = true;
	}

	/**
	 * Determine if monster is in fear mode
	 * @return true if the monster in fear, false if not
	 */
	public boolean isInFear() {
		return _inFear;
	}

	/**
	 * If the monster is trapped in the monsters cage
	 * @return true if yes, false if not
	 */
	public boolean isTrapped() {
		return _isTrapped;
	}

	/**
	 * Trap the monster in the cage, and release it after the given delay
	 * @param delay delay value in milliseconds
	 */
	public void setReleaseTime(int delay) {
		_isTrapped = true;
		_releaseTime = System.currentTimeMillis() + delay;
	}

	/**
	 * Back the monster to the normal mode (after the special stage)
	 */
	public void setNormalMode() {
		setSpriteImage(_image);
		_inFear = false;
		stopAnimation();
	}

	/**
	 * Set the monster to be in fear
	 */
	protected void setFearMode() {
		try {
			setSpriteImage(ImageIO.read(AssetsManager.getResource(Monster.class, "scatterMode.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		startAnimation();

		_inFear = true;
	}

	public abstract void fearFromSuperPacman();
	public abstract void fearFromMightyPacman();

	@Override
	public void move() {
		// release the monster if release time has come
		if (System.currentTimeMillis() > _releaseTime)
			_isTrapped = false;
		
		// set direction based on the next step in the current path
		if (isStep()) {
			if (_path != null && _nextStep < _path.getLength()) {
				if (_path.getStep(_nextStep).y - getPosition().y < 0)
					setDirection(Direction.UP);
				else if (_path.getStep(_nextStep).y - getPosition().y > 0)
					setDirection(Direction.DOWN);
				else if (_path.getStep(_nextStep).x - getPosition().x < 0)
					setDirection(Direction.LEFT);
				else if (_path.getStep(_nextStep).x - getPosition().x > 0)
					setDirection(Direction.RIGHT);
			} else {
				_requestNewPath = true;
			}
		}

		super.move();

		// increment the path step index
		if (isStep())
			_nextStep++;
	}
}
