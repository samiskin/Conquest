package Board;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/** A frame of a sprite animation, storing the next frame to travel through
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class Frame
{

	private BufferedImage img;
	private Frame next;


	/** Creates a frame from a single image, linking to itself as the next frame
	 * 
	 * @param img the image of the frame */
	public Frame(BufferedImage img)
	{
		this.img = img;
		next = this;
	}

	/** Creates a frame and the frame that comes after it
	 * 
	 * @param img the image of the frame
	 * @param nextFrame the frame that follows */
	public Frame(BufferedImage img, Frame nextFrame)
	{
		this.img = img;
		next = nextFrame;
	}


	/** Sets the next frame of this frame
	 * 
	 * @param nextFrame the frame for this frame ot link to */
	public void setNext(Frame nextFrame)
	{
		next = nextFrame;
	}

	/** Gets the next frame
	 * 
	 * @return the frame that follows this one */
	public Frame getNext()
	{
		return next;
	}

	/** Gets the image that this frame draws
	 * 
	 * @return */
	public Image getImage()
	{
		return img;
	}


	/** Draws the image at a certain location
	 * 
	 * @param g the graphics to draw with
	 * @param x the x coordinate to draw at
	 * @param y the y coordinate to draw at
	 * @param container the container to draw on */
	public void draw(Graphics g, int x, int y, Container container)
	{
		g.drawImage(img, x, y, x + img.getWidth(), y + img.getHeight(), 0, 0,
				img.getWidth(), img.getHeight(), container);
	}

	/** Draws the image at a certain location at a specific size
	 * 
	 * @param g the graphics to draw with
	 * @param x the x coordinate to draw at
	 * @param y the y coordinate to draw at
	 * @param width the width of the image
	 * @param height the height the image
	 * @param container the container to draw on */
	public void draw(Graphics g, int x, int y, int width, int height,
			Container container)
	{
		g.drawImage(img, x, y, x + width, y + height, 0, 0, img.getWidth(),
				img.getHeight(), container);
	}

}
