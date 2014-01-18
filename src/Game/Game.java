package Game;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import Board.Action;
import Board.Action.Type.Target;
import Board.Board;
import Board.Sprite;
import Board.ActionTooltip;
import Board.Unit;
import Board.Action.Type;
import Board.UnitEntry.Pose;
import Menu.Main;

/** Runs a complete game of Conquest
 * @author Shiranka Miskin
 * @version January 2013 */
public class Game
{

	private LinkedList<Player> players;

	private Board board;
	private GameState gameState;
	public static final String boardDir = "res/boards/";
	private ActionTooltip toolTip;

	private Unit selectedUnit;
	private Point targetedPoint;
	private Action selectedAction;
	private HashSet<Point> affectedTiles;
	private Action hoverAbility;
	private Point movementDest;
	private Dimension screenSize;
	private Player currentPlayer;

	private boolean gameOver;

	private int scrollX;
	private int scrollY;

	/** A list of all states the game can be in (it rotates through them through
	 * each turn) */
	private enum GameState {
		PICKGRID, SELECTACTION, MOVE, USEABILITY, MOVEANIM, UNITSELECT, NEXTPLAYER, TARGET;
	}


	/** Creates a game from a board with a specified screen size for scrolling
	 * @param board The board that the game is played on
	 * @param screenSize The size of the screen */
	public Game(Board board, Dimension screenSize)
	{

		this.board = board;
		gameState = GameState.UNITSELECT;
		players = new LinkedList<Player>();
		this.screenSize = screenSize;
		gameOver = false;
	}


	/** Returns if the game has ended or not
	 * @return True if the game has ended, false if not */
	public boolean isOver()
	{
		return gameOver;
	}

	/** Adds a player to the current game
	 * @param player The player that is being added */
	public void addPlayer(Player player)
	{
		players.add(player);
		for (Unit unit : player.getUnits())
			board.addUnit(unit, players.size() - 1);

	}

	/** Returns the board of the game
	 * @return The board the game is played on */
	public Board getBoard()
	{
		return board;
	}

	/** Starts the game */
	public void start()
	{
		currentPlayer = players.getFirst();
		currentPlayer.initTurn();
	}

	/** Changes the state of the game to another state
	 * @param state The state to change to */
	public void setGameState(GameState state)
	{
		switch (state)
		{
		case UNITSELECT:
			toolTip = null;
			selectedUnit = null;
			currentPlayer.resetUnitSelection();
			board.clearMovementGrid();
			board.clearAbilityGrid();
			setTargeted(null);
			gameState = GameState.UNITSELECT;
			break;

		case SELECTACTION:
			selectedAction = null;
			currentPlayer.initTurn();
			if (selectedUnit.isUsable())
			{
				initTooltip(selectedUnit);
				board.clearMovementGrid();
				board.clearAbilityGrid();
				gameState = GameState.SELECTACTION;
			} else
				setGameState(GameState.UNITSELECT);
			break;

		case MOVE:
			board.initMovementGrid(selectedUnit);
			if (!currentPlayer.isHuman())
				board.hideMovementGrid();
			toolTip = null;
			gameState = GameState.MOVE;
			break;

		case TARGET:
			if (currentPlayer.isHuman())
				board.initAbilityGrid(selectedAction, selectedUnit);
			toolTip = null;
			gameState = GameState.TARGET;
			break;

		case USEABILITY:
			// Animate for Attack
			selectedUnit.playOnce(Pose.ATK_DOWN, Pose.IDLE);
			// Animate all units to be damaged if they are affected
			// by this ability (some abilities can affect multiple locations)
			affectedTiles = selectedAction.getAffectedLocations(targetedPoint);
			for (Point point : affectedTiles)
			{
				Unit unit = board.getUnitAt(point);
				if (unit != null)
				{
					selectedAction.affectUnit(unit);
					unit.playOnce(Pose.DAMAGE, Pose.IDLE);
				}
				selectedAction.affectTile(board.getTile(point));
			}

			board.clearAbilityGrid();
			gameState = GameState.USEABILITY;
			break;

		case NEXTPLAYER:
			currentPlayer.resetUnits();
			if (currentPlayer.hasActiveUnits())
				players.addLast(players.pop());
			else
				players.removeFirst();
			currentPlayer = players.getFirst();
			currentPlayer.initTurn();
			currentPlayer.resetUnitSelection();
			break;

		case MOVEANIM:
			board.startUnitMoving(selectedUnit, movementDest);
			board.hideMovementGrid();
			gameState = GameState.MOVEANIM;
			break;

		}
	}

	/** Sets the selected unit
	 * @param unit the unit that is now selected */
	public void setSelected(Unit unit)
	{
		selectedUnit = unit;
	}

	/** Sets the targeted point
	 * @param p the point that is currently being targeted */
	public void setTargeted(Point p)
	{
		targetedPoint = p;
	}

	/** Sets the destination of the move command
	 * @param p the point to move to */
	public void setMovementDest(Point p)
	{
		movementDest = p;
	}

	/** Cancels the current selection */
	public void cancelSelection()
	{
		selectedUnit = null;
		toolTip = null;
		board.clearMovementGrid();
	}

	/** Gets a list of all the players in the game
	 * @return all the players currently in the game */
	public LinkedList<Player> getPlayers()
	{
		return players;
	}

	/** Runs the logic for the tooltip, letting it interact with the game board
	 * appropriately */
	private void runTooltip()
	{

		if (toolTip.getHover() != hoverAbility)
		{
			hoverAbility = toolTip.getHover();
			board.clearAbilityGrid();
			board.clearMovementGrid();
			if (hoverAbility != null)
			{
				if (hoverAbility.getType() == Type.MOVE)
					board.initMovementGrid(selectedUnit);
				else
					board.initAbilityGrid(hoverAbility, selectedUnit);
			}
		}
	}

	/** Uses the selected action and transtiions to whatever game state is
	 * necessary */
	private void useAction()
	{
		switch (selectedAction.getType())
		{
		case MOVE:
			setGameState(GameState.MOVE);
			break;
		case NOTHING:
			selectedUnit.setUnusable();
			setGameState(GameState.SELECTACTION);
			break;
		default:
			setGameState(GameState.TARGET);
			break;
		}
	}

	/** Runs the game based on what state the board is currently at */
	public void run()
	{
		board.changeOffset(scrollX, scrollY, screenSize);

		switch (gameState)
		{
		case UNITSELECT:

			// Move on to the next player if either the player cannot
			// make a move, or he has just finished off all the other players
			// (in which the game will end when moving to the next player)
			if (!currentPlayer.hasUsableUnits() || players.size() <= 1)
				setGameState(GameState.NEXTPLAYER);

			selectedUnit = currentPlayer.pickUnit();
			if (selectedUnit != null)
				if (selectedUnit.isUsable())
					setGameState(GameState.SELECTACTION);
			break;

		case SELECTACTION:
			// Waits until the player selects an action (selected action
			// will equal -null- until a decision is made
			selectedAction = currentPlayer.pickAbility();

			if (currentPlayer.isHuman() && toolTip != null)
				runTooltip();

			if (selectedAction != null)
				useAction();
			break;

		case MOVE:
			// Once a unit has been selected by the player,
			// move on to the animation of that unit moving
			movementDest = currentPlayer.pickMovementDest();
			if (movementDest != null)
			{
				setGameState(GameState.MOVEANIM);
			}
			break;

		case TARGET:
			targetedPoint = currentPlayer.pickAbilityTarget();
			if (targetedPoint != null)
				setGameState(GameState.USEABILITY);
			break;

		case USEABILITY:
			boolean unitsDone = true;
			// Check all the units that were hit by the ability
			// If they finished animating, check if they were
			// eliminated and remove them from the game if so
			for (Point point : affectedTiles)
			{
				Unit unit = board.getUnitAt(point);
				if (unit != null)
				{
					if (unit.isDoneAnim())
					{
						if (!unit.isAlive())
						{
							for (Player player : players)
								if (player.hasUnit(unit))
									player.removeUnit(unit);
							board.removeUnit(unit);
						}
					} else
						unitsDone = false;
				}
			}
			// Once all the units have completed their animation
			// the game can move on
			if (unitsDone)
			{
				// Abilities other than move cost 2 stages
				selectedUnit.advStage(2);
				selectedAction = null;
				if (currentPlayer.isHuman())
				{
					Human player = (Human) currentPlayer;
					player.resetAction();
				}
				setGameState(GameState.UNITSELECT);
			}
			break;

		// Wait until the board is finished animating before
		// moving on to the next stage
		case MOVEANIM:
			if (!board.isUnitMoving())
			{
				selectedUnit.advStage();
				movementDest = null;
				if (currentPlayer.isHuman())
				{
					Human player = (Human) currentPlayer;
					player.resetUnitSelection();
				}
				setGameState(GameState.UNITSELECT);
			}
			break;
		}

		// If the only units on the board are that of the players,
		// they have won, and so the game can end
		if (currentPlayer.getUnits().size() == board.getUnits().size())
			gameOver = true;

	}

	/** Initializes the tooltip for a certain unit
	 * @param unit The unit to create the tooltip at */
	private void initTooltip(Unit unit)
	{
		// Makes sure the tooltip is created where the user can see it
		Point topLeft = new Point();
		Point unitPos = board.scale(board.getUnitPos(unit));

		// If the tooltip being created on the right of the unit would be
		// hidden,
		// make the tooltip created to the left of them
		if (unitPos.x + board.getScale() > board.getPixelWidth()
				- ActionTooltip.getBoxSize().width)
			topLeft = new Point(unitPos.x - ActionTooltip.getBoxSize().width,
					unitPos.y);
		else
			topLeft = new Point(unitPos.x + board.getScale(), unitPos.y);
		toolTip = new ActionTooltip(unit, topLeft, board.getOffset());
	}

	// View

	/** Draws the elements of the game
	 * @param g The graphics to draw with
	 * @param container the container to draw on */
	public void draw(Graphics g, Container container)
	{
		board.draw(g, container);

		// Draw other UI Elements
		if (toolTip != null && currentPlayer.isHuman())
		{
			container.setCursor(Main.mainCursor);
			toolTip.draw(g, container);
		} else
			container.setCursor(Main.mainCursor);
	}


	/** Gets player mouse input
	 * @param event the mouse event */
	public void getMousePress(MouseEvent event)
	{

		// If the player right clicks, return to the previous menu
		if (event.getButton() == 3)
		{
			switch (gameState)
			{
			case SELECTACTION:
				setGameState(GameState.UNITSELECT);
				break;
			case MOVE:
				setGameState(GameState.SELECTACTION);
				break;
			case USEABILITY:
				setGameState(GameState.SELECTACTION);
				break;
			case TARGET:
				setGameState(GameState.SELECTACTION);
				break;
			}
		} else if (currentPlayer.isHuman())
		{
			// Otherwise get the player's decision
			Human human = (Human) currentPlayer;
			switch (gameState)
			{
			case UNITSELECT:
				human.getUnitSelection(event);
				break;
			case SELECTACTION:
				human.getUnitAction(toolTip, event);
				break;
			case MOVE:
				human.getMovementTarget(event);
				break;
			case TARGET:
				human.getAbilityTarget(event);
				break;
			}
		}
	}

	/** Handles mouse movement in the game
	 * @param event the mouse event */
	public void getMouseMovement(MouseEvent event)
	{
		int bounds = 50; // The size of the border where the mouse begins
							// scrolling
		int scrollSpeed = 25;
		scrollX = 0;
		scrollY = 0;

		// Scroll when the mouse is at the edges of the screen
		if (event.getPoint().x < bounds)
			scrollX = scrollSpeed;
		else if (event.getPoint().x > screenSize.width - bounds)
			scrollX = -scrollSpeed;
		if (event.getPoint().y < bounds)
			scrollY = scrollSpeed;
		else if (event.getPoint().y > screenSize.height - bounds)
			scrollY = -scrollSpeed;

		board.getMouseMovement(event);
		if (gameState == GameState.SELECTACTION)
			toolTip.getMouseMovement(event);
	}

}
