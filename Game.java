import java.util.List;

public class Game {
    // Define our variables, 2D grid, player object
    // player input and game running
    private char[][] dungeonMap;
    private Player player;
    private Bot bot;
    private Input input;
    private boolean isRunning;
    private int totalGold;
    private int[] exitCoords;

    public Game(char[][] map) {
        // Initialise the game, define the map and starting point, 
        // prompt the user for the first input and set the game as running
        this.dungeonMap = map;
        this.player = new Player(7, 1);
        this.bot = new Bot(3,3);
        this.input = new Input();
        this.isRunning = true;
        this.totalGold = calculateTotalGold();
        this.exitCoords = Map.findExitCoords(dungeonMap);
        
        if (exitCoords == null) {
            throw new IllegalStateException("No exit tile ('E') found on the map!");
        }
    }

    // This command represents the actions that occur in one turn
    public void start() {
        while (isRunning) {
            // Prints the map
            printMap(dungeonMap);
            playerTurn();
            botTurn();
            checkGameOver();
        }
    }

    public void playerTurn() {
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
    }

    public void botTurn() {
        // Find all gold tiles on the map
        List<int[]> goldCoordinates = Map.findGoldCoords(dungeonMap);

        // Bot determines and moves toward its closest target
        bot.findClosestTarget(
            goldCoordinates,
            player.getX(),
            player.getY(),
            dungeonMap,
            totalGold
        );

        // Update the bot's position on the map
        dungeonMap[bot.getY()][bot.getX()] = 'B';
    }

    private int calculateTotalGold() {
        List<int[]> goldCoordinates = Map.findGoldCoords(dungeonMap);
        return goldCoordinates.size();
    }

    private void checkGameOver() {
        // If the bot and player occupy the same tile, the game is over
        if (bot.getX() == player.getX() && bot.getY() == player.getY()) {
            System.out.println("Game Over! The bot caught you!");
            isRunning = false;
        }

        // If the bot has enough gold and reaches the exit, the game ends
        if (bot.getGoldCount() >= Map.findGoldCoords(dungeonMap).size() / 2 && bot.getCurrentTile() == 'E') {
            System.out.println("Game Over! The bot escaped with enough gold!");
            isRunning = false;
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