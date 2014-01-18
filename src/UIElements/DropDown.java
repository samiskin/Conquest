package UIElements;

import java.util.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

import Menu.Main;

/** DropDown class for a drop down menu which allows the user to select from a
 * list of options
 * @author Shiranka Miskin
 * @version January 2013 */
public class DropDown
{

	private TreeSet<TextButton> options;
	private TextButton currentSelection;
	private boolean expanded;
	private Font font;
	private Dimension size;
	private Point bottomLeft;
	private Point topLeft;
	private Color hover;

	/** Constructor for a DropDown object
	 * @param base the base text of the Drop Down menu
	 * @param font the font of the object
	 * @param p the position of the object given a point as its top left corner
	 * @param size the size of the object */
	public DropDown(String base, Font font, Point p, Dimension size)
	{
		options = new TreeSet<TextButton>();
		currentSelection = new TextButton(base, font, p, size);
		topLeft = new Point(p);
		bottomLeft = new Point(p.x, p.y + size.height);
		this.size = size;
		expanded = false;
		this.font = font;
		hover = currentSelection.getHoverColor();
	}

	/** Constructor for a DropDown object with a given hover colour
	 * @param base the base text of the Drop Down menu
	 * @param font the font of the object
	 * @param p the position of the object given a point as its top left corner
	 * @param size the size of the object
	 * @param hover the colour to make the Drop Down when it is hovered over */
	public DropDown(String base, Font font, Point p, Dimension size, Color hover)
	{
		this(base, font, p, size);
		this.hover = hover;
		currentSelection.setHoverColor(hover);

	}

	/** Constructor for a DropDown object without a given font
	 * @param base the base text of the Drop Down menu
	 * @param p the position of the object given a point as its top left corner
	 * @param size the size of the object
	 * @param hover the colour to make the Drop Down when it is hovered over */
	public DropDown(String base, Point p, Dimension size, Color hover)
			throws IOException
	{
		this(base, Main.getFont("Kalinga", 18), p, size, hover);
	}

	/** Constructor for a DropDown object without a font or hover colour
	 * @param base the base text of the Drop Down menu
	 * @param p the position of the object given a point as its top left corner
	 * @param size the size of the object */
	public DropDown(String base, Point p, Dimension size) throws IOException
	{
		this(base, Main.getFont("Kalinga", 18), p, size);
	}

	/** Add an option to the dropdown menu
	 * @param str the string to put as the option */
	public void addOption(String str)
	{
		TextButton newButton = new TextButton(str, font, bottomLeft, size);
		newButton.setHoverColor(hover);
		options.add(newButton);
		setLocations();
	}

	/** Set the value of the drop down menu to your current selection
	 * @param button the old selection of this menu */
	private void setSelection(TextButton button)
	{
		// remove the old selection
		options.remove(button);
		// set the value to current selection
		options.add(currentSelection);
		currentSelection = button;
		setLocations();
		currentSelection.setLocation(topLeft);

	}

	/** Returns which button is currently selected
	 * @return which button is currently selected */
	public TextButton getSelected()
	{
		return currentSelection;
	}

	/** Sets the locations of all the drop down options given the top left (for
	 * when the drop down menu is expanded) */
	private void setLocations()
	{
		currentSelection.setLocation(topLeft);
		for (TextButton button : options)
		{
			button.setLocation(bottomLeft);
			bottomLeft.translate(0, size.height);
		}
		bottomLeft = new Point(topLeft.x, topLeft.y + size.height);
	}

	/** Resets the drop down menu (collapses it) */
	public void reset()
	{
		expanded = false;
		currentSelection.setClicked(false);
	}

	/** Draw method for a Drop Down menu
	 * @param g Graphics
	 * @param container the container to draw it to */
	public void draw(Graphics g, Container container)
	{
		currentSelection.draw(g, container);

		if (expanded)
		{
			for (TextButton option : options)
			{
				option.draw(g, container);
			}
		}
	}

	/** Given a mouse event, check and return if the mouse has clicked on this
	 * Drop Down menu
	 * @param event a mouse event given by a mouse listener
	 * @return true if the Drop Down menu is clicked, false if not */
	public boolean getMousePress(MouseEvent event)
	{
		if (!expanded)
		{
			if (currentSelection.getMousePress(event))
			{
				expanded = true;
				return true;
			}
		} else
		{
			TextButton selected = null;
			for (TextButton button : options)
			{
				if (button.getMousePress(event))
				{
					selected = button;
				}
			}
			if (selected != null)
			{
				currentSelection.setClicked(false);
				selected.setClicked(false);
				setSelection(selected);
				expanded = false;
				return true;
			}
			if (currentSelection.getMousePress(event))
			{
				expanded = false;
				currentSelection.setClicked(false);
				return true;
			}
		}
		return false;
	}

	/** Given a mouse event, check and return if the mouse is over this Drop Down
	 * @param event a mouse event given by a mouse listener
	 * @return true if the mouse is within the Drop Down, false if not */
	public void getMouseMovement(MouseEvent event)
	{
		currentSelection.getMouseMovement(event);
		if (expanded)
			for (TextButton button : options)
				button.getMouseMovement(event);
	}

}
