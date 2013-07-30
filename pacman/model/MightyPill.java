package pacman.model;

import javax.swing.ImageIcon;

import pacman.controllers.GameEngine;
import pacman.views.utils.AssetsManager;

/**
 *  Mighty Pill
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class MightyPill extends Pill {
	private static final long serialVersionUID = -1096374292609342594L;

	/**
	 * Creates a new mighty pill
	 */
	public MightyPill() {
		super();
		setIcon(new ImageIcon(AssetsManager.getResource(MightyPill.class, "img.png")));
	}
	
	/**
	 * Handles collision with pacman (kind of visitor pattern)
	 */
	@Override
	public void collideWith(Pacman pacman, GameEngine engine) {
		engine.eatMightyPill(pacman, this);
	}
}
