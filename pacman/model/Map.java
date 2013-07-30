package pacman.model;

import java.awt.Dimension;
import java.awt.Point;

/**
 *  Game map
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class Map {
	/* codes map */
	private int[][] _collisionMap;
	/* stationary objects map ("Real" objects: JLabels / JPanels) */
	private StationaryObject[][] _objectsMap;
	/* map dimensions */
	private Dimension _gameDimension;
	/* cage position (initial position for the monsters) */
	private Point _cagePosition;
	/* pacman initial position */
	private Point _pacmanInitialPosition;
	/* total pills in the current map */
	private int _totalPills;
	
	/**
	 * Creates a new map
	 * @param map given array of codes
	 * @param cagePosition cage position (initial position for monsters)
	 * @param pacmanInitialPosition pacman initial position
	 */
	protected Map(int[][] map, Point cagePosition, Point pacmanInitialPosition) {
		_collisionMap = map;
		_gameDimension = new Dimension(_collisionMap[0].length, _collisionMap.length);
		_cagePosition = cagePosition;
		_pacmanInitialPosition = pacmanInitialPosition;
		_objectsMap = new StationaryObject[_gameDimension.height][_gameDimension.width];
		
		// creating the stationary objects array base on the codes:
		// 0 - empty panel
		// 1 - a wall
		// 2 - regular pill
		// 3 - mighty pill
		// 4 - super pill
		// -1 - cage gate
		for (int i = 0; i < _gameDimension.height; i++) {
			for (int j = 0; j < _gameDimension.width; j++) {
				if (_collisionMap[i][j] == 1) {
					// wall
					_objectsMap[i][j] = new Wall();
				} else if (_collisionMap[i][j] == 2) {
					// regular pill
					_objectsMap[i][j] = new Pill();
					_totalPills++;
				} else if (_collisionMap[i][j] == 3) {
					// mighty pill
					_objectsMap[i][j] = new MightyPill();
					_totalPills++;
				} else if (_collisionMap[i][j] == 4) {
					// super pill
					_objectsMap[i][j] = new SuperPill();
					_totalPills++;
				} else if (_collisionMap[i][j] == -1) {
					_objectsMap[i][j] = new CageGate();
				} else {
					_objectsMap[i][j] = new StationaryObject(null);
				}
			}
		}
		
	}
	
	/**
	 * Get map's total pills
	 * @return total pills
	 */
	public int getTotalPills() {
		return _totalPills;
	}
	
	/**
	 * Get cage position
	 * @return cage position
	 */
	public Point getCagePosition() {
		return _cagePosition;
	}
	
	/**
	 * Get pacman initial position
	 * @return pacman initial position
	 */
	public Point getPacmanInitialPosition() {
		return _pacmanInitialPosition;
	}
	
	/**
	 * Get game dimension
	 * @return game dimension based on the map size
	 */
	public Dimension getGameDimension() {
		return _gameDimension;
	}
	
	/**
	 * Get the collision map (codes map)
	 * @return collision map
	 */
	public int[][] getCollisionMap() {
		return _collisionMap;
	}
	
	/**
	 * Get the stationary objects map (jcomponents map)
	 * @return stationary objects map
	 */
	public StationaryObject[][] getStationaryObjectsMap() {
		return _objectsMap;
	}
	
	public boolean canMove(ControllableObject object, Point position) {
		return canMove(object, position.x, position.y);
	}
	
	/**
	 * Determines if the given object can be moved to the given position
	 * @param object controllable object
	 * @param position target position
	 * @return true if can move, false if not
	 */
	public boolean canMove(ControllableObject object, int x, int y) {
		int height = getGameDimension().height;
		int width = getGameDimension().width;

		// if the target position is out of the panel size
		if (y < 0)
			y = height - 1;
		else if (y >= height)
			y = 0;

		if (x < 0)
			x = width - 1;
		else if (x >= width)
			x = 0;
		
		if (_objectsMap[y][x] != null)
			return _objectsMap[y][x].isCollidableWith(object);
		
		return true;
	}
	
	/**
	 * Get first level map
	 * @return Map object that represents the first game level
	 */
	public static Map getFirstLevelMap() {
		int[][] map = {
				// 0 - empty panel
				// 1 - a wall
				// 2 - regular pill
				// 3 - mighty pill
				// 4 - super pill
				// -1 - cage gate
				{ 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 },
				{ 1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1 },
				{ 1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1 },
				{ 1,3,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,4,1 },
				{ 1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1 },
				{ 1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1 },
				{ 1,2,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,2,1 },
				{ 1,2,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,2,1 },
				{ 1,2,2,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,2,2,1 },
				{ 1,1,1,1,1,1,2,1,1,1,1,1,0,1,1,0,1,1,1,1,1,2,1,1,1,1,1,1 },
				{ 0,0,0,0,0,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,0,0,0,0,0 },
				{ 0,0,0,0,0,1,2,1,1,0,1,1,1,-1,-1,1,1,1,0,1,1,2,1,0,0,0,0,0 },
				{ 1,1,1,1,1,1,2,1,1,0,1,0,0,0,0,0,0,1,0,1,1,2,1,1,1,1,1,1 },
				{ 0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0 }, // PORTAL
				{ 1,1,1,1,1,1,2,1,1,0,1,0,0,0,0,0,0,1,0,1,1,2,1,1,1,1,1,1 },
				{ 0,0,0,0,0,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,0,0,0,0,0 },
				{ 0,0,0,0,0,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,0,0,0,0,0 },
				{ 1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1 },
				{ 1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1 },
				{ 1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1 },
				{ 1,4,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,3,1 },
				{ 1,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,1 },
				{ 1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1 },
				{ 1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1 },
				{ 1,2,2,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,2,2,1 },
				{ 1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1 },
				{ 1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1 },
				{ 1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1 }, 
				{ 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 },
				
		};
			return new Map(map, new Point(15, 12), new Point(14, 16));
	}
}
