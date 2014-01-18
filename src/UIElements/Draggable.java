package UIElements;

import java.awt.Point;
import java.awt.event.MouseEvent;

/** Draggable abstract class, for objects that can be dragged and moved by the
 * mouse
 * @author Shiranka Miskin
 * @version December 2012 */
public abstract class Draggable extends Clickable
{

	protected Point offset;
	protected Point clickLocation;

	/** Constructor for a new draggable object
	 * 
	 * @param topLeft the top left location of the button */
	public Draggable(Point topLeft)
	{
		super(topLeft);
		offset = new Point();
	}

	/** Given a mouse event of being released, cement the translation by
	 * assigning the new location to the stored coordinates
	 * @param event a mouse event given by a mouse listener */
	public void getMouseRelease(MouseEvent event)
	{
		translate(offset.x, offset.y);
		offset = new Point();
		clicked = false;
	}

	/** Given a mouse event, check to see if this draggable is clicked or not
	 * 
	 * @param event a mouse event given by a mouse listener */
	public boolean getMousePress(MouseEvent event)
	{
		if (super.getMousePress(event))
		{
			clickLocation = event.getPoint();
			return true;
		}
		return false;
	}

	/** Given a mouse event, and given that the mouse is holding this draggable,
	 * move the draggable based on the mouse movement
	 * 
	 * @param event a mouse event given by a mouse listener
	 * @return true if the draggable object was dragged, false if not */
	public boolean getMouseDragged(MouseEvent event)
	{
		if (clicked)
		{
			offset.setLocation(event.getPoint().x - clickLocation.x,
					event.getPoint().y - clickLocation.y);
			return true;
		}
		return false;

	}

}
