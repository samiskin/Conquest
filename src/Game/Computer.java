package Game;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import Board.Action;
import Board.StatSet;
import Board.Unit;

/** The A.I. player of Conquest
 * @author Shiranka Miskin
 * @version January 2013 */
public class Computer extends Player
{

	public double[][] influenceMap;
	public double[][] allyMap;
	public double[][] enemyMap;
	private Mindset currentMindset;

	private Action selectedAbility;

	/** An enumeration of the different states the computer can take
	 * @author Shiranka Miskin
	 * @version January 2013 */
	private enum Mindset {
		POSITION, SURVIVE, ATTACK;
	}

	/** Creates a computer player
	 * @param name the name of the player
	 * @param color the color associated with the player */
	public Computer(String name, Color color)
	{
		super(name, color);
	}

	/** Returns if the player is a human controller player or an A.I
	 * @return false as this is a Computer player object */
	public boolean isHuman()
	{
		return false;
	}


	/** Decides what mindset the computer player will take The mindset will
	 * affect how it decides to act in its two phases */
	private void decideMindset()
	{
		generateInfluenceMap();
		Unit unit = selectedUnit;
		StatSet stats = unit.getCurrentStats();

		int surviveThreshold = 20;

		if (stats.getHealth() * 10 + getInfluence(board.getUnitPos(unit)) < surviveThreshold)
			currentMindset = Mindset.SURVIVE;
		else if (board.hasTargets(unit, unit.getAttack()))
			currentMindset = Mindset.ATTACK;
		else
			currentMindset = Mindset.POSITION;

	}

	/** Gets the influence at a point (positive being good, negative being bad)
	 * @param p the point to check
	 * @return the current influence at that point */
	private double getInfluence(Point p)
	{
		return influenceMap[p.x][p.y];
	}

	/** Blurs a number map a specified amount of times by averaging all the
	 * pixels with the pixels around it
	 * @param grid the grid to blur
	 * @param numBlurs how many times to blur the grid
	 * @return the updated blurred map */
	private double[][] blur(double[][] grid, int numBlurs)
	{
		for (int iterations = 0; iterations < numBlurs; iterations++)
		{
			double[][] temp = new double[grid.length][grid[0].length];
			// Blur by everything near it on the map
			for (int x = 0; x < grid.length; x++)
				for (int y = 0; y < grid[0].length; y++)
				{
					if (board.isValid(x, y))
					{
						double sum = 0;
						int numTiles = 1;
						sum += grid[x][y];
						if (board.isValid(x - 1, y))
						{
							sum += grid[x - 1][y];
							numTiles++;
						}
						if (board.isValid(x + 1, y))
						{
							sum += grid[x + 1][y];
							numTiles++;
						}
						if (board.isValid(x, y - 1))
						{
							sum += grid[x][y - 1];
							numTiles++;
						}
						if (board.isValid(x, y + 1))
						{
							sum += grid[x][y + 1];
							numTiles++;
						}
						sum /= numTiles;
						temp[x][y] = sum;
					}
				}
			grid = temp;
		}
		return grid;
	}

	/** Creates an influence map of the current board. The map gives a view of
	 * the battlefield. The more positive a number is, the better it is for the
	 * player. The more negative it is the more dangerous it would be for the
	 * player to have units in that area */
	private void generateInfluenceMap()
	{
		influenceMap = new double[board.getGridWidth()][board.getGridHeight()];
		allyMap = new double[board.getGridWidth()][board.getGridHeight()];
		enemyMap = new double[board.getGridWidth()][board.getGridHeight()];

		// Set initial dangers
		for (Unit unit : board.getUnits())
		{
			double factor;
			if (hasUnit(unit))
				factor = 1;
			else
				factor = -1;
			StatSet stats = unit.getCurrentStats();
			int danger = stats.getAttack() * 100 + stats.getRange() * 50
					+ stats.getMovement() * stats.getAttack() * 5
					+ stats.getHealth() * 50;

			influenceMap[board.getUnitPos(unit).x][board.getUnitPos(unit).y] = danger
					* factor;
			if (hasUnit(unit))
				allyMap[board.getUnitPos(unit).x][board.getUnitPos(unit).y] = danger;
			else
				enemyMap[board.getUnitPos(unit).x][board.getUnitPos(unit).y] = danger;
		}

		// Blurs enough for the numbers to be able to propagate
		// along the entire map
		influenceMap = blur(influenceMap, 100);
		allyMap = blur(allyMap, 100);
		enemyMap = blur(enemyMap, 100);

	}

	/** Decides on which unit to use
	 * @return the unit the player is selecting */
	public Unit pickUnit()
	{

		// The AI picks the unit at its lowest stage, to ensure that all units
		// move together and can react to each other
		int lowestStage = Integer.MAX_VALUE;
		for (Unit unit : units)
			if (unit.isUsable() && unit.getStage() < lowestStage)
			{
				selectedUnit = unit;
				lowestStage = unit.getStage();
			}
		return selectedUnit;
	}

	/** Decides on what action to use
	 * @return the action that the A.I has decided to use */
	public Action pickAbility()
	{
		decideMindset();

		switch (currentMindset)
		{
		case SURVIVE:
			selectedAbility = selectedUnit.getMove();
			break;
		case POSITION:
			selectedAbility = selectedUnit.getMove();
			break;
		case ATTACK:
			selectedAbility = selectedUnit.getAttack();
			break;
		default:
			selectedAbility = selectedUnit.getMove();
			break;
		}

		return selectedAbility;
	}

	/** Picks a destination to send the selected unit to
	 * @return the point to move to */
	public Point pickMovementDest()
	{

		decideMindset();

		Point dest = null;
		double highestInfluence;
		switch (currentMindset)
		{
		// If the unit is trying to run away, escape to the highest influence
		// area
		case SURVIVE:
			highestInfluence = Integer.MIN_VALUE;
			for (int x = 0; x < influenceMap.length; x++)
				for (int y = 0; y < influenceMap[0].length; y++)
					if (board.isInMovementRange(x, y)
							&& influenceMap[x][y] > highestInfluence)
					{
						dest = new Point(x, y);
						highestInfluence = influenceMap[x][y];
					}

			break;
		case POSITION:
			double attackThreshold = 50;
			highestInfluence = Integer.MIN_VALUE;
			// If the location on the board is too dangerous, regroup to the
			// closest location to the unit's allies
			if (allyMap[board.getUnitPos(selectedUnit).x][board
					.getUnitPos(selectedUnit).y] < attackThreshold)
			{
				for (int x = 0; x < allyMap.length; x++)
					for (int y = 0; y < allyMap[0].length; y++)
						if (board.isInMovementRange(x, y)
								&& allyMap[x][y] > highestInfluence)
						{
							dest = new Point(x, y);
							highestInfluence = allyMap[x][y];
						}
			} else
				// Otherwise the unit can move to the attack, therefore travel
				// towards the enemy by weighing the enemy map more
				for (int x = 0; x < enemyMap.length; x++)
					for (int y = 0; y < enemyMap[0].length; y++)
						if (board.isInMovementRange(x, y))
						{
							// If the unit is in a dangerous area, consider
							// their allies,
							// otherwise simply head towards the enemy
							double consideration = 0;
							if (enemyMap[x][y] > 100)
								consideration = enemyMap[x][y] * 2
										+ allyMap[x][y];
							else
								consideration = enemyMap[x][y];
							if (consideration > highestInfluence)
							{
								dest = new Point(x, y);
								highestInfluence = consideration;
							}
						}

			break;
		case ATTACK:
			break;
		}

		return dest;
	}

	/** Picks a target for the selected ability
	 * @return the target for the ability */
	public Point pickAbilityTarget()
	{
		Collection<Unit> targets = board.getTargets(selectedUnit,
				selectedAbility);
		Point targetPos = null;
		int lowestHealth = Integer.MAX_VALUE;
		// The unit targets the enemy with the lowest health so that all enemies
		// are focused down
		// but all the A.I's units
		for (Unit target : targets)
			if (target.getCurrentStats().getHealth() < lowestHealth)
			{
				lowestHealth = target.getCurrentStats().getHealth();
				targetPos = board.getUnitPos(target);
			}
		return targetPos;

	}

}
