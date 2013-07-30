package pacman.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *  A path determined by PathFinder object (using A* Algorithm for AI).
 *  A path is represented by series of points (=Steps).
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class Path {
	/* list of steps */
	private List<Point> _steps;
	
	/**
	 * Create an empty path
	 */
	public Path() {
		_steps = new ArrayList<Point>();
	}

	/**
	 * Get the length of the path
	 * 
	 * @return The number of steps in this path
	 */
	public int getLength() {
		return _steps.size();
	}
	
	/**
	 * Get the step at a given index in the path
	 * 
	 * @param index The index of the step to retrieve
	 * @return The step information, the position on the map
	 */
	public Point getStep(int index) {
		return _steps.get(index);
	}

	/**
	 * Prepend a step to the path.  
	 * 
	 * @param x The x coordinate of the new step
	 * @param y The y coordinate of the new step
	 */
	public void prependStep(Point point) {
		_steps.add(0, point);
	}
	
	/**
	 * Check if this path contains the given step
	 * 
	 * @param x The x coordinate of the step to check for
	 * @param y The y coordinate of the step to check for
	 * @return True if the path contains the given step
	 */
	public boolean contains(int x, int y) {
		return _steps.contains(new Point(x, y));
	}
}

