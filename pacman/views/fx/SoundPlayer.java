package pacman.views.fx;

import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import pacman.views.utils.AssetsManager;

/**
 *  Sound Player Helper
 *  @author     Lidan Hifi
 *  @version    1.0
 */
public class SoundPlayer
{
	// player fields
	private static final int EXTERNAL_BUFFER_SIZE = 128000;
	private static final String SOUNDS_PATH = "sounds/"; // path for sounds
	private static SourceDataLine[] _channels = new SourceDataLine[2];
	private static boolean _playSound = true;
	
	/**
	 * toggle sounds on/off
	 */
	public static void toggleSound() {
		stopAllSounds();
		
		_playSound = !_playSound;
	}
	
	/**
	 * play sound for new game
	 */
	public static void playNewGameSound() {
		URL soundFile = AssetsManager.getResource(SOUNDS_PATH + "newgame.wav");
		stopAllSounds();
		play(soundFile, 0);
	}
	
	/**
	 * play sound for eating pills
	 */
	public static void playEatPillSound() {
		URL soundFile = AssetsManager.getResource(SOUNDS_PATH + "eatPill.wav");
		play(soundFile, 0);
	}
	
	/**
	 * play sound for eating super pills
	 */
	public static void playEatSuperPillSound() {
		URL soundFile = AssetsManager.getResource(SOUNDS_PATH + "eatSuperPill.wav");
		play(soundFile, 0);
	}
	
	/**
	 * play sound for eating monsters
	 */
	public static void playEatMonsterSound() {
		URL soundFile = AssetsManager.getResource(SOUNDS_PATH + "eatMonster.wav");
		play(soundFile, 0);
	}
	
	/**
	 * play special stage sound
	 */
	public static void playSpecialStageSound() {
		URL soundFile = AssetsManager.getResource(SOUNDS_PATH + "specialStage.wav");
		stopAllSounds();
		play(soundFile, 1);
	}
	
	/**
	 * play sound for eating monsters
	 */
	public static void playPacmanDieSound() {
		URL soundFile = AssetsManager.getResource(SOUNDS_PATH + "pacmanDie.wav");
		stopAllSounds();
		play(soundFile, 0);
	}
	
	/**
	 * play siren sound
	 */
	public static void playSirenSound() {
		URL soundFile = AssetsManager.getResource(SOUNDS_PATH + "siren.wav");
		play(soundFile, 0);
	}
	
	private static void stopAllSounds() {
		for (int i = 0; i < _channels.length; i++) {
			if (_channels[i] != null)
				_channels[i].close();
		}
	}
	
	/**
	 * play sound
	 * @param inputFile file's URL
	 */
	private static void play(URL inputFile, int channel) {
		if (_playSound) {		// check if sound is on, and stop any sound
			if (_channels[channel] != null)
				_channels[channel].close();
			
			// final variables, for accessing from the sound's Thread
			final URL soundFile = inputFile;
			final int cIndex = channel; 
			
			// play the sound in a different thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					// read file
					AudioInputStream audioInputStream = null;
					try {
						audioInputStream = AudioSystem.getAudioInputStream(soundFile);
						/*
						  From the AudioInputStream, i.e. from the sound file, we fetch information about the format of the audio data.
						  These information include the sampling frequency, the number of channels and the size of the samples.
						  These information are needed to ask Java Sound for a suitable output line for this audio file.
						 */
						AudioFormat	audioFormat = audioInputStream.getFormat();
			
						/*
						  Asking for a line is a rather tricky thing. We have to construct an Info object that specifies the desired properties for the line.
						  First, we have to say which kind of line we want. The possibilities are: SourceDataLine (for playback), Clip (for repeated playback) and TargetDataLine (forrecording).
						  Here, we want to do normal playback, so we ask for a SourceDataLine. Then, we have to pass an AudioFormat object, so that
						  the Line knows which format the data passed to it will have.
						  Furthermore, we can give Java Sound a hint about how big the internal buffer for the line should be. This
						  isn't used here, signaling that we don't care about the exact size. Java Sound will use some default value for the buffer size.
						 */
						DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			
						_channels[cIndex] = (SourceDataLine) AudioSystem.getLine(info);
			
						/* The line is there, but it is not yet ready to receive audio data. We have to open the line. */
						_channels[cIndex].open(audioFormat);
			
						/* Still not enough. The line now can receive data, but will not pass them on to the audio output device (which means to your sound card). This has to be activated. */
						_channels[cIndex].start();
			
						/*
						  Ok, finally the line is prepared. Now comes the real job: we have to write data to the line. We do this in a loop. First, we read data from the
						  AudioInputStream to a buffer. Then, we write from this buffer to the Line. This is done until the end of the file is reached, which is detected by a
						  return value of -1 from the read method of the AudioInputStream.
						 */
						int	nBytesRead = 0;
						byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
						while (nBytesRead != -1) {
							nBytesRead = audioInputStream.read(abData, 0, abData.length);
							if (nBytesRead >= 0) {
								_channels[cIndex].write(abData, 0, nBytesRead);
							}
						}
			
						/* Wait until all data are played. This is only necessary because of the bug noted below. (If we do not wait, we would interrupt the playback by prematurely closing the line and exiting the VM.) */
						_channels[cIndex].drain();
			
						/* All data are played. We can close the shop. */
						_channels[cIndex].close();
					}
					catch (Exception e)
					{
						System.err.println(e.getMessage());
					}
				}
			}).start();
		}
	}
}