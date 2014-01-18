package UIElements;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

/** Clickable abstract class that is used by any object that allows the mouse to
 * click it. Changes color when the mouse is hovering over it or clicking it
 * @author Shiranka Miskin
 * @version December 2012 */
public abstract class Clickable
{


	protected Point topLeft;
	protected Color normalColor;
	protected Color hoverColor;
	protected boolean mouseOver;
	protected boolean clicked;

	/** Constructor for a clickable object
	 * 
	 * @param topLeft the top left corner of a clickable object */
	public Clickable(Point topLeft)
	{
		this.topLeft = topLeft;
		mouseOver = false;
		clicked = false;
		normalColor = new Color(235, 235, 235, 255);
		hoverColor = new Color(239, 167, 7, 255);
	}

	/** Constructor for a clickable object if specific colours are given
	 * 
	 * @param topLeft the top left corner of the clickable object
	 * @param normal the colour it will be normally
	 * @param hover the colour it will be when the mouse is over it */
	public Clickable(Point topLeft, Color normal, Color hover)
	{
		this(topLeft);
		normalColor = normal;
		hoverColor = hover;
	}

	/** Constructor for a clickable object given only a hover colour, leaving the
	 * normal color as default
	 * 
	 * @param topLeft the top left corner of the clickable object
	 * @param hover the colour it will be when the mouse is over it */
	public Clickable(Point topLeft, Color hover)
	{
		this(topLeft);
		hoverColor = hover;
	}

	/** Sets the normal colour of a given clickable to a value
	 * 
	 * @param color the colour to set this clickable's normal colour to */
	public void setNormalColor(Color color)
	{
		normalColor = color;
	}

	/** Sets the hover colour of a given clickable to a value
	 * 
	 * @param color the colour to set this clickable's hover colour to */
	public void setHoverColor(Color color)
	{
		hoverColor = color;
	}

	/** Sets the colors to transparent to make the button hidden */
	public void setTransparent()
	{
		normalColor = new Color(0, 0, 0, 0);
		hoverColor = new Color(0, 0, 0, 0);
	}

	/** Returns the normal colour of the clickable
	 * 
	 * @return the normal colour of the clickable */
	public Color getNormalColor()
	{
		return normalColor;
	}

	/** Returns the hover colour of the clickable
	 * 
	 * @return the hover colour of the clickable */
	public Color getHoverColor()
	{
		return hoverColor;
	}

	/** Abstract class to see if a point is contained within a button since
	 * buttons have different dimensions (circle, rectangle, etc)
	 * 
	 * @param p the point to check if it is inside the button or not
	 * @return true if the point is inside, false if the point is outside */
	public abstract boolean contains(Point p);

	/** Moves this clickable to another location on the screen
	 * 
	 * @param dx the new X of the button
	 * @param dy the new Y of the button */
	public void translate(int dx, int dy)
	{
		topLeft.translate(dx, dy);
	}

	/** Sets the location of this clickable given a point
	 * 
	 * @param p the point to set the location of this object to */
	public void setLocation(Point p)
	{
		topLeft = p;
	}

	/** Returns the location of the button
	 * 
	 * @return the location of the button */
	public Point getLocation()
	{
		return topLeft;
	}

	/** Given a mouse event, check and return if the mouse is over this clickable
	 * 
	 * @param event a mouse event given by a mouse listener
	 * @return true if the mouse is within the clickable, false if not */
	public boolean getMouseMovement(MouseEvent event)
	{
		mouseOver = contains(event.getPoint());
		return mouseOver;
	}

	/** Given a mouse event, check and return if the mouse has clicked on this
	 * button
	 * 
	 * @param event a mouse event given by a mouse listener
	 * @return true if the button is clicked, false if not */
	public boolean getMousePress(MouseEvent event)
	{
		if (contains(event.getPoint()))
		{
			clicked = !clicked;
		}
		return contains(event.getPoint());
	}

	/** Returns if the button has already been clicked or not
	 * 
	 * @return true if the button has been clicked, false if not. */
	public boolean isClicked()
	{
		return clicked;
	}

	/** Set whether this button has been clicked or not
	 * 
	 * @param click true to set the button to be clicked, false to set to not
	 *            have been clicked. */
	public void setClicked(boolean click)
	{
		clicked = click;
	}

	/** Sets this button to have not been clicked */
	public void reset()
	{
		clicked = false;
	}

	/** Abstact draw method, since different buttons are different shapes
	 * 
	 * @param g the graphics context to use when drawing
	 * @param container the container to draw to */
	public abstract void draw(Graphics g, Container container);

}
