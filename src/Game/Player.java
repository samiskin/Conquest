package Game;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import Board.Action;
import Board.Board;
import Board.Unit;

/** The superclass of all types of players of Conquest
 * @author Shiranka Miskin
 * @version January 2013 */
public abstract class Player
{


	protected ArrayList<Unit> units;
	protected String name;
	protected Game game;
	protected Board board;
	protected Color color;
	protected Unit selectedUnit;


	/** Creates a new player
	 * @param name the name of the player
	 * @param color the player's corresponding color */
	public Player(String name, Color color)
	{
		this.name = name;
		units = new ArrayList<Unit>();
		this.color = color;
	}


	/** Gets the color assosiated with the player
	 * @return the color of the player */
	public Color getColor()
	{
		return color;
	}

	/** Sets the game the player is participating in
	 * @param game the game that is being played */
	public void assignGame(Game game)
	{
		this.game = game;
		board = game.getBoard();
	}

	/** Gets all the units that the player owns
	 * @return the list of the player's units */
	public ArrayList<Unit> getUnits()
	{
		return units;
	}

	/** Adds a new unit to the player's list of units
	 * @param unit the unit to add */
	public void addUnit(Unit unit)
	{
		units.add(unit);

	}

	/** Method ran at the start of a player's turn if the player requires any
	 * operations to be completed at that time */
	public void initTurn()
	{
	}

	/** Resets the unit the player is selecting */
	public void resetUnitSelection()
	{
		selectedUnit = null;
	}

	/** Removes a unit from the player's list of units
	 * @param unit the unit to remove */
	public void removeUnit(Unit unit)
	{
		units.remove(unit);
	}

	/** Returns the player's name
	 * @return the player's name */
	public String getName()
	{
		return name;
	}

	/** Determines if the player owns a unit or not
	 * @param unit the unit to check
	 * @return true if the player owns the unit, false if not */
	public boolean hasUnit(Unit unit)
	{
		return units.contains(unit);
	}

	/** Determines if the player has any more living units left
	 * @return true if the player still has units, false if none are left */
	public boolean hasActiveUnits()
	{
		for (Unit unit : units)
			if (unit.isAlive())
				return true;
		return false;

	}

	/** Determines if the player has any more usable units left (units that can
	 * still move and have not expended their moves)
	 * @return true if there are still usable units, false if not */
	public boolean hasUsableUnits()
	{
		for (Unit unit : units)
			if (unit.isUsable())
				return true;
		return false;
	}

	/** Resets the stages of every unit the player owns */
	public void resetUnits()
	{
		for (Unit unit : units)
			unit.resetStage();
	}

	/** Determines if the player is a human player or a computer controlled
	 * player
	 * @return true if the player is human controlled, false if not */
	public abstract boolean isHuman();

	/** Decides on a unit to be selected
	 * @return the unit to be selected */
	public abstract Unit pickUnit();

	/** Decides on the action to be selected
	 * @return the action to be selected */
	public abstract Action pickAbility();

	/** Decides on a destination for the selected unit to move to
	 * @return the destination of the unit */
	public abstract Point pickMovementDest();

	/** Decides on a target to use the selected ability on
	 * @return the target of the ability */
	public abstract Point pickAbilityTarget();

}
