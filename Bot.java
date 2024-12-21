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

    private boolean checkTile(int newX, int newY, char[][] dungeonMap) {
        if (newX < 0 || newY < 0 || newY >= dungeonMap.length || newX > dungeonMap[0].length){
            return false;
        }
        return dungeonMap[newY][newX] != '#';
    }

    public void handleSpecialTiles(char[][] dungeonMap) {
        if (currentTile == 'G') {
            // Pickup gold
            System.out.println("The bot picked up gold!");
            goldCount += 1;
            currentTile = '.'; // Replace gold with floor
        } else if (currentTile == 'E' && goldCount >= Map.findGoldCoords(dungeonMap).size() / 2) {
            // Exit the dungeon
            System.out.println("The bot has exited the dungeon with enough gold!");
            System.exit(0); // End the game
        }
    }

    public int getPlayerDistance(int playerX, int playerY) {
        return Math.abs(playerX - x) + Math.abs(playerY - y);
    }

    public int[] findClosestGold(List<int[]> goldCoordinates) {
        int[] closestGold = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (int[] gold : goldCoordinates) {
            int distance = Math.abs(gold[0] - x) + Math.abs(gold[1] - y);
            if (distance < shortestDistance) {
                closestGold = gold;
                shortestDistance = distance;
            }
        }

        return closestGold;
    }

    public int getExitDistance(char[][] dungeonMap) {
        int[] exitCoordinates = Map.findExitCoords(dungeonMap);
        if (exitCoordinates == null) {
            return Integer.MAX_VALUE; // If no exit, return a large number
        }
        return Math.abs(exitCoordinates[0] - x) + Math.abs(exitCoordinates[1] - y);
    }

    public void findClosestTarget(List<int[]> goldCoordinates, int playerX, int playerY, char[][] dungeonMap, int totalGold) {
        int[] exitCoordinates = Map.findExitCoords(dungeonMap);

        if (goldCount >= totalGold / 2 && exitCoordinates != null) {
            // If the bot has enough gold, prioritize the exit or player
            int exitDistance = getExitDistance(dungeonMap);
            int playerDistance = getPlayerDistance(playerX, playerY);

            if (exitDistance <= playerDistance) {
                moveTowards(exitCoordinates[0], exitCoordinates[1], dungeonMap); // Move toward the exit
            } else {
                moveTowards(playerX, playerY, dungeonMap); // Move toward the player
            }
        } else {
            // Prioritize the closest gold or player
            int[] closestGold = findClosestGold(goldCoordinates);

            if (closestGold != null) {
                int goldDistance = Math.abs(closestGold[0] - x) + Math.abs(closestGold[1] - y);
                int playerDistance = getPlayerDistance(playerX, playerY);

                if (goldDistance <= playerDistance) {
                    moveTowards(closestGold[0], closestGold[1], dungeonMap); // Move toward the gold
                } else {
                    moveTowards(playerX, playerY, dungeonMap); // Move toward the player
                }
            } else {
                // No gold left, move toward the player
                moveTowards(playerX, playerY, dungeonMap);
            }
        }

        // Handle special tiles after moving
        handleSpecialTiles(dungeonMap);
    }

    public void moveTowards(int targetX, int targetY, char[][] dungeonMap) {
        int newX = x;
        int newY = y;

        // Determine the best move
        if (targetX > x) {
            newX = x + 1; // Move right
        } else if (targetX < x) {
            newX = x - 1; // Move left
        } else if (targetY > y) {
            newY = y + 1; // Move down
        } else if (targetY < y) {
            newY = y - 1; // Move up
        }

        // Check if the move is valid
        if (checkTile(newX, newY, dungeonMap)) {
            // Update the map
            dungeonMap[y][x] = currentTile; // Restore the bot's current tile
            x = newX;
            y = newY;
            currentTile = dungeonMap[y][x]; // Update the bot's current tile
            dungeonMap[y][x] = 'B'; // Place the bot in its new position
        }
    }
    // Find gold

    // Ideally it locates the player and then locates the gold that is closest to the player
    // Then if the gold is closer than the player, it moves towards the gold
    // If the player is closer it moves towards the player

}


