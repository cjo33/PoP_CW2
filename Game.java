public class Game {
    // Define our variables, 2D grid, player object
    // player input and game running
    private char[][] dungeonMap;
    private Player player;
    private Input input;
    private boolean isRunning;

    public Game(char[][] map) {
        // Initialise the game, define the map and starting point, 
        // prompt the user for the first input and set the game as running
        this.dungeonMap = map;
        this.player = new Player(7, 1);
        this.input = new Input();
        this.isRunning = true;
    }

    // This command represents the actions that occur in one turn
    public void start() {
        while (isRunning) {
            // reset the tile where the player was back to what it was
            dungeonMap[player.getY()][player.getX()] = player.getCurrentTile();
            // Get the input command
            String command = input.getInput();
            // Process the input command
            input.processCommand(command, player, this, dungeonMap);
            // Gets the information of the new tile the player will go onto
            player.setCurrentTile(dungeonMap[player.getY()][player.getX()]);
            // Moves the player onto that tile and replaces with a P
            dungeonMap[player.getY()][player.getX()] = 'P';
            // Prints the map
            printMap(dungeonMap);
        }
    }

    // This function iterates over the 2D array and prints it to the terminal
    public void printMap(char[][] map) {
        // iterate through each row
        for (int i = 0; i < map.length; i++) {
            // iterate over each character and print it out
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    // If the game is quit, set boolean isRunning to false and end the while loop in start
    public void quit(){
        isRunning = false;
    }
}