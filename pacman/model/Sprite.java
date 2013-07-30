package pacman.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *  Sprite represents an object that can be animated using an image contains a sequence of frames
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class Sprite extends JLabel {
	private static final long serialVersionUID = -9012270587810440480L;
	private static final int BLOCK_SIZE = 24;
	/* animation timer & frames per second */
	private Timer _animationTimer;
	private int _fps = 25;
	/* the sprite image */
	private BufferedImage _sprite = null;
	/* the frame index & angle */
	private int _spriteX;
	private int _spriteAngle;

	/**
	 * Creates a new sprite object
	 */
	public Sprite() {
		// initialize the animation timer
		_animationTimer = new Timer(1000 / _fps, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_spriteX = (_spriteX + BLOCK_SIZE) % _sprite.getWidth();
			}
		});
	}

	/**
	 * Start the sprite animation
	 */
	public void startAnimation() {
		_animationTimer.start();
	}

	/**
	 * Stop the sprite animation
	 */
	public void stopAnimation() {
		_animationTimer.stop();
	}
	
	/**
	 * Change the animation frames per second
	 * @param fps fps value
	 */
	protected void setFPS(int fps) {
		_fps = fps;
		_animationTimer.setDelay(1000 / _fps);
	}

	/**
	 * Set the sprite image (sequence of frames in one image)
	 * @param sprite sprite image
	 */
	protected void setSpriteImage(BufferedImage sprite) {
		_sprite = sprite;
		_spriteX = 0;
	}
	
	/**
	 * Get the current sprite image
	 * @return
	 */
	protected BufferedImage getSpriteImage() {
		return _sprite;
	}

	@Override
	protected void paintComponent(Graphics g) {
		setSize(BLOCK_SIZE, BLOCK_SIZE);
		
		// "crop" the current frame
		BufferedImage currentFrame = _sprite.getSubimage(_spriteX, 0, BLOCK_SIZE, BLOCK_SIZE);

		// change the sprite angle
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform trans = new AffineTransform();
		trans.rotate(Math.toRadians(_spriteAngle), currentFrame.getWidth() / 2, currentFrame.getHeight() / 2);
		
		// draw the sprite
		g2d.drawImage(currentFrame, trans, this);
	}

	/**
	 * Change the sprite angle
	 * @param spriteAngle
	 */
	protected void setSpriteAngle(int spriteAngle) {
		_spriteAngle = spriteAngle;
	}
}
