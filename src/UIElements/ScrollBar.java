package UIElements;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/** Scroll Bar allows the user to drag a bar around within a boundary.
 * @author Shiranka Miskin
 * @version January 2013 */
public class ScrollBar extends DraggableRectangle
{
	
	private Rectangle bounds;

	/** Constructor for a scroll bar object
	 * 
	 * @param bar the rectangle the bar takes up
	 * @param bounds the boundries the bar can be dragged to */
	public ScrollBar(Rectangle bar, Rectangle bounds)
	{
		super(bar, null, null);
		this.bounds = bounds;
	}

	/** Constructor for a scroll bar object given colours
	 * 
	 * @param bar the rectangle the bar takes up
	 * @param bounds the boundaries the bar can be dragged to
	 * @param normal the colour to set the bar to normally
	 * @param hover the colour to set the bar to when hovered over */
	public ScrollBar(Rectangle bar, Rectangle bounds, Color normal, Color hover)
	{
		this(bar, bounds);
		setNormalColor(normal);
		setHoverColor(normal);
	}

	/** Returns the difference between the location of the scroll bar and it's
	 * bounds
	 * 
	 * @return a point which contains how far away the bar is from the bounds */
	public Point getDifference()
	{
		return new Point((offset.x + box.x) - bounds.x, (offset.y + box.y)
				- bounds.y);
	}

	/** Resets the scrollbar and places the bar at the top left of its
	 * rectangular boundary */
	public void reset()
	{
		super.reset();
		offset = new Point();
		box.x = bounds.x;
		box.y = bounds.y;
	}

	/** Draw method for Scroll Bar objects
	 * 
	 * @param g Graphics
	 * @param container the container to draw to */
	public void draw(Graphics g, Container container)
	{
		super.draw(g, container);
	}

	/** Given a mouse event, and given that the mouse is holding this scroll bar,
	 * move the scroll bar based on the mouse movement
	 * 
	 * @param event a mouse event given by a mouse listener
	 * @return true if the scroll bar has been dragged, false if not */
	public boolean getMouseDragged(MouseEvent event)
	{
		if (clicked)
		{
			offset.x = event.getPoint().x - clickLocation.x;
			offset.y = event.getPoint().y - clickLocation.y;
			if (offset.y + box.y + box.height > bounds.getMaxY())
				offset.y = (int) (bounds.getMaxY() - box.height - box.y);
			else if (offset.y + box.y < bounds.getMinY())
				offset.y = bounds.y - box.y;

			if (offset.x + box.x + box.width > bounds.getMaxX())
				offset.x = (int) (bounds.getMaxX() - box.width - box.x);
			else if (offset.x + box.x < bounds.getMinX())
				offset.x = bounds.x - box.x;
			return true;
		}
		return false;
	}

}
