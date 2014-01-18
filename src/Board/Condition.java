package Board;

/** The Condition Class for various conditions that can be placed on units
 * because of abilites used by enemy or allied units
 * Currently unused as more complex abilities are not added at this moment
 * @author Derek Chylinski
 * @version January 2013 
 */
public class Condition
{

	private int turnsLeft; // how many turns are left in the condition before it
							// expires
	private Type type; // what type of condition the condition is
	private Unit unit; // the unit that has this condition on it

	/** Condition Constructor makes a new condition based on a given condition
	 * and unit it affects
	 * @param condition the type of condition
	 * @param unit the unit to affect */
	Condition(Type condition, Unit unit)
	{
		type = condition;
		this.unit = unit;
		turnsLeft = type.duration;
		// conditions that change stats over time start affecting stats here
		switch (type)
		{
		case GODSPEED:
			unit.slow(-2);
			break;
		case SLOWDOWN:
			unit.slow(2);
			break;
		case BATTLECRY:
			break;
		}
	}

	/** Gets the type of action it is
	 * @return the type of action */
	public Type getType()
	{
		return type;
	}

	/** Updates the condition and effects the unit that the condition effects.
	 * @param unit the unit the condition effects */
	public void updateCondition(Unit unit)
	{
		switch (type)
		{
		case POISON:
			unit.damage(2);
			break;
		case BLEED:
			unit.damage(2);
			break;
		}
	}

	/** An enumeration of each type of condition
	 * @author Derek Chylinski */
	public enum Type {
		FOCUS(1), BLEED(2), GODSPEED(3), SLOWDOWN(2), POISON(2), BATTLECRY(3);


		private final int duration;

		/** Creates a new type of condition
		 * @param duration how many turns the condition lasts */
		Type(int duration)
		{
			this.duration = duration;
		}

		/** returns the duration of a given condition
		 * @return the duration of the given condition */
		public int getDuration()
		{
			return duration;
		}
	}
}
