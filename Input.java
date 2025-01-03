import java.io.File;
import java.util.Scanner;

public class Input {
    // define our scanner variable that will read the input
    private Scanner scanner;
    private String[] validCommands = {"move n", "move e", "move s", "move w", "hello", "gold", "pickup", "look", "help", "quit"};
    private boolean validInput;
    private String command;
    private int viewSize;
    private String filePath;

    public Input() {
        // Initialises the scanner to read from the system
        this.scanner = new Scanner(System.in);
        // Sets the view size from the Map class
        this.viewSize = Map.getViewSize();
    }

    // Function to list available maps and let the player choose one then set the file path
    public String chooseMap() {
        // Creates a file object which equals the folder Maps, this contains all of the potential maps the user can play
        File folder = new File("Maps");
        // Create a list of all the files from the maps folder which end in .txt
        File[] mapFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        // Check to ensure there are maps in the folder
        if (mapFiles == null || mapFiles.length == 0) {
            System.out.println("No txt files found in the " + folder + " folder.");
            return null;
        }

        // Prompt the user for a choice of map
        System.out.println("Select a map: ");
        System.out.println("(Higher number = more challenging)");
        // Iterate through the list of maps and print them to let the user choose from
        for (int i = 0; i < mapFiles.length; i++) {
            System.out.println(mapFiles[i].getName());
        }

        // Initialise choice as an invalid option
        int choice = -1;
        // While the user hasn't entered a valid number
        while (choice < 1 || choice > mapFiles.length) {
            System.out.print("Enter the number of your choice: ");
            // If the input is an integer
            if (scanner.hasNextInt()) {
                // Set the integer equal to choice, if it is a valid number (between 1 and the number of maps)
                // Then break the while loop, otherwise prompt for a number again
                choice = scanner.nextInt();
            } else {
                // If not a valid integer, skip that selection and start again
                scanner.next();
            }
        }

        // Set the file name of the map from the list that matches that choice value (-1 as we started at 1)
        String mapName = mapFiles[choice - 1].getName();
        // Return the full relative file path
        return "Maps/" + mapName;
    }

    // Function to let the player choose a difficulty level
    public String setDifficulty() {
        // Display the difficuly options
        System.out.println("Choose a difficulty level: easy, normal or hard");
        String difficulty = "";
        
        // While difficulty is not one of the valid options (easy, normal, hard), prompt the user to input a difficulty level
        while (!difficulty.equals("easy") && !difficulty.equals("normal") && !difficulty.equals("hard")) {
            System.out.print("Enter your choice: ");
            difficulty = scanner.nextLine().toLowerCase().trim();
        }
        return difficulty;
    }

    // This gets the input from the player
    public String getInput() {
        // Prompt the user for an input
        System.out.print("Enter command: ");
        // Sets the scanner to only just take the first line, 
        // and for formats it to all lowercase and no whitespace
        return scanner.nextLine().toLowerCase().trim();
    }

    // Check it is a valid input
    public boolean checkInput(String command){
        // Loop through each of the valid commands
        for (String valid : validCommands){
            // If any of them match then return true
            if (valid.equals(command)){
                return true;
            }
        }
        // Otherwise return false
        return false;
    }

    // This processes whatever comments the player inputs and calls the relevant function
    public void processCommand(String command, Player player, Game game, char[][] dungeonMap) {
        // Check it is valid
        validInput = checkInput(command);
        // If it isnt valid then print the list of valid commands and 
        // promt the user for another input until it is valid
        while(!validInput){
            // Provide the player with the list of valid inputs
            System.out.println("Invalid command. Please enter one of: " + String.join(", ", validCommands));
            // Get the input command
            command = getInput();
            // Check it is valid
            validInput = checkInput(command);
        }

        // Get the current x and y co-ords of the player
        int playerX = player.getX();
        int playerY = player.getY();
        // Initialises the variable to store the next tile
        char nextTile;
        // Goes through the different commands of move nswe
        switch (command) {
            case "move n" -> {
                // Add the try method in case the map doesn't have a wall boarder
                // This means it will catch the error and print Fail as opposed to ending the game
                try {
                    // Gets the tile the player would move to
                    nextTile = dungeonMap[playerY - 1][playerX];
                    // Check they can move to that tile
                    if (player.checkTile(nextTile)) {
                        // Move the player north by one
                        player.moveNorth();
                        // Print out that it has been successful
                        System.out.println("Success! The player moved north");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Fail: You can't walk into a wall!");
                }
            }
            // Same as above but checks and moves the player down
            case "move s" -> {
                try {
                    nextTile = dungeonMap[playerY + 1][playerX];
                    if (player.checkTile(nextTile)) {
                        player.moveSouth();
                        System.out.println("Success! The player moved south");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Fail: You can't walk into a wall!");
                }
            }
            // Same as above but checks and moves the player to the left
            case "move w" -> {
                try{
                    nextTile = dungeonMap[playerY][playerX - 1];
                    if (player.checkTile(nextTile)) {
                        player.moveWest();
                        System.out.println("Success! The player moved west");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Fail: You can't walk into a wall!");
                }
            }
            // Same as above but checks and moves the player to the right
            case "move e" -> {
                try {
                    nextTile = dungeonMap[playerY][playerX + 1];
                    if (player.checkTile(nextTile)) {
                        player.moveEast();
                        System.out.println("Success! The player moved east");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Fail: You can't walk into a wall!");
                }
            }
            // Call the hello function to print the player's target gold
            case "hello" -> game.hello();
            // Call the gold functino to print the player's current gold count
            case "gold" -> player.gold();
            // If they input pickup, call the pickup function to pickup the gold
            case "pickup" -> player.pickupGold(dungeonMap);
            // Look around and update the map
            case "look" -> player.look();
            // Prints the help function
            case "help" -> help();
            // Try and quit the game
            case "quit" -> {
                // If they exit and are on an exit tile then print you win
                if (player.getCurrentTile() == 'E' && player.getGoldCount() >= game.getPlayerGoldReq()) {
                    System.out.println("Win! You collected " + player.getGoldCount() + " out of " + game.getPlayerGoldReq() + " gold!");
                    game.quit();
                } else if (player.getCurrentTile() == 'E') {
                    System.out.println("Lose! You only collected " + player.getGoldCount() + " gold! ");
                    System.out.println("You needed " + game.getPlayerGoldReq() + " to win.");
                    game.quit();
                } else {
                    System.out.println("Lose! You are not on an exit tile");
                    game.quit();
                }
            }
        }
    }

    // This function displays the information the player might need if they are stuck and call the help function
    private void help() {
        System.out.println("Game objective:");
        System.out.println("    Move round the map and pickup enough gold to exit the dungeon.");
        System.out.println("    Watch out for the bot player who will also be trying to collect gold and exit.");
        System.out.println("    If the bot spots you, it will chase you and if you are caught, you will lose the game.");
        System.out.println("List of commands:");
        System.out.println("    Move <direction>:");
        System.out.println("        This moves the player either north, south, east or west.");
        System.out.println("        <direction>: n/s/e/w");
        System.out.println("    Look:");
        System.out.println("        This allows the player to see a " + viewSize + "x" + viewSize + " area centred around the player.");
        System.out.println("    Pickup:");
        System.out.println("        If the player is standing on a gold tile, this allows the player to pick it up.");
        System.out.println("    Quit:");
        System.out.println("        If the player is on the exit tile wih enough gold, this command lets them exit the dungeon.");
        System.out.println("        If they don't have enough gold or are not on the exit tile, they will lose.");
        System.out.println("    Hello:");
        System.out.println("        This command prints how much gold is required for the player to win.");
        System.out.println("    Gold:");
        System.out.println("        This command prints how much gold the player has.");
    }
}