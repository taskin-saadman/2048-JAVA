package TwentyFortyEight;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class App extends PApplet {
    //the main purpose of extending from PApplet is to modify graphics and handle events for my desired app

    public static int GRID_SIZE = 4; // 4x4 grid is default, can be changed using cmd-line arguments
    public static final int CELL_SIZE = 150; // Cell size in pixels
    public static final int CELL_SPACING = 8; // Space between cells
    public static int WIDTH = GRID_SIZE * CELL_SIZE;
    public static int HEIGHT = GRID_SIZE * CELL_SIZE;
    public static final int FPS = 30; //frame rate
    public static Random random = new Random(); //create a new random object

    private Cell[][] board; //2D array of cells to represent the game board
    private boolean gameOver = false; //game over flag
    private int startTime; //start time for the timer
    private int elapsedSeconds; //variable to track elapsed time

    //constructor
    public App() {
        this.board = new Cell[GRID_SIZE][GRID_SIZE]; //creating game board
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT); //set the size of app window
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player
     * and map elements.
     * This function will be run only once at the beginning of the program.
     * It is not called every frame.
     * It is used to set up the initial state of the game.
     */
    @Override
    public void setup() {
        frameRate(FPS); //Specifies the number of frames to be displayed every second

        //instantiating every individual cell in the board
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                board[x][y] = new Cell(y, x); //x and y are swapped to be similar to Cartesian format
            }
        }
        //spawn 2 random tiles during setup
        spawnRandomTile();
        spawnRandomTile();

        startTime = millis(); //timestamp during setup
        elapsedSeconds = 0; //set elapsed time to 0 at the beginning
    }

    //method to spawn random tile in an empty cell
    private void spawnRandomTile(){
        //create an array list to store empty cells first
        List<Cell> emptyCells = new ArrayList<Cell>();

        //add all empty cells to the list
        for(int x = 0; x < GRID_SIZE; x++){
            for(int y = 0; y < GRID_SIZE; y++){
                if(board[x][y].isEmpty()) emptyCells.add(board[x][y]);
            }
        }

        //if at least 1 empty cell available, randomly spawn 2 or 4
        //otherwise do nothing
        if(!emptyCells.isEmpty()){
            //get an empty cell using a random index from the list
            Cell randomCell = emptyCells.get(random.nextInt(emptyCells.size()));
            int value = (random.nextInt(2) + 1) * 2; //spawn either 2 or 4 (50-50 chance)
            //set the spawned value to the random cell
            randomCell.setValue(value);
        }
     }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
public void keyPressed(KeyEvent event) {
    if (gameOver){ // press 'r' to restart the game if game over
        if(event.getKey() == 'r'){
            restartGame();
        }
        return; //no need to execute rest of this method if game over and not restarted
    }

    boolean moved = false;
    resetAllMergedFlags(); //reset merged flags before each move

    //move tiles based on key pressed
    switch(event.getKeyCode()){ //integer value of the key pressed
        case PConstants.UP:
            moved = move(-1, 0); //decrease x in the array (up)
            break;
        case PConstants.DOWN:
            moved = move(1, 0); //increase x in the array (down)
            break;
        case PConstants.LEFT:
            moved = move(0, -1); //decrease y in the array (left)
            break;
        case PConstants.RIGHT:
            moved = move(0, 1); //increase y in the array (right)
            break;
        default:
            break;
    }
    //if any tile has moved, spawn a new tile (if no space available, does nothing)
    if(moved){
        spawnRandomTile();
        if(!canMove()){ //if no more possible moves, game over
            gameOver = true;
        }
    }
}


//returns true if any tile has moved, false otherwise
private boolean move(int dx, int dy) {
    boolean moved = false; //boolean to track if any tile has moved

    //set up the starting coordinates for the loops
    int startX = (dx > 0)?(GRID_SIZE - 1):0; //moving down
    int startY = (dy > 0)?(GRID_SIZE - 1):0; //moving right

    //set up ending coordinates
    int endX = (dx > 0) ? -1:GRID_SIZE;
    int endY = (dy > 0) ? -1:GRID_SIZE;

    //set up the step values for the loops (increment or decrement)
    int stepX = (dx > 0)?-1:1;
    int stepY = (dy > 0)?-1:1;

    for (int x = startX; x != endX; x += stepX) {
        for (int y = startY; y != endY; y += stepY) {
            Cell current = board[x][y];
            if (current.isEmpty()) continue;

            //if the current cell is not empty, check if it can move or merge with the next cell in the direction of movement
            int newX = x;
            int newY = y;

            //keep moving in the direction until no more movement is possible
            while(true){
                int nextX = newX + dx;
                int nextY = newY + dy;

                //break if next cell is out of bounds
                if (nextX < 0 || nextX >= GRID_SIZE || nextY < 0 || nextY >= GRID_SIZE) break;

                Cell next = board[nextX][nextY]; 

                if (next.isEmpty()){ //move the current cell to next cell if next cell is empty
                    next.setValue(current.getValue()); //change value of next cell
                    current.setValue(0); //current cell becomes empty
                    current = next;
                    newX = nextX; 
                    newY = nextY; 
                    moved = true; //update moved flag
                }
                else if(current.canMergeWith(next)){ //merge if possible
                    next.setValue(next.getValue() * 2);
                    next.setMerged(true); //update merged flag for next cell
                    current.setValue(0);
                    moved = true; //update moved flag
                    break;
                }
                else break;
            }
        }
    }

    return moved; //return the flag
}

//method to reset merged flags before each move
private void resetAllMergedFlags(){
    for(int x = 0; x < board.length; x++){
        for(int y = 0; y < board[x].length; y++){
            board[x][y].setMerged(false); //reset merged flags for all cells
        }
    }
}


//method to check is the game is still playable
private boolean canMove() {
    //conditions: if empty space available or any kind of merge is possible
    for(int x = 0; x < GRID_SIZE; x++){
        for(int y = 0; y < GRID_SIZE; y++){
            Cell current = board[x][y];

            if(current.isEmpty()){
                return true; //if the current cell is empty, game is still playable
            }

            //check down and right mergability for current cell
            if(x + 1 < GRID_SIZE && current.canMergeWith(board[x + 1][y])){ 
                return true;
            }
            if(y + 1 < GRID_SIZE && current.canMergeWith(board[x][y + 1])){ 
                return true;
            }
        }
    }
    return false; //no possible way to continue the game
}


    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == PConstants.LEFT){ //left-click to spawn a tile
            Cell pressedCell = getCellFromMousePosition(e.getX(), e.getY()); //get the cell from mouse position
            if(pressedCell.isEmpty()){//if the cell is empty, spawn 2 or 4 in that cell;
                pressedCell.setValue((random.nextInt(2) + 1) * 2); //set the value of the cell to 2 or 4
            }
        }
    }

    //this method helps to get the cell from mouse position (implemented in mousePressed method)
    public Cell getCellFromMousePosition(int x, int y) {
        return board[y/CELL_SIZE][x/CELL_SIZE]; //integer division is used to get the cell index
    }

    //this method is used to change the color of the cell when mouse hovers over it
    //mouseX and mouseY are built-in variables in processing to get the current mouse position
    public boolean isHoveringCell(int x, int y) {
        if(mouseX > x*CELL_SIZE && mouseX < (x+1)*CELL_SIZE 
                && mouseY > y*CELL_SIZE && mouseY < (y+1)*CELL_SIZE){ //check if mouse is hovering inside the cell
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() { //graphics of each frame is drawn by this method
        background(250); //need to clear the screen every frame with a brownish background


        if(!gameOver){ //update timer each frame if game is not over
            elapsedSeconds = (millis() - startTime) / 1000; //current timestamp - start time
        }



        textSize(70);
        strokeWeight(15); //width of the border of shapes (gap between cells)
        stroke(156, 139, 124); //colour of border of shapes
        
        for(int x = 0; x < GRID_SIZE; x++){
            for(int y = 0; y < GRID_SIZE; y++){
                if(isHoveringCell(x, y)){ //setting the colour of the cell when mouse hovers over it
                    fill(232, 207, 184);
                }
                else{ //default colour of the cell
                    fill(189, 172, 151);
                }
                
                rect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE, CELL_SIZE); //draw each cell (rectangle)
                Cell cell = board[y][x]; //get each cell (Cartesian format)

                int value = cell.getValue(); //get the value of the cell
                if(value > 0){ //implement tile color based on value
                    int[] color = getTileColor(value);
                    fill(color[0], color[1], color[2]); //fill takes RGB values
                    rect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE, CELL_SIZE); //draw tile (rectangle) with the color based on value

                    // after drawing tile, draw text value on top
                    fill(0); // Black text for contrast
                    textAlign(CENTER, CENTER);
                    textSize(40);
                    //convert value to string and draw it in the center of the cell
                    text(String.valueOf(value), (x + 0.5f) * CELL_SIZE, (y + 0.5f) * CELL_SIZE); 
                }

            }
        }

        if(gameOver){ //if game is over, show game over message
            fill(0);
            textAlign(CENTER, CENTER); //centrally align the text
            textSize(80);
            text("GAME OVER", WIDTH / 2, HEIGHT / 2); //show message in the center of the screen
        }  
        
        fill(0); 
        textAlign(RIGHT, TOP); 
        textSize(25);
        text("Time: " + elapsedSeconds + "s", WIDTH - 10, 10);

    }

    //method for setting tile color based on value
    public int[] getTileColor(int value) {
        switch (value){ //returns RGB value as a new int array for each case
            //used AI to generate these RGB values inside the arrays
            case 2: return new int[]{238, 228, 218};
            case 4: return new int[]{237, 224, 200};
            case 8: return new int[]{242, 177, 121};
            case 16: return new int[]{245, 149, 99};
            case 32: return new int[]{246, 124, 95};
            case 64: return new int[]{246, 94, 59};
            case 128: return new int[]{237, 207, 114};
            case 256: return new int[]{237, 204, 97};
            case 512: return new int[]{237, 200, 80};
            case 1024: return new int[]{237, 197, 63};
            case 2048: return new int[]{237, 194, 46};
            default: return new int[]{60, 58, 50}; //for value > 2048
        }
    }
    
    
    //method for game restart
    private void restartGame() {
        //individually reset each cell in the board
        for(int x = 0; x < GRID_SIZE; x++){
            for(int y = 0; y < GRID_SIZE; y++){
                board[x][y].reset(); //value and merged flag reset
            }
        }
        //spawn 2 random tiles again
        spawnRandomTile();
        spawnRandomTile();
        gameOver = false; //reset game over flag

        startTime = millis();//reset startTime to current timestamp
        elapsedSeconds = 0;
    }
    
        
    public static void main(String[] args) {
        //if user has provided any command line arguments, set the grid size to that value
        //otherwise, default is 4x4 grid
        if(args.length > 0){
                GRID_SIZE = Integer.parseInt(args[0]);
        }

        //update WIDTH and HEIGHT based on the new GRID_SIZE
        WIDTH = GRID_SIZE * CELL_SIZE;
        HEIGHT = GRID_SIZE * CELL_SIZE;

        //run the main method of PApplet class to start the game
        PApplet.main("TwentyFortyEight.App"); //App class inside TwentyFortyEight package
    }

}
