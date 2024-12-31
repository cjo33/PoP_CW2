import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Bot {
    // This is similar to how the Player class was set up
    // Initialise the x,y, currentTile and gold couunt variables for the Bot
    private int x;
    private int y;
    private char currentTile;
    private int goldCount;
    private char[][] visibleArea;
    private int visCentreX;
    private int visCentreY;
    private int[] exitCoords;
    private int[] playerCoords;
    private List<int[]> goldCoordinates;
    private int halfSize;

    // Again similar to Player class set the intitial location and count
    public Bot(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.currentTile = '.';
        this.goldCount = 0;
        this.exitCoords = null;
        this.playerCoords = null;
        this.goldCoordinates = new ArrayList<>();
        this.halfSize = Map.getViewSize()/2;

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

    // Allows other scripts to retrieve the area visible to the bot
    public char[][] getVisibleArea() {
        return visibleArea;
    }

    // Update the centre of the visible area
    public void look() {
        this.visCentreX = x;
        this.visCentreY = y;
    }

    // Update the visible area
    public void updateVisibleArea(char[][] dungeonMap) {
        this.visibleArea = Map.generateView(dungeonMap, visCentreX, visCentreY);
    }

    // This function finds all the special tiles in the visible Area
    private void updateSpecialTiles() {
        // Initialise/Reset them to zero in case tiles have changed
        exitCoords = null;
        playerCoords = null;
        goldCoordinates = new ArrayList<>();

        // Loop through each character
        for (int i = 0; i < visibleArea.length; i++) {
            for (int j = 0; j < visibleArea[i].length; j++) {
                char tile = visibleArea[i][j];
                int globalX = visCentreX - halfSize + j;
                int globalY = visCentreY - halfSize + i;

                if (tile == 'E') {
                    exitCoords = new int[] {globalX, globalY};
                    System.out.println("Exit Coords: " + Arrays.toString(exitCoords));
                } else if (tile == 'P') {
                    playerCoords = new int[] {globalX, globalY};
                    System.out.println("Player Coords: " + Arrays.toString(playerCoords));
                } else if (tile == 'G') {
                    goldCoordinates.add(new int[] {globalX, globalY});
                    System.out.println("Gold Coords: " + goldCoordinates);
                }
            }
        }
    }

    // This determines what action the bot will take
    public void makeMove(char[][] dungeonMap, int botGoldRequired) {
        // First update the visual area of the bot and what special tiles are contained
        updateVisibleArea(dungeonMap);
        updateSpecialTiles();
        // If the bot has enough gold to exit the dungeon
        if (goldCount >= botGoldRequired) {
            // If it can see the exit, then move straight to that
            if (exitCoords != null) {
                moveTowards(exitCoords[0], exitCoords[1], dungeonMap);
                System.out.println("Ugg I go to exit with gold");
            // If it can't see the exit but can see the player, move to the player
            } else if (playerCoords != null) {
                moveTowards(playerCoords[0], playerCoords[1], dungeonMap);
                System.out.println("Ugg I go to player with gold");
            // Otherwise just go towards the closest boundary
            } else {
                noTargets(dungeonMap);
                System.out.println("Ugg I have no target but enough gold");
            }
        // If the bot doesn't have enough gold
        } else {
            // If it can see either the player or a gold tile, move to whichever is closest
            if (playerCoords != null || !goldCoordinates.isEmpty()) {
                int[] closestTarget = findClosestTarget(goldCoordinates, playerCoords);
                moveTowards(closestTarget[0], closestTarget[1], dungeonMap);
                System.out.println("Closest target: " + closestTarget[0]);
                System.out.println("Closest target: " + closestTarget[1]);
                System.out.println("Ugg I go to closest target");
            // Otherwise just go towards the closest boundary
            } else {
                noTargets(dungeonMap);
                System.out.println("Ugg I have no target and no gold");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////// Close target ////////////////////////////////////////////////////////////////////////


    private int[] findClosestTarget(List<int[]> goldCoordinates, int[] playerCoords) {
        // Initialise values, set shortest distance to large number so that there is always
        // a gold that is closer to the bot and it doesn't break
        int[] closestTarget = null;
        int shortestDistance = 999999;

        // Check if there is a player coordinate found
        if (playerCoords != null) {
            // If so thejn calcuate how far it is from the bot
            int distance = Math.abs(playerCoords[0] - x) + Math.abs(playerCoords[1] - y);
            // If it is the closest target so far, then update the closest target and the shortest distance for the next target
            if (distance < shortestDistance) {
                closestTarget = playerCoords;
                shortestDistance = distance;
            }
        }
        // First check if the list of coordinates is empty
        if(!goldCoordinates.isEmpty())
            // If not, then loop through each coordinate
            for (int[] gold : goldCoordinates) {
                // Calculate how far it is from the bot
                int distance = Math.abs(gold[0] - x) + Math.abs(gold[1] - y);
                // If it is the closest target so far, then update the closest target and the shortest distance for the next target
                if (distance < shortestDistance) {
                    closestTarget = gold;
                    shortestDistance = distance;
                }
        }
        // Return the coordinates of the closest target
        return closestTarget;
    }


    //////////////////////////////////////////////////////////////////////// No target ////////////////////////////////////////////////////////////////////////

    
    private void noTargets(char[][] dungeonMap) {
        if (isOnEdgeOfVision()) {
            look(); // Update vision
            System.out.println("The bot is looking around");
        } else {
            moveToNearestEdge(dungeonMap);
        }
    }

    private boolean isOnEdgeOfVision() {
        return x == visCentreX - halfSize || x == visCentreX + halfSize || y == visCentreY - halfSize || y == visCentreY + halfSize;
    }

    private List<int[]> findEdgeCoordinates() {
        List<int[]> edges = new ArrayList<>();

        // Add edges in each direction
        edges.add(new int[] {(visCentreX + halfSize), y}); // Top
        edges.add(new int[] {(visCentreX - halfSize), y}); // Bottom
        edges.add(new int[] {x, (visCentreY + halfSize) }); // Right
        edges.add(new int[] {x, (visCentreY - halfSize) }); // Left
    
        return edges;
    }

    private List<int[]> filterValidEdges(List<int[]> edges, char[][]dungeonMap) {
        List<int[]> validEdges = new ArrayList<>();
        for (int[] edge : edges) {
            if (checkTile(edge[0], edge[1], dungeonMap)) {
                validEdges.add(edge);
            }
        }
        return validEdges;
    }

    // private int[] findClosestEdge(List<int[]> validEdges) {
    //     int[] closestEdge = null;
    //     int shortestDistance = Integer.MAX_VALUE;
    
    //     for (int[] edge : validEdges) {
    //         int distance = Math.abs(edge[0] - (x - (visCentreX - halfSize))) + Math.abs(edge[1] - (y - (visCentreY - halfSize)));
    //         if (distance < shortestDistance) {
    //             closestEdge = edge;
    //             shortestDistance = distance;
    //         }
    //     }
    
    //     return closestEdge;
    // }

    private int[] selectRandomEdge(List<int[]> validEdges) {
        if (validEdges.isEmpty()) {
            return null; // No valid edges
        }
        Random random = new Random();
        return validEdges.get(random.nextInt(validEdges.size()));
    }

    private void moveToNearestEdge(char[][] dungeonMap) {
        // Step 1: Find all edges
        List<int[]> edges = findEdgeCoordinates();
    
        // Step 2: Filter out walls
        List<int[]> validEdges = filterValidEdges(edges, dungeonMap);
    
        // Step 3: Randomly select a valid edge
        int[] randomEdge = selectRandomEdge(validEdges);
    
        // Step 4: Move toward the randomly selected valid edge
        if (randomEdge != null) {
            System.out.println("Moving to random edge: " + Arrays.toString(randomEdge));
            moveTowards(randomEdge[0], randomEdge[1], dungeonMap);
        } else {
            System.out.println("No valid edges found, staying put.");
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////// Do something, old code ////////////////////////////////////////////////////////////////////////

    // Checks to ensure that the new potential position of the bot is within the bounds of the map
    private boolean checkTile(int newX, int newY, char[][] dungeonMap) {
        if (newX < 0 || newY < 0 || newY > dungeonMap.length || newX > dungeonMap[0].length){
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

    // This function gives commands to the bot as to where to go, based on the coordinates of the target and the map
    public void moveTowards(int targetX, int targetY, char[][] dungeonMap) {
        int newX = x;
        int newY = y;
        System.out.println("X:" + x);
        System.out.println("Y:" + y);
        System.out.println("Target X:" + targetX);
        System.out.println("Target Y:" + targetY);
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
            // dungeonMap[y][x] = currentTile;
            // define our x and y values and update the current tile
            x = newX;
            y = newY;
            // currentTile = dungeonMap[y][x];
            // // Place the bot in the new position
            // dungeonMap[y][x] = 'B';
        }
    }
}


