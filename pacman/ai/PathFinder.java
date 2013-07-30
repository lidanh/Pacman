package pacman.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pacman.model.ControllableObject;
import pacman.model.Map;

/**
 *  PathFinder finds the shortest path between a given source point and target point, using A* Algorithm
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class PathFinder {
	/* The set of nodes that have been searched through */
	private List<Node> _closed;
	/* The set of nodes that we don't yet consider fully searched */
	private List<Node> _open = new ArrayList<Node>();
	/* The map for searching */
	private Map _map;
	/* The maximum depth of search */
	private int _maxSearchDistance;
	/* The set of nodes across the map */
	private Node[][] _nodes;

	/**
	 * Create a path finder object
	 * 
	 * @param map The map to be searched
	 * @param maxSearchDistance The maximum depth for searching
	 */
	public PathFinder(Map map, int maxSearchDistance) {
		_map = map;
		_maxSearchDistance = maxSearchDistance;

		_closed = new ArrayList<Node>();
		_nodes = new Node[map.getGameDimension().height][map.getGameDimension().width];

		// initialize the nodes array
		for (int i = 0; i < map.getGameDimension().height; i++) {
			for (int j = 0; j < map.getGameDimension().width; j++) {
				_nodes[i][j] = new Node(j,i);
			}
		}
	}

	/**
	 * Find the shortest path from the source location to the target location (avoiding blockages).
	 * 
	 * @param object The object that will be moving along the path.
	 * 
	 * @param source The source coordinates
	 * @param target The target coordinates
	 * @return The shortest path found from start to end, or null if no path can be found.
	 */

	public Path findPath(ControllableObject object, Point source, Point target) {
		// check the destination. if is blocked, we can't get there
		if (!_map.canMove(object, target))
			return null;

		// initialization for A* algorithm. 
		// The closed group is empty. Only the starting node is in the open list.
		_nodes[source.y][source.x].resetNode();
		_closed.clear();
		_open.clear();
		addToOpen(_nodes[source.y][source.x]);

		_nodes[target.y][target.x].setParent(null);

		int currentDepth = 0;

		// while we haven't exceeded the max search depth
		while(currentDepth < _maxSearchDistance && _open.size() > 0) {
			// pull out the first node in the open list, 
			// this is determined to be the most likely to be the next step based on the heuristic function (the list is being sorted each insertion).
			Node current = _open.get(0);
			if (current == _nodes[target.y][target.x])
				break; // reached the target!

			// remove the first node from open and add it to closed list
			_closed.add(_open.remove(0)); 

			// search through all the neighbors of the current node, evaluating them as next steps
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if ((dx == 0 && dy == 0) || (dx != 0 && dy != 0)) // not the current position, nor the diagonals
						continue;

					// determine the location of the neighbor and evaluate it
					int tx = current.getPosition().x + dx;
					int ty = current.getPosition().y + dy;

					if (tx >= 0 && tx < _map.getGameDimension().width && ty >= 0 && ty < _map.getGameDimension().height && _map.canMove(object, tx, ty)) {
						// the cost to get to this node is cost the current plus the movement
						// cost to reach this node Note that the heuristic value is only used in the sorted open list
						float nextStepCost = current.getCost() + 1;
						Node neighbour = _nodes[ty][tx];

						// if the new cost wev'e determined for this node is lower,
						// than it has been previously makes sure the node hasen't determined that there might have been a better path to get to this node 
						// so it needs to be re-evaluated

						if (nextStepCost < neighbour.getCost()) {
							if (_open.contains(neighbour))
								_open.remove(neighbour);

							if (_closed.contains(neighbour))
								_closed.remove(neighbour);
						}

						// if the node hasn't already been processed and discarded 
						// then reset its cost to our current cost and add it as a next possible step (to the open list)
						if (!_open.contains(neighbour) && !_closed.contains(neighbour)) {
							neighbour.setCost(nextStepCost);

							// set heuristic value to the distance using distance formula (pythagoras)
							float deltaX = target.x - source.x;
							float deltaY = target.y - source.y;
							neighbour.setHeuristic((float)(Math.sqrt((deltaX*deltaX)+(deltaY*deltaY))));

							currentDepth = Math.max(currentDepth, neighbour.setParent(current));
							addToOpen(neighbour);
						}
					}
				}
			}
		}

		// there was no path found. just return null
		if (_nodes[target.y][target.x].getParent() == null)
			return null;

		// at this point it definitely found a path so we can uses the parent references
		// of the nodes to find out way from the target location back
		// to the start recording the nodes on the way
		Path path = new Path();
		Node currNode = _nodes[target.y][target.x];
		while (currNode != _nodes[source.y][source.x]) { // until reach the source node
			path.prependStep(currNode.getPosition());
			currNode = currNode.getParent();
		}
		path.prependStep(source);

		// HALLELUJAH! We've got the shortest path!!!
		return path;
	}

	/**
	 * Add node to the open list and sort the list
	 * @param node
	 */
	private void addToOpen(Node node) {
		_open.add(node);
		Collections.sort(_open);
	}

	/**
	 *  A node in the search graph
	 *  @author     Lidan Hifi
	 *  @version    1.0
	 */
	private class Node implements Comparable<Node> {
		/* game coordinate */
		private Point _position;
		/* The path cost for this node */
		private float _cost;
		/* The parent of this node (we reached during the search) */
		private Node _parent;
		/* The heuristic cost of this node */
		private float _heuristic;
		/* The search depth of this node */
		private int _depth;

		/**
		 * Creates a new node
		 * 
		 * @param x The x coordinate of the node
		 * @param y The y coordinate of the node
		 */
		public Node(int x, int y) {
			setPosition(new Point(x, y));
		}

		/**
		 * Set the parent of this node
		 * 
		 * @param parent The parent node which lead us to this node
		 * @return The depth we have no reached in searching
		 */
		public int setParent(Node parent) {
			if (parent != null)
				_depth = parent._depth + 1;
			_parent = parent;

			return _depth;
		}

		/**
		 * Get node's parent
		 * @return node's parent
		 */
		public Node getParent() {
			return _parent;
		}

		/**
		 * @see Comparable#compareTo(Object)
		 */
		@Override
		public int compareTo(Node other) {
			float f = _heuristic + _cost;
			float of = other._heuristic + other._cost;

			if (f < of) {
				return -1;
			} else if (f > of) {
				return 1;
			} else {
				return 0;
			}
		}

		/**
		 * Set heuristic value for this node
		 * @param value heuristic value
		 */
		public void setHeuristic(float value) {
			_heuristic = value;
		}

		/**
		 * reset node to default values
		 */
		public void resetNode() {
			_depth = 0;
			_cost = 0;
		}

		/**
		 * Get node's cost
		 * @return node's cost
		 */
		public float getCost() {
			return _cost;
		}

		/**
		 * Set node's cost
		 * @param cost node's cost
		 */
		public void setCost(float cost) {
			_cost = cost;
		}

		/**
		 * Get node's position
		 * @return node's position
		 */
		public Point getPosition() {
			return _position;
		}

		/**
		 * Set node's position
		 * @param position position
		 */
		public void setPosition(Point position) {
			_position = position;
		}
	}
}
