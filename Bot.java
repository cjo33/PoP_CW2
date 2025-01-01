import java.util.ArrayList;
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
                // Find the tile at that location
                char tile = visibleArea[i][j];
                // Then work out it's global location
                // (As opposed to it's relative location on the visual area)
                int globalX = visCentreX - halfSize + j;
                int globalY = visCentreY - halfSize + i;
                // Go through checking for the different special tiles
                if (tile == 'E') {
                    exitCoords = new int[] {globalX, globalY};
                } else if (tile == 'P') {
                    playerCoords = new int[] {globalX, globalY};
                } else if (tile == 'G') {
                    goldCoordinates.add(new int[] {globalX, globalY});
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
            // If it can't see the exit but can see the player, move to the player
            } else if (playerCoords != null) {
                moveTowards(playerCoords[0], playerCoords[1], dungeonMap);
            // Otherwise just go towards the closest boundary
            } else {
                noTargets(dungeonMap);
            }
        // If the bot doesn't have enough gold
        } else {
            // If it can see either the player or a gold tile, move to whichever is closest
            if (playerCoords != null || !goldCoordinates.isEmpty()) {
                int[] closestTarget = findClosestTarget(goldCoordinates, playerCoords);
                moveTowards(closestTarget[0], closestTarget[1], dungeonMap);
            // Otherwise just go towards the closest boundary
            } else {
                noTargets(dungeonMap);
            }
        }
    }

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
    
    private void noTargets(char[][] dungeonMap) {
        // First checks to see if the bot is at the edge of it's vision
        if (isOnEdgeOfVision()) {
            // If so then it should look to update its vision
            look();
            System.out.println("The bot is looking around");
        } else {
            // Otherwise it should move toward an edge
            findAnEdge(dungeonMap);
        }
    }

    // This function checks if the bot is on the edge of it's vision
    private boolean isOnEdgeOfVision() {
        return x == visCentreX - halfSize || x == visCentreX + halfSize || y == visCentreY - halfSize || y == visCentreY + halfSize;
    }

    // This function finds the coordinates of all the closest edge tiles to the bot (only change one axis)
    private List<int[]> findEdgeCoordinates() {
        List<int[]> edges = new ArrayList<>();

        // Add edges in each direction
        edges.add(new int[] {(visCentreX + halfSize), y}); // Top
        edges.add(new int[] {(visCentreX - halfSize), y}); // Bottom
        edges.add(new int[] {x, (visCentreY + halfSize) }); // Right
        edges.add(new int[] {x, (visCentreY - halfSize) }); // Left
    
        return edges;
    }

    // This function goes through each of the edges and removes those that are walls
    private List<int[]> filterValidEdges(List<int[]> edges, char[][]dungeonMap) {
        List<int[]> validEdges = new ArrayList<>();
        // For each edge, check if it is a wall, if it isn't then add it to the list of valid edges the bot can go to
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

    // This function selects a random edge for the bot to walk towards
    // Not using the closest edge function as it could get stuck in a loop of moving away from a wall and back
    private int[] selectRandomEdge(List<int[]> validEdges) {
        // Check to ensure there are valid edges
        if (validEdges.isEmpty()) {
            return null;
        }
        // Return a random edge
        Random random = new Random();
        return validEdges.get(random.nextInt(validEdges.size()));
    }

    // This function calls the helper functions to instruct the bot on how to reach an edge and look for more targets
    private void findAnEdge(char[][] dungeonMap) {
        // First locate all the edge coordinates
        List<int[]> edges = findEdgeCoordinates();
        // Remove all the invalid edges
        List<int[]> validEdges = filterValidEdges(edges, dungeonMap);
        // Select a random edge to walk towards
        int[] randomEdge = selectRandomEdge(validEdges);
        // Check there is an edge target and then move
        if (randomEdge != null) {
            moveTowards(randomEdge[0], randomEdge[1], dungeonMap);
        } else {
            System.out.println("No valid edges found, staying put.");
        }
    }
    
    
    // Checks to ensure that the new potential position of the bot is within the bounds of the map
    private boolean checkTile(int newX, int newY, char[][] dungeonMap) {
        if (newX < 0 || newY < 0 || newY >= dungeonMap.length || newX >= dungeonMap[0].length){
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
            // define our new x and y values
            x = newX;
            y = newY;
        }
    }
}


