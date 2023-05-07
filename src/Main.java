import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedMap;

public class Main {
    static char BORDER = '@';
    static char SUBMARINE = '#';
    static char MISS = 'X';

    static char HIT = 'V';
    static char EMPTY = '–';
    static final int INVALID_ORIENTATION = 1;
    static final int ILLEGAL_TILE = 2;
    static final int EXCEEDING = 3;
    static final int OVERLAP = 4;
    static final int BORDERING = 5;
    static final int TRIED = 6;
    static final int VALID = 0;

    public static Scanner scanner;
    public static Random rnd;

    public static void battleshipGame() {
        /**this func manages and executes a single round of the game*/
        boolean playerWon = false;
        char[][] playerBoard = setGameBoard();
        char[][] computerBoard = new char[playerBoard.length][playerBoard[0].length];
        int[] amountsAndSizes = getSizesAndAmounts();
        System.out.println("Your current game board:");
        printBoard(playerBoard);
        placePlayerSubmarines(playerBoard, amountsAndSizes);
        placeComputerSubmarines(computerBoard, amountsAndSizes);
        playerBoard = resetBoard(playerBoard);
        computerBoard = resetBoard(computerBoard);
        playerWon = manageAttacks(playerBoard, computerBoard, countSubs(amountsAndSizes));
        if (playerWon)
            System.out.println("You won the game!");
        else
            System.out.println("You lost ):");
    }


    public static void main(String[] args) throws IOException {
        String path = args[0];
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Total of " + numberOfGames + " games.");
        for (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            String blab = scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
        //scanner = new Scanner(System.in);
    }


    public static void fillBoard(char[][] board) {
        /**
         *
         * @param board - a blank matrix in the sizes given by the user.
         * this function fills the board with the EMPTY character
         */
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    public static char[][] setGameBoard() {
        /**
         * this function gets the width and length of the board from the user.
         * @returns gameBoard - a matrix with the wanted size filled with the EMPTY character(an empty board).
         */
        System.out.println("Enter the board size");
        String boardSize = scanner.nextLine();
        int amountOfRows = Integer.parseInt(boardSize.substring(0, boardSize.indexOf('X')));
        int amountOfColumns = Integer.parseInt(boardSize.substring(boardSize.indexOf('X') + 1));
        char[][] gameBoard = new char[amountOfRows][amountOfColumns];
        fillBoard(gameBoard);
        return gameBoard;
    }

    public static int countInStr(char c, String str) {
        /**this function counts and returns the number of appearances og the char c in the string str.*/
        int n = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c)
                n++;
        }
        return n;
    }

    public static int castSubstringToInt(String str, int start, int end) {
        /**this function gets a string that contains only digits and returns the digits in the same order as an int.*/
        if (end == -1)
            end = str.length();
        return Integer.parseInt(str.substring(start, end));
    }

    public static int[] getSizesAndAmounts() {
        /**
         *
         * this function gets the amounts of battleships of each size.
         * @returns an arrey with the even places holding amounts and the odd places holding the sizes.
         */
        System.out.println("Enter the battleships sizes");
        String shipSizes = scanner.nextLine();
        int size = countInStr('X', shipSizes) * 2;
        int[] amountsAndSizes = new int[size];
        for (int i = 0; i < size; i += 2) {
            amountsAndSizes[i] = castSubstringToInt(shipSizes, 0, shipSizes.indexOf('X'));
            amountsAndSizes[i + 1] = castSubstringToInt(shipSizes, shipSizes.indexOf('X') + 1, shipSizes.indexOf(' '));
            shipSizes = shipSizes.substring(shipSizes.indexOf(' ') + 1);
        }
        return amountsAndSizes;
    }

    public static int checkValidity(int x, int y, String orientation, int size, char[][] board) {
        /**
         *
         * @param x - the row value the use wants to put the battleship in.
         * @param y - the column value the use wants to put the battleship in.
         * @param orientation - the orientation of the battleship wanted by the user("0" for horizontal and "1" for
         *                      vertical).
         * @param size - the length of the battleship the user tries to place.
         * @param board - the current player board(before placing the new battleship) including a border around each
         *                battleship.
         *
         * <p>this function gets the above parameters and checks if the according placement is legal according to the
         * given rules of the game.
         * @returns a fiiting state code - VALID (=0) for a valid placement or a fitting error code otherwise.
         * </p>
         */
        Boolean bordering = false;
        int n = board.length;
        int m = board[0].length;
        if (!(orientation.equals("0") || orientation.equals("1")))
            return INVALID_ORIENTATION;
        if (x >= n || y >= m || x < 0 || y < 0)
            return ILLEGAL_TILE;
        if (orientation.equals("0")) {
            if (y + size - 1 >= m)
                return EXCEEDING;
            for (int i = 0; i < size; i++) {
                if (board[x][y + i] == SUBMARINE)
                    return OVERLAP;
                else if (board[x][y + i] == BORDER)
                    bordering = true;
            }
            if(bordering)
                return BORDERING;
        } else {
            if (x + size - 1 >= n)
                return EXCEEDING;
            for (int i = 0; i < size; i++) {
                if (board[x + i][y] == SUBMARINE)
                    return OVERLAP;
                else if (board[x + i][y] == BORDER)
                    bordering = true;
            }
            if(bordering)
                return BORDERING;
        }
        return VALID;
    }
    /**
     *
     * @param num - the number whose digits are to be counted
     * <p>
     *      this function returns the amount of digits in the number
     * @returns digit count
     * </p>
     */
    public static int countDigits(int num) {
        int count = 0;
        if (num == 0)
            return 1;
        while (num > 0) {
            num /= 10;
            count++;
        }
        return count;
    }

    /**
     *
     * @param mat - the matrix containing the board to print
     * <p>
     *      this function prints the board according to the format
     * </p>
     */
    public static void printBoard(char[][] mat) {
        int maxDigits = countDigits(mat.length);
        for(int i = 0; i < maxDigits+1; i++)
            System.out.print(" ");
        for (int j = 0; j < mat[0].length-1; j++) {
            System.out.print(j + " ");
        }
        System.out.println(mat[0].length-1);
        for (int i = 0; i < mat.length; i++) {
            for (int k = 0; k < maxDigits - countDigits(i); k++)
                System.out.print(" ");
            System.out.print(i + " ");
            for (int j = 0; j < mat[0].length - 1; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println(mat[i][mat[0].length-1]);
        }
        System.out.println("");
    }

    /**
     *
     * @param code - the error code
     *             <p>
     *                  this function prints an error message according to the error code.
     *             </p>
     *
     */
    public static void printError(int code) {
        switch (code) {
            case (INVALID_ORIENTATION):
                System.out.println("Illegal orientation, try again!");
                break;
            case (ILLEGAL_TILE):
                System.out.println("Illegal tile, try again!");
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
            case (TRIED):
                System.out.println("Tile already attacked, try again!");
                break;
        }
    }

    /**
     *
     * @param playerBoard - the matrix containing the player's board
     * @param x - the row of the submarine to be added
     * @param y - the column of the submarine to be added
     * @param orientation - horizontal or vertical
     * @param size - the size of the submarine
     *             <p>
     *                  this function adds the submarine to the given coordinates, and pads around it with border tiles.
     *             </p>
     */
    public static void addSubmarine(char[][] playerBoard, int x, int y, String orientation, int size) {
        int n = playerBoard.length;
        int m = playerBoard[0].length;
        if (orientation.equals("0")) {
            for (int i = 0; i < size; i++) {
                playerBoard[x][y + i] = SUBMARINE;
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        if (!(x + j < 0 || x + j >= n || y + i + k < 0 || y + i + k >= m)) {
                            if (playerBoard[x + j][y + i + k] != SUBMARINE)
                                playerBoard[x + j][y + i + k] = BORDER;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                playerBoard[x + i][y] = SUBMARINE;
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        if (!(x + i + j < 0 || x + i + j >= n || y + k < 0 || y + k >= m)) {
                            if (playerBoard[x + i + j][y + k] != SUBMARINE)
                                playerBoard[x + i + j][y + k] = BORDER;
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param playerBoard - the matrix containing the player's board
     * @param amountsAndSizes - an array containing the amount of each size of submarine
     *      <p>
     *              places the player's submarines by receiving user inputs
     *      </p>
     */
    public static void placePlayerSubmarines(char[][] playerBoard, int[] amountsAndSizes) {
        int size;
        int amount;
        int errorCode = INVALID_ORIENTATION;
        int x = 0;
        int y = 0;
        String orientation = "";
        String placeAndOrientation;
        for (int amountIndex = 0; amountIndex < amountsAndSizes.length; amountIndex += 2) {
            amount = amountsAndSizes[amountIndex];
            size = amountsAndSizes[amountIndex + 1];
            for (int i = 0; i < amount; i++) {
                System.out.println("Enter location and orientation for battleship of size " + size);
                while (errorCode != 0) {
                    placeAndOrientation = scanner.nextLine();
                    x = castSubstringToInt(placeAndOrientation, 0, placeAndOrientation.indexOf(","));
                    placeAndOrientation = placeAndOrientation.substring(placeAndOrientation.indexOf(", ") + 2);
                    y = castSubstringToInt(placeAndOrientation, 0, placeAndOrientation.indexOf(","));
                    orientation = placeAndOrientation.substring(placeAndOrientation.indexOf(",") + 2);
                    errorCode = checkValidity(x, y, orientation, size, playerBoard);
                    printError(errorCode);
                }
                addSubmarine(playerBoard, x, y, orientation, size);
                System.out.println("Your current game board:");
                printBoard(resetBoard(playerBoard));
                errorCode = INVALID_ORIENTATION;
            }
        }
    }

    /*
        Returns random integer in the given range, where if low > high -1 will be returned
     */
    public static int randomIntInRange(int low, int high) {
        if (low > high)
            return -1;
        else {
            return rnd.nextInt((high - low) + 1) + low;
        }
    }

    /**
     *
     * @param board - the computer's board
     * @param amountsAndSizes - an array containing the amount of each size of submarine
     */
    public static void placeComputerSubmarines(char[][] board, int[] amountsAndSizes) {
        fillBoard(board);
        int size;
        int amount;
        int x = 0;
        int y = 0;
        boolean valid = false;
        int n = board.length;
        int m = board[0].length;
        String orientation = "";
        for (int amountIndex = 0; amountIndex < amountsAndSizes.length; amountIndex += 2) {
            amount = amountsAndSizes[amountIndex];
            size = amountsAndSizes[amountIndex + 1];
            for (int i = 0; i < amount; i++) {
                while (!valid) {
                    x = randomIntInRange(0, n - 1);
                    y = randomIntInRange(0, m - 1);
                    orientation = Integer.toString(randomIntInRange(0, 1));
                    valid = checkValidity(x, y, orientation, size, board) == VALID;
                }
                addSubmarine(board, x, y, orientation, size);
                valid = false;
            }
        }
    }

    /**
     *
     * @param board - the board to be reset
     *      <p>
     *              erases the border tiles
     *      </p>
     * @return - returns the same board without the border tiles
     */
    public static char[][] resetBoard(char[][] board) {
        char[][] hiddenBoard = new char[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == BORDER)
                    hiddenBoard[i][j] = EMPTY;
                else {
                    hiddenBoard[i][j] = board[i][j];
                }
            }
        }
        return hiddenBoard;
    }

    /*
        Asks the player to attack a submarine, and attacks the given location. Then prints whether a hit
        was made and returns true if it was sunk, false otherwise.
     */
    public static boolean playerAttack(char[][] computerBoard) {
        int x = 0, y = 0, checked = INVALID_ORIENTATION;
        String coordinates;
        System.out.println("Enter a tile to attack");
        while (!(checked == VALID)) {
            coordinates = scanner.nextLine();
            x = castSubstringToInt(coordinates, 0, coordinates.indexOf(","));
            y = castSubstringToInt(coordinates, coordinates.indexOf(",") + 2, coordinates.length());
            checked = checkAttack(x, y, computerBoard);
            printError(checked);
        }
        if (computerBoard[x][y] == SUBMARINE) {
            System.out.println("That is a hit!");
            computerBoard[x][y] = HIT;
            return checkSunk(computerBoard, x, y);
        }
        if (computerBoard[x][y] == EMPTY) {
            System.out.println("That is a miss!");
            computerBoard[x][y] = MISS;
        }
        return false;
    }

    /**
     *
     * @param board - the computer's board.
     * @return - returns the matrix containing the computer's board, replacing submarines with empty tiles.
     */
    public static char[][] coverCompBoard(char[][] board) {
        char[][] coveredBoard = new char[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == SUBMARINE)
                    coveredBoard[i][j] = EMPTY;
                else {
                    coveredBoard[i][j] = board[i][j];
                }
            }
        }
        return coveredBoard;
    }

    /**
     *
     * @param x - row
     * @param y - column
     * @param board - board to be attacked
     * @return - returns a fitting error code if the attack was illegal and a valid code if it was legal.
     */
    public static int checkAttack(int x, int y, char[][] board) {
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) {
            return ILLEGAL_TILE;
        }
        if (board[x][y] == MISS || board[x][y] == HIT) {
            return TRIED;
        }
        return VALID;
    }

    /**
     *
     * @param board - the player's board to be attacked.
     * @return - returns wether or not a ship was sunk during the attack
     */
    public static boolean computerAttack(char[][] board) {
        int checked = INVALID_ORIENTATION, x = 0, y = 0;
        while (checked != VALID) {
            x = randomIntInRange(0, board.length - 1);
            y = randomIntInRange(0, board[0].length - 1);
            checked = checkAttack(x, y, board);
        }
        System.out.println("The computer attacked (" + x + ", " + y + ")");
        if (board[x][y] == SUBMARINE) {
            System.out.println("That is a hit!");
            board[x][y] = HIT;
            return checkSunk(board, x, y);
        }
        else {
            board[x][y] = MISS;
            System.out.println("That is a miss!");
        }
        return false;
    }

    /**
     *
     * @param amountsAndSizes - an array containing the amount of each size of submarine
     * @return - the amount of submarines each player has
     */
    public static int countSubs(int[] amountsAndSizes) {
        int count = 0;
        for (int i = 0; i < amountsAndSizes.length; i += 2)
            count += amountsAndSizes[i];
        return count;
    }

    /**
     *
     * @param board - the board to be checked
     * @param x - row
     * @param y - column
     * @return - true if the ship given is horizontal
     */
    public static boolean checkHorizontal(char[][] board, int x, int y) {
        if (board.length == 1) {
            return true;
        } else {
            if (((x == 0 && board[1][y] == EMPTY)||(x == 0 && board[1][y] == MISS)) ||
                    (x == board.length-1 && board[x-1][y] == EMPTY)||(x == board.length-1 && board[x-1][y] == MISS) ||
                    (x > 0 && x < board.length-1 && (board[x-1][y] == EMPTY || board[x-1][y] == MISS) &&
                            (board[x+1][y] == EMPTY || board[x+1][y] == MISS)))
                return true;
            else
                return false;
        }
    }

    /*
        After an attack, checks if the ship attacked was sunk.
     */
    public static boolean checkSunk(char[][] board, int x, int y) {
        boolean horizontal = checkHorizontal(board, x, y);
        boolean spaceNotSeen = true;
        if (horizontal) {
            for (int i = y; i < board[0].length && spaceNotSeen; i++) {
                if (board[x][i] == SUBMARINE) {
                    return false;
                }
                spaceNotSeen = (board[x][i] != EMPTY && board[x][i] != MISS);
            }
            spaceNotSeen = true;
            for (int i = y; i >= 0 && spaceNotSeen; i--) {
                if (board[x][i] == SUBMARINE)
                    return false;
                spaceNotSeen = (board[x][i] != EMPTY && board[x][i] != MISS);
            }
        } else {
            for (int i = x; i < board.length && spaceNotSeen; i++) {
                if (board[i][y] == SUBMARINE)
                    return false;
                spaceNotSeen = (board[i][y] != EMPTY && board[i][y] != MISS);
            }
            spaceNotSeen = true;
            for (int i = x; i >= 0 && spaceNotSeen; i--) {
                if (board[i][y] == SUBMARINE)
                    return false;
                spaceNotSeen = (board[i][y] != EMPTY && board[i][y] != MISS);
            }
        }
        return true;
    }

    /**
     *
     * @param board - player's board
     * @return - returns the player's board with Xs instead of Vs and -s instead of Xs.
     */
    public static char[][] coverPlayerBoard(char[][] board){
        char[][] newBoard = new char[board.length][board[0].length];
        for(int i =0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j] == HIT)
                    newBoard[i][j] = MISS;
                else if (board[i][j] == MISS)
                    newBoard[i][j] = EMPTY;
                else
                    newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

    /**
     *
     * @param playerBoard - player's board
     * @param computerBoard - computer's board
     * @param subCount - the amount of submarines on each board
     *                 <p>
     *                 the function manages attacks, alternating between computer and player, printing guessing and game boards
     *                 and when ships are drowned. Returns who won at the end of the game.
     *                 </p>
     * @return - returns true if player won, false if computer won.
     */
    public static boolean manageAttacks(char[][] playerBoard, char[][] computerBoard, int subCount) {
        int playerCount = subCount;
        int compCount = subCount;
        while (playerCount > 0 && compCount > 0) {
            System.out.println("Your current guessing board:");
            printBoard(coverCompBoard(computerBoard));
            if (playerAttack(computerBoard)) {
                compCount--;
                System.out.println("The computer's battleship has been drowned, " + compCount + " more battleships to go!");
            }
            if (compCount > 0 && computerAttack(playerBoard)) {
                playerCount--;
                System.out.println("Your battleship has been drowned, you have left " + playerCount + " more battleships!");
            }
            if (compCount > 0) {
                System.out.println("Your current game board:");
                printBoard(coverPlayerBoard(playerBoard));
            }
        }
        if (playerCount > 0)
            return true;
        return false;
    }
}