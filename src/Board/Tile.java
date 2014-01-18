package Board;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** A Tile object, with an image to display, the cost to traverse it, and its
 * name
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class Tile
{

	private String tileName;
	private Sprite sprite;
	private int cost;
	public static Map<String, Sprite> tileSprites = new HashMap<String, Sprite>();
	public static Map<String, Sprite> UITiles = new HashMap<String, Sprite>();
	public static Map<String, Integer> defaultCosts = new HashMap<String, Integer>();
	private static final String spriteDir = "res/tiles/";

	// Constructors

	/** Creates a tile from its name (using the default directory and a default
	 * cost of 1)
	 * 
	 * @param tileName the name of the tile */
	public Tile(String tileName)
	{
		this.tileName = tileName;
		sprite = tileSprites.get(tileName);
		if (sprite == null)
			sprite = UITiles.get(tileName);
		cost = 1;
	}

	/** Creates a tile
	 * 
	 * @param tileName the name of the type of tile
	 * @param cost the set cost of the tile */
	public Tile(String tileName, int cost)
	{
		this(tileName);
		this.cost = cost;
	}

	/** Creates a copy of a tile
	 * 
	 * @param selectedTile the tile to copy */
	public Tile(Tile selectedTile)
	{
		tileName = selectedTile.tileName;
		sprite = selectedTile.sprite;
		cost = selectedTile.cost;
	}


	/** Loads all tiles in the tile directory into a sprite map so that a new
	 * sprite does not have to be created for each instance
	 * 
	 * @throws IOException */
	public static void loadTiles() throws IOException
	{
		// Loads all the basic tiles of the board used in the maps
		File folder = new File(spriteDir);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				tileSprites.put(
						listOfFiles[i]
								.getName()
								.substring(
										0,
										listOfFiles[i].getName().lastIndexOf(
												'.')).toLowerCase(),
						new Sprite(listOfFiles[i]));
			}
		}

		// Loads all the tiles used for the UI such as displaying
		File UIFolder = new File(spriteDir + "UI Tiles/");
		listOfFiles = UIFolder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				UITiles.put(
						listOfFiles[i]
								.getName()
								.substring(
										0,
										listOfFiles[i].getName().lastIndexOf(
												'.')).toLowerCase(),
						new Sprite(listOfFiles[i]));
			}
		}

		// Set the default costs
		defaultCosts.put("water", 6);
		defaultCosts.put("mountain", Board.MAX_TILE_COST);
		defaultCosts.put("grass", 1);
		defaultCosts.put("plains", 2);
		defaultCosts.put("rocks", 4);

		System.out.println("Tiles: " + tileSprites.keySet());
		System.out.println("UI Tiles: " + UITiles.keySet());
	}

	/** Directly sets the cost of movement for the tile
	 * 
	 * @param newCost the new cost */
	public void setCost(int newCost)
	{
		cost = newCost;
	}

	/** Sets the current tile to be unwalkable */
	public void setUnwalkable()
	{
		cost = Board.MAX_TILE_COST;
	}


	/** Changes the movement cost of the tile
	 * 
	 * @param changeInCost How much to change the cost of the tile by
	 * @return The updated cost of movement */
	public int changeCost(int changeInCost)
	{
		if (cost < Integer.MAX_VALUE)
			cost += changeInCost;
		return cost;
	}

	/** Gets the current cost to tarverse this tile
	 * 
	 * @return the cost of the tile */
	public int getCost()
	{
		return cost;
	}

	/** Gets the name of the tile
	 * 
	 * @return the name of the tile */
	public String getName()
	{
		return tileName;
	}

	/** Returns the name of the tile as its string representation
	 * 
	 * @return the name of the tile */
	public String toString()
	{
		return tileName;
	}

	/** Draws the tile at a specified location
	 * 
	 * @param g The graphics object to draw with
	 * @param x The x coordinate to draw at
	 * @param y The y coordinate to draw at
	 * @param scale The scale of the tile
	 * @param container The container to draw on */
	public void draw(Graphics g, int x, int y, int scale, Container container)
	{
		sprite.draw(g, x, y, scale, scale, container);

	}

	/** Draws the tile at a specified point
	 * 
	 * @param g The graphics object to draw with
	 * @param point the point to draw at
	 * @param scale The scale of the tile
	 * @param container the container to draw on */
	public void draw(Graphics g, Point point, int scale, Container container)
	{
		draw(g, point.x, point.y, scale, container);
	}

}
