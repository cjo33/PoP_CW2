import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map {
    /// This will find the text file
    /// Then go through each line and add it to an array
    
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
            System.out.println("Error when loading the map file" + e.getMessage());
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

    public static int[] findExitCoords(char[][] dungeonMap) {
        for (int y = 0; y < dungeonMap.length; y++) {
            for (int x = 0; x < dungeonMap[y].length; x++) {
                if (dungeonMap[y][x] == 'E') {
                    return new int[] { x, y }; // Return the coordinates of the exit tile
                }
            }
        }
        return null; // Return null if no exit tile is found
    }
}
