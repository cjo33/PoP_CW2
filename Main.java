public class Main {
    public static void main(String[] args) {
        // Create a new instance of the input function
        Input input = new Input();
        // Prompt the user to select a map and get the file path
        String filePath = input.chooseMap();
        // Load the map
        char[][] dungeonMap = Map.loadMap(filePath);
        // Read in the name and win condition
        Map.readMetadata(filePath);
        // Set the difficulty to easy or hard (default is normal)
            // Easy difficulty: Bot needs 100% of the gold to exit and player needs half the set map's win condition
            // Normal difficulty: Bot and player need the map's set win condition
            // Hard difficulty: Bot needs 25% of the total gold to exit and player needs the map's win condition
        String difficulty = input.setDifficulty();
        // Start a new game
        Game game = new Game(dungeonMap, difficulty);
        game.start();
    }  
}
