package pacman.views.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *  Assets Manager- for accessing App's resources from a central object
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class AssetsManager {
	public static final String ASSETS_PATH="/pacman/views/assets/";	// Assets Path
	public static Color _gameLablesColor;
	private static Font _baseFont;
	
	/**
	 * get current level foreground color
	 * @return current level foreground color
	 */
	public static Color getCurrentLevelForegroundColor() {
		return _gameLablesColor;
	}
	
	/**
	 * get resource URL (for simple access from DISK or from JAR File)
	 * the assets folders is ordered by the class name, so we send the class as an argument, and the asset name
	 * @param classObj	class object for accessing class asset's folder
	 * @param assetName asset name (image, sound, etc.)
	 * @return asset URL
	 */
	public static URL getResource(Class<?> classObj ,String assetName) {
		return classObj.getResource(ASSETS_PATH + classObj.getSimpleName() + "/" + assetName);
	}
	
	/**
	 * get resource URL by name, without any specific class
	 * @param assetName
	 * @return asset URL
	 */
	public static URL getResource(String assetName) {
		return URLClassLoader.class.getResource(ASSETS_PATH + assetName);
	}
	
	/**
	 * get resource's InputStream, for object that need InputStream only
	 * @param fullPath asset's path
	 * @return asset InputStream
	 */
	public static InputStream getResourceAsStream(String fullPath) {
		return URLClassLoader.class.getResourceAsStream(ASSETS_PATH + fullPath);
	}
	
	/**
	 * get application base font, and register the font in the {@link GraphicsEnvironment}
	 * @return application {@link Font}
	 */
	public static Font getBaseFont() {
		if (_baseFont == null) {
			try {
				// try to read the font file
				_baseFont = Font.createFont(Font.TRUETYPE_FONT, getResourceAsStream("fonts/dosFont.ttf"));
				
				// register the font in the local graphics environment
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			    ge.registerFont(_baseFont);
			} catch (IOException e) {
				_baseFont = new Font("Arial", Font.PLAIN, 18);
			} catch (FontFormatException e) {
				_baseFont = new Font("Arial", Font.PLAIN, 18);
			}
		}
		
		return _baseFont;
	}
}
