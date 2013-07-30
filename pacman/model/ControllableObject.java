package pacman.model;

import java.awt.Color;
import java.awt.Point;

/**
 *  Represents a controllable object
 *  for example: pacman (controlled by the game player)
 *  			 monsters (controlled by AI manager)
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public abstract class ControllableObject extends Sprite {
	private static final long serialVersionUID = -786478397202315877L;
	protected static final Color TEXT_COLOR = Color.white;
	private static final int BLOCK_SIZE =  24;
	/* Default values */
	private Point _position = new Point(0, 0);
	private Direction _direction = Direction.NONE;
	private Direction _temporaryDirection = null;
	private int _velocity = 3; // the velocity MUST be multipliers of 24 (block size): 2,3,4,6,8,12
	private Map _gameMap;

	/**
	 * Creates a new controllable object with the given map
	 * @param collisionMap
	 */
	public ControllableObject(Map collisionMap) {
		_gameMap = collisionMap;
	}

	/**
	 * Move the object for the chosen direction.
	 * direction changes every step (24 px)
	 */
	public void move() {
		int dx = 0, dy = 0;

		// Check direction and if the object can be moved there
		if (_direction == Direction.UP) {
			if (getCollisionMap().canMove(this, getPosition().x, getPosition().y - 1))
				dy -= _velocity;
			else
				setDirection(Direction.NONE);
			
		} else if (_direction == Direction.DOWN) {
			if (getCollisionMap().canMove(this, getPosition().x, getPosition().y + 1))
				dy += _velocity;
			else
				setDirection(Direction.NONE);
			
		} else if (_direction == Direction.RIGHT) {
			if (getCollisionMap().canMove(this, getPosition().x + 1, getPosition().y))
				dx += _velocity;
			else
				setDirection(Direction.NONE);
			
		} else if (_direction == Direction.LEFT) {
			if (getCollisionMap().canMove(this, getPosition().x - 1, getPosition().y))
				dx -= _velocity;
			else
				setDirection(Direction.NONE);
		}
		
		// set the object to the position
		setBounds((getBounds().x + dx) % getParent().getSize().width, (getBounds().y + dy) % getParent().getSize().height, BLOCK_SIZE, BLOCK_SIZE);

		// make step of one block
		if (isStep()) {
			// update the current position
			setPosition((getBounds().x) / BLOCK_SIZE, (getBounds().y) / BLOCK_SIZE);
			
			// change direction on step
			if (_temporaryDirection != null && !_temporaryDirection.equals(_direction)) {
				_direction = _temporaryDirection;
				_temporaryDirection = null;
			}
		}
	}

	/**
	 * Determines if object moved a one step (24 px)
	 * @return true if is in step, false otherwise
	 */
	protected boolean isStep() {
		return (getBounds().x % BLOCK_SIZE == 0 && getBounds().y % BLOCK_SIZE == 0);
	}
	
	/**
	 * Get stationary objects map
	 * @return
	 */
	protected StationaryObject[][] getStationaryObjectsMap() {
		return getCollisionMap().getStationaryObjectsMap();
	}

	/**
	 * Get object position at the game board
	 * @return object current position
	 */
	public Point getPosition() {
		return _position;
	}

	/**
	 * Set object position at the game board
	 * @param x object x position
	 * @param y object y position
	 */
	public void setPosition(int x, int y) {
		if (_gameMap.canMove(this, x, y)) {
			_position.move(x, y);
		}
		
		if (getParent() != null)
			setBounds(x * 24, y * 24, getHeight(), getWidth());
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		if (y + height > getParent().getHeight())
			y = 0;
		else if (y < 0)
			y = getParent().getHeight() - getHeight();

		if (x + width > getParent().getWidth())
			x = 0;
		else if (x < 0)
			x = getParent().getWidth() - getWidth();

		super.setBounds(x, y, width, height);
	}

	/**
	 * Get object's current direction
	 * @return direction (UP, DOWN, RIGHT, LEFT or NULL)
	 */
	public Direction getDirection() {
		return _direction;
	}

	/**
	 * Change object's direction
	 * the change will be committed in the next step
	 * @param direction
	 */
	public void setDirection(Direction direction) {
		this._temporaryDirection = direction;
	}

	/**
	 * Get object's velocity
	 * @return
	 */
	public int getVelocity() {
		return _velocity;
	}
	
	/**
	 * Get collisions map
	 * @return
	 */
	protected Map getCollisionMap() {
		return _gameMap;
	}

	/**
	 * Set pacman velocity (must be multipliers of BLOCK_SIZE)
	 * for example 24: 2,3,4,6,8,12
	 * @param velocity
	 */
	protected void setVelocity(int velocity) {
		if (BLOCK_SIZE % velocity == 0)
			this._velocity = velocity;
	}
}
