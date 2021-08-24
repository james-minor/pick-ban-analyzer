package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class MapAnalyzer
{

	static String clientKey = "11fc95e3-d3e7-4852-8a6c-0b4da04b93bb";  // Client authentication token.
	static JTextArea console = new JTextArea(9, 25);
	
	public static void main(String[] args)
	{
		// Setting look and feel of app.
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		// Initialzing the JFrame.
		JFrame frame = new JFrame("Pick-Ban Analyzer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setResizable(false);

		// Setting the frame Icons.
		ImageIcon img = new ImageIcon("resources/icon.png");
		frame.setIconImage(img.getImage());
		
		// Defining component JPanels.
		JPanel textInputPanel = new JPanel();
		textInputPanel.setLayout(new GridLayout(5, 2));
		
		JPanel outputPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		// Initializing the text input fields.
		JTextField[] textFields = new JTextField[5];
		for(int i = 0; i < textFields.length; i++)
		{
			JPanel _panel = new JPanel();
			
			JTextField field = new JTextField(20);
			field.setToolTipText("Please enter an opposing players Faceit nickname.");
			
			_panel.add(new JLabel("Player Name " + (i + 1) + ":"));
			_panel.add(field);
			
			textInputPanel.add(_panel);
			textFields[i] = field;
		}
		
		// Setting console parameters.
		console.setEditable(false);
		outputPanel.add(console);
				
		// Adding JButtons.
		JButton clearButton = new JButton("Clear Names");
		clearButton.setPreferredSize(new Dimension(180, 20));
		buttonPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for(JTextField field : textFields)
				{
					field.setBackground(Color.WHITE);
					field.setText("");
				}
			}
		});
		
		JButton submitButton = new JButton("Submit Names");
		submitButton.setPreferredSize(new Dimension(180, 20));
		buttonPanel.add(submitButton);
		submitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				console.setText("");
				
				ArrayList<Player> playerList = new ArrayList<Player>();
				
				for(JTextField field : textFields)
				{
					field.setBackground(Color.WHITE);
					
					if(!field.getText().equals(""))
					{
						Player _player = new Player(field.getText());
						if(_player.validated)
						{
							playerList.add(_player);
						}
						else
						{
							field.setBackground(Color.PINK);
						}
					}
				}
				
				Player[] players = new Player[playerList.size()];
				for(int i = 0; i < playerList.size(); i++)
				{
					players[i] = playerList.get(i);
				}
				
				analyzePlayers(players);
			}
		});
		
		// Finalizing the JFrame.
		frame.getContentPane().add(BorderLayout.NORTH, textInputPanel);
		frame.getContentPane().add(BorderLayout.CENTER, outputPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
		frame.getRootPane().setDefaultButton(submitButton);
		frame.pack();
		frame.setVisible(true);
	}
	
	/* Analyzes and returns a GameMap array of the maps to ban.
	 * players: An array of Player objects to fetch data for.
	 */
	public static void analyzePlayers(Player[] players)
	{
		GameMap[] maps = new GameMap[8];	// Array to hold map information.
		
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
		Arrays.sort(maps, new Comparator<GameMap>()
		{
			@Override
		    public int compare(GameMap map1, GameMap map2)
			{
				if (map1.value < map2.value)
					return 1;
				if (map1.value > map2.value)
					return -1;
				return 0;
			}
		});
		
		// Displaying ban order.
		if(players.length > 0)
		{
			console.append("Ban maps in this order: \n");
			int count = 1;
			for(GameMap map : maps)
			{
				String output = (count + ": " + map.name + "\n");
				console.append(output);
				
				count++;
			}	
		}
		else
		{
			console.append("Please input a Faceit nickname.\n");
		}
	}
	
	/* Formats large numbers with commas in between every 3 decimal places.
	 * value: The value to format.
	 */
	public static String formatLargeIntegers(int value)
	{
	    DecimalFormat format = new DecimalFormat("###,###,###");
	    return format.format(value);
	}
	
}