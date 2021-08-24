package main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Player
{
	
	static String clientKey = "11fc95e3-d3e7-4852-8a6c-0b4da04b93bb";  // Client authentication token.
	
	String name;				// Faceit nickname.
	String playerId;			// Internal Faceit player ID.
	
	int eloRating;				// Faceit ELO rating.
	
	boolean validated;			// If false the player was not found in the FaceIt DB.
	
	// Player map statistics for each map.
	double[][] mapStatistics = {
		{0.0, 0.0, 0.0},	// Nuke			Table Layout:
		{0.0, 0.0, 0.0},	// Vertigo		WIN PERCENTAGE, K/D RATIO, AMOUNT OF MATCHES
		{0.0, 0.0, 0.0},	// Train		
		{0.0, 0.0, 0.0},	// Cache		
		{0.0, 0.0, 0.0},	// Overpass		
		{0.0, 0.0, 0.0},	// Mirage		
		{0.0, 0.0, 0.0},	// Inferno
		{0.0, 0.0, 0.0}		// Dust 2
	};
	
	/* Constructor for the Player class.
	 * name: The Faceit nickname of the player.
	 */
	public Player(String name)
	{
		this.name = name;
		this.validated = false;
		
		getPlayerData(this.name);
		
		if(this.validated)
		{
			getPlayerStats(this.playerId);			
		}
	}
	
	/* Constructor for the player class that allows manual setting of player information.
	 * name: The Faceit nickname of the player.
	 * playerId: The Faceit internal player ID.
	 * elo: The players Faceit ELO rating.
	 * mapStats: A populated array of map statistics.
	 */
	public Player(String name, String playerId, int elo, double[][] mapStats)
	{
		this.name = name;
		this.playerId = playerId;
		this.eloRating = elo;
		this.mapStatistics = mapStats;
	}

	/* Returns the calculated point value for a specific map.
	 * index: Index in the map array to calculate for.
	 */
	public int getMapPointValue(int index)
	{
		double _winPercentage = mapStatistics[index][0];
		double _killDeathRatio = mapStatistics[index][1];
		double _matchCount = mapStatistics[index][2];
		
		return (int) ((_winPercentage * Math.sqrt(_matchCount) * _killDeathRatio) * (eloRating / 1650.0));
	}
	
	/* Populates general player data from Faceit.
	 * name: The Faceit nickname of the player to query for.
	 */
	@SuppressWarnings("rawtypes")
	private void getPlayerData(String name)
	{
		// Player data request: https://open.faceit.com/data/v4/players?nickname=<PLAYER NAME>
		String _requestUrl = "https://open.faceit.com/data/v4/players?nickname=" + this.name;
		try
		{			
			// Getting internal player ID.
			JSONObject _parser = (JSONObject) new JSONParser().parse(httpRequest(_requestUrl));
			this.playerId = (String) _parser.get("player_id");

			// Getting player ELO rating.
			Map _games = ((Map) _parser.get("games"));	        
	        Map _csgo = ((Map) _games.get("csgo"));
	        this.eloRating = Math.toIntExact((long) _csgo.get("faceit_elo"));
	        
	        this.validated = true;
		}
		catch(Exception e)
		{
			System.out.println("ERROR: " + name + " could not be found!");
		};
	}
	
	/* Populates CS:GO player stats from Faceit.
	 * playerId: The Faceit player ID of the player to query for.
	 */
	@SuppressWarnings("rawtypes")
	private void getPlayerStats(String playerId)
	{
		// Player CS:GO stats request: https://open.faceit.com/data/v4/players/<PLAYER ID>/stats/csgo
		String _requestUrl = "https://open.faceit.com/data/v4/players/" + this.playerId + "/stats/csgo";
		try
		{
			JSONObject _parser = (JSONObject) new JSONParser().parse(httpRequest(_requestUrl));
			
			// Iterating through each map.
			JSONArray _maps = (JSONArray) _parser.get("segments");
			Iterator _iterator = _maps.iterator();
			while (_iterator.hasNext()) {
	            JSONObject _map = (JSONObject) _iterator.next();
				String _mapName = (String) _map.get("label");
				int _index = 0;
				
	            if(_map.get("mode").equals("5v5"))
	            {
	            	switch(_mapName)
	            	{
		            	case "de_nuke":
	            			_index = 0;
		            		break;
		            	case "de_vertigo":
	            			_index = 1;
		            		break;
		            	case "de_train":
	            			_index = 2;
		            		break;
		            	case "de_cache":
	            			_index = 3;
		            		break;
		            	case "de_overpass":
	            			_index = 4;
		            		break;
		            	case "de_mirage":
	            			_index = 5;
		            		break;
		            	case "de_inferno":
	            			_index = 6;
		            		break;
		            	case "de_dust2":
	            			_index = 7;
		            		break;
	            	}
	            	
	            	Map _stats = ((Map) _map.get("stats"));
	            	
	            	this.mapStatistics[_index][0] = Double.parseDouble((String) _stats.get("Win Rate %"));
	            	this.mapStatistics[_index][1] = Double.parseDouble((String) _stats.get("Average K/D Ratio"));
	            	this.mapStatistics[_index][2] = Double.parseDouble((String) _stats.get("Matches"));
	            }
	        }
			
            System.out.println("Successfully gathered data for player: " + this.name);
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		};
	}
	
	/* Sents an HTTP GET request to a specific IP.
	 * url: The url to request from.
	 */
    private static String httpRequest(String url) throws IOException, InterruptedException
    {
    	HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
        	.uri(URI.create(url))
        	.header("Authorization", "Bearer " + clientKey)
        	.build();

        HttpResponse<String> response = client.send(request,
        	HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
	
}