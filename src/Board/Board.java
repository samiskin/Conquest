package Board;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import Board.Action.Type.Target;
import Board.UnitEntry.Pose;

/** A board of tiles and units, able to display all the tiles and units as well
 * as displays of unit ranges, displays of ability ranges, and animating
 * complete movement of units from location to location
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class Board implements Comparable<Board>
{


	private Tile[][] board;
	private String name;
	private Dimension size;
	private int scale;
	public static final int DEFAULT_SCALE = 44;

	private ArrayList<Unit> units;
	private Map<Unit, Point> unitPos;
	private boolean unitInMovement;
	private Unit movingUnit;
	private static final int unitMovementSpeed = 5;
	private Point movingUnitOffset;
	private Point offset;
	private LinkedList<Point> movementPath;
	private HashMap<Point, Point> movementGrid;
	private HashSet<Point> abilityGrid;
	private Action currentAbility;

	private static Tile terrainTile = new Tile("terrain");
	private static Tile terrainTargetTile = new Tile("terraintarget");
	private static Tile attackTile = new Tile("attack");
	private static Tile attackTargetTile = new Tile("attacktarget");
	private static Tile allyTile = new Tile("heal");
	private static Tile allyTargetTile = new Tile("healtarget");
	private Tile targetTile;
	private Tile abilityTile;
	private Target targetType;
	private int[][] movementCost;
	private boolean displayMovementGrid;
	private Point cursorLoc;
	private static Tile cursorImg;
	private ArrayList<Point> spawnPoints;
	private int spawnRange = 1;
	public static final int MAX_TILE_COST = 99;

	/** Loads a board based on its file name
	 * 
	 * @param fileName the name of the .txt file
	 * @throws FileNotFoundException */
	public Board(String fileName) throws FileNotFoundException
	{
		this(new File(fileName));
	}

	/** Creates a board from a link to a file
	 * 
	 * @param file the file to load
	 * @throws FileNotFoundException */
	public Board(File file) throws FileNotFoundException
	{
		@SuppressWarnings("resource")
		Scanner in = new Scanner(file);

		// Reads in the name of the map
		name = in.nextLine();

		// Loads all the spawn points of the map
		int numSpawnPoints = in.nextInt();
		spawnPoints = new ArrayList<Point>(numSpawnPoints);
		for (int i = 0; i < numSpawnPoints; i++)
		{
			spawnPoints.add(new Point(in.nextInt(), in.nextInt()));
		}

		// Loads the board details itself, with the size of the board
		// and each tile and cost of that tile
		size = new Dimension(in.nextInt(), in.nextInt());
		board = new Tile[size.width][size.height];
		for (int y = 0; y < size.height; y++)
			for (int x = 0; x < size.width; x++)
				board[x][y] = new Tile(in.next());
		for (int y = 0; y < size.height; y++)
			for (int x = 0; x < size.width; x++)
				board[x][y].setCost(in.nextInt());

		// Initializes the other variables
		init();
	}

	/** Creates a board of a file name but specifies the scale of each tile
	 * 
	 * @param fileName the name of the .txt file
	 * @param scale the scale of each tile
	 * @throws FileNotFoundException */
	public Board(String fileName, int scale) throws FileNotFoundException
	{
		this(fileName);
		this.scale = scale;
	}

	/** Creates a default board of grass with spawn points at each corner and a
	 * border of mountains
	 * 
	 * @param name the name of the board
	 * @param dim the size of the board */
	public Board(String name, Dimension dim)
	{
		this.name = name;
		size = dim;
		spawnPoints = new ArrayList<Point>();
		spawnPoints.add(new Point(1, 1));
		spawnPoints.add(new Point(size.width - 2, 1));
		spawnPoints.add(new Point(1, size.height - 2));
		spawnPoints.add(new Point(size.width - 2, size.height - 2));
		board = new Tile[size.width][size.height];

		// Covers the entire board in grass
		for (int x = 0; x < size.width; x++)
			for (int y = 0; y < size.height; y++)
			{
				if (x == 0 || y == 0 || x == size.width - 1
						|| y == size.height - 1)
				{
					board[x][y] = new Tile("mountain");
					board[x][y].setUnwalkable();
				} else
					board[x][y] = new Tile("grass");
			}
		init();
	}

	/** Initializes the board */
	private void init()
	{
		units = new ArrayList<Unit>();
		unitPos = new HashMap<Unit, Point>();

		scale = DEFAULT_SCALE;

		movementCost = new int[size.width][size.height];
		movementGrid = new HashMap<Point, Point>();
		movementPath = new LinkedList<Point>();
		abilityGrid = new HashSet<Point>();

		offset = new Point();

		cursorLoc = new Point();
		cursorImg = new Tile("cursor");
	}

	/** Automatically re-scales the board so that the entire board is visible
	 * 
	 * @param boardDisplay the rectangle to fit inside */
	public void autofit(Rectangle boardDisplay)
	{
		setScale(Math.min(boardDisplay.width / getGridWidth(),
				boardDisplay.height / getGridHeight()));
		setOffset(new Point(
				(int) (boardDisplay.getCenterX() - getPixelWidth() / 2),
				(int) (boardDisplay.getCenterY() - getPixelHeight() / 2)));
	}

	/** Automatically scales the board so that there is no blank space
	 * 
	 * @param size the size to scale it to */
	public void autoScale(Dimension size)
	{
		setScale((int) Math.ceil(Math.max(size.getWidth() / getGridWidth(),
				size.getHeight() / getGridHeight())));
	}

	/** Calculates if a unit can target an enemy with its ability
	 * 
	 * @param unit The unit casting the ability
	 * @param ability The ability being casted
	 * @return true of the unit has an available target, false if not */
	public boolean hasTargets(Unit unit, Action ability)
	{
		for (Unit target : units)
		{
			if (ability.isValidTarget(target, unit)
					&& unit.isInAttackRange(getUnitPos(unit),
							getUnitPos(target)))
				return true;
		}
		return false;
	}

	/** Initializes the grid of points that the selected ability can affect
	 * 
	 * @param ability
	 * @param unit */
	public void initAbilityGrid(Action ability, Unit unit)
	{
		clearAbilityGrid();
		currentAbility = ability;
		targetType = ability.getTarget();
		switch (targetType)
		{
		case TILE:
			targetTile = terrainTargetTile;
			abilityTile = terrainTile;
			break;
		case ENEMY:
			targetTile = attackTargetTile;
			abilityTile = attackTile;
			break;
		case ALLY:
			targetTile = allyTargetTile;
			abilityTile = allyTile;
			break;
		}
		abilityGrid.addAll(ability.getTargetablePoints(getUnitPos(unit)));

	}

	/** Clears the ability grid */
	public void clearAbilityGrid()
	{
		abilityGrid.clear();
	}

	/** Calculates if a target point is in range of an ability of a unit
	 * 
	 * @param ability The ability being casted
	 * @param unit The unit casting the ability
	 * @param target The target of the ability
	 * @return True if the target can be hit, false if not */
	public boolean isInAbilityRange(Action ability, Unit unit, Point target)
	{
		return isInAbilityRange(ability, getUnitPos(unit), target);
	}

	/** Calculates if a target point is in range of an ability casted from a
	 * point
	 * 
	 * @param ability The ability being casted
	 * @param center The point where the ability is being casted
	 * @param target The target of the ability
	 * @return */
	public boolean isInAbilityRange(Action ability, Point center, Point target)
	{
		return ability.isTargetable(center, target);
	}

	/** Initializes the flood fill movement grid generation
	 * 
	 * @param unit the unit that is moving */
	public void initMovementGrid(Unit unit)
	{
		displayMovementGrid = true;
		resetMovementCost();
		generateMovementGrid(getUnitPos(unit).x, getUnitPos(unit).y,
				getUnitPos(unit).x, getUnitPos(unit).y, unit.getMoveRange(), 0,
				null);
	}

	/** Generates a grid of all points a unit can travel to as well as stores
	 * their parent for pathfinding
	 * 
	 * @param startX The start position of the search
	 * @param startY The end position of the search
	 * @param x The current x coordinate of the recursion
	 * @param y The current y coordinate of the recursion
	 * @param maxRange The maximum range that can be reached
	 * @param currentRange The current range of the recursion
	 * @param parent The current parent in the recursion */
	private void generateMovementGrid(int startX, int startY, int x, int y,
			int maxRange, int currentRange, Point parent)
	{
		if (isValid(x, y) && ((x == startX && y == startY) || isWalkable(x, y)))
		{
			if (currentRange < movementCost[x][y]
					&& currentRange + board[x][y].getCost() < maxRange)
			{
				// Increases the current range count by how much it costed
				// to travel over that grid space (assuming you're not already
				// on
				// that space
				if (x != startX || y != startY)
					currentRange += board[x][y].getCost();
				Point p = new Point(x, y);
				// Stores the point along with its parent which is used when
				// pathfinding
				movementGrid.put(p, parent);
				// Sets the current cost it took to get to this location in the
				// array
				movementCost[x][y] = currentRange;
				// Recursively calls the method once again for each direction
				generateMovementGrid(startX, startY, x, y + 1, maxRange,
						currentRange, p);
				generateMovementGrid(startX, startY, x, y - 1, maxRange,
						currentRange, p);
				generateMovementGrid(startX, startY, x + 1, y, maxRange,
						currentRange, p);
				generateMovementGrid(startX, startY, x - 1, y, maxRange,
						currentRange, p);
			}
		}
	}

	/** Hides the movement grid from vision */
	public void hideMovementGrid()
	{
		displayMovementGrid = false;
	}

	/** Clears all data relating to movement */
	public void clearMovementGrid()
	{
		movementGrid.clear();
		movementPath.clear();
		resetMovementCost();
	}

	/** Resets the cost of the movement grid */
	private void resetMovementCost()
	{
		for (int x = 0; x < size.width; x++)
			for (int y = 0; y < size.height; y++)
				movementCost[x][y] = MAX_TILE_COST;
	}

	/** Creates a path based on the current movement grid from the center of the
	 * grid to the specified point
	 * 
	 * @param p the point to travel to */
	private void createMovementPath(Point p)
	{
		if (movementGrid.containsKey(p))
		{
			movementPath.addFirst(p);
			createMovementPath(movementGrid.get(p));
		}
	}

	/** If a unit is supposed to be moving this method will advance them on their
	 * path */
	private void advanceMovingUnit()
	{
		// Determines the current location of the unit on the screen
		Point currentPoint = scale(getUnitPos(movingUnit));
		currentPoint.translate(movingUnitOffset.x, movingUnitOffset.y);

		// Determines the next point that this unit will attempt to reach
		Point target = scale(movementPath.getFirst());

		// Moves the unit towards the target
		if (target.x < currentPoint.x)
			movingUnitOffset.x -= unitMovementSpeed;
		else if (target.x > currentPoint.x)
			movingUnitOffset.x += unitMovementSpeed;
		else
			movingUnitOffset.x = 0;

		if (target.y < currentPoint.y)
			movingUnitOffset.y -= unitMovementSpeed;
		else if (target.y > currentPoint.y)
			movingUnitOffset.y += unitMovementSpeed;
		else
			movingUnitOffset.y = 0;

		// If the unit is close enough to the target point
		if (Math.abs(target.x - currentPoint.x) < unitMovementSpeed
				&& Math.abs(target.y - currentPoint.y) < unitMovementSpeed)
		{
			// Snap the unit to the grid location
			setUnitPos(movingUnit, movementPath.getFirst());
			// Reset the offset (as now it is based off of the new location)
			movingUnitOffset.setLocation(0, 0);
			// Remove the old target as it has been reached
			movementPath.pop();

			// If the list is empty then the movement is complete
			if (movementPath.isEmpty())
			{
				unitInMovement = false;
				movingUnit.setPose(Pose.IDLE);
				return;
			}
			// Otherwise set the unit to its appropriate pose
			else
			{
				target = movementPath.getFirst();
				if (target.x < getUnitPos(movingUnit).x)
					movingUnit.setPose(Pose.MOVE_LEFT);
				else if (target.x > getUnitPos(movingUnit).x)
					movingUnit.setPose(Pose.MOVE_RIGHT);
				else if (target.y < getUnitPos(movingUnit).y)
					movingUnit.setPose(Pose.MOVE_UP);
				else if (target.y > getUnitPos(movingUnit).y)
					movingUnit.setPose(Pose.MOVE_DOWN);
			}
		}

	}

	/** Initialize the variables to begin the movement animation of a specified
	 * unit
	 * 
	 * @param unit the unit that will be moving along the movement path */
	public void startUnitMoving(Unit unit, Point dest)
	{
		unitInMovement = true;
		movingUnit = unit;
		movingUnitOffset = new Point();
		movementPath.clear();
		createMovementPath(dest);

	}

	/** Detects if a unit is currently moving or not
	 * 
	 * @return true if a unit is moving, false if not */
	public boolean isUnitMoving()
	{
		return unitInMovement;
	}

	/** Uses a previous generated movement grid to calculate if a point is able
	 * to be moved to
	 * 
	 * @param p The point to check
	 * @return True if the point can be reached, false if not */
	public boolean isInMovementRange(Point p)
	{
		return movementGrid.containsKey(p);
	}

	/** Uses a previous generated movement grid to calculate if a point is able
	 * to be moved to
	 * 
	 * @param x The x coordinate of the point to check
	 * @param y The y coordinate of the point to check
	 * @return True if the point can be reached, false if not */
	public boolean isInMovementRange(int x, int y)
	{
		return movementGrid.containsKey(new Point(x, y));
	}


	/** Returns if a location can be traversed by a unit
	 * 
	 * @param p the point to check
	 * @return true if a unit can walk over the point, false if they cannot */
	public boolean isWalkable(Point p)
	{
		return board[p.x][p.y].getCost() < MAX_TILE_COST
				&& !unitPos.containsValue(p);
	}

	/** Returns if a location can be traversed by a unit
	 * 
	 * @param x the x coordinate to check
	 * @param y the y coordinate to check
	 * @return true if a unit can walk over the point, false if they cannot */
	public boolean isWalkable(int x, int y)
	{
		return board[x][y].getCost() != MAX_TILE_COST
				&& !unitPos.containsValue(new Point(x, y));
	}

	/** Returns if a point is within the board bounds and traversable
	 * 
	 * @param p The point to check
	 * @return true if the location is valid, false if not */
	public boolean isValid(Point p)
	{
		return isValid(p.x, p.y);
	}

	/** Returns if a point is within the board bounds and traversable
	 * 
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @return true if the location is valid, false if not */
	public boolean isValid(int x, int y)
	{
		return x >= 0 && y >= 0 && x < board.length && y < board[0].length
				&& board[x][y].getCost() < MAX_TILE_COST;
	}

	/** Resets the current board */
	public void reset()
	{
		units.clear();
		unitPos.clear();
		unitInMovement = false;
		init();
	}

	/** Write the board data to a file
	 * 
	 * @param fileName The name of the file to load
	 * @throws IOException */
	public void writeToFile() throws IOException
	{

		FileWriter out = new FileWriter("res/boards/" + name + ".txt");

		out.write(name + "\r\n");

		// Write the number of spawn points followed by
		// the coordinates of all of them
		out.write(spawnPoints.size() + "\r\n");
		for (Point p : spawnPoints)
			out.write(p.x + " " + p.y + "\r\n");

		// Write the dimensions followed by a grid of each type
		// of tile, followed by another grid of each tile cost
		out.write(size.width + " " + size.height + "\r\n");
		for (int y = 0; y < size.height; y++)
		{
			for (int x = 0; x < size.width; x++)
				out.write(board[x][y].getName() + " ");
			out.write("\r\n");
		}
		for (int y = 0; y < size.height; y++)
		{
			for (int x = 0; x < size.width; x++)
				out.write(board[x][y].getCost() + " ");
			out.write("\r\n");
		}

		out.close();
	}

	/** Compares two boards alphabetically by their name */
	public int compareTo(Board other)
	{
		return name.compareTo(other.name);
	}

	/** Adds a unit to the board
	 * 
	 * @param unit The unit to add
	 * @param p The point to place it at */
	public void addUnit(Unit unit, Point p)
	{
		units.add(unit);
		unitPos.put(unit, p);
	}

	/** Adds a unit around a spawnpoint
	 * 
	 * @param unit The unit to spawn
	 * @param spawnPoint The point to spawn it around */
	public void addUnit(Unit unit, int spawnPoint)
	{
		// Checks all tiles surrounding the point until an available one is
		// found
		for (int y = -spawnRange; y <= spawnRange; y++)
			for (int x = -spawnRange; x <= spawnRange; x++)
				if (isWalkable(spawnPoints.get(spawnPoint).x + x,
						spawnPoints.get(spawnPoint).y + y))
				{
					addUnit(unit, new Point(spawnPoints.get(spawnPoint).x + x,
							spawnPoints.get(spawnPoint).y + y));
					return;
				}

		// If the unit has still not been added, increase the range of
		// the spawn placement and try again, resetting the spawn range
		// after it is completed
		if (!units.contains(unit))
		{
			spawnRange++;
			addUnit(unit, spawnPoint);
			spawnRange--;
		}
	}

	/** Removes a unit from the board
	 * 
	 * @param unit The unit to remove */
	public void removeUnit(Unit unit)
	{
		units.remove(unit);
		unitPos.remove(unit);
	}

	/** Scales a point on the board to its corresponding point on the pixel
	 * display
	 * 
	 * @param p The point to scale
	 * @return The scaled position on the display */
	public Point scale(Point p)
	{
		return new Point(p.x * scale + offset.x, p.y * scale + offset.y);
	}

	/** Sets the scale of the tiles of the board
	 * 
	 * @param scale the new scale of the board */
	public void setScale(int scale)
	{
		this.scale = scale;
	}

	/** Sets the position of a unit
	 * 
	 * @param unit The unit to place
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point */
	public void setUnitPos(Unit unit, int x, int y)
	{
		setUnitPos(unit, new Point(x, y));
	}

	/** Sets the position of a unit
	 * 
	 * @param unit The unit to place
	 * @param p The point to place it at */
	public void setUnitPos(Unit unit, Point p)
	{
		unitPos.put(unit, p);
	}

	/** Changes the offset of the board
	 * 
	 * @param deltaX the change in the x coordinate
	 * @param deltaY the change in the y coordinate
	 * @param screenSize the size of the screen displaying the board */
	public void changeOffset(int deltaX, int deltaY, Dimension screenSize)
	{
		offset.translate(deltaX, deltaY);

		offset.x = Math.max(offset.x, screenSize.width - getPixelWidth());
		offset.x = Math.min(offset.x, 0);
		offset.y = Math.max(offset.y, screenSize.height - getPixelHeight());
		offset.y = Math.min(offset.y, 0);
	}

	/** Sets the current offset of the board
	 * 
	 * @param offset */
	public void setOffset(Point offset)
	{
		this.offset.setLocation(offset);
	}

	/** Sets the tile at the specified position
	 * 
	 * @param p the position of the tile to replace
	 * @param tile the replacement tile */
	public void setTile(Point p, Tile tile)
	{
		board[p.x][p.y] = tile;
	}

	/** Sets the cost of a tile
	 * 
	 * @param p the position of the tile to alter
	 * @param cost the new cost of the tile */
	public void setCost(Point p, int cost)
	{
		board[p.x][p.y].setCost(cost);
	}

	/** Returns an integer array of all the costs of each tile
	 * 
	 * @return the array of costs */
	public int[][] getCostGrid()
	{
		int[][] grid = new int[board.length][board[0].length];
		for (int x = 0; x < board.length; x++)
			for (int y = 0; y < board[0].length; y++)
				grid[x][y] = board[x][y].getCost();
		return grid;
	}

	/** Returns all valid locations around a point
	 * 
	 * @param center The center of the locations
	 * @return A collection of points that surround the center */
	public Collection<Point> getAdjacentLocations(Point center)
	{
		Collection<Point> points = new HashSet<Point>();
		Point up = new Point(center);
		up.translate(0, 1);
		Point down = new Point(center);
		down.translate(0, -1);
		Point left = new Point(center);
		left.translate(-1, 0);
		Point right = new Point(center);
		right.translate(1, 0);

		if (isValid(up))
			points.add(up);
		if (isValid(down))
			points.add(down);
		if (isValid(left))
			points.add(left);
		if (isValid(right))
			points.add(right);

		return points;
	}

	/** Returns all units on the board
	 * 
	 * @return the units placed on this board */
	public Collection<Unit> getUnits()
	{
		return units;
	}

	/** Gets the current offset of the board
	 * 
	 * @return The current offset of the board */
	public Point getOffset()
	{
		return offset;
	}

	/** Returns a list of all possible targets a unit can have using an ability
	 * 
	 * @param unit The unit casting the ability
	 * @param ability The ability being casted
	 * @return A list of all targets it can have */
	public Collection<Unit> getTargets(Unit unit, Action ability)
	{
		HashSet<Unit> targets = new HashSet<Unit>();
		for (Unit target : units)
		{
			if (ability.isValidTarget(target, unit)
					&& unit.isInAttackRange(getUnitPos(unit),
							getUnitPos(target)))
				targets.add(target);
		}
		return targets;
	}

	/** Gets the grid location corresponding to a x and y pixel coordinate
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the point corresponding to the grid coordinate */
	public Point getGridLoc(int x, int y)
	{
		return new Point((x - offset.x) / scale, (y - offset.y) / scale);
	}

	/** Gets the grid location corresponding to a x and y pixel coordinate
	 * 
	 * @param p the pixel coordinate
	 * @return the point corresponding to the grid coordinate */
	public Point getGridLoc(Point p)
	{
		return getGridLoc(p.x, p.y);
	}

	/** Returns the maximum players allowed for the board
	 * 
	 * @return */
	public int getMaxPlayers()
	{
		return spawnPoints.size();
	}

	/** Gets the tile at a certain point
	 * 
	 * @param p The point to check
	 * @return The tile that lies at that point */
	public Tile getTile(Point p)
	{
		return board[p.x][p.y];
	}

	/** Gets the tile at a certain point
	 * 
	 * @param x The x coordinate to check
	 * @param y The y coordinate to check
	 * @return The tile that lies at that point */
	public Tile getTile(int x, int y)
	{
		return board[x][y];
	}

	/** Returns the tile at a pixel coordinate
	 * 
	 * @param x the x coordinate of the pixel
	 * @param y the y coordinate of the pixel
	 * @return The tile at that location */
	public Tile getTileAtPixel(int x, int y)
	{
		return board[(x - offset.x) / scale][(y - offset.y) / scale];
	}

	/** Returns the tile at a pixel coordinate
	 * 
	 * @param p the point coordinate of the pixel
	 * @return The tile at that location */
	public Tile getTileAtPixel(Point p)
	{
		return board[(p.x - offset.x) / scale][(p.y - offset.y) / scale];
	}

	/** Returns the current location of the selection cursor
	 * 
	 * @return The location of the cursor */
	public Point getCursorLoc()
	{
		return cursorLoc;
	}

	/** Gets the width of the grid
	 * 
	 * @return the width of the grid */
	public int getGridWidth()
	{
		return board.length;
	}

	/** Gets the height of the grid
	 * 
	 * @return the height of the grid */
	public int getGridHeight()
	{
		return board[0].length;
	}

	/** Gets the width of the grid in pixels
	 * 
	 * @return the width of the grid in pixels */
	public int getPixelWidth()
	{
		return board.length * scale;
	}

	/** Gets the height of the grid in pixels
	 * 
	 * @return the height of the grid */
	public int getPixelHeight()
	{
		return board[0].length * scale;
	}

	/** Gets the unit located at a certain point
	 * 
	 * @param p The point of the unit
	 * @return the unit at the specified point */
	public Unit getUnitAt(Point p)
	{
		for (Unit unit : units)
		{
			if (getUnitPos(unit).equals(p))
				return unit;
		}
		return null;
	}

	/** Gets the position of a unit
	 * 
	 * @param unit the unit to check
	 * @return the position of that unit on the board */
	public Point getUnitPos(Unit unit)
	{
		return unitPos.get(unit);
	}

	/** Returns the name of the board
	 * 
	 * @return the name of the board */
	public String getName()
	{
		return name;
	}

	/** Returns a list of all the spawn points of the board
	 * 
	 * @return the list of spawn points */
	public ArrayList<Point> getSpawnPoints()
	{
		return spawnPoints;
	}

	/** Returns the current scale of the board
	 * 
	 * @return the current scale of the board */
	public int getScale()
	{
		return scale;
	}

	/** Returns the current movementPath
	 * 
	 * @return the current movementPath */
	public LinkedList<Point> getMovementPath()
	{
		return movementPath;
	}

	/** Draws the board
	 * 
	 * @param g the graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{

		// Draws all the tiles of the board
		for (int x = 0; x < board.length; x++)
		{
			for (int y = 0; y < board[x].length; y++)
			{
				board[x][y].draw(g, x * scale + offset.x, y * scale + offset.y,
						scale, container);
			}
		}

		// If the movement grid is currently to be displayed, draw all the tiles
		// for it
		if (displayMovementGrid)
		{
			for (Point p : movementGrid.keySet())
			{
				terrainTile.draw(g, scale(p), scale, container);
			}

			for (Point p : movementPath)
			{
				terrainTargetTile.draw(g, scale(p), scale, container);
			}
		}

		// If an ability is being chosen, display the range indicators and the
		// target for it
		for (Point p : abilityGrid)
		{
			abilityTile.draw(g, scale(p), scale, container);
		}
		if (abilityGrid.contains(cursorLoc))
		{
			HashSet<Point> affectedPoints = currentAbility
					.getAffectedLocations(cursorLoc);
			for (Point p : affectedPoints)
				targetTile.draw(g, scale(p), scale, container);
		}

		// Draws all the units of the board
		for (Unit unit : units)
		{
			if (!(unitInMovement && unit == movingUnit))
				unit.draw(g, scale(getUnitPos(unit)), scale, container);
		}
		if (unitInMovement)
		{
			// Move the unit along its path before drawing it at its offsetted
			// location
			advanceMovingUnit();
			Point offsetPoint = scale(getUnitPos(movingUnit));
			offsetPoint.translate(movingUnitOffset.x, movingUnitOffset.y);
			movingUnit.draw(g, offsetPoint, scale, container);
		}

		// Draws the board cursor
		cursorImg.draw(g, scale(cursorLoc), scale, container);
	}


	/** Handles mouse input to the board
	 * 
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		// Sets the location of the cursor
		cursorLoc = getGridLoc(event.getPoint());

		// Recalculates the movement path based on the updated cursor location
		if (movementGrid != null && !unitInMovement)
		{
			movementPath.clear();
			createMovementPath(getGridLoc(event.getPoint()));
		}

	}

}
