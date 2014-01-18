package Menu;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import Board.Board;
import Board.Sprite;
import Game.Game;
import Game.Player;

/** Runs all the menus for setting up the game Chooses map, picks players, and
 * selects units
 * @author Shiranka Miskin
 * @version January 2013 */
public class GameSetupMenu extends Menu
{

	// Variables

	private MapMenu mapSelect;
	private PlayerMenu playerSelect;
	private ArrayList<UnitMenu> unitSelect;
	private GameMenu gameMenu;
	private Menu gameOver;
	private Menu currentMenu;
	private int currentPlayer;
	private Game game;
	private Board board;


	/** Creates a setup menu and begins from the Map selection
	 * @param img The background image
	 * @param size The size of the menu
	 * @throws IOException */
	public GameSetupMenu(Sprite img, Dimension size) throws IOException
	{
		super(img, size);
		initMenus();
		currentMenu = mapSelect;
	}

	/** Creates a setup menu and begins from the Map selection
	 * @param str The name of the file for the background image
	 * @param size the size of the menu
	 * @throws FileNotFoundException
	 * @throws IOException */
	public GameSetupMenu(String str, Dimension size)
			throws FileNotFoundException, IOException
	{
		this(new Sprite(backgroundDir + str), size);
	}


	/** Initializes all the menus */
	private void initMenus()
	{
		try
		{
			mapSelect = new MapMenu(size);
			unitSelect = new ArrayList<UnitMenu>();
			gameOver = new Menu("GameOver.jpg", size);

			playerSelect = new PlayerMenu(size);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// Add the links to the game over menu
		gameOver.addClickable("Main Menu", Main.getFont("Kalinga", 21),
				new Point(540, 320), new Dimension(200, 35), Main.mainMenu);
		gameOver.addClickable("Exit Game", Main.getFont("Kalinga", 21),
				new Point(540, 360), new Dimension(200, 35), Main.exitGame);

	}

	/** Moves on to the next menu if necessary */
	public void nextMenu()
	{
		if (currentMenu.isComplete())
		{
			// If the user has finished selecting a map, the game menu can now
			// be initialized and the current menu moves on to the player select
			if (currentMenu == mapSelect)
			{
				// The board's scale needs to be reset (as they were changed
				// for the map menu display
				board = mapSelect.getSelected();
				board.setScale(Board.DEFAULT_SCALE);

				if (board.getPixelWidth() < size.width
						|| board.getPixelHeight() < size.height)
					board.autoScale(size);

				game = new Game(board, size);
				try
				{
					gameMenu = new GameMenu(game, size);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				currentMenu = playerSelect;
				playerSelect.setMaxPlayers(board.getMaxPlayers());
				playerSelect.init();
			}
			// Create a unit selection menu for every player
			else if (currentMenu == playerSelect)
			{
				for (Player player : playerSelect.getPlayers())
					try
					{
						unitSelect.add(new UnitMenu(size, player));
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				currentPlayer = 0;
				currentMenu = unitSelect.get(currentPlayer);
			} else if (unitSelect.contains(currentMenu))
			{
				currentPlayer++;
				// If all the players are done, move on to the game menu
				if (currentPlayer >= playerSelect.getPlayers().size())
				{
					for (Player player : playerSelect.getPlayers())
					{
						player.assignGame(game);
						game.addPlayer(player);
					}

					currentMenu = gameMenu;
					game.start();
				} else
					// Otherwise move on to the next unit selection menu
					currentMenu = unitSelect.get(currentPlayer);
			} else if (currentMenu == gameMenu)
			{
				currentMenu = gameOver;
			} else if (currentMenu == gameOver)
			{
				completed = true;
				nextMenu = gameOver.nextMenu;
			}
		}
	}

	/** Checks if the menu needs to be switched then runs the current menu */
	public void run()
	{
		nextMenu();
		currentMenu.run();
	}

	/** Resets the menus */
	public void reset()
	{
		super.reset();
		mapSelect.reset();
		playerSelect.reset();
		unitSelect.clear();
		currentMenu = mapSelect;
	}


	/** Draws the current menu
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		super.draw(g, container);
		currentMenu.draw(g, container);
	}

	// Controller

	/** Handles mouse clicks
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		currentMenu.getMousePress(event);
	}

	/** Handles mouse movement
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		currentMenu.getMouseMovement(event);
	}

	/** Handles mouse dragging
	 * @param event the mouse event */
	public void getMouseDragged(MouseEvent event)
	{
		currentMenu.getMouseDragged(event);
	}

	/** Handles the mouse being released
	 * @param event the mouse event */
	public void getMouseRelease(MouseEvent event)
	{
		currentMenu.getMouseRelease(event);
	}

	/** Handles keyboard input
	 * @param event the keyboard event */
	public void getKeyInput(KeyEvent event)
	{
		currentMenu.getKeyInput(event);
	}

}
