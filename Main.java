public class Main {
    public static void main(String[] args) {
        // Set the file path and load the map
        char[][] dungeonMap = Map.loadMap("test_map.txt");
        // Set the difficulty to easy or hard (default is easy)
        // Easy difficulty: Bot needs 100% of the gold to exit and player needs 25%
        // Hard difficulty: Bot needs 50% to exit and player needs 50%
        String difficulty = "hard";
        // Start a new game
        Game game = new Game(dungeonMap, difficulty);
        game.start();
    }  
}
