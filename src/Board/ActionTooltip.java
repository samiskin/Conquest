package Board;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import Board.Action.Type;

/** A tooltip that displays all the abilities that a unit can use and allows the
 * user to pick a selection
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class ActionTooltip
{
	private ArrayList<Action> abilities;
	private ArrayList<Rectangle> selectionBoxes;
	private static final Dimension size = new Dimension(128, 32);
	private Sprite img;
	private Sprite hoverImg;
	private Point offset;
	private Action selected;
	private Action hover;
	private static Font font = new Font("Helvetica", Font.BOLD, 12);

	/** Creates a tooltip for a specified unit
	 * 
	 * @param unit the unit the tooltip is displaying
	 * @param topLeft the top left point of the tooltip on the board itself (not
	 *            the screen)
	 * @param offset the current offset between the board and the screen display */
	public ActionTooltip(Unit unit, Point topLeft, Point offset)
	{
		abilities = new ArrayList<Action>();
		selectionBoxes = new ArrayList<Rectangle>();

		// Add all the abilities of the unit as well as an option
		// to cancel the unit's turn
		abilities.addAll(unit.getAbilities());
		abilities.add(new Action(Type.NOTHING, unit));
		// Adds all the rectangles as a drop down menu
		for (int box = 0; box < abilities.size(); box++)
		{
			Point p = new Point(topLeft.x - offset.x, topLeft.y - offset.y
					+ box * size.height);
			selectionBoxes.add(new Rectangle(p, size));
		}

		// This step only needs to be done once when the image is null
		// because the variable is a static variable
		if (img == null)
			try
			{
				img = new Sprite("res/UI/TooltipRect.png");
				hoverImg = new Sprite("res/UI/TooltipRectSelected.png");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		this.offset = offset;
	}

	/** Gets the dimensions of a single selection box
	 * 
	 * @return The dimension of a selection box */
	public static Dimension getBoxSize()
	{
		return size;
	}

	/** Gets the dimensions of the entire tooltip, factoring in every selection
	 * box for each of the abilities
	 * 
	 * @return the dimensions of the entire tooltip */
	public Dimension getFullSize()
	{
		return new Dimension(size.width, size.height * abilities.size());
	}

	/** Gets the selected action
	 * 
	 * @return the selected action if one has been selected, null if not */
	public Action getSelected()
	{
		return selected;
	}

	/** Gets the action that the user's mouse is currently hovering over
	 * 
	 * @return The action if the user's mouse is hovering over one, null if they
	 *         are not */
	public Action getHover()
	{
		return hover;
	}

	/** Draws the tooltip on the screen
	 * 
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		for (int box = 0; box < abilities.size(); box++)
		{

			// Relocates the display based on the current offset of the board
			Point boxLocation = selectionBoxes.get(box).getLocation();
			boxLocation.translate(offset.x, offset.y);

			// Draws a special box for the one the user is currently hovering
			// their mouse over on the screen
			if (hover == abilities.get(box))
				hoverImg.draw(g, boxLocation, container);
			else
				img.draw(g, boxLocation, container);

			// Draws the respective icon for the ability
			abilities.get(box).getType().getIcon()
					.draw(g, boxLocation.x + 6, boxLocation.y + 6, container);

			// Writes the name of the ability
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawString(abilities.get(box).getName(), boxLocation.x + 48,
					boxLocation.y + 22);
		}

	}

	/** Detects if the user has clicked on an option or not
	 * 
	 * @param event The mouse input */
	public void getMouseInput(MouseEvent event)
	{

		// Moves the detected location to account for board offset on the screen
		Point p = event.getPoint();
		p.x -= offset.x;
		p.y -= offset.y;

		// Checks for which ability the user clicked on
		if (event.getButton() == 1)
			for (int box = 0; box < selectionBoxes.size(); box++)
				if (selectionBoxes.get(box).contains(p))
					selected = abilities.get(box);
	}

	/** Detects if the user is hovering their mouse over an option
	 * 
	 * @param event the mouse option */
	public void getMouseMovement(MouseEvent event)
	{

		// Moves the detected location to account for board offset on the screen
		Point p = event.getPoint();
		p.x -= offset.x;
		p.y -= offset.y;

		// Resets the current hover position to account for no ability being
		// selected
		hover = null;

		// Checks for which ability the user clicked on
		for (int box = 0; box < selectionBoxes.size(); box++)
			if (selectionBoxes.get(box).contains(p))
				hover = abilities.get(box);
	}

}