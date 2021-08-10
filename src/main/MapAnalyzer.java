package main;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class MapAnalyzer {

	static String clientKey = "11fc95e3-d3e7-4852-8a6c-0b4da04b93bb";  // Client authentication token.
	
	public static void main(String[] args) {
		
		Player[] players = new Player[5];	// Array to hold every player on opposing team.
		GameMap[] maps = new GameMap[8];	// Array to hold map information.
		
		Scanner scanner = new Scanner(System.in);
		
		// Getting player input.
		System.out.println("Enter opposing team names (seperate each name with a space).");
		System.out.println("NOTE: Faceit nicknames ARE case sensitive!\n");
		
		String[] playerNames = scanner.nextLine().split(" ");
		
		// Validating the amount of players.
		while(playerNames.length < players.length)
		{
			System.out.println("\nERROR: The amount of players: " + playerNames.length + ", is too low!");
			
			System.out.println("\nEnter opposing team names (seperate each name with a space).");
			System.out.println("NOTE: Faceit nicknames ARE case sensitive!\n");
			playerNames = scanner.nextLine().split(" ");
		}
		
		scanner.close();  // Closing scanner to prevent memory leak.
		
		// Populating player array.
		for(int i = 0; i < players.length; i++)
		{
			players[i] = new Player(playerNames[i]);
		}
		
		// Populating mapValue array.
		for(int a = 0; a < 8; a++)
		{
			int _mapValue = 0;
			for(int b = 0; b < players.length; b++)
			{
				_mapValue += players[b].getMapPointValue(a);
			}
			
			switch(a)
			{
				case 0:
					maps[a] = new GameMap("Nuke", _mapValue);
					break;
				case 1:
					maps[a] = new GameMap("Vertigo", _mapValue);
					break;
				case 2:
					maps[a] = new GameMap("Train", _mapValue);
					break;
				case 3:
					maps[a] = new GameMap("Cache", _mapValue);
					break;
				case 4:
					maps[a] = new GameMap("Overpass", _mapValue);
					break;
				case 5:
					maps[a] = new GameMap("Mirage", _mapValue);
					break;
				case 6:
					maps[a] = new GameMap("Inferno", _mapValue);
					break;
				case 7:
					maps[a] = new GameMap("Dust 2", _mapValue);
					break;
			}
		}
		
		// Sorting map list.
		Arrays.sort(maps, new Comparator<GameMap>() {
			@Override
		    public int compare(GameMap map1, GameMap map2) {
				if (map1.value < map2.value)
					return 1;
				if (map1.value > map2.value)
					return -1;
				return 0;
			}
		});
		
		// Displaying ban order.
		System.out.println("\nBan maps in this order: ");
		int count = 1;
		for(GameMap map : maps)
		{
			System.out.format("%-15s%19s", count + ": " + map.name + "  ", formatLargeIntegers(map.value) + "\n");
			count++;
		}
		
	}
	
	/* Formats large numbers with commas in between every 3 decimal places.
	 * value: The value to format.
	 */
	public static String formatLargeIntegers(int value) {
	    DecimalFormat format = new DecimalFormat("###,###,###");
	    return format.format(value);
	}
	
}