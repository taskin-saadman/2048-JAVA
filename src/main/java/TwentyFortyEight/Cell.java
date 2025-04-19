package TwentyFortyEight; 
//Cell class represents a single cell in the 2048 game board
public class Cell {

    private int x; // x position of the cell
    private int y; // y position of the cell
    private int value;
    private boolean merged; //used to track if a cell has already merged this turn
    //helps prevent double merging

    //Constructor
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        value = 0; //default value of each cell
        merged = false; //initially merge flag is false
    }

    //getter for merged
    public boolean isMerged(){
        return merged;
    }

    //setter for merged
    public void setMerged(boolean merged){
        this.merged = merged;
    }

    //reset method (resets value and merged flag)
    public void reset(){
        value = 0;
        merged = false;
    }

    //method to check valid spawn locations (if a cell is empty or not) 
    public boolean isEmpty(){
        return value == 0; //since default value is 0
    }

    //method to check if merging is possible with another cell
    public boolean canMergeWith(Cell other){
        /*conditions to check if merging is possible:
        1. both cells have same value but not empty
        2. both cells have not already merged this turn (to avoid double merging)
        3. can't allow merging with itself (this and other can't be the same object)
        */
        return this != other && !this.isEmpty() && this.value == other.value && !this.merged && !other.merged; 
    }

    public int getValue()  { //getter for value
        return value;
    }

    public void setValue(int value) { //setter for value
        this.value = value;
    }
}
