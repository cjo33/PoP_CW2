import java.util.List;

public class Bot {
    // This is similar to how the Player class was set up
    // Initialise the x,y, currentTile and gold couunt variables for the Bot
    private int x;
    private int y;
    private char currentTile;
    private int goldCount;

    // Again similar to Player class set the intitial location and count
    public Bot(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.currentTile = '.';
        this.goldCount = 0;
    }

    // Allows other scripts to get x co-ord
    public int getX() {
        return x;
    }

    // Allows other scripts to get y co-ord
    public int getY() {
        return y;
    }

    // Allows other scripts to get current tile the bot is on
    public char getCurrentTile() {
        return currentTile;
    }

    // Allows other scripts to change the tile the bot is on
    public void setCurrentTile(char tile) {
        this.currentTile = tile;
    }

    // Allows other scripts to get the gold count of the bot
    public int getGoldCount() {
        return goldCount;
    }

    // Checks to ensure that the new potential position of the bot is within the bounds of the map
    private boolean checkTile(int newX, int newY, char[][] dungeonMap) {
        if (newX < 0 || newY < 0 || newY >= dungeonMap.length || newX > dungeonMap[0].length){
            return false;
        }
        return dungeonMap[newY][newX] != '#';
    }

    // Instructions for the bot if they are on a G or E tile
    public boolean handleSpecialTile(char[][] dungeonMap, int totalGold) {
        // If they are on a gold tile
        if (currentTile == 'G') {
            System.out.println("The bot is picking up gold!");
            // Add one to the bot's gold count 
            goldCount += 1;
            // Update the floor of the map to be a '.' instead of G
            currentTile = '.';
            dungeonMap[y][x] = 'B';
            // If they are on a gold tile, return true to take a up a turn
            return true;
        // If they are on an E tile and they have enough money
        } else if (currentTile == 'E' && goldCount >= totalGold / 2) {
            System.out.println("The bot has exited the dungeon with enough gold!");
            // Then end the game
            System.exit(0);
            // Likewise return true to indicate it takes up a turn
            return true;
        }

        // Otherwise return false to tell the bot to move to a new tile
        return false;
    }

    // This works out how far the bot is from the player
    public int getPlayerDistance(int playerX, int playerY) {
        return Math.abs(playerX - x) + Math.abs(playerY - y);
    }

    // This goes through the array of current gold on the map an finds the one closest to the bot
    public int[] findClosestGold(List<int[]> goldCoordinates) {
        // Initialise values, set shortest distance to large number so that there is always
        // a gold that is closer to the bot and it doesn't break
        int[] closestGold = null;
        int shortestDistance = 9999999;
        // Goes through each remaining gold on the map
        for (int[] gold : goldCoordinates) {
            // Calculate the distance between the bot and the gold
            int distance = Math.abs(gold[0] - x) + Math.abs(gold[1] - y);
            // If that is the closest gold so far then update the shortest distance and closest gold
            if (distance < shortestDistance) {
                closestGold = gold;
                shortestDistance = distance;
            }
        }
        // Return the closest gold to the bot
        return closestGold;
    }

    // This function is needed only if we make the bot decide bewtween going towards the player or the bot
    // // This function returns the distance from the bot to the exit tile
    // public int getExitDistance(char[][] dungeonMap) {
    //     int[] exitCoordinates = Map.findExitCoords(dungeonMap);
    //     return Math.abs(exitCoordinates[0] - x) + Math.abs(exitCoordinates[1] - y);
    // }

    // This function determines where the bot is moving towards
    public void findClosestTarget(List<int[]> goldCoordinates, int playerX, int playerY, char[][] dungeonMap, int totalGold, int botGoldRequired) {
        // Get the exit coords (already checked in game that they exist)
        int[] exitCoordinates = Map.findExitCoords(dungeonMap);

        // If the bot has enough gold, go towards the player (more difficult)
        if (goldCount >= botGoldRequired) {
            moveTowards(exitCoordinates[0], exitCoordinates[1], dungeonMap);

            // Code to make the bot decide between going towards the player or the exit
            // This should decrease the difficulty
            // // If the bot has enough gold, prioritize the exit or player
            // int exitDistance = getExitDistance(dungeonMap);
            // int playerDistance = getPlayerDistance(playerX, playerY);
            // if (exitDistance <= playerDistance) {
            //     moveTowards(exitCoordinates[0], exitCoordinates[1], dungeonMap); // Move toward the exit
            // } else {
            //     moveTowards(playerX, playerY, dungeonMap); // Move toward the player
            // }
        
        // If the bot doesn't have enough gold to exit, then it goes towards either the player or the gold based on which is closer
        } else {
            // Prioritize the closest gold or player
            int[] closestGold = findClosestGold(goldCoordinates);

            // If there is still gold on the map
            if (closestGold != null) {
                // Calculate distance to the nearest gold
                int goldDistance = Math.abs(closestGold[0] - x) + Math.abs(closestGold[1] - y);
                // Calculate distance to the player
                int playerDistance = getPlayerDistance(playerX, playerY);

                // If gold is closer, go towards the gold
                if (goldDistance <= playerDistance) {
                    moveTowards(closestGold[0], closestGold[1], dungeonMap);
                } 
                // If player is closer, go towards the player
                else {
                    moveTowards(playerX, playerY, dungeonMap);
                }
            // No gold left, move toward the player
            } else {
                moveTowards(playerX, playerY, dungeonMap);
            }
        }
    }

    // This function gives commands to the bot as to where to go, based on the coordinates of the target and the map
    public void moveTowards(int targetX, int targetY, char[][] dungeonMap) {
        int newX = x;
        int newY = y;

        // Move right
        if (targetX > x) {
            newX = x + 1;
        } 
        // Move left
        else if (targetX < x) {
            newX = x - 1; 
        } 
        // Move down
        else if (targetY > y) {
            newY = y + 1;
        } 
        // Move up
        else if (targetY < y) {
            newY = y - 1;
        }

        // Check if the move is valid then update the map
        if (checkTile(newX, newY, dungeonMap)) {
            // Update the map with the old value of that tile
            dungeonMap[y][x] = currentTile;
            // define our x and y values and update the current tile
            x = newX;
            y = newY;
            currentTile = dungeonMap[y][x];
            // Place the bot in the new position
            dungeonMap[y][x] = 'B';
        }
    }
}


