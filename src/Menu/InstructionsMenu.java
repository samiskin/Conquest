package Menu;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import UIElements.CircleButton;

import Board.Sprite;

/** Displays the set of instructions for the game
 * @author Shiranka Miskin
 * @version January 2013
 */
public class InstructionsMenu extends Menu
{
	private ArrayList<Sprite> backgrounds;
	private int currentBackground;
	private CircleButton nextButton;
	private static String bgDir = "res/backgrounds/";

	/** Creates a new Instructions Menu 
	 * @param size	the size of the menu
	 * @throws IOException
	 */
	public InstructionsMenu(Dimension size) throws IOException
	{
		super(size);
		backgrounds = new ArrayList<Sprite>();
		backgrounds.add(new Sprite(bgDir + "Instructions - Conquest.jpg"));
		backgrounds
				.add(new Sprite(bgDir + "Instructions - The Game Screen.jpg"));
		backgrounds.add(new Sprite(bgDir
				+ "Instructions - The Level Editor.jpg"));

		nextButton = new CircleButton(new Point(1200, 320), 11);

	}

	/** Resets the menu
	 */
	public void reset()
	{
		super.reset();
		currentBackground = 0;
	}

	/** Draws the menu to the board
	 * @param g	the graphics to draw with
	 * @param container	the container to draw on
	 */
	public void draw(Graphics g, Container container)
	{
		backgrounds.get(currentBackground).draw(g, container);
		nextButton.draw(g, container);
	}

	/** Handles mouse movement
	 * @param event	the mouse event
	 */
	public void getMouseMovement(MouseEvent event)
	{
		nextButton.getMouseMovement(event);
	}

	/** Handles mouse clicks
	 * @param event	the mouse event
	 */
	public void getMousePress(MouseEvent event)
	{
		// If the next button is clicked move on to the next menu
		if (nextButton.getMousePress(event))
		{
			nextButton.reset();
			currentBackground++;
			if (currentBackground >= backgrounds.size())
			{
				reset();
				completed = true;
			}
		}
	}

}
