package Board;

/** An object that stores all the stats a unit can have, created in order to
 * allow for simple parameters throughout the program.
 * @author Shiranka Miskin
 * @version January 2013 */
public class StatSet
{

	private int health;
	private int attack;
	private int movement;
	private int range;

	/** Creates an object storing all the stats of a unit
	 * @param health The health of the unit
	 * @param attack The attack damage of the unit
	 * @param movement The movement range of the unit
	 * @param range The attack range of the unit */
	public StatSet(int health, int attack, int movement, int range)
	{
		this.health = health;
		this.attack = attack;
		this.movement = movement;
		this.range = range;
	}

	/** Creates a copy of previous stats
	 * @param sourceStats the previous stats to copy */
	public StatSet(StatSet sourceStats)
	{
		health = sourceStats.health;
		attack = sourceStats.attack;
		movement = sourceStats.movement;
		range = sourceStats.range;
	}

	/** Changes the current health by a certain amount
	 * @param dH The change in health
	 * @return The new updated health */
	public int changeHealth(int dH)
	{
		health += dH;
		return health;
	}

	/** Changes the current attack damage by a certain amount
	 * @param dA the change in attack damage
	 * @return the new updated attack damage */
	public int changeAttack(int dA)
	{
		attack += dA;
		return attack;
	}

	/** Changes the current movement range by a certain amount
	 * @param dM the change in movement range
	 * @return the new updated movement range */
	public int changeMovement(int dM)
	{
		movement += dM;
		return movement;
	}

	/** Changes the current basic attack range by a certain amount
	 * @param dR The change in basic attack range
	 * @return */
	public int changeRange(int dR)
	{
		range += dR;
		return range;
	}

	/** Gets the current health stat
	 * @return The current health stat */
	public int getHealth()
	{
		return health;
	}

	/** Gets the current attack damage stat
	 * @return The current attack damage stat */
	public int getAttack()
	{
		return attack;
	}

	/** Gets the current movement range stat
	 * @return The current movement range */
	public int getMovement()
	{
		return movement;
	}

	/** Gets the current basic attack range stat
	 * @return The current basic attack range */
	public int getRange()
	{
		return range;
	}

}
