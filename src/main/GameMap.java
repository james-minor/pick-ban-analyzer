package main;

public class GameMap {

	String name;	// The name of the CS map.
	int value;		// The point value assigned to the map.
	
	/* Constructor for GameMap objects.
	 * name: The name of the map. (ex: Dust 2, Mirage, etc).
	 * value: The point value calculated for the map.
	 */
	public GameMap(String name, int value)
	{
		this.name = name;
		this.value = value;
	}
	
}
