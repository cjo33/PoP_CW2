public class Main {
    public static void main(String[] args) {
        // Set the file path and load the map
        char[][] dungeonMap = Map.loadMap("test_map.txt");
        // Start a new game
        Game game = new Game(dungeonMap);
        game.start();
    }  
}
