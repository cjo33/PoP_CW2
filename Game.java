import java.util.List;

public class Game {
    // Define our variables
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
    public static boolean shouldQuit = false;

    // This starts the game and is called from Main
    public Game(char[][] map, String difficulty) {
        this.isRunning = true;
        // Define the map and set the initial value for checkDistance as false
        this.dungeonMap = map;
        this.checkDistance = false;
        // Calculate the total gold to set the targets for the player and bot
        this.totalGold = calculateTotalGold();
        // Set those targets based on the difficulty
        setGoldRequirements(difficulty);
        // Check and find the exit coords so it is possible to win
        this.exitCoords = Map.findExitCoords(dungeonMap);
        if (exitCoords == null) {
            System.out.println("No exit tile ('E') found on the map!");
            quit();
        }

        // Print out the name of the map and the win conditions for the player and bot
        // Only print if no errors have been displayed (makes it more obvious why it broke)
        if(isRunning && !shouldQuit){
            // Display the welcome message
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Welcome to Dungeon's of Doom!");
            System.out.println("You are playing on the " + Map.getMapName() + " map.");
            System.out.println("You are playing on " + difficulty + " mode.");
            System.out.println("The objective of the game is to move around the map, pickup enough gold and exit the dungeon.");
            System.out.println("Beware! There is a dangerous bot who is lurking, trying to catch you or pick up the gold for themselves.");
            System.out.println("If you manage to reach an exit tile with " + playerGoldRequired + " gold, you will win the game.");
            System.out.println("But if the bot reaches the exit tile with " + botGoldRequired + " gold or catches you, you will lose.");
            System.out.println("Enter the help command for more information on what each of the different commands do.");
            System.out.println("Good luck!");
        }

        // Sets a random initial location for the bot and player
        // then checks they are at least the mimimum distance apart
        // Continues trying until they checkDistance is updated to true
        // To make the game harder, reduce minimum distance in the checkDistance function in the map class
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


        // Initialise the view points for the player and bot
        player.look();
        bot.look();
    }

    // This command represents the actions that occur in one turn
    public void start() {
        while (isRunning && !shouldQuit) {
            // // Prints the map for testing
            // printWholeMap(dungeonMap);

            // Update and print visible area
            player.updateVisibleArea(dungeonMap);
            printVisibleArea(player.getVisibleArea());
            
            // Update the bot's view
            bot.updateVisibleArea(dungeonMap);
            // // Print the bot's visible area for testing
            // System.out.println("~~~~~~~~~~~~~~~~~~~~");
            // printVisibleArea(bot.getVisibleArea());

            // Bot goes first
            botTurn();
            // Game checks for lose conditionss
            checkGameOver();
            // Then players turn
            playerTurn();
        }
    }

    // This function determines what happens on the player's turn
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

    // This function determines what happens on the bot's turn
    private void botTurn() {
        // Check if the bot is on a special tile 
        // If so perform the action and end the turn
        if (bot.handleGoldTile(dungeonMap, this)) {
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

    // This function checks to see if any of the lose conditions are met
    private void checkGameOver() {
        // If the bot and player occupy the same tile, the game is over
        if (bot.getX() == player.getX() && bot.getY() == player.getY()) {
            System.out.println("Game Over! The bot caught you!");
            quit();
        }
        // If the bot has enough gold and reaches the exit, the game ends
        if (bot.getGoldCount() >= botGoldRequired && bot.getCurrentTile() == 'E') {
            System.out.println("Lose! The bot has left the dungeon with enough gold!");
            quit();
        }
        // If the bot has picked up so much gold such that it isn't possible for the player to pick up enough to win the game, then exit
        if (totalGold - bot.getGoldCount() < playerGoldRequired) {
            System.out.println("Lose! The bot has picked up too much gold and there isn't enough remaining in the dungeon to win!");
            quit();
        }
    }

    // Use this function to display the whole map for testing
    // This function iterates over the 2D array and prints it to the terminal
    public void printWholeMap(char[][] map) {
        // Iterate through each row
        for (int i = 0; i < map.length; i++) {
            //Iiterate over each character and print it out
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    // This function takes the visible area created and prints it by iterating through the character array
    private void printVisibleArea(char[][] visibleArea) {
        // Iterate through each row        
        for (char[] row : visibleArea) {
            // Iterate over each character and print it out
            for (char tile : row) {
                System.out.print(tile);
            }
            System.out.println();
        }
    }

    // This sets the gold requirements based on whether the game is set to easy, normal or hard
    private void setGoldRequirements(String difficulty) {
        int mapWin = Map.getWinCondition();
        // Check to ensure the map's win condition is not more than the total gold
        if (mapWin > totalGold){
            System.out.println("Not enough gold on the map for the win condition");
            quit();
        }
        // Set the requirements based on what difficulty is set to in Main
        switch (difficulty.toLowerCase()) {
            case "easy":
                // Bot needs 100% of the gold
                botGoldRequired = (int) Math.ceil(totalGold * 1.00);
                // Player only needs only half the map set amount
                // Force to round up and return an integer
                playerGoldRequired = (int) Math.ceil(mapWin / 2.0);
                break;
            case "normal":
                // Bot and player need the set win condition
                botGoldRequired = mapWin;
                playerGoldRequired = mapWin;
                break;
            case "hard":
                // Bot requires 25% of the total gold to exit
                botGoldRequired = (int) Math.ceil(totalGold * 0.25);
                // Player needs the map set amount
                playerGoldRequired = mapWin;
                break;
            default:
                // If invalid difficulty mode, tell the player it is invalid and what they can choose from
                System.out.println("Invalid difficulty level: " + difficulty);
                System.out.println("Please try again and choose from easy, normal or hard");
                // Then quit the game
                quit();
        }
    }
    // If the game is quit, set boolean isRunning to false and shouldQuit to true and end the while loop in start
    // Need the shouldQuit so that it can be called in static functions in other classes
    public void quit(){
        shouldQuit = true;
        isRunning = false;
    }

    // Displays the amount of gold the player needs to win when they input the hello command
    public void hello(){
        System.out.println("Gold to win: " + playerGoldRequired);
    }
    
    // This function allows other classes to access the player's gold required
    public int getPlayerGoldReq() {
        return playerGoldRequired;
    }

    // This function allows other classes to access the bot's gold required
    public int getBotGoldReq() {
        return botGoldRequired;
    }
}