package Menu;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import Board.Board;

/** Runs all the menus dealing with the level designer
 * 
 * @author Shiranka Miskin
 * @version Janaury 2013 */
public class DesignSetupMenu extends Menu
{

	private Menu currentMenu;
	private MapMenu mapSelect;
	private Menu newOrLoad;
	private NewMapMenu newMap;
	private MapEditor editor;
	private Font font;

	/** Initializes the design menu starting at the menu where the user decides
	 * whether to create a new map or load an old one
	 * 
	 * @param size
	 * @throws IOException */
	public DesignSetupMenu(Dimension size) throws IOException
	{
		super(size);
		initMenus();
		currentMenu = newOrLoad;

	}

	/** Initializes all the menus of the board */
	private void initMenus()
	{
		font = Main.getFont("Kalinga", 18);

		try
		{
			mapSelect = new MapMenu(size);
			newOrLoad = new Menu(size);
			newMap = new NewMapMenu(size);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		MenuLink newMapButton = new MenuLink("New Map", font, new Point(430,
				295), new Dimension(200, 50), new Color(63, 192, 38, 0), newMap);
		MenuLink loadMapButton = new MenuLink("Load Map", font, new Point(650,
				295), new Dimension(200, 50), new Color(30, 52, 183, 0),
				mapSelect);

		newOrLoad.addClickable(newMapButton);
		newOrLoad.addClickable(loadMapButton);

	}

	/** Resets all the menus */
	public void reset()
	{
		super.reset();
		initMenus();
		currentMenu = newOrLoad;
	}

	/** Runs the current board and switches it once it is completed */
	public void run()
	{
		if (currentMenu.isComplete())
		{
			// If the user has specified a new map, initialize the editor
			if (currentMenu == newMap)
			{
				try
				{
					editor = new MapEditor(
							new Board(newMap.getName(), new Dimension(
									newMap.getWidth(), newMap.getHeight())),
							size);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				currentMenu = editor;
				// If the user has selected a map, load the editor for the map
			} else if (currentMenu == mapSelect)
			{
				try
				{
					editor = new MapEditor(mapSelect.getSelected(), size);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				currentMenu = editor;
			} else if (currentMenu == newOrLoad)
				currentMenu = newOrLoad.nextMenu;
			else
				completed = true;

		}
	}

	/** Draws the current menu to the screen
	 * 
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		currentMenu.draw(g, container);
	}

	/** Handles a mouse event
	 * 
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		currentMenu.getMousePress(event);
	}

	/** Lets the current menu handle mouse movement
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		currentMenu.getMouseMovement(event);
	}

	/** Handles when the mouse is released
	 * @param event the mouse event */
	public void getMouseRelease(MouseEvent event)
	{
		currentMenu.getMouseRelease(event);
	}

	/** Handles when the mouse is dragged
	 * @param event the mouse event */
	public void getMouseDragged(MouseEvent event)
	{
		currentMenu.getMouseDragged(event);
	}

	/** Handles when the keyboard buttons are pressed
	 * @param event the keyboard event */
	public void getKeyInput(KeyEvent event)
	{
		currentMenu.getKeyInput(event);
	}

}
