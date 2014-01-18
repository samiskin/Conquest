package Board;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Board.Action.Type;

/** The UnitEntry class holds all the default data for a specific unit 
 * @author Shiranka Miskin
 * @version January 2013
 */
public enum UnitEntry {
	BESERKER("beserker", 35, 11, 8, 3, Type.FOCUSENERGY), 
	MAGE("mage", 25, 8, 8, 7, Type.FROST), 
	ROGUE("rogue", 20, 6, 12, 7, Type.BLEED), 
	SPELLSWORD("spellsword", 30, 7, 9, 6, Type.GODSPEED), 
	CLERIC("cleric", 25, 6, 7, 10, Type.HEAL), 
	SLIME("slime", 25, 7, 7, 7, Type.SLOWDOWN), 
	GHOST("ghost", 20, 7, 8, 12, Type.BLINK), 
	GOLEM("golem", 35, 10, 7, 7, Type.QUAKE), 
	SNAKE("snake", 25, 8, 8, 9, Type.POISON), 
	ORC("orc", 30, 7, 6, 8, Type.BATTLECRY);
	
	
	private Map <Pose, Sprite> sprites;
	private Sprite portrait;
	private static final String spriteDir ="res/units/"; 	

	private final String name;
	private final Type ability;
	private final StatSet defaultStats;
	
	
	/** Creates a new entry
	 * @param name		the name of the unit
	 * @param health	the base health of the unit
	 * @param attack	the base attack of the unit
	 * @param movement	the base movement range of the unit
	 * @param range		The base attack range of the unit
	 */
	UnitEntry(String name, int health, int attack, int movement, int range, Type ability)
	{
		this.name = name;
		defaultStats = new StatSet(health,attack,movement,range);
		this.ability = ability;
		loadSprites(spriteDir+name);
	}
	
	
	/** Loads all the sprites for this unit
	 * @param fileName	the name of the unit
	 */
	private void loadSprites(String fileName){
		sprites = new HashMap<Pose,Sprite>();
		try {
			// Load the sprites for the damage animation
			Sprite damage = new Sprite(fileName+"_damage",".gif");
			sprites.put(Pose.DAMAGE,damage);
			// Load the sprites for the base/idle animation
			Sprite idle = new Sprite(fileName+"_idle",".gif");
			sprites.put(Pose.IDLE,idle);
			// Load the sprites for the move up animation
			Sprite moveUp = new Sprite(fileName+"_move_back",".gif");
			sprites.put(Pose.MOVE_UP,moveUp);
			// Load the sprites for the move down animation
			Sprite moveDown = new Sprite(fileName+"_move_front",".gif");
			sprites.put(Pose.MOVE_DOWN,moveDown);
			// Load the sprites for the move left animation
			Sprite moveLeft = new Sprite(fileName+"_move_left",".gif");
			sprites.put(Pose.MOVE_LEFT,moveLeft);
			// Load the sprites for the move right animation
			Sprite moveRight = new Sprite(fileName+"_move_right",".gif");
			sprites.put(Pose.MOVE_RIGHT,moveRight);
			// Load the sprites for the attack down animation
			Sprite attackDown = new Sprite(fileName+"_attack_front",".gif");
			sprites.put(Pose.ATK_DOWN,attackDown);
			// if the unit has uni-directional attacks, it is stored under attack down.
			try {
				Sprite attackUp = new Sprite(fileName+"_attack_back",".gif");
				sprites.put(Pose.ATK_UP,attackUp);
				
				Sprite attackLeft = new Sprite(fileName+"_attack_left",".gif");
				sprites.put(Pose.ATK_LEFT,attackLeft);
				
				Sprite attackRight = new Sprite(fileName+"_attack_right",".gif");
				sprites.put(Pose.ATK_RIGHT,attackRight);
				// try to find directional attacks but if none are found store attack down as
				// all four directional attacks.
			} catch (IOException e) {
				
				sprites.put(Pose.ATK_UP, sprites.get(Pose.ATK_DOWN)); 
				sprites.put(Pose.ATK_LEFT, sprites.get(Pose.ATK_DOWN)); 
				sprites.put(Pose.ATK_RIGHT, sprites.get(Pose.ATK_DOWN)); 
			}				
			
			try{
				portrait = new Sprite(spriteDir+name+"_portrait.gif");
			} catch (IOException e) {
				portrait = new Sprite(spriteDir+"blank_portrait.gif");
			}
		} catch (IOException e) {
				System.out.println(fileName);
			e.printStackTrace();
		}
	}
	
	/** Gets the appropriate sprite for this unit depending on its pose
	 * @param pose	the pose of the unit
	 * @return	the sprite of this unit in the specified pose
	 */
	public Sprite getSprite(Pose pose)
	{
		return sprites.get(pose);
	}
	
	/** Gets the unit portrait of the unit
	 * @return	the unit portrait
	 */
	public Sprite getPortrait()
	{
		return portrait;
	}
	
	/** Returns the name of this type of unit
	 * @return the name of the unit
	 */
	public String toString()
	{
		return name;
	}
	
	/** Gets the stats of the unit
	 * @return	the stats of the unit
	 */
	public StatSet getStats()
	{
		return defaultStats;
	}
	
	/** Gets the ability of the unit
	 * @return	the ability of the unit
	 */
	public Type getAbility() 
	{
		return ability;
	}
	
	
	/** An enumeration of all possible poses for a unit
	 * @author Shiranka Miskin
	 * @version January 2013
	 */
	public enum Pose{
		IDLE,DAMAGE,MOVE_UP,MOVE_DOWN,MOVE_LEFT,MOVE_RIGHT,ATK_UP,ATK_DOWN,ATK_LEFT,ATK_RIGHT;
	}

}
