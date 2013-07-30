package pacman.controllers;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;

import pacman.ai.PathFinder;
import pacman.model.Direction;
import pacman.model.Map;
import pacman.model.MightyPacman;
import pacman.model.MightyPill;
import pacman.model.Monster;
import pacman.model.Pacman;
import pacman.model.Pill;
import pacman.model.StationaryObject;
import pacman.model.StrongMonster;
import pacman.model.SuperPacman;
import pacman.model.SuperPill;
import pacman.model.WeakMonster;
import pacman.views.AppWindow;
import pacman.views.GameView;
import pacman.views.fx.SoundPlayer;


/**
 *  Pacman Game Engine- Manages the game logic
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class GameEngine implements Runnable {
	/* Game constants */
	private static final int SPECIAL_STAGE_TIME = 10;
	private static final int MONSTERS_DELAY = 3000;
	private static final int PACMAN_LIVES = 2;
	private static final int POINTS_EATING_PILL = 1;
	private static final int POINTS_EATING_SUPER_PILL = 10;
	private static final int POINTS_EATING_MONSTER = 100;
	private static final int MAX_CHEAT_USE = 2;
	/* Game timers */
	private Timer _gameTimer;
	private static final int FPS = 60;
	private Timer _specialStageTimer;
	/* Game map */
	private Map _levelMap = Map.getFirstLevelMap();
	/* UI Components */
	private AppWindow _window;
	private GameView _gameView = new GameView(_levelMap);
	private Pacman _pacman;
	/* Path Finder- AI Manager */
	private PathFinder _ai;
	/* Game Object & information */
	private int _remainingLives;
	private List<Monster> _monsters = new ArrayList<Monster>();
	private int _points;
	private int _remainingPills;
	private String _cheat = ""; // if types OOP - enter to the special stage
	private int _cheatUse = 0;

	/**
	 * Creates a new Game Engine
	 */
	public GameEngine() {
		// initialize base UI Components
		_window = new AppWindow();
		_window.showView(_gameView);
		_window.setWindowInScreenCenter();
		_gameView.addKeyListener(new MovePacmanListener());
		_gameView.setFocusable(true);

		// initialize the path finder object with the current map
		_ai = new PathFinder(_levelMap, 100);

		// initialize the game timer
		_gameTimer = new Timer(1000 / FPS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGame();
			}
		});

		// initialize the special stage timer
		_specialStageTimer = new Timer(SPECIAL_STAGE_TIME * 1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// stop the special stage, back to the normal pacman and stop the special stage timer
				_pacman = new Pacman(_levelMap);
				_gameView.setPacman(_pacman);
				for (Monster m : _monsters) {
					m.setNormalMode();
				}
				_specialStageTimer.stop();
			}
		});

		initializeNewGame();
		gameRestart();
	}

	/*
	 * Initialize new pacman game
	 */
	public void initializeNewGame() {
		// stop any timer (if running)
		_gameTimer.stop();
		_specialStageTimer.stop();

		// Set game variables to their default values
		// and reset the map
		_remainingLives = PACMAN_LIVES;
		_levelMap = Map.getFirstLevelMap();
		_remainingPills = _levelMap.getTotalPills();
		_gameView.newGame(_levelMap);
		_cheatUse = MAX_CHEAT_USE;
		_cheat = "";

		// initialize monsters
		if (_monsters.size() == 0) {
			for (int i = 0; i < 2; i++) {
				// Weak monsters
				WeakMonster w = new WeakMonster(_levelMap);
				_monsters.add(w);
			}

			for (int i = 0; i < 2; i++) {
				// Strong monsters
				StrongMonster s = new StrongMonster(_levelMap);
				_monsters.add(s);
			}
			_gameView.setMonsters(_monsters);
		} else {
			for (Monster m : _monsters) {
				m.initializeMonster();
			}
		}

		// play new game sound
		SoundPlayer.playNewGameSound();
	}

	/*
	 * Restart game match
	 */
	public void gameRestart() {
		// initialize new pacman and set its position to the initial position
		_pacman = new Pacman(_levelMap);
		_gameView.setPacman(_pacman);
		_pacman.setPosition(_levelMap.getPacmanInitialPosition().x, _levelMap.getPacmanInitialPosition().y);

		// set monsters position and release them in different delays
		int delay = MONSTERS_DELAY;
		for (Monster m : _monsters) {
			m.setNormalMode();
			m.setPosition(_levelMap.getCagePosition().x, _levelMap.getCagePosition().y);
			m.setReleaseTime(delay);
			delay += MONSTERS_DELAY;
		}

		// starts the game timer
		_gameTimer.start();
	}

	/**
	 * Timer tick- updating the game
	 */
	public void updateGame() {
		// move the pacman
		_pacman.move();

		// move the monsters
		for(Iterator<Monster> it = _monsters.iterator(); it.hasNext(); ) {
			// check if a monster requests a new path
			Monster m = it.next();

			if (m.requestNewPath()) {
				// the initial target of each monster is the pacman.
				// if the game is in the special stage (the monster is "IN FEAR")
				// or based on the monsters's probability for getting a random path
				// we choose a random target for the new path
				Point target = _pacman.getPosition();
				if (m.isInFear() || Math.random() < m.getRandomPathProbability()) {
					target = new Point((int)(Math.random() * _levelMap.getGameDimension().width), (int)(Math.random() * _levelMap.getGameDimension().height));
				}

				// find the path to the chosen target using AI Manager (A* algorithm)
				m.setPath(_ai.findPath(m, m.getPosition(), target));
			}

			// move the monster
			m.move();

			// pacman collision with monster
			if (_pacman.getBounds().intersects(m.getBounds())) {
				// check if pacman can eat the monster using Visitor pattern
				if (_pacman.eats(m)) {
					// get the monster back to the cage
					m.setPosition(_levelMap.getCagePosition().x, _levelMap.getCagePosition().y); 
					m.setReleaseTime(SPECIAL_STAGE_TIME * 1000);
					_points += POINTS_EATING_MONSTER;
					SoundPlayer.playEatMonsterSound();
				} else {
					// monster beats pacman.
					// stop the game. 
					// pacman is die for 2 seconds, and start another match (or game over if there is no remaining lives)
					_pacman.die();
					_gameTimer.stop();
					_specialStageTimer.stop();
					_remainingLives--;
					SoundPlayer.playPacmanDieSound();

					// delay 2 seconds and start another match
					try {
						Thread.sleep(2000);
						if (_remainingLives < 0) {
							// Game Over
							// initialize a new game
							initializeNewGame();
						}

						// start another match
						gameRestart();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// pacman eating pills
		StationaryObject[][] map = _levelMap.getStationaryObjectsMap();
		StationaryObject collidableObject = map[_pacman.getPosition().y][_pacman.getPosition().x];

		if (collidableObject != null && collidableObject.isCollidableWith(_pacman)) {
			map[_pacman.getPosition().y][_pacman.getPosition().x].collideWith(_pacman, this);
		}

		// determines if pacman ate all the pills
		if (_remainingPills == 0) {
			// FINISH GAME & WIN!
			_gameTimer.stop();
			initializeNewGame();
			gameRestart();
		}
	}


	/**
	 * eat a pill (uses the visitor pattern)
	 * @param pacman pacman object
	 * @param pill pill to be eaten
	 */
	public void eatPill(Pacman pacman, Pill pill) {
		pill.getParent().remove(pill);
		_levelMap.getStationaryObjectsMap()[pacman.getPosition().y][pacman.getPosition().x] = null;
		_points += POINTS_EATING_PILL;
		_remainingPills--;
	}

	/**
	 * Enter to the special stage (set the monster to be in fear, play music, change the pacman)
	 * @param pacman pacman to change for the special stage
	 */
	private void enterSpecialStage(Pacman pacman) {
		// set the current pacman to the special stage's pacman
		_pacman = pacman;
		_gameView.setPacman(_pacman);

		// set monsters to be in fear from the current pacman
		for (Monster m : _monsters) {
			m.setPath(null);
			m.setNormalMode();
			if (_pacman instanceof SuperPacman)
				m.fearFromSuperPacman();
			else if (_pacman instanceof MightyPacman)
				m.fearFromMightyPacman();
		}

		SoundPlayer.playSpecialStageSound();

		_specialStageTimer.restart();
	}

	/**
	 * eat a super pill (uses the visitor pattern)
	 * @param pacman pacman object
	 * @param pill SuperPill to be eaten
	 */
	public void eatSuperPill(Pacman pacman, SuperPill pill) {
		removeStationaryObjectFromBoard(pacman, pill);
		_points += POINTS_EATING_SUPER_PILL;
		_remainingPills--;
		SoundPlayer.playEatSuperPillSound();

		enterSpecialStage(new SuperPacman(_levelMap));
	}

	/**
	 * eat a mighty pill (uses the visitor pattern)
	 * @param pacman pacman object
	 * @param pill MightyPill to be eaten
	 */
	public void eatMightyPill(Pacman pacman, MightyPill pill) {
		removeStationaryObjectFromBoard(pacman, pill);
		_points += POINTS_EATING_SUPER_PILL;
		_remainingPills--;
		SoundPlayer.playEatSuperPillSound();

		enterSpecialStage(new MightyPacman(_levelMap));
	}

	/**
	 * Remove stationary object (for example a pill after eating)
	 * @param pacman pacman object
	 * @param object stationary object for removal
	 */
	private void removeStationaryObjectFromBoard(Pacman pacman, StationaryObject object) {
		object.getParent().remove(object);
		_levelMap.getStationaryObjectsMap()[pacman.getPosition().y][pacman.getPosition().x] = null;
	}

	@Override
	/**
	 * Run the application
	 */
	public void run() {
		_window.setVisible(true);
	}

	/**
	 *  Change Pacman direction- Keyboard Listener
	 *  @author     Lidan Hifi
	 *  @version    1.0
	 */
	class MovePacmanListener extends KeyAdapter {
		/*
		 * Set pacman direction
		 * Space = stop the pacman
		 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				_pacman.setDirection(Direction.UP);
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				_pacman.setDirection(Direction.DOWN);
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				_pacman.setDirection(Direction.RIGHT);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				_pacman.setDirection(Direction.LEFT);
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				_pacman.setDirection(Direction.NONE);
			}

			/* Game Cheat */
			// if you type "OOP" during the game, you'll switch to the special stage!
			// you can use this cheat only 2 times in each game
			if (_cheatUse > 0) {
				if (e.getKeyCode() == KeyEvent.VK_O) {
					_cheat += "O";
				} else if (e.getKeyCode() == KeyEvent.VK_P) {
					_cheat += "P";
				} else {
					_cheat = "";
				}

				if (_cheat.equals("OOP")) {
					enterSpecialStage(new SuperPacman(_levelMap));
					_cheat = "";
					_cheatUse--;
				}
			}
		}
	}

	/**
	 *  Exit Application Listener
	 *  @author     Lidan Hifi
	 *  @version    1.0
	 */
	class ExitApplicationListener extends WindowAdapter implements ActionListener, MouseListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

		@Override
		public void mouseClicked(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) {
			System.exit(0);
		}

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }
	}
}
