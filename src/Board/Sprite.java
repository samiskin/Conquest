package Board;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/** Stores an animation of either a single frame or numerous frames and draws it.
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class Sprite
{

	private String filePath;

	private Frame currentFrame;
	private Frame lastFrame;
	private Frame firstFrame;
	private int numFrames;
	private boolean playOnce;
	private boolean stopped;
	private int frameRate;
	private int frameMs;
	private long msCount;
	private long lastMsCount;


	/** Creates a single frame sprite from a file
	 * 
	 * @param file the image file
	 * @throws IOException */
	public Sprite(File file) throws IOException
	{
		firstFrame = new Frame(ImageIO.read(file));
		currentFrame = firstFrame;
		lastFrame = firstFrame;
		lastFrame.setNext(firstFrame);
		filePath = file.toString();

		numFrames = 1;

		playOnce = false;
		stopped = false;

		msCount = 0;
		lastMsCount = System.currentTimeMillis();

	}

	/** Creates a single frame sprite from a file name
	 * 
	 * @param fileName the name of the file
	 * @throws IOException */
	public Sprite(String fileName) throws IOException
	{
		this(new File(fileName));
	}

	/** Creates an animated sprite with the base name of the images The image
	 * filed must be titled with the base name followed by an underscore
	 * followed by the frame number
	 * 
	 * @param fileName The name of the file (not including the _#)
	 * @param fileType The type of image (ex: .jpg, .png, .gif)
	 * @throws IOException */
	public Sprite(String fileName, String fileType) throws IOException
	{
		this(fileName + "_1" + fileType);

		boolean complete = false;
		for (int frame = 2; !complete; frame++)
		{
			try
			{
				insertFrame(fileName + "_" + frame + fileType);
				numFrames++;
			} catch (IOException e)
			{
				complete = true;
			}
		}

	}

	/** Creates a copy of a previously existing sprite
	 * 
	 * @param other the other sprite to copy */
	public Sprite(Sprite other)
	{
		firstFrame = other.firstFrame;
		currentFrame = firstFrame;
		lastFrame = other.lastFrame;
		filePath = other.filePath;
		numFrames = other.numFrames;
		playOnce = false;
		stopped = false;
		msCount = 0;
		lastMsCount = System.currentTimeMillis();
	}


	/** Advances which frame the animation is on if necessary */
	private void animate()
	{
		// Decides on the frame rate based on the total number of frames
		if (numFrames == 2)
			frameRate = 3;
		else
			frameRate = 6;
		frameMs = 1000 / frameRate;

		// Change frames if the current frame has been active for long enough
		if (msCount > frameMs)
		{
			// If the sprite was set to only animate once and the animation
			// has ended, reset the sprite but pause it at that last frame
			if (playOnce && currentFrame == lastFrame)
			{
				loop();
				pause();
			}
			currentFrame = currentFrame.getNext();

			// Reset the timer for the next frame
			msCount = 0;
		}
		msCount += System.currentTimeMillis() - lastMsCount;
		;
		lastMsCount = System.currentTimeMillis();

	}

	/** Sets the sprite to animte */
	public void play()
	{
		stopped = false;
	}

	/** Stops the sprite from animating */
	public void pause()
	{
		stopped = true;
	}

	/** Sets the sprite to run its animation once then stop */
	public void playOnce()
	{
		playOnce = true;
	}

	/** Sets the sprite to constantly loop through its frames */
	public void loop()
	{
		playOnce = false;
	}

	/** Calculates if the sprite is only set to play once and the last frame has
	 * been displayed
	 * 
	 * @return true if the sprite has completed its animation, false if not */
	public boolean isComplete()
	{
		if (playOnce && currentFrame == lastFrame)
		{
			// Only return true if the last frame has finished displaying
			// for its full duration
			if (msCount > frameMs)
				return true;
		}
		return stopped;
	}

	/** Start the sprite animation from the beginning */
	public void reset()
	{
		currentFrame = firstFrame;
	}

	/** Returns the file that this sprite was generated from
	 * 
	 * @return the file path to the image of the first frame */
	public String toString()
	{
		return filePath;
	}

	/** Inserts a frame at the end of the animation
	 * 
	 * @param fileName the name of the image to add
	 * @throws IOException */
	public void insertFrame(String fileName) throws IOException
	{
		insertFrame(new File(fileName));
	}

	/** Inserts a frame at the end of the animation
	 * 
	 * @param file the file corresponding to the image to add
	 * @throws IOException */
	public void insertFrame(File file) throws IOException
	{
		Frame newFrame = new Frame(ImageIO.read(file));
		lastFrame.setNext(newFrame);
		lastFrame = newFrame;
		lastFrame.setNext(firstFrame);
	}

	/** Gets the actual image object for the current frame
	 * 
	 * @return the image object being displayed */
	public Image getImage()
	{
		return currentFrame.getImage();
	}

	/** Gets the width of the current image being displayed
	 * 
	 * @param observer the observer that the image is being displayed on
	 * @return the width of the image */
	public int getWidth(ImageObserver observer)
	{
		return currentFrame.getImage().getWidth(observer);
	}

	/** Gets the height of the current image being displayed
	 * 
	 * @param observer the observer that the image is being displayed on
	 * @return the height of the image */
	public int getHeight(ImageObserver observer)
	{
		return currentFrame.getImage().getHeight(observer);
	}


	/** Draws the frame at its default size at a point
	 * 
	 * @param g the graphics to draw with
	 * @param topLeft the top left corner coordinate of the image
	 * @param container the container to draw on */
	public void draw(Graphics g, Point topLeft, Container container)
	{
		draw(g, topLeft.x, topLeft.y, container);
	}

	/** Draws the frame at its default size at a point
	 * 
	 * @param g the graphics to draw with
	 * @param x the x coordinate of the top left corner of the image
	 * @param y the y coordinate of the top left corner of the image
	 * @param container the container to draw on */
	public void draw(Graphics g, int x, int y, Container container)
	{
		currentFrame.draw(g, x, y, container);
		if (!stopped)
			animate();
	}

	/** Draws a sprite at the specified height at a specified point
	 * 
	 * @param g the graphics to draw with
	 * @param x the x coordinate to draw at
	 * @param y the y coordinate to draw at
	 * @param width the width to draw the image at
	 * @param height the height to draw the image at
	 * @param container the container to draw the image on */
	public void draw(Graphics g, int x, int y, int width, int height,
			Container container)
	{
		currentFrame.draw(g, x, y, width, height, container);
		if (!stopped)
			animate();
	}

	/** Draws a sprite at the specified height at a specified point
	 * 
	 * @param g the graphics to draw with
	 * @param topLeft the top left corner of the image
	 * @param width the width to draw the image at
	 * @param height the height to draw the image at
	 * @param container the container to draw the image on */
	public void draw(Graphics g, Point topLeft, int width, int height,
			Container container)
	{
		draw(g, topLeft.x, topLeft.y, width, height, container);
	}

	/** Draws an image at the default location of the top left corner of the
	 * screen
	 * 
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		draw(g, 0, 0, container);
	}

}
