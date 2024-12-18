import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class map_load {
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

    // Print for testing
    public static void printMap(char[][] map){
        // Loop over each row
        for (int i = 0; i < map.length; i++) {
            // Loop over each character in the row
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args){
        // Load in the map
        String filePath = "test_map.txt";
        char[][] dungeonMap = loadMap(filePath);
        // Print the map
        printMap(dungeonMap);
    }
}
