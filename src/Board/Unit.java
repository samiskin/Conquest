package Board;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import Board.Action.Type;
import Board.UnitEntry.Pose;
import Game.Player;

/** A Unit class of the game, detailing its behavior and appearance
 * @author Shiranka Miskin
 * @version January 2013 */
public class Unit implements Comparable<Unit>
{


	private String spriteName;
	private StatSet baseStats;
	private StatSet currentStats;
	private ArrayList<Action> abilities;
	private HashSet<Condition> conditions;
	private Action attack;
	private Action move;
	private int stage;
	private Color outlineColor;
	private final int outlineWidth = 2;
	public static final int maxMoves = 2;
	private Player player;

	private UnitEntry unitEntry;

	private Sprite currentSprite;
	private Pose nextPose;
	private boolean playOnce;


	/** Creates a unit with its controlling player and its type
	 * @param sprite The type of unit it is (the UnitEntry parameter holds all
	 *            the sprites for the unit as well as its default stats)
	 * @param player The player that owns this unit */
	public Unit(UnitEntry sprite, Player player)
	{
		this.unitEntry = sprite;
		abilities = new ArrayList<Action>();
		spriteName = sprite.name();

		// Every unit defaults at an idle pose that loops continuously
		setPose(Pose.IDLE);
		playOnce = false;

		// The stats of the unit are stored in the UnitEntry
		baseStats = sprite.getStats();
		currentStats = new StatSet(baseStats);

		// Every unit begins with a basic attack and a move ability
		move = new Action(Type.MOVE, this);
		attack = new Action(Type.ATTACK, this);

		abilities.add(move);
		abilities.add(attack);

		stage = 0;
		outlineColor = Color.black;
		this.player = player;
		outlineColor = player.getColor();
	}


	/** Updates a unit's conditions (called at the start of each player's turn) */
	public void update()
	{
		for (Condition condition : conditions)
		{
			condition.updateCondition(this);
		}
	}

	/** Adds a condition to this unit's list of conditions */
	public void addCondition(Condition.Type type)
	{
		conditions.add(new Condition(type, this));
	}

	/** Damages the unit by a certain amount
	 * @param amt how much to damage the unit by
	 * @return the updated health */
	public int damage(int amt)
	{
		return currentStats.changeHealth(-amt);
	}

	/** Slows the unit's movement range by a certain amount
	 * @param amt how much to slow the unit by
	 * @return the updated movement range */
	public int slow(int amt)
	{
		return currentStats.changeMovement(amt);
	}

	/** Restores the unit's movement stats to its base amount
	 * @return the restored movement range */
	public int restoreMovement()
	{
		return currentStats.changeMovement(baseStats.getMovement()
				- currentStats.getMovement());
	}

	/** Sets the unit to assume a pose and run that animation once, then move on
	 * to another pose
	 * @param current the pose to take on and play once
	 * @param next the pose to move on to once the current animation is done */
	public void playOnce(Pose current, Pose next)
	{
		setPose(current);
		currentSprite.playOnce();
		nextPose = next;
		playOnce = true;

	}

	/** Determines if the unit has finished animating
	 * @return true if the animation has completed, false if not */
	public boolean isDoneAnim()
	{
		return !playOnce;
	}

	/** Sets the unit to a certain pose
	 * @param pose the pose to assume */
	public void setPose(Pose pose)
	{
		currentSprite = new Sprite(unitEntry.getSprite(pose));
	}

	/** Compares two units by the name of their type */
	public int compareTo(Unit other)
	{
		return spriteName.compareTo(other.spriteName);
	}

	/** Advances the stage of this unit by 1
	 * @return the updated stage */
	public int advStage()
	{
		return ++stage;
	}

	/** Increases the stage of the unit by a specified amount
	 * @param amt how much to advance the stage by
	 * @return the updated stage */
	public int advStage(int amt)
	{
		stage += amt;
		return stage;
	}

	/** Sets the stage of the unit back to 0 */
	public void resetStage()
	{
		stage = 0;
	}

	/** Sets the unit to a state where it has exhausted all of its move phases
	 * and cannot make any more */
	public void setUnusable()
	{
		stage = maxMoves;
	}

	/** Determines if a unit can still make a move or not
	 * @return true if the unit can make more moves, false if not */
	public boolean isUsable()
	{
		return stage < maxMoves;
	}

	/** Determines if the target is attackable from this unit if this unit was at
	 * a certain center position
	 * @param center the position of this unit
	 * @param target the position of its target
	 * @return true if this unit can attack the target, false if not */
	public boolean isInAttackRange(Point center, Point target)
	{
		return getAttack().isTargetable(center, target);
	}

	/** Determines if a unit is allied with this unit (from the same player)
	 * @param other the other unit to check
	 * @return true if these units are from the same player, false if they are
	 *         not */
	public boolean isAllied(Unit other)
	{
		return player.hasUnit(other);
	}

	/** Determines if the unit is currently alive (it still has health)
	 * @return true if the unit is alive, false if not */
	public boolean isAlive()
	{
		return currentStats.getHealth() > 0;
	}

	/** Gets the team color of the unit (which is based on the unit's owner)
	 * @return The team color of the unit */
	public Color getColor()
	{
		return outlineColor;
	}

	/** Gets the default attack of a unit
	 * @return The basic attack action of this unit */
	public Action getAttack()
	{
		return attack;
	}

	/** Gets the movement ability of a unit
	 * @return The movement ability of the unit */
	public Action getMove()
	{
		return move;
	}

	/** Gets a list of all the abilities the unit has
	 * @return A collection of the unit's abilities */
	public Collection<Action> getAbilities()
	{
		return abilities;
	}

	/** Gets how far a unit can move
	 * @return the current movement range of theu nit */
	public int getMoveRange()
	{
		return currentStats.getMovement();
	}

	/** Gets the base stats of the unit
	 * @return the base stats of the unit */
	public StatSet getBaseStats()
	{
		return baseStats;
	}

	/** Gets the stats the unit is currently at
	 * @return the current stats */
	public StatSet getCurrentStats()
	{
		return currentStats;
	}

	/** Gets what stage the unit is currently at
	 * @return the stage the unit is currently at */
	public int getStage()
	{
		return stage;
	}

	/** Gets what sprite the unit is currently assuming (which depends on its
	 * pose)
	 * @return the current sprite of the unit */
	public Sprite getSprite()
	{
		return currentSprite;
	}

	/** Returns the portrait for this unit
	 * @return Returns this unit's portrait */
	public Sprite getPortrait()
	{
		return unitEntry.getPortrait();
	}

	/** Returns the name of the unit
	 * @return the name of the unit */
	public String toString()
	{
		return spriteName;
	}

	/** Draws the unit's current sprite
	 * @param g the graphics to draw with
	 * @param x the x coordinate to draw at
	 * @param y the y coordinate to draw at
	 * @param scale the scale of the unit
	 * @param container the container to draw on */
	public void draw(Graphics g, int x, int y, int scale, Container container)
	{
		if (isUsable())
			g.setColor(outlineColor);
		else
			g.setColor(Color.gray);
		for (int border = 1; border <= outlineWidth; border++)
			g.drawRect(x + border, y + border, scale - 2 * border, scale - 2
					* border);

		int height = currentSprite.getImage().getHeight(container);
		int width = currentSprite.getImage().getWidth(container);
		width = (int) (scale * (1.0 * width / height));
		// Center the unit
		x = x - (width / 2 - scale / 2);
		currentSprite.draw(g, x, y, width, scale, container);

		if (playOnce && currentSprite.isComplete())
		{
			playOnce = false;
			setPose(nextPose);
		}

	}

	/** Draws the unit's current sprite
	 * @param g the graphics to draw with
	 * @param p the point to draw at
	 * @param scale the scale of the unit
	 * @param container the container to draw on */
	public void draw(Graphics g, Point p, int scale, Container container)
	{
		draw(g, p.x, p.y, scale, container);
	}

}
