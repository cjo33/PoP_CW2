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
    private int playerGoldRequired;
    private int botGoldRequired;
    private boolean checkDistance;
    private int[] playerStart;
    private int[] botStart;

    // This starts the game and is called from Main
    public Game(char[][] map, String difficulty) {
        // Define the map and set the initial value for checkDistance as false
        this.dungeonMap = map;
        this.checkDistance = false;

        // Sets a random initial location for the bot and player
        // then checks they are at least the mimimum distance apart
        // Continues trying until they checkDistance is updated to true
        while(checkDistance == false){
            // Place the player on a random tile
            playerStart = Map.findRandomTile(dungeonMap);
            this.player = new Player(playerStart[0], playerStart[1]);

            // Place the bot on a random tile
            botStart = Map.findRandomTile(dungeonMap);
            this.bot = new Bot(botStart[0], botStart[1]);
           
            // Checks to see if the initial random locations are far enough apart
            // If yes then checkDistance = True, otherwise it tries another intial spot
            checkDistance = Map.checkDistance(player, bot, dungeonMap);
        }

        // Mark player and bot positions on the map
        dungeonMap[playerStart[1]][playerStart[0]] = 'P';
        dungeonMap[botStart[1]][botStart[0]] = 'B';

        // Prompt the user for the first input and set the game as running
        this.input = new Input();
        this.isRunning = true;
        // Calculate the total gold to set the targets for the player and bot
        this.totalGold = calculateTotalGold();
        // Set those targets based on the difficulty
        setGoldRequirements(difficulty);
        // Check and find the exit coords
        this.exitCoords = Map.findExitCoords(dungeonMap);
        if (exitCoords == null) {
            System.out.println("No exit tile ('E') found on the map!");
            quit();
        }
        player.look();
        bot.look();
    }

    // This command represents the actions that occur in one turn
    public void start() {
        while (isRunning) {
            // // Prints the map
            // printMap(dungeonMap);
            // Print visible area
            player.updateVisibleArea(dungeonMap);
            printVisibleArea(player.getVisibleArea());
            
            System.out.println("This is the map for the bot:");
            bot.updateVisibleArea(dungeonMap);
            printVisibleArea(bot.getVisibleArea());
            // Bot goes first
            botTurn();
            // Game checks for lose conditionss
            checkGameOver();
            // Then players turn
            playerTurn();
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

    private void botTurn() {
        // Check if the bot is on a special tile 
        // If so perform the action and end the turn
        if (bot.handleSpecialTile(dungeonMap, totalGold)) {
            return;}
        // reset the tile where the player was back to what it was
        dungeonMap[bot.getY()][bot.getX()] = bot.getCurrentTile();
        // Bot decides its next move based on the visible area and current priorities
        bot.makeMove(dungeonMap, botGoldRequired);
        // Gets the information of the new tile the player will go onto
        bot.setCurrentTile(dungeonMap[bot.getY()][bot.getX()]);
        // Update the bot's position on the map
        dungeonMap[bot.getY()][bot.getX()] = 'B';
    }

    // Calculate the total gold in the map
    private int calculateTotalGold() {
        // Make a list of all the gold coordiates and return the size of the list
        List<int[]> goldCoordinates = Map.findGoldCoords(dungeonMap);
        return goldCoordinates.size();
    }

    private void checkGameOver() {
        // If the bot and player occupy the same tile, the game is over
        if (bot.getX() == player.getX() && bot.getY() == player.getY()) {
            System.out.println("Game Over! The bot caught you!");
            quit();
        }

        // If the bot has enough gold and reaches the exit, the game ends
        if (bot.getGoldCount() >= botGoldRequired && bot.getCurrentTile() == 'E') {
            System.out.println("Game Over! The bot escaped with enough gold!");
            quit();
        }
    }

    // This function iterates over the 2D array and prints it to the terminal
    public void printMap(char[][] map) {
        // Iterate through each row
        for (int i = 0; i < map.length; i++) {
            //Iiterate over each character and print it out
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    private void printVisibleArea(char[][] visibleArea) {
        System.out.println("Visible Area:");
        for (char[] row : visibleArea) {
            for (char tile : row) {
                System.out.print(tile);
            }
            System.out.println();
        }
    }

    // This sets the gold requirements based on whether the game is set to easy or hard
    private void setGoldRequirements(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                // Bot needs 100% of the gold
                botGoldRequired = (int) Math.ceil(totalGold * 1.00);
                // Player only needs 25%
                playerGoldRequired = (int) Math.ceil(totalGold * 0.25);
                break;
            case "hard":
                // Both require 50% of the gold to exit
                botGoldRequired = (int) Math.ceil(totalGold * 0.50);
                playerGoldRequired = (int) Math.ceil(totalGold * 0.50);
                break;
            default:
                // throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
                System.out.println("Invalid difficulty level: " + difficulty);
                quit();
                // isRunning = false;
        }
    }
    // If the game is quit, set boolean isRunning to false and end the while loop in start
    public void quit(){
        isRunning = false;
    }
}