package Menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import UIElements.CircleButton;
import UIElements.RectangleButton;
import UIElements.TextButton;

import Board.Sprite;
import Board.StatSet;
import Board.Unit;
import Board.UnitEntry;
import Game.Player;

/** A menu that allows the user to select units to play
 * @author Shiranka Miskin
 * @version Janunary 2013 */
public class UnitMenu extends Menu
{

	private Player player;
	private static TreeSet<UnitEntry> units;
	private static Map<RectangleButton, UnitEntry> buttons;
	private ArrayList<UnitEntry> selectedUnits;
	private ArrayList<RectangleButton> cancelButtons;
	private CircleButton goButton;
	private TextButton title;
	private static Dimension thumbnailSize = new Dimension(128, 128);
	private static int unitsPerRow;
	private static int maxUnits = 4;
	private Sprite statDisplay;
	private Font statFont;
	private RectangleButton hoverButton;

	/** Creates the new unit menu
	 * @param size the size of the menu
	 * @param player the player that is picking
	 * @throws IOException */
	public UnitMenu(Dimension size, Player player) throws IOException
	{
		super("Map.jpg", size);
		this.player = player;
		init();
		cancelButtons = new ArrayList<RectangleButton>();

		title = new TextButton(player.getName(), Main.getFont("Kalinga", 18),
				new Point(565, 40), new Dimension(150, 40), Color.white,
				new Color(0, 0, 0, 0));
		title.setBorderWidth(2);
		title.setBorderOffset(2);
		title.setTextColor(Color.black);
		title.setBorderColor(player.getColor());

		goButton = new CircleButton(new Point(640, 440), 12);
		goButton.setHoverColor(new Color(141, 255, 40));

		statDisplay = new Sprite("res/UI/StatDisplay.gif");
		statFont = Main.getFont("Kalinga", 16);
	}

	/** Resets the menu */
	public void reset()
	{
		super.reset();
		selectedUnits = new ArrayList<UnitEntry>();
	}

	/** Resets the amount of units, and if the player is computer controlled,
	 * randomly select units and proceed */
	private void init()
	{
		selectedUnits = new ArrayList<UnitEntry>();

		if (!player.isHuman())
		{
			ArrayList<UnitEntry> unitTypes = new ArrayList<UnitEntry>(units);
			for (int i = 0; i < maxUnits; i++)
			{
				player.addUnit(new Unit(
						unitTypes.get((int) (Math.random() * maxUnits)), player));
			}
			completed = true;
		}
	}

	/** Loads all the units and the displays This might use large files and so is
	 * made static and done once
	 * @throws IOException */
	public static void loadUnitDisplay()
	{
		units = new TreeSet<UnitEntry>();
		for (UnitEntry unit : UnitEntry.values())
			units.add(unit);
		buttons = new HashMap<RectangleButton, UnitEntry>();

		Point center = new Point(640, 265);

		// Calculate the placements of the unit portraits
		unitsPerRow = 5;
		int spaceBetweenRows = 20;
		int spaceBetweenColumns = 20;
		int columns = Math.min(unitsPerRow, units.size());
		int rows = (int) Math.ceil(1.0 * units.size() / unitsPerRow);
		int width = columns * (thumbnailSize.width + spaceBetweenColumns)
				- spaceBetweenColumns;
		int height = rows * (thumbnailSize.height + spaceBetweenRows)
				- spaceBetweenRows;

		// Create all the buttons on the screen
		Point buttonLoc = new Point(center.x - width / 2, center.y - height / 2);
		for (UnitEntry unit : units)
		{
			RectangleButton button = new RectangleButton(new Point(buttonLoc),
					thumbnailSize, unit.getPortrait());
			button.setBorderWidth(3);

			buttons.put(button, unit);
			if (buttons.size() % unitsPerRow == 0)
			{
				buttonLoc.translate(-width + thumbnailSize.width,
						thumbnailSize.height + spaceBetweenRows);
			} else
				buttonLoc.translate(thumbnailSize.width + spaceBetweenColumns,
						0);
		}

	}

	/** Resets the location of all the selected units */
	private void setSelectionLocations()
	{
		Point center = new Point(640, 539);
		int spaceBetweenRows = 20;
		int width = selectedUnits.size()
				* (thumbnailSize.width + spaceBetweenRows) - spaceBetweenRows;
		Point buttonLoc = new Point(center.x - width / 2, center.y
				- thumbnailSize.height / 2);
		for (int unit = 0; unit < selectedUnits.size(); unit++)
		{
			cancelButtons.get(unit).setLocation(buttonLoc);
			buttonLoc.translate(thumbnailSize.width + spaceBetweenRows, 0);
		}
	}

	/** Adds a unit to the current selections
	 * @param unit the unit that was selected */
	private void addSelection(UnitEntry unit)
	{
		selectedUnits.add(unit);
		cancelButtons.add(new RectangleButton(new Point(), thumbnailSize, unit
				.getPortrait()));
		setSelectionLocations();
	}

	/** Removes a selection from the current selections
	 * @param button the button that was clicked */
	private void removeSelection(RectangleButton button)
	{
		selectedUnits.remove(cancelButtons.indexOf(button));
		cancelButtons.remove(button);
		setSelectionLocations();
	}

	/** Draws the box that displays the unit's stats if the user's mouse is
	 * hovering over it on the unit selection area.
	 * @param g
	 * @param container */
	private void drawStatDisplay(Graphics g, Container container)
	{
		if (hoverButton != null)
		{
			// Set the display to draw right underneath the button being
			// hovered over
			int x = hoverButton.getLocation().x;
			int y = hoverButton.getLocation().y + thumbnailSize.height + 3;

			statDisplay.draw(g, x, y, container);

			StatSet stats = buttons.get(hoverButton).getStats();

			g.setFont(statFont);
			g.setColor(Color.black);
			g.drawString("" + stats.getAttack(), x + 30, y + 20);
			g.drawString("" + stats.getMovement(), x + 30, y + 40);

			// Make sure the right display is right-aligned
			// The stats do not exceed 99, therefore there isn't
			// any need for dealing with 3 or more digit numbers
			if (stats.getHealth() >= 10)
				g.drawString("" + stats.getHealth(), x + 78, y + 20);
			else
				g.drawString("" + stats.getHealth(), x + 86, y + 20);
			if (stats.getRange() >= 10)
				g.drawString("" + stats.getRange(), x + 78, y + 40);
			else
				g.drawString("" + stats.getRange(), x + 86, y + 40);

		}
	}

	/** Draws the menu on the screen
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		super.draw(g, container);

		// Draw the buttons
		for (RectangleButton button : buttons.keySet())
			button.draw(g, container);
		for (RectangleButton button : cancelButtons)
			button.draw(g, container);

		title.draw(g, container);
		goButton.draw(g, container);

		// Draw the lines that appear beside the go button
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(2f));
		int lineLength = 325;
		int spaceFromButton = 20;
		Point center = goButton.getCenter();
		g2.drawLine(center.x - spaceFromButton - lineLength, center.y, center.x
				- spaceFromButton, center.y);
		g2.drawLine(center.x + spaceFromButton, center.y, center.x
				+ spaceFromButton + lineLength, center.y);

		drawStatDisplay(g, container);

	}

	/** Handles mouse movement
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		hoverButton = null;

		// Check if any of the units in the selection area need to
		// be outlined in green to show they can be selected
		for (RectangleButton button : buttons.keySet())
		{
			if (button.getMouseMovement(event))
			{
				button.setBorderColor(Color.GREEN);
				hoverButton = button;
			} else
				button.setBorderColor(Color.white);
		}

		// Check if any of the units that have already been selected
		// need to be outlined in red to show they can be removed
		for (RectangleButton button : cancelButtons)
		{
			if (button.getMouseMovement(event))
				button.setBorderColor(Color.RED);
			else
				button.setBorderColor(Color.white);
		}

		goButton.getMouseMovement(event);
	}

	/** Handles mouse clicks
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		// Check the selection buttons to see if a unit needs to be
		// added to the current number of selections
		for (RectangleButton button : buttons.keySet())
		{
			if (button.getMousePress(event) && selectedUnits.size() < maxUnits)
				addSelection(buttons.get(button));
			else
				button.setClicked(false);
		}

		// The removal has to be done after the for each loop to avoid
		// a concurrent modification exception
		RectangleButton selected = null;
		for (RectangleButton button : cancelButtons)
		{
			if (button.getMousePress(event))
				selected = button;
			else
				button.setClicked(false);
		}
		if (selected != null)
			removeSelection(selected);

		if (goButton.getMousePress(event))
		{
			// Only allow the user to proceed if one or more units
			// have been selected
			if (!selectedUnits.isEmpty())
			{
				for (UnitEntry unitEntry : selectedUnits)
					player.addUnit(new Unit(unitEntry, player));
				completed = true;
			} else
				goButton.reset();
		}
	}

}
