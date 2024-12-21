public class Player {
    // Define our variables, private so only through this script 
    // and calling the north, south, west, east commands can you move the player
    private int x;
    private int y;
    private char currentTile;
    private int goldCount;

    // Initialises the player with a a starting location and 0 gold count
    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.currentTile = '.';
        this.goldCount = 0;
    }

    // Allows other scripts to get x co-ord
    public int getX(){
        return x; 
    }

    // Allows other scripts to get y co-ord
    public int getY(){
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


    // Check if the player can move onto the next tile
    public boolean checkTile(char tile) {
        switch (tile) {
            case '#': 
                System.out.println("You can't walk into a wall!"); 
                return false;
            case '.': 
            case 'G': 
            case 'E': 
                return true;
            default: 
                System.out.println("Unknown tile type: " + tile); 
                return false;
        }
    }

    public void pickupGold(char[][] dungeonMap) {
        // Check if the player is on a gold tile
        if (currentTile == 'G') {
            // If they are then print positive statement and add 1 to the count
            System.out.println("You picked up the gold!");
            goldCount += 1;
            // Update the dungeon map at that location to a '.' instead of the G
            dungeonMap[y][x] = '.';
        } else {
            // If there isn't gold, then print negative statement
            System.out.println("There's no gold here to pick up.");
        }
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
}
