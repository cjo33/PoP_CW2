import java.util.Scanner;

public class Input {
    // define our scanner variable that will read the input
    private Scanner scanner;

    public Input() {
        // Initialises the scanner to read from the system
        this.scanner = new Scanner(System.in);
    }

    public String getInput() {
        // Prompt the user for an input
        System.out.print("Enter command: ");
        // Sets the scanner to only just take the first line, 
        // and for formats it to all lowercase and no whitespace
        return scanner.nextLine().toLowerCase().trim();
    }

    public void processCommand(String command, Player player, Game game, char[][] dungeonMap) {
        // Get the current x and y co-ords of the player
        int playerX = player.getX();
        int playerY = player.getY();
        // initialises the variable to store the next tile
        char nextTile;
        // Goes through the different commands of nswe
        switch (command) {
            case "north":
                // Gets the tile the player would move to
                nextTile = dungeonMap[playerY - 1][playerX];
                // Check they can move to that tile
                if (player.checkTile(nextTile)) {
                    // Move the player north by one
                    player.moveNorth();
                }
                break;
            case "south":
                nextTile = dungeonMap[playerY + 1][playerX];
                if (player.checkTile(nextTile)) {
                    player.moveSouth();
                }
                break;
            case "west":
                nextTile = dungeonMap[playerY][playerX - 1];
                if (player.checkTile(nextTile)) {
                    player.moveWest();
                }
                break;
            case "east":
                nextTile = dungeonMap[playerY][playerX + 1];
                if (player.checkTile(nextTile)) {
                    player.moveEast();
                }
                break;
            case "pickup":
                player.pickupGold(dungeonMap);
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
            default:
                System.out.println("Invalid command. Please try again.");
                break;
        }
    }
}