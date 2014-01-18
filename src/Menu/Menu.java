package Menu;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

import UIElements.TextButton;

import Board.Sprite;

/** A menu with a background image and clickable areas that link to other menus
 * @author Shiranka Miskin
 * @version January 2012 */
public class Menu
{

	protected Sprite mainImage;

	// A list of all the areas that
	protected ArrayList<MenuLink> clickables;

	// Variables for if the menu is completed and what menu to proceed to
	// afterwards
	protected boolean completed;
	protected Menu nextMenu;
	protected Dimension size;
	public static String backgroundDir = "res/backgrounds/";

	/** Initializes the Menu object
	 * @param img The image to display as a background
	 * @param container The container in which the menu exists */
	public Menu(Sprite img, Dimension size)
	{
		mainImage = img;
		clickables = new ArrayList<MenuLink>();
		completed = false;
		this.size = size;
		nextMenu = Main.mainMenu;
	}

	/** Creates a menu with the default image of the background map
	 * @param size the size of the menu in pixels
	 * @throws IOException */
	public Menu(Dimension size) throws IOException
	{
		this("Map.jpg", size);
	}

	/** Creates a menu using the name of the background image file and the size
	 * @param fileName the name of the background image
	 * @param size the size of the menu
	 * @throws IOException */
	public Menu(String fileName, Dimension size) throws IOException
	{
		this(new Sprite(backgroundDir + fileName), size);
	}

	/** Resets the current menu */
	public void reset()
	{
		completed = false;
		for (MenuLink button : clickables)
		{
			button.setClicked(false);
		}
	}

	/** Parameter to find out if the menu is completed, and if the program should
	 * proceed to the next menu
	 * @return True if the menu is complete, false if not */
	public boolean isComplete()
	{
		return completed;
	}

	/** Runs anything needed for the specific menu A generic menu has nothing to
	 * run, as it simply displays the background and allows the user to click on
	 * various locations on it */
	public void run()
	{

	}

	/** Adds a clickable area in the menu, that links to another menu
	 * @param position The top left corner of the clickable area
	 * @param dimension The size of the area
	 * @param link What menu the area links to */
	public void addClickable(String text, Font font, Point position,
			Dimension dimension, Menu link)
	{
		clickables.add(new MenuLink(text, font, position, dimension, link));
	}

	/** Adds a clickable area of the map that links to another menu
	 * @param text the text of the button
	 * @param font the font of the text
	 * @param position the top left of the button
	 * @param dimension the size of the button
	 * @param color the color of the button
	 * @param link the menu it links to */
	public void addClickable(String text, Font font, Point position,
			Dimension dimension, Color color, Menu link)
	{
		clickables.add(new MenuLink(text, font, position, dimension, color,
				link));
	}

	/** Adds a menu link to the menu
	 * @param menuLink the menu link to add */
	public void addClickable(MenuLink menuLink)
	{
		clickables.add(menuLink);

	}

	/** Sets the hover color of every clickable button on the menu
	 * @param color the new hover color of the buttons */
	public void setClickableHover(Color color)
	{
		for (MenuLink button : clickables)
			button.setHoverColor(color);
	}

	/** Draws the background image of the menu
	 * @param g the graphics context to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		draw(g, ImageObserver.WIDTH, ImageObserver.HEIGHT, container);
	}

	/** Draws the menu to the board at a specified size
	 * @param g the graphics to draw on
	 * @param width the width of the display
	 * @param height the height of the display
	 * @param container the container to draw on */
	public void draw(Graphics g, int width, int height, Container container)
	{
		mainImage.draw(g, width, height, container);
		for (MenuLink clickable : clickables)
			clickable.draw(g, container);
	}

	/** Handles mouse input for the menu
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		for (MenuLink button : clickables)
		{
			if (button.getMousePress(event) && button.isClicked())
			{
				nextMenu = button.getLink();
				completed = true;
			}
		}
	}

	/** Handles mouse movement for the menu
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		for (MenuLink clickable : clickables)
			clickable.getMouseMovement(event);
	}

	/** Handles mouse release for the menu
	 * @param event the mouse event */
	public void getMouseRelease(MouseEvent event)
	{

	}

	/** Handles mouse dragging for the menu
	 * @param event the mouse event */
	public void getMouseDragged(MouseEvent event)
	{

	}

	/** Handles keyboard input for the menu
	 * @param event The KeyEvent to operate with */
	public void getKeyInput(KeyEvent event)
	{

	}

	/** A text button that can link to another menu object when clicked
	 * @author Shiranka Miskin
	 * @version December 2012 */
	protected class MenuLink extends TextButton
	{
		private Menu link;

		/** Creates a new menu link
		 * @param text the text of the button
		 * @param font the font of the text
		 * @param position the top left of the button
		 * @param dimension the size of the button
		 * @param link the menu this button links to */
		public MenuLink(String text, Font font, Point position,
				Dimension dimension, Menu link)
		{
			super(text, font, position, dimension);
			this.link = link;
		}

		/** Creates a new menu link
		 * @param text the text on the button
		 * @param font the font of the text
		 * @param position the top left of the button
		 * @param dimension the size of the button
		 * @param color the color of the button
		 * @param link the menu this button links to */
		public MenuLink(String text, Font font, Point position,
				Dimension dimension, Color color, Menu link)
		{
			super(text, font, position, dimension, color);
			this.link = link;
		}

		/** Method to find out what the area links to
		 * @return the menu that corresponds with the area */
		public Menu getLink()
		{
			return link;
		}
	}

}