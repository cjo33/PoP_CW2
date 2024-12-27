import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {

    // Reads the map file and stores in an array
    public static char[][] loadMap(String filePath) {
        // Stores the rows of the map
        ArrayList<char[]> rows = new ArrayList<>();
        
        // Attempts to read the map file
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Iterates through br to add each character to the array
            while ((line = br.readLine()) != null){
                char[] row = line.toCharArray();
                rows.add(row);
            }
        } 
        // Displays error method if it can't read the map file
        catch (IOException e) {
            System.out.println("Error loading map: " + e.getMessage());
        }
        
        // Convert the array list of rows into a 2D array
        char[][] map = new char[rows.size()][];
        // Iterate through each row and add it to the map
        for (int i = 0; i < rows.size(); i++){
            map[i] = rows.get(i);
        }
        return map;
    }

    // This finds where all the gold is located on the map
    public static List<int[]> findGoldCoords(char[][] dungeonMap) {
        // Initialise our array list of coordinates
        List<int[]> goldCoords = new ArrayList<>();
        // Loop through each tile in the map and check if its a gold tile
        // If so then add the coordinates of it to the list
        for (int y = 0; y< dungeonMap.length; y++){
            for (int x = 0; x < dungeonMap[y].length; x++) {
                if (dungeonMap[y][x] == 'G'){
                    goldCoords.add(new int[] { x, y });
                }
            }
        }
        // Return the array of gold coordinates
        return goldCoords;
    }

    // This finds the exit coords that is used by the bot 
    // in working out the closest target
    public static int[] findExitCoords(char[][] dungeonMap) {
        // Loops through the y and x coords searching for the E tile
        for (int y = 0; y < dungeonMap.length; y++) {
            for (int x = 0; x < dungeonMap[y].length; x++) {
                if (dungeonMap[y][x] == 'E') {
                    // Returns the coords of the exit tile
                    return new int[] { x, y };
                }
            }
        }
        // Return null if no exit tile is found, this is checked at the start
        // ensures the player can win the game
        return null; 
    }

    // This generates a valid random tile that the bot and player can start on
    public static int[] findRandomTile(char[][] dungeonMap) {
        // Initialises an array that holds all valid input tiles
        List<int[]> floorTiles = new ArrayList<>();

        // Iterates through the y an x coords searching for '.' tiles
        for (int y = 0; y < dungeonMap.length; y++) {
            for (int x = 0; x < dungeonMap[y].length; x++) {
                if (dungeonMap[y][x] == '.') {
                    // Adds the coords of these '.' tiles to the array
                    floorTiles.add(new int[] { x, y });
                }
            }
        }

        // Checks to ensure that there are valid starting tiles
        // This should be changed to ensur there are more than 2 valid tiles and then just a more simple error
        if (floorTiles.isEmpty()) {
            // throw new IllegalStateException("No valid floor tiles ('.') available on the map!");
            System.exit(0);
        }

        // Picks a random tile from the list
        Random random = new Random();
        return floorTiles.get(random.nextInt(floorTiles.size()));
    }

    // This calculates the maximum potential distance between the players
    public static int maxDistance(char[][] dungeonMap){
        // calculate the maximum distance betwteen two players could be in the map
        // -7 because of the walls and the player tiles taking up space
        int maxDist = dungeonMap[0].length + dungeonMap.length - 7;
        // Then return relative to this value, the minimum distance apart you want the players start at
        // Must be divided by more than 1, the closer to 1 the easier the game
        return (int) Math.round(maxDist/1.5);
    }

    // Checks the player and the bot are starting far enough apart
    public static boolean checkDistance(Player player, Bot bot, char[][] dungeonMap){
        // Calculates the distance between the players
        int distance = Math.abs(player.getX() - bot.getX()) + Math.abs(player.getY() - bot.getY());
        // Returns true if the distance bewtween the players is greater than our minimum intial starting distance
        return distance > maxDistance(dungeonMap);
    }

    public static char[][] generateView(char[][] dungeonMap, int centerX, int centerY) {
        int mapHeight = dungeonMap.length;
        int mapWidth = dungeonMap[0].length;
        int viewSize = 9;
        char[][] view = new char[viewSize][viewSize];

        int halfSize = viewSize / 2;

        for (int i = 0; i < viewSize; i++) {
            for (int j = 0; j < viewSize; j++) {
                int mapY = centerY - halfSize + i;
                int mapX = centerX - halfSize + j;

                // Fill with '#' if out of bounds, otherwise take from the dungeon map
                if (mapY < 0 || mapY >= mapHeight || mapX < 0 || mapX >= mapWidth) {
                    view[i][j] = '#';
                } else {
                    view[i][j] = dungeonMap[mapY][mapX];
                }
            }
        }

        return view;
    }

}
