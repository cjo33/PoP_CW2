import java.util.Scanner;

public class Input {
    // define our scanner variable that will read the input
    private Scanner scanner;
    private String[] validCommands = {"n", "e", "s", "w", "pickup", "look", "quit"};
    private boolean validInput;
    private String command;

    public Input() {
        // Initialises the scanner to read from the system
        this.scanner = new Scanner(System.in);
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
        System.out.println("Returns false");
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
        // initialises the variable to store the next tile
        char nextTile;
        // Goes through the different commands of nswe
        switch (command) {
            case "n":
                // Gets the tile the player would move to
                nextTile = dungeonMap[playerY - 1][playerX];
                // Check they can move to that tile
                if (player.checkTile(nextTile)) {
                    // Move the player north by one
                    player.moveNorth();
                }
                break;
            case "s":
                nextTile = dungeonMap[playerY + 1][playerX];
                if (player.checkTile(nextTile)) {
                    player.moveSouth();
                }
                break;
            case "w":
                nextTile = dungeonMap[playerY][playerX - 1];
                if (player.checkTile(nextTile)) {
                    player.moveWest();
                }
                break;
            case "e":
                nextTile = dungeonMap[playerY][playerX + 1];
                if (player.checkTile(nextTile)) {
                    player.moveEast();
                }
                break;
            case "pickup":
                player.pickupGold(dungeonMap);
                break;
            case "look":
                player.look();
                break;
            case "quit":
                // If they exit and are on an exit tile then print you win
                if (player.getCurrentTile() == 'E') {
                    System.out.println("You won! You collected " + player.getGoldCount() + " gold!"); // Placeholder for now
                    game.quit();
                } else {
                    System.out.println("Goodbye!");
                    game.quit();
                }
                break;
            // Ideally it prompts the user for a different command if its false
            default:
                System.out.println("Invalid command. Please try again.");
                break;
        }
    }
}