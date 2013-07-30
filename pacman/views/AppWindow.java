package pacman.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *  Main application frame, contains the game view panel
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class AppWindow extends JFrame {
	private static final long serialVersionUID = -5539770173610616384L;
	private static Point _mouseDraggingCoordinates; 	// point for window dragging

	public AppWindow() {
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initializeUI();
	}

	/**
	 * set window in the screen center
	 */
	public void setWindowInScreenCenter() {
		Dimension size = getToolkit().getScreenSize();
		this.setLocation(size.width / 2 - getWidth() / 2, size.height / 2 - getHeight() / 2);
	}

	private void initializeUI() {
		// set border layout
		Container c = getContentPane();
		c.setLayout(new BorderLayout());


		// mouse listeners for window dragging
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				_mouseDraggingCoordinates = null;
			}
			@Override
			public void mousePressed(MouseEvent e) {
				_mouseDraggingCoordinates = e.getPoint();
			}
		});

		addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point currentPoint = e.getLocationOnScreen();	// get pointer location
				setLocation(currentPoint.x - _mouseDraggingCoordinates.x, currentPoint.y - _mouseDraggingCoordinates.y);	// set window location
			}
		});

		// event handler for window closing
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent we){
				Frame frame = (Frame)we.getSource();
				frame.dispose();
			}
		});		
	}

	/**
	 * show view in the application window
	 * @param view - Any application view (for example {@link GameView})
	 */
	public void showView(JPanel view) {
		// remove any components from the frame's content pane
		Container c = getContentPane();
		for (Component comp : c.getComponents()) {
			c.remove(comp);
		}

		// add the given view to the frame's center
		c.add(view, BorderLayout.CENTER);
		view.updateUI();

		// set the view visibility and repaint the application window
		view.setVisible(true);
		pack();
		repaint();
	}
}
