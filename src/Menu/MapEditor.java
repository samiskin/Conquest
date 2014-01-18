package Menu;

import Board.Board;
import Board.Sprite;
import Board.Tile;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import UIElements.CircleButton;
import UIElements.TextField;
import UIElements.RectangleButton;

/** The Map Editor menu that allows the user to alter a map's tiles, stats, and
 * spawn points
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class MapEditor extends Menu
{

	private Board board;
	private Sprite overlay;
	private int scrollX, scrollY;
	private ArrayList<Point> spawnPoints;
	private ArrayList<RectangleButton> selectionBoxes;
	private HashMap<RectangleButton, Tile> tileSelections;
	private RectangleButton selectedButton;
	private Tile selectedTile;
	private Point selectedGridPos;
	private CostEditor costEditor;
	private Sprite spawnIndicator;
	private CircleButton spawnButton;
	private CircleButton saveButton;

	/** Creates a new map editor menu
	 * 
	 * @param board the board that will be edited
	 * @param size the size of the screen
	 * @throws IOException */
	public MapEditor(Board board, Dimension size) throws IOException
	{
		super(size);
		this.board = board;
		// Makes the board display from the top right of the screen
		// as well as makes sure the board can cover the screen
		board.setOffset(new Point(0, 0));
		board.autoScale(size);
		new Tile("SpawnPoint");
		overlay = new Sprite("res/backgrounds/LevelDesignOverlay.png");

		int numTiles = Tile.tileSprites.size();
		selectionBoxes = new ArrayList<RectangleButton>();
		tileSelections = new HashMap<RectangleButton, Tile>();
		int selectionSize = 48;
		int spaceBetween = 10;
		Point topLeft = new Point(640 - ((numTiles)
				* (selectionSize + spaceBetween) - spaceBetween) / 2, 575);
		for (String str : Tile.tileSprites.keySet())
		{
			RectangleButton button = new RectangleButton(new Point(topLeft.x
					+ selectionBoxes.size() * (selectionSize + spaceBetween),
					topLeft.y), new Dimension(selectionSize, selectionSize),
					Tile.tileSprites.get(str));
			selectionBoxes.add(button);
			tileSelections.put(button, new Tile(str));
		}

		selectedButton = selectionBoxes.get(0);
		selectedButton.setClicked(true);
		selectedTile = tileSelections.get(selectedButton);
		selectedButton.setBorderColor(Color.GREEN);

		spawnPoints = board.getSpawnPoints();
		spawnIndicator = new Sprite("res/UI/SpawnIndicator.png");

		Sprite saveImg = new Sprite("res/UI/SaveButton.png");
		Sprite saveHoverImg = new Sprite("res/UI/SaveButton_Hover.png");

		saveButton = new CircleButton(new Point(660, 551), 15, saveImg,
				saveHoverImg);
		saveButton.setHoverColor(new Color(147, 254, 255));

		Sprite spawnIcon = new Sprite("res/UI/SpawnButton.png");
		Sprite spawnHoverIcon = new Sprite("res/UI/SpawnButton_Hover.png");

		spawnButton = new CircleButton(new Point(620, 551), 15, spawnIcon,
				spawnHoverIcon);
		spawnButton.setHoverColor(Color.yellow);

	}

	/** Initializes the cost editor for a point on the board by determining the
	 * point where it is created
	 * 
	 * @param gridPos the grid coordinate of the point on the board */
	private void initCostEditor(Point gridPos)
	{
		// Makes sure the tooltip is created where the user can see it
		Point topLeft = new Point();
		Point pixelPos = board.scale(gridPos);

		// If the editor being created on the right of the point would be
		// hidden, make the cost editor created to the left of them
		if (pixelPos.x + board.getScale() > board.getPixelWidth()
				- CostEditor.WIDTH)
			topLeft = new Point(pixelPos.x - CostEditor.WIDTH, pixelPos.y);
		else
			topLeft = new Point(pixelPos.x + board.getScale(), pixelPos.y);

		try
		{
			costEditor = new CostEditor(board.getTile(gridPos), topLeft,
					board.getOffset());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/** Draws all the elements of the */
	public void draw(Graphics g, Container container)
	{
		board.changeOffset(scrollX, scrollY, size);
		board.draw(g, container);

		for (Point p : spawnPoints)
			spawnIndicator.draw(g, board.scale(p).x, board.scale(p).y,
					board.getScale(), board.getScale(), container);

		if (spawnButton.isClicked())
		{
			Point p = board.scale(board.getCursorLoc());
			p.translate(1, 1);

			g.setColor(Color.yellow);
			g.drawRect(p.x, p.y, board.getScale() - 2, board.getScale() - 1);
		}

		overlay.draw(g, container);
		for (RectangleButton button : selectionBoxes)
			button.draw(g, container);
		if (selectedGridPos != null)
			costEditor.draw(g, container);
		saveButton.draw(g, container);
		spawnButton.draw(g, container);
	}

	/** Handles mouse clicks
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{
		if (event.getButton() == 1)
		{

			// A selection being made is checked so that the user cannot click
			// both on a menu selection as well as a board tile
			boolean selectionMade = false;

			if (spawnButton.getMousePress(event))
				selectionMade = true;

			// If the save button is clicked, write the board to a file
			if (saveButton.getMousePress(event))
			{
				try
				{
					board.writeToFile();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				saveButton.reset();
				selectionMade = true;
				return;
			}

			// If a point is selected and the cost changing menu is active,
			// ignore the rest of the mouse behavior
			if (selectedGridPos != null)
			{
				if (!costEditor.getMousePress(event))
					selectedGridPos = null;
				return;
			}

			for (RectangleButton button : selectionBoxes)
			{
				if (button.getMousePress(event))
				{
					selectionMade = true;
					selectedGridPos = null;
					// If the button is now clicked, then the user
					// has made a new selection
					if (button.isClicked())
					{
						// Reset the old selection if it exists
						if (selectedButton != null)
						{
							selectedButton.setClicked(false);
							selectedButton.setBorderColor(Color.white);
						}
						selectedButton = button;
						selectedTile = tileSelections.get(button);
						button.setBorderColor(Color.GREEN);
					}
					// If the button is now not clicked anymore, then
					// the user is cancelling their selection
					else
					{
						button.setBorderColor(Color.white);
						selectedButton = null;
						selectedTile = null;
					}

				}
			}
			// If a selection is not made,then the board tiles can be checked.
			if (!selectionMade)
			{
				selectedGridPos = board.getGridLoc(event.getPoint());
				// If the user is currently in the spawn editor mode,
				// check only the spawn points
				if (spawnButton.isClicked())
				{
					if (spawnPoints.contains(selectedGridPos))
						spawnPoints.remove(selectedGridPos);
					else
						spawnPoints.add(selectedGridPos);
					selectedGridPos = null;
				}
				// If the user has a tile type currently selected, assign the
				// selected coordinate with that type of tile
				else if (selectedTile != null)
				{
					board.setTile(selectedGridPos, new Tile(selectedTile));
					Integer cost = Tile.defaultCosts.get(selectedTile
							.toString());
					if (cost != null)
						board.setCost(selectedGridPos, cost);
					else
						board.setCost(selectedGridPos, 1);
					selectedGridPos = null;
				} else
					initCostEditor(selectedGridPos);
			}

		} else
		{
			// If the user is not left clicking, they are cancelling any of
			// their
			// current selections
			if (selectedButton != null)
			{
				selectedButton.setClicked(false);
				selectedButton.setBorderColor(Color.WHITE);
			}
			selectedButton = null;
			selectedTile = null;
			selectedGridPos = null;
		}
	}

	/** Handles mouse movement by scrolling if necessary and highlighting buttons
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{

		saveButton.getMouseMovement(event);
		spawnButton.getMouseMovement(event);

		int bounds = 50; // The size of the border where the mouse begins
							// scrolling
		int scrollSpeed = 25;
		scrollX = 0;
		scrollY = 0;

		// Scroll when the mouse is at the edges of the screen
		if (event.getPoint().x < bounds)
			scrollX = scrollSpeed;
		else if (event.getPoint().x > size.width - bounds)
			scrollX = -scrollSpeed;
		if (event.getPoint().y < bounds)
			scrollY = scrollSpeed;
		else if (event.getPoint().y > size.height - bounds)
			scrollY = -scrollSpeed;

		if (selectedGridPos == null)
			board.getMouseMovement(event);

		for (RectangleButton box : selectionBoxes)
			box.getMouseMovement(event);

		if (selectedGridPos != null)
			costEditor.getMouseMovement(event);

	}

	/** Handles keyboard input
	 * @param event the keyboard event */
	public void getKeyInput(KeyEvent event)
	{
		if (selectedGridPos != null)
		{
			// Make sure that the user can only enter numbers
			if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE
					|| event.getKeyCode() == KeyEvent.VK_ENTER
					|| Character.isDigit(event.getKeyChar()))
			{
				costEditor.getKeyInput(event);
				board.setCost(selectedGridPos, costEditor.getCost());
			}

			if (event.getKeyCode() == KeyEvent.VK_ENTER)
				selectedGridPos = null;

		}
	}

	/** Creates a menu that lets the user edit the cost of of a selected tile
	 * @author Shiranka Miskin */
	private class CostEditor extends TextField
	{
		private Point offset;
		private Point origOffset;
		public static final int WIDTH = 110;
		public static final int HEIGHT = 30;

		public CostEditor(Tile tile, Point topLeft, Point offset)
				throws IOException
		{
			super("Cost = ", topLeft, new Dimension(WIDTH, HEIGHT));
			text += tile.getCost();
			this.offset = offset;
			origOffset = new Point(offset);
			setHoverColor(new Color(0, 0, 0, 200));
		}

		public int getCost()
		{
			String cost = text.substring(7);
			if (cost.length() < 1)
				return 0;
			return Integer.parseInt(cost);
		}

		public void draw(Graphics g, Container container)
		{
			translate(offset.x - origOffset.x, offset.y - origOffset.y);
			super.draw(g, container);
			translate(-offset.x + origOffset.x, -offset.y + origOffset.y);
		}

		public boolean getMousePress(MouseEvent event)
		{

			translate(offset.x - origOffset.x, offset.y - origOffset.y);
			if (super.getMousePress(event))
			{
				text = baseText;
				translate(-offset.x + origOffset.x, -offset.y + origOffset.y);
				return true;
			}
			translate(-offset.x + origOffset.x, -offset.y + origOffset.y);
			return false;
		}

		public boolean getMouseMovement(MouseEvent event)
		{

			translate(offset.x - origOffset.x, offset.y - origOffset.y);
			if (super.getMouseMovement(event))
			{
				translate(-offset.x + origOffset.x, -offset.y + origOffset.y);
				return true;
			}
			translate(-offset.x + origOffset.x, -offset.y + origOffset.y);
			return false;
		}

		public void getKeyInput(KeyEvent event)
		{
			if (Character.isLetterOrDigit(event.getKeyChar()))
			{
				if (text.length() < baseText.length() + 2)
					super.getKeyInput(event);
			} else
				super.getKeyInput(event);

		}

	}

}
