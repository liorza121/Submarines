import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
public class Main {
    static char BORDER = '@';
    static char SUBMARINE = '#';
    static char MISS = 'X';
    static char EMPTY = '-';
    static final int INVALID_ORIENTATION = 1;
    static final int ILLEGAL_TILE = 2;
    static final int EXCEEDING  = 3;
    static final int OVERLAP = 4;
    static final int BORDERING = 5;
    static final int VALID = 0;
    public static Scanner scanner;
    public static Random rnd;

    public static void battleshipGame() {
        char[][] playerBoard = setGameBoard();
        int[] amountsAndSizes = getSizesAndAmounts();
        //placePlayerSubmarines(playerBoard, amountsAndSizes);
        char[][] computerBoard = new char[playerBoard.length][playerBoard[0].length];
        placeComputerSubmarines(computerBoard, amountsAndSizes);
        printMat(computerBoard);
    }


    public static void main(String[] args) throws IOException {
        //String path = args[0];
        //scanner = new Scanner(new File(path));
        //scanner = new Scanner(System.in);
        //int numberOfGames = scanner.nextInt();
        //scanner.nextLine();

        //System.out.println("Total of " + numberOfGames + " games.");

        /*f
        or (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
        */
        scanner = new Scanner(System.in);
        battleshipGame();

    }


    public static char[][] setGameBoard(){
        System.out.println("Enter the board size");
        String boardSize = scanner.nextLine();
        int amountOfRows = Integer.parseInt(boardSize.substring(0, boardSize.indexOf('X')));
        int amountOfColumns = Integer.parseInt(boardSize.substring(boardSize.indexOf('X') + 1));
        char[][] gameBoard = new char[amountOfRows][amountOfColumns];
        for(int i = 0; i < amountOfRows; i++) {
            for(int j = 0; j < amountOfColumns; j++) {
                gameBoard[i][j] = EMPTY;
            }
        }
        return gameBoard;
    }
    public static int countInStr(char c, String str) {
        int n = 0;
        for(int i = 0; i < str.length();i++) {
            if(str.charAt(i) == c)
                n++;
        }
        return n;
    }
    public static int castSubstringToInt(String str,int start, int end)
    {
        if (end==-1)
            end=str.length();
        return Integer.parseInt(str.substring(start,end));
    }
    public static int[] getSizesAndAmounts(){
        System.out.println("Enter the battleships sizes:");
        String shipSizes = scanner.nextLine();
        int size = countInStr('X',shipSizes) * 2;
        int[] amountsAndSizes = new int[size]; //i is amount, i+1 is size
        for(int i = 0; i < size; i += 2){
            amountsAndSizes[i] = castSubstringToInt(shipSizes,0, shipSizes.indexOf('X'));
            amountsAndSizes[i+1] = castSubstringToInt(shipSizes,shipSizes.indexOf('X')+1, shipSizes.indexOf(' '));
            shipSizes = shipSizes.substring(shipSizes.indexOf(' ')+1);
        }
        return amountsAndSizes;
    }
    public static int checkValidity(int x, int y, String orientation, int size, char[][] board){
        //System.out.println("x is: " + x + " y is: " + y + " or is: " + orientation);
        int n = board.length;
        int m = board[0].length;
        if (!(orientation.equals("0") || orientation.equals("1")))
            return INVALID_ORIENTATION;
        if(x >= n || y >= m || x < 0 || y < 0)
            return ILLEGAL_TILE;
        if (orientation.equals("0")){
            if(y+size-1 >= n)
                return EXCEEDING;
            else{
                for(int i = 0; i < size; i++){
                    if(board[x][y+i] == SUBMARINE)
                        return OVERLAP;
                    else if(board[x][y+i] == BORDER)
                        return BORDERING;
                }
            }
        }
        else{
            if(x-size+1 < 0)
                return EXCEEDING;
            for(int i = 0; i < size; i++) {
                if (board[x - i][y] == SUBMARINE)
                    return OVERLAP;
                else if(board[x - i][y] == BORDER)
                    return BORDERING;
            }
        }
        return VALID;
    }
    public static void printMat(char[][] mat){
        for(int i = 0; i < mat.length; i++){
            for(int j = 0; j < mat[0].length; j++){
                System.out.print(mat[i][j]);
            }
            System.out.println();
        }
    }
    public static void printError(int code){
        switch(code) {
            case (INVALID_ORIENTATION):
                System.out.println("Illegal orientation, try again!");
                break;
            case (ILLEGAL_TILE):
                System.out.println("Illegal tile. try again!");
                break;
            case (EXCEEDING):
                System.out.println("Battleship exceeds the boundaries of the board, try again!");
                break;
            case (OVERLAP):
                System.out.println("Battleship overlaps another battleship, try again!");
                break;
            case (BORDERING):
                System.out.println("Adjacent battleship detected, try again!");
                break;
        }
    }
    public static void addSubmarine(char[][] playerBoard, int x, int y, String orientation, int size) {
        int n = playerBoard.length;
        int m = playerBoard[0].length;
        if(orientation.equals("0")){
            for(int i = 0; i < size; i++) {
                playerBoard[x][y+i] = SUBMARINE;
                for(int j = -1; j <=1; j++) {
                    for(int k = -1; k <= 1;k++){
                        if(!(x+j < 0 || x+j >= n || y+i+k < 0 || y+i+k >= m)){
                            if(playerBoard[x+j][y+i+k] != SUBMARINE)
                                playerBoard[x+j][y+i+k] = BORDER;
                        }
                    }
                }
            }
        }
        else{
            for(int i = 0; i < size; i++) {
                playerBoard[x-i][y] = SUBMARINE;
                for(int j = -1; j <=1; j++) {
                    for(int k = -1; k <= 1;k++){
                        if(!(x-i+j < 0 || x-i+j >= n || y+k < 0 || y+k >= m)){
                            if(playerBoard[x-i+j][y+k] != SUBMARINE)
                                playerBoard[x-i+j][y+k] = BORDER;
                        }
                    }
                }
            }
        }
    }
    public static void placePlayerSubmarines(char[][] playerBoard, int[] amountsAndSizes){
        int size ;
        int amount;
        int errorCode = INVALID_ORIENTATION;
        int x = 0;
        int y = 0;
        String orientation = "";
        String placeAndOrientation;
        for(int amountIndex = 0; amountIndex < amountsAndSizes.length; amountIndex+= 2){
            amount = amountsAndSizes[amountIndex];
            size = amountsAndSizes[amountIndex+1];
            for(int i = 0; i < amount; i++){
                System.out.println("enter location and orientation for battleship of size " + size);
                while(errorCode != 0) {
                    placeAndOrientation = scanner.nextLine();
                    x = castSubstringToInt(placeAndOrientation,0,placeAndOrientation.indexOf(','));
                    placeAndOrientation = placeAndOrientation.substring(placeAndOrientation.indexOf(',')+1);
                    y = castSubstringToInt(placeAndOrientation,0,placeAndOrientation.indexOf(','));
                    orientation = placeAndOrientation.substring(placeAndOrientation.indexOf(',')+1);
                    errorCode = checkValidity(x,y,orientation,size, playerBoard);
                    printError(errorCode);
                }
                addSubmarine(playerBoard,x,y,orientation,size);
                printMat(playerBoard);
                errorCode = INVALID_ORIENTATION;
            }
        }
    }
    /*
        Returns random integer in the given range, where if low > high -1 will be returned
     */
    public static int randomIntInRange(int low, int high) {
        if(low > high)
            return -1;
        else{
            Random rn = new Random();
            return rn.nextInt((high-low) +1) + low;
        }
    }
    public static void placeComputerSubmarines(char[][] board, int[] amountsAndSizes){
        int size; int amount; int x=0; int y=0; boolean valid = false;
        int n = board.length; int m = board[0].length;
        String orientation = "";
        Random rn = new Random();
        for(int amountIndex = 0; amountIndex < amountsAndSizes.length; amountIndex+= 2){
            amount = amountsAndSizes[amountIndex];
            size = amountsAndSizes[amountIndex+1];
            for(int i = 0; i < amount; i++){
                while(!valid) {
                    x = randomIntInRange(0,n-1);
                    y = randomIntInRange(0,m)-1;
                    orientation = Integer.toString(randomIntInRange(0,2));
                    valid = checkValidity(x,y,orientation,size, board) == VALID;
                }
                addSubmarine(board,x,y,orientation,size);
                valid = false;
            }
        }
    }
}


