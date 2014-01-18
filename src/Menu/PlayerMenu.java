package Menu;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import UIElements.CircleButton;
import UIElements.DropDown;
import Game.Computer;
import Game.Human;
import Game.Player;

/** A menu that lets the user select whether the player will be a human
 * controlled player, computer controlled, or none
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class PlayerMenu extends Menu
{

	private ArrayList<Player> players;
	private ArrayList<Color> playerColors;
	private Point center;
	private ArrayList<DropDown> dropDowns;
	private CircleButton goButton;
	private static Dimension menuSize = new Dimension(150, 40);
	private int maxPlayers;


	/** Creates the player menu
	 * 
	 * @param size the size of the menu
	 * @throws IOException */
	public PlayerMenu(Dimension size) throws IOException
	{
		super(size);
		players = new ArrayList<Player>();
		dropDowns = new ArrayList<DropDown>();
		center = new Point(size.width / 2, size.height / 2);

		goButton = new CircleButton(center, 12);
		goButton.setHoverColor(new Color(141, 255, 40));
	}

	/** Resets the menu */
	public void reset()
	{
		super.reset();
		players.clear();
		dropDowns = new ArrayList<DropDown>();
		init();
		goButton.setClicked(false);
	}

	/** Sets the maximum players allowed
	 * 
	 * @param max the maximum number of players */
	public void setMaxPlayers(int max)
	{
		maxPlayers = max;
	}

	/** Initializes the selections */
	public void init()
	{
		dropDowns.clear();
		playerColors = getUniqueColors(maxPlayers);
		double angle = 2 * Math.PI / maxPlayers;
		int radius = 200;
		for (int i = 1; i <= maxPlayers; i++)
		{
			Point p = rotatePoint(new Point(center.x, center.y - radius),
					center, angle * i);
			p.translate(-menuSize.width / 2, -menuSize.height / 2);
			DropDown menu = null;
			try
			{
				menu = new DropDown("Human", p, menuSize,
						playerColors.get(dropDowns.size()));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			menu.addOption("Computer");
			menu.addOption("None");
			dropDowns.add(menu);
		}

	}

	/** Creates a specified number of unique colors. Algorithm copied from
	 * http://
	 * stackoverflow.com/questions/3403826/how-to-dynamically-compute-a-list
	 * -of-colors
	 * 
	 * @param amount the number of colors to generate
	 * @return the list of the colors */
	private ArrayList<Color> getUniqueColors(int amount)
	{
		ArrayList<Color> colors = new ArrayList<Color>(amount);

		// The colors are kept dark so that the white text
		// is always able to be shown
		final int lowerLimit = 0x10;
		final int upperLimit = 0xE0;
		final int colorStep = (int) ((upperLimit - lowerLimit) / Math.pow(
				amount, 1f / 3));

		for (int R = lowerLimit; R < upperLimit; R += colorStep)
			for (int G = lowerLimit; G < upperLimit; G += colorStep)
				for (int B = lowerLimit; B < upperLimit; B += colorStep)
				{
					if (colors.size() >= amount)
					{ // The calculated step is not very precise, so this
						// safeguard is appropriate
						return colors;
					} else
					{
						Color color = new Color(R, G, B);
						colors.add(color);
					}
				}
		return colors;
	}

	/** Rotates a point by a certain angle around a center point
	 * @param point the point to rotate
	 * @param center the center of rotation
	 * @param angle the angle of rotation
	 * @return the rotated point */
	private Point rotatePoint(Point point, Point center, double angle)
	{
		Point p = new Point(point);

		p.translate(-center.x, -center.y);
		p.setLocation(p.x * Math.cos(angle) - p.y * Math.sin(angle),
				p.y * Math.cos(angle) + p.x * Math.sin(angle));
		p.translate(center.x, center.y);

		return p;
	}

	/** Gets the list of players
	 * @return the list of players */
	public ArrayList<Player> getPlayers()
	{
		return players;
	}

	/** Runs the menu */
	public void run()
	{
		if (goButton.isClicked())
		{
			int humanCount = 0;
			int computerCount = 0;
			players.clear();
			for (int i = 0; i < maxPlayers; i++)
			{
				// Adds the appropriate player based on what selection the
				// drop down menu is currently set at
				if (dropDowns.get(i).getSelected().getText() == "Human")
					players.add(new Human("Player " + ++humanCount,
							playerColors.get(i)));
				else if (dropDowns.get(i).getSelected().getText() == "Computer")
					players.add(new Computer("Computer " + ++computerCount,
							playerColors.get(i)));
			}
			if (players.size() < 2)
				goButton.reset();
			else
				completed = true;
		}
	}

	/** Draws the menu to the board
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		super.draw(g, container);

		int numPlayers = 0;
		for (DropDown menu : dropDowns)
			if (menu.getSelected().getText() != "None")
				numPlayers++;
		if (numPlayers < 2)
			goButton.setHoverColor(Color.red);
		else
			goButton.setHoverColor(Color.green);

		goButton.draw(g, container);
		for (DropDown menu : dropDowns)
		{
			menu.draw(g, container);
		}
	}

	/** Handles mouse movement
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		goButton.getMouseMovement(event);
		for (DropDown dropDown : dropDowns)
			dropDown.getMouseMovement(event);
	}

	/** Handles the mouse press
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		goButton.getMousePress(event);

		for (DropDown dropDown : dropDowns)
			if (!dropDown.getMousePress(event))
				dropDown.reset();
	}

}
