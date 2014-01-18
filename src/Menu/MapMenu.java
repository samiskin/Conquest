package Menu;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import UIElements.ScrollBar;
import UIElements.TextButton;

import Board.Board;
import Board.Sprite;

/** Lets the user select a map out of all the maps stored in the boards folder
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class MapMenu extends Menu
{

	private final String boardLoc = "res/boards/";
	private ArrayList<Board> boards;
	private ArrayList<TextButton> buttons;
	private Board selectedBoard;
	private TextButton selectedButton;
	private final Dimension boardButtonSize = new Dimension(220, 40);
	private Font boardButtonFont;
	private Rectangle boardButtonArea;
	private ScrollBar scrollBar;
	private Rectangle boardDisplay;
	private TextButton goButton;

	/** Creates a new menu
	 * 
	 * @param background the background image
	 * @param size the size */
	public MapMenu(Sprite background, Dimension size)
	{
		super(background, size);

		loadAllBoards();
		loadAllButtons();

		// Create the button to load the map
		goButton = new TextButton("Go!", Main.getFont("Sword Art Online", 32),
				new Point(1127, 542), new Dimension(107, 52));

		// The selection defaults to the first board
		loadBoard(boards.get(0));
		selectedButton = buttons.get(0);
		selectedButton.setClicked(true);

		// The scroll bar lets the menu show any number of boards
		// depending on how many they have created
		Point scrollTopLeft = new Point(50 + boardButtonSize.width + 6, 50);
		int scrollWidth = 9;
		int barHeight = 30;

		int scrollHeight = boardButtonArea.height;
		if (barHeight > 0)
		{
			scrollBar = new ScrollBar(new Rectangle(scrollTopLeft,
					new Dimension(scrollWidth, barHeight)), new Rectangle(
					scrollTopLeft, new Dimension(scrollWidth, scrollHeight)));
			scrollBar.setBorderWidth(1);
		}

	}

	/** Creates a map with the default background
	 * 
	 * @param size the size of the menu
	 * @throws IOException */
	public MapMenu(Dimension size) throws IOException
	{
		this(new Sprite(backgroundDir + "MapSelect.jpg"), size);
	}

	/** Resets the menu and all of its elements */
	public void reset()
	{
		super.reset();
		loadAllBoards();
		loadAllButtons();
		loadBoard(selectedBoard);
		scrollBar.reset();
	}

	/** Loads a board and resizes it in order to fit the display boundaries
	 * 
	 * @param board the board to display */
	private void loadBoard(Board board)
	{
		int border = 15;
		board.reset();
		boardDisplay = new Rectangle(new Point(288 + border, 47 + border),
				new Dimension(946 - border * 2, 490 - border * 2));
		board.autofit(boardDisplay);
		selectedBoard = board;
	}

	/** Loads all the boards found in the board directory into the arraylist and
	 * sorts them by name */
	private void loadAllBoards()
	{
		// Load all the boards in the board directory
		File boardFolder = new File(boardLoc);
		File[] boardTxts = boardFolder.listFiles();
		boards = new ArrayList<Board>();
		for (File boardTxt : boardTxts)
		{
			try
			{
				boards.add(new Board(boardTxt.toString()));
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		// Sorts the list of boards alphabetically by name
		Collections.sort(boards);

	}

	/** Loads all the buttons for the boards */
	private void loadAllButtons()
	{
		buttons = new ArrayList<TextButton>();

		boardButtonFont = Main.getFont("Kalinga", 18);

		// List all of the boards with a button for each
		Point listTopLeft = new Point(50, 50);
		boardButtonArea = new Rectangle(new Point(listTopLeft), new Dimension(
				boardButtonSize.width, size.height - listTopLeft.y * 2));
		for (Board board : boards)
		{
			buttons.add(new TextButton(board.getName(), boardButtonFont,
					new Point(listTopLeft), boardButtonSize));
			listTopLeft.translate(0, boardButtonSize.height + 2);
		}
	}

	/** Returns what board the user has currently selected
	 * 
	 * @return the selected board */
	public Board getSelected()
	{
		return selectedBoard;
	}

	/** Draws the menu on the board
	 * 
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		super.draw(g, container);
		goButton.draw(g, container);

		if (scrollBar != null)
			scrollBar.draw(g, container);
		selectedBoard.draw(g, container);
		g.setFont(boardButtonFont);
		g.setColor(Color.white);
		g.drawString("Max Players: " + selectedBoard.getMaxPlayers(), 322, 575);
		g.drawString("Size: " + selectedBoard.getGridWidth() + " x "
				+ selectedBoard.getGridHeight(), 605, 575);

		g.setClip(boardButtonArea.x, boardButtonArea.y, boardButtonArea.width,
				boardButtonArea.height);

		for (TextButton button : buttons)
			button.draw(g, container);
		g.setClip(new Rectangle(size));

	}

	/** Handles mouse movement
	 * 
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		goButton.getMouseMovement(event);
		for (TextButton button : buttons)
		{
			button.getMouseMovement(event);
		}
		scrollBar.getMouseMovement(event);
	}

	/** Handles mouse releases for the scroll bar
	 * 
	 * @param event the mouse event */
	public void getMouseRelease(MouseEvent event)
	{
		scrollBar.getMouseRelease(event);
	}

	/** Handles mouse dragging for the scroll bar
	 * 
	 * @param event the mouse event */
	public void getMouseDragged(MouseEvent event)
	{
		int oldY = scrollBar.getDifference().y;
		scrollBar.getMouseDragged(event);
		int deltaY = scrollBar.getDifference().y - oldY;

		// Scrolls all the buttons
		for (TextButton button : buttons)
		{
			button.translate(0, -2 * deltaY);
		}

	}

	/** Handles mouse clicks
	 * 
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		if (goButton.contains(event.getPoint()))
			completed = true;

		// Check the buttons if the mouse is in the area (otherwise
		// the user could click buttons not being displayed in the area)
		if (boardButtonArea.contains(event.getPoint()))
			for (TextButton button : buttons)
			{
				if (button.getMousePress(event))
				{
					selectedButton.setClicked(false);
					selectedButton = button;
					selectedButton.setClicked(true);
					loadBoard(boards.get(buttons.indexOf(button)));
				}
			}

		scrollBar.getMousePress(event);
	}

}
