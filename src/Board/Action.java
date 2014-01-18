package Board;

import java.awt.Point;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import Board.Action.Type.Target;

/** Action class Handles an action that a unit can use on either a Tile or
 * another Unit
 * 
 * @author Shiranka Miskin
 * @version January 2013 */

public class Action
{


	private Type type; // What spell this action is
	private Unit user; // The user of the spell
	private int bounds; // The area this affect can target (used for getting all
						// targetable points)

	/** Creates an action of a certain type for a unit
	 * 
	 * @param spell the type of action
	 * @param user the unit that is casting it */
	public Action(Type spell, Unit user)
	{
		type = spell;
		this.user = user;
		setBounds();
	}

	/** Sets the maximum range of the action based on its type Bounds is used to
	 * aid the getTargetablePoints method */
	private void setBounds()
	{
		switch (type)
		{
		case ATTACK:
			bounds = user.getCurrentStats().getRange();
			break;
		case MOVE:
			bounds = user.getCurrentStats().getMovement();
			break;
		}
	}

	/** Applies this ability's effect to a unit
	 * 
	 * @param unit the unit to affect */
	public void affectUnit(Unit unit)
	{
		switch (type)
		{
		case ATTACK:
			unit.damage(unit.getCurrentStats().getAttack());
			break;
		}
	}

	/** Applies this ability's effect to a tile
	 * 
	 * @param tile the tile to affect */
	public void affectTile(Tile tile)
	{
		switch (type)
		{

		}
	}

	/** Returns if the target able to be targeted by the ability
	 * 
	 * @param target The target of the ability whether it be a tile or a unit
	 * @param user The player who owns caster of the ability
	 * @return true if the target can be affected by this ability, false if not */
	public boolean isValidTarget(Object target, Unit user)
	{
		if (target instanceof Unit)
		{
			if (getTarget() == Target.ALLY)
				return user.isAllied((Unit) target);
			else if (getTarget() == Target.ENEMY)
				return !user.isAllied((Unit) target);
		} else if (target instanceof Tile)
		{
			if (getTarget() == Target.TILE)
				return true;
		}
		return false;

	}

	/** Determines if the grid location on the board contains a valid target for
	 * this ability
	 * 
	 * @param p the grid location
	 * @param board the board to check
	 * @return true if the ability can target that location, false if not */
	public boolean isValidTarget(Point p, Board board)
	{
		if (board.getUnitAt(p) != null)
		{
			if (getTarget() == Target.ALLY)
				return user.isAllied(board.getUnitAt(p));
			else if (getTarget() == Target.ENEMY)
				return !user.isAllied(board.getUnitAt(p));
		} else if (getTarget() == Target.TILE)
			return true;
		return false;
	}

	/** Determines if an ability activated from a point can target another point
	 * Used for certain abilities that might only attack in a line or other
	 * specifics
	 * 
	 * @param center The source of the ability
	 * @param target The targeted point of the ability
	 * @return True if the action can target that point, false if not */
	public boolean isTargetable(Point center, Point target)
	{
		// Finds the Manhattan distance between two points
		int distance = Math.abs(center.x - target.x)
				+ Math.abs(center.y - target.y);
		switch (type)
		{
		// The basic attack damages anywhere in the Manhattan distance of
		// the unit's range
		case ATTACK:
			return distance < bounds;
		}
		return false;
	}

	/** Calculates all points that are targetable by an ability
	 * 
	 * @param center the origin of the skill
	 * @return a collection of all points that this skill can affect */
	public Collection<Point> getTargetablePoints(Point center)
	{
		HashSet<Point> points = new HashSet<Point>();
		for (int x = -bounds; x < bounds; x++)
			for (int y = -bounds; y < bounds; y++)
			{
				Point target = new Point(center);
				target.translate(x, y);
				if (isTargetable(center, target))
					points.add(target);
			}
		return points;
	}

	/** Returns a list of all locations that this ability can affect (useful for
	 * Area of Effect)
	 * 
	 * @param p The point that is targeted
	 * @return A set of all points around the target that are affected */
	public HashSet<Point> getAffectedLocations(Point p)
	{
		HashSet<Point> affectedPoints = new HashSet<Point>();
		switch (type)
		{
		case MOVE:
			affectedPoints.add(p);
			break;
		case ATTACK:
			affectedPoints.add(p);
			break;
		}

		return affectedPoints;
	}

	/** Returns the name of the action
	 * 
	 * @return the name of the action */
	public String getName()
	{
		return type.getName();
	}

	/** Gets the type of action it is
	 * 
	 * @return the type of action */
	public Type getType()
	{
		return type;
	}

	/** Returns the type of ability that is associated with this action
	 * 
	 * @return the type of ability */
	public Target getTarget()
	{
		return type.target;
	}

	/** All the types of abilities that can be casted Each ability has their
	 * name, icon, and what kind of target they can attack, whether it be an
	 * ally (for healing/buffs), an enemy (for damage/debuffs), or for tiles
	 * (affecting units that walk on it)
	 * 
	 * @author Shiranka Miskin & Derek Chylinski
	 * @version January 2013 */
	public enum Type {
		MOVE("Move", Target.TILE), NOTHING("Nothing", Target.TILE), ATTACK(
				"Attack", Target.ENEMY), FOCUSENERGY("FocusEnergy", Target.ALLY), FROST(
				"Frost", Target.TILE), BLEED("Bleed", Target.ENEMY), GODSPEED(
				"Godspeed", Target.ALLY), HEAL("Heal", Target.ALLY), POISON(
				"Poison", Target.ENEMY), SLOWDOWN("Slowdown", Target.ENEMY), QUAKE(
				"Quake", Target.ENEMY), BATTLECRY("Battlecry", Target.ALLY), BLINK(
				"Blink", Target.ALLY);

		private String name;
		private Sprite icon;
		private Target target;

		/** The different types of targets an action can have
		 * 
		 * @author Shiranka Miskin */
		public enum Target {
			ENEMY, ALLY, TILE;
		}

		/** Creates a type of ability along with the icon that goes with it
		 * 
		 * @param name the name of the ability
		 * @param target the type of target it hits */
		Type(String name, Target target)
		{
			this.name = name;
			this.target = target;
			try
			{
				icon = new Sprite("res/UI/" + name + "Icon.gif");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		/** Returns the type of target the ability can hit
		 * 
		 * @return the type of target */
		public Target getTarget()
		{
			return target;
		}

		/** Gets the name of an ability
		 * 
		 * @return the name of this type of ability */
		public String getName()
		{
			return name;
		}

		/** Gets the icon that is associated with this ability
		 * 
		 * @return the icon for this ability */
		public Sprite getIcon()
		{
			return icon;
		}
	}

}
