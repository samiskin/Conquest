package UIElements;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import Board.Sprite;

/** Rectangle button class, which acts like a DraggableRectangle
 * however it does not respond to dragging
 * @author Shiranka Miskin
 * @version January 2013 */
public class RectangleButton extends DraggableRectangle
{
	/** Constructor for a rectangle button
	 * @param topLeft the top left corner of the rectangle
	 * @param size the size of the rectangle
	 * @param img the image to place on the rectangle
	 * @param hoverImg the image to place on the rectangle when something is
	 *            hovering over it */
	public RectangleButton(Point topLeft, Dimension size, Sprite img,
			Sprite hoverImg)
	{
		super(topLeft, size, img, hoverImg);
	}

	/** Constructor for a rectangle button
	 * @param topLeft the top left corner of the rectangle
	 * @param size the size of the rectangle
	 * @param img the image to place on the rectangle */
	public RectangleButton(Point topLeft, Dimension size, Sprite img)
	{
		super(topLeft, size, img, img);
	}

	/** Empty because buttons are not draggable.
	 * @param event a mouse even given by a mouse listener
	 * @return false as a rectangle button cannot be dragged */
	public boolean getMouseDragged(MouseEvent event)
	{
		return false;
	}

}
