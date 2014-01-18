package UIElements;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import Board.Sprite;

/** Draggable Rectangle class for rectangles that can be dragged
 * across the screen by the mouse
 * @author Shiranka Miskin
 * @version December 2012 */
public class DraggableRectangle extends Draggable
{

	protected Rectangle box;
	protected Sprite img;
	protected Sprite hoverImg;
	protected int borderOffset;
	protected int borderWidth;
	protected Color borderColor;

	/** Constructor for a Draggable Rectangle object
	 * @param topLeft the top left corner of the rectangle
	 * @param size the size of the rectangle
	 * @param img the image to place on the rectangle
	 * @param hoverImg the image to place on the rectangle when something is
	 *            hovering over it */
	public DraggableRectangle(Point topLeft, Dimension size, Sprite img,
			Sprite hoverImg)
	{
		super(topLeft);
		box = new Rectangle(topLeft, size);
		this.img = img;
		this.hoverImg = hoverImg;
		init();
	}

	/** Constructor for a Draggable Rectangle given a rectangle object instead of
	 * a topLeft and size
	 * @param rect the rectangle to make into this draggable rectangle
	 * @param img the image to place on the rectangle
	 * @param hoverImg the image to place on the rectangle when something is
	 *            hovering over it */
	public DraggableRectangle(Rectangle rect, Sprite img, Sprite hoverImg)
	{
		super(rect.getLocation());
		box = rect;
		this.img = img;
		this.hoverImg = img;
		init();
	}

	/** Initializes the draggable rectangle by resetting core variables */
	private void init()
	{
		borderOffset = 0;
		borderWidth = 2;
		borderColor = Color.white;
		clickLocation = new Point();
	}

	/** Returns the size of the box
	 * @return the size of the rectangle */
	public Dimension getSize()
	{
		return box.getSize();
	}

	/** Makes the rectangle transparent (so that only the image shows) */
	public void setTransparent()
	{
		super.setTransparent();
		setBorderColor(new Color(0, 0, 0, 0));
	}

	/** Sets the colour of the border of this draggable rectangle
	 * @param color the colour to set the border colour to */
	public void setBorderColor(Color color)
	{
		borderColor = color;
	}

	/** Sets the width of the border of this draggable rectangle
	 * @param width the width to set the border width to */
	public void setBorderWidth(int width)
	{
		borderWidth = width;
	}

	/** Sets the offset of the border of this draggable rectangle
	 * @param offset the offset to set the border offset to */
	public void setBorderOffset(int offset)
	{
		borderOffset = offset;
	}

	/** Sets the image of this draggable rectangle given a sprite
	 * @param sprite the image to set the image to */
	public void setImg(Sprite sprite)
	{
		img = sprite;
		hoverImg = sprite;
	}

	/** Sets the image of this draggable rectangle given two sprites for the
	 * normal and hover image
	 * @param imgSprite the image to set the normal image to
	 * @param hoverSprite the image to set the hover image to */
	public void setImg(Sprite imgSprite, Sprite hoverSprite)
	{
		img = imgSprite;
		hoverImg = hoverSprite;
	}

	/** Checks to see if the given point is contained in this draggable rectangle
	 * @return true if the point is in, false if the point is out */
	public boolean contains(Point p)
	{
		return box.contains(p);
	}

	/** Moves the draggable rectangle given a new location in two ints
	 * @param dx the new X position of the rectangle
	 * @param dy the new Y position of the rectangle */
	public void translate(int dx, int dy)
	{
		super.translate(dx, dy);
		box.translate(dx, dy);
	}

	/** Sets the location of the draggable rectangle given a point
	 * @param p the new point to set the location of this rectangle to */
	public void setLocation(Point p)
	{
		super.setLocation(p);
		box.setLocation(p);
	}

	public Point getLocation()
	{
		return box.getLocation();
	}

	/** Draw method for a draggable rectangle
	 * @param g Graphics
	 * @param container the container to draw to */
	public void draw(Graphics g, Container container)
	{

		translate(offset.x, offset.y);

		if (mouseOver || clicked)
			g.setColor(hoverColor);
		else
			g.setColor(normalColor);
		g.fillRect(box.x, box.y, box.width, box.height);

		if (img != null)
		{
			if (mouseOver || clicked)
				hoverImg.draw(g, box.getLocation(), container);
			else
				img.draw(g, box.getLocation(), container);
		}

		g.setColor(borderColor);
		for (int border = borderOffset; border <= borderWidth + borderOffset; border++)
		{
			g.drawRect(box.x + border, box.y + border, box.width - 2 * border,
					box.height - 2 * border);
		}

		translate(-offset.x, -offset.y);
	}

}
