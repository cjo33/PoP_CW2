public class Player {
    // Define our variables, private so only through this script 
    // and calling the north, south, west, east commands can you move the player
    private int x;
    private int y;
    private char currentTile;
    private int goldCount;
    private char[][] visibleArea;
    private int visCentreX;
    private int visCentreY;

    // Initialises the player with a a starting location and 0 gold count
    public Player(int startX, int startY) {
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

    // Allows other scripts to get current tile the player is on
    public char getCurrentTile() {
        return currentTile;
    }

    // Allows other scripts to change the tile the player is on
    public void setCurrentTile(char tile) {
        this.currentTile = tile;
    }

    // Allows other scripts to retrieve the gold count
    public int getGoldCount() {
        return goldCount;
    }

    // Allows other scripts to retrieve the area visible to the player
    public char[][] getVisibleArea() {
        return visibleArea;
    }

    // Check if the player can move onto the next tile
    public boolean checkTile(char tile) {
        switch (tile) {
            case '#' -> {
                System.out.println("Fail: You can't walk into a wall!"); 
                return false;
            }
            case '.' -> {
                return true;
            }
            case 'G' -> {
                return true;
            } 
            case 'E' -> {
                return true;
            }
            // Will return false if the map contains tiles not in initial description (just a precaution)
            default -> {
                System.out.println("Unknown tile type: " + tile); 
                return false;
            }
        }
    }

    // Function to check and pickup gold
    public void pickupGold(char[][] dungeonMap) {
        // Check if the player is on a gold tile
        if (currentTile == 'G') {
            // If they are then add 1 to the count
            goldCount += 1;
            // Print positive statement and the gold count
            System.out.println("Success! You picked up the gold!");
            System.out.println("Current Gold: " + goldCount);
            // Update the dungeon map at that location to a '.' instead of the G
            dungeonMap[y][x] = '.';
        } else {
            // If there isn't gold, then print negative statement
            System.out.println("Fail! There's no gold here to pick up.");
        }
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

    // Move the player North
    public void moveNorth() {
        y-= 1;
    }
    
    // Move the player South
    public void moveSouth() {
        y += 1;
    }

    // Move the player West
    public void moveWest() {
        x -= 1;
    }

    // Move the player East
    public void moveEast() {
        x += 1;
    }

    // Displays the amount of gold the player has when they input the gold command
    public void gold(){
        System.out.println("Gold owned: " + goldCount);
    }

}
