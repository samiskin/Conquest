package Game;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import Board.Action;
import Board.ActionTooltip;
import Board.Unit;

/** A Human controlled player of Conquest
 * 
 * @author Shiranka Miskin
 * @version January 2013 */
public class Human extends Player
{

	private Action selectedAction;
	private Point selectedDest;
	private Point selectedTarget;


	/** Creates a new human controlled player
	 * 
	 * @param name the name of the player
	 * @param color the color associated with this player */
	public Human(String name, Color color)
	{
		super(name, color);
	}

	/** Determines if the player is player controller or ai controlled
	 * 
	 * @return true for this is a human controller class */
	public boolean isHuman()
	{
		return true;
	}

	/** Initializes the turn for the player, erasing data from the previous turn */
	public void initTurn()
	{
		selectedAction = null;
		selectedDest = null;
		selectedTarget = null;
	}

	public Unit pickUnit()
	{
		return selectedUnit;
	}

	public Action pickAbility()
	{
		return selectedAction;
	}

	public void resetAction()
	{
		selectedAction = null;
	}

	public Point pickMovementDest()
	{

		return selectedDest;
	}

	public Point pickAbilityTarget()
	{
		return selectedTarget;
	}

	public void getUnitSelection(MouseEvent event)
	{
		if (event.getButton() == 1)
		{
			Unit unit = board.getUnitAt(board.getCursorLoc());
			if (unit != null && units.contains(unit) && unit.isUsable())
				selectedUnit = unit;
		}
	}

	public void getUnitAction(ActionTooltip toolTip, MouseEvent event)
	{
		toolTip.getMouseInput(event);
		if (toolTip.getSelected() != null)
		{
			selectedAction = toolTip.getSelected();
		}

	}

	public void getMovementTarget(MouseEvent event)
	{
		if (event.getButton() == 1
				&& board.isInMovementRange(board.getGridLoc(event.getPoint())))
		{
			selectedDest = board.getGridLoc(event.getPoint());
		}
	}

	public void getAbilityTarget(MouseEvent event)
	{
		if (event.getButton() == 1
				&& board.isInAbilityRange(selectedAction, selectedUnit,
						board.getGridLoc(event.getPoint()))
				&& selectedAction.isValidTarget(
						board.getGridLoc(event.getPoint()), board))
			selectedTarget = board.getGridLoc(event.getPoint());
	}


}
