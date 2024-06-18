import java.util.Random;

public class BoardGame {
    // determine the probability of winning a board game
    // the average number of moves necessary for one of the players to “win” the
    // game.

    // boardSpace nested class
    protected class BoardSpace {
        int value;
        int position;
        boolean occupied;
        char player;

        public BoardSpace(int v, int p) {
            value = v;
            position = p;
            occupied = false;
        }

        public char getPreviousPlayer() {
            return player;
        }

        public void playerTakesSpace(char player) {
            occupied = true;
            this.player = player;
        }

        public void playerLeavesSpace() {
            occupied = false;
            this.player = ' ';
        }
    }

    class Player {
        char player;
        int points = 0;
        int position = 0; // start block //????
        int movesMade = 0;
    }

    // board variable --> doubly linked list
    DoublyLinkedList<BoardSpace> board;
    Random rand = new Random();
    int numPlayers;
    Player[] players;
    static int turn = 1;

    int endIndex;

    public void createBoard() {
        board = new DoublyLinkedList<>();
        int[] boardSpacesValue = { 5, 10, 8, 10, 7, 5, 9, 10, 6, 7, 10, 6, 5, 8, 9, 5, 10, 5, 9, 6, 8, 7, 10, 6, 8 };
        int position = 1;

        for (int n : boardSpacesValue) {
            BoardSpace bs = new BoardSpace(n, position++);
            board.addLast(bs);
        }
        // special last boardSpace for End Space
        endIndex = position++;
        BoardSpace end = new BoardSpace(0, endIndex);
        board.addLast(end);
    }

    public void setPlayers(int n) {
        numPlayers = n;

        players = new Player[n];
        for (int i = 0; i < n; i++) {
            players[i] = new Player(); // create player thing
            switch (i + 1) {
                case 1:
                    players[i].player = 'A';
                    break;
                case 2:
                    players[i].player = 'B';
                    break;
                case 3:
                    players[i].player = 'C';
                    break;
                case 4:
                    players[i].player = 'D';
                    break;
            }
        }
    }

    public void printSpace(char[] spaceArray) {
        for (int i = 0; i < spaceArray.length; i++)
            System.out.print(spaceArray[i]);
    }

    public char[] resetCharArray(char[] space) { // so we don't have to create new variables
        for (int i = 0; i < 4; i++) {
            if (i == 0 || i == 3) {
                space[i] = '|';
            } else {
                space[i] = ' ';
            }
        }
        return space;
    }

    public void printBoard() { // 9, 7, 9 (squares) (+1 for end index)
        char[] space = { '|', ' ', ' ', '|' };
        char[] start = { '|', 'S', 'T', 'A', 'R', 'T', ' ', ' ', ' ', ' ', ' ', ' ', '|' };
        char[] end = { '|', 'E', 'N', 'D', ' ', ' ', ' ', '|' };

        // see who is at start, player position 0
        int startArrayIndex = 8;
        for (int i = 0; i < players.length; i++) {
            if (players[i].position == 0) {
                start[7] = 'P';
                start[startArrayIndex++] = players[i].player;
            }
        }
        printSpace(start);

        // board spaces
        for (int i = 1; i <= board.size(); i++) {
            BoardSpace bs = board.moveNode(i);
            // check if occupied, print player if so
            if (bs.occupied) {
                char player = bs.player;
                space[1] = 'P';
                space[2] = player;
                // if end index...
                if (i == 26) {
                    end[5] = 'P';
                    end[6] = player;
                    break;
                }
            }
            printSpace(space);
            resetCharArray(space);

            if (i == 9 || i == 16) {
                System.out.print("\n             ");
            }

        }

        // end space
        printSpace(end);
        System.out.println();
    }

    public char getWhoseTurn() { // whose turn it is
        int player = turn % numPlayers;
        char playerS = ' ';

        if (player == 0) { // yes
            player = numPlayers;
        }

        switch (player) {
            case 1:
                // player 1's turn // PlayerA
                playerS = 'A';
                break;
            case 2:
                // player 2
                playerS = 'B';
                break;
            case 3:
                // player 3
                playerS = 'C';
                break;
            default: // Player 4
                playerS = 'D';
                break;
        }
        return playerS;
    }

    public Player getPlayer(char p) {
        switch (p) {
            case 'A':
                return players[0];
            case 'B':
                return players[1];
            case 'C':
                return players[2];
            default: // Player 'D'
                return players[3];
        }
    }

    public int getPlayerPosition(char p) {
        int position = 0;
        for (int i = 1; i <= board.size(); i++) {
            BoardSpace bs = board.moveNode(i);

            if (bs.player == p) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void bootPlayerToStart(char player) {
        Player pl = getPlayer(player);
        pl.points = 0;
        pl.position = 0;
    }

    public int rollDice() {
        return rand.nextInt(6) + 1; // 1 to 6
    }

    public boolean isGameOver() { // assuming we just occupy the space
        if (board.last().occupied) {
            char play = board.last().player;
            Player endPlayer = getPlayer(play);
            if (endPlayer.points >= 44)
                return true;
            else
                bootPlayerToStart(play);
        }
        return false;
    }

    public Player winner() {
        if (!isGameOver()) {
            return null;
        }
        char pl = board.last().getPreviousPlayer();
        Player player = getPlayer(pl);

        return player;
    }

    public void makeMove() {
        // whose Turn?
        char player = getWhoseTurn();
        Player curPlayer = getPlayer(player);
        int currentSpace = getPlayerPosition(player); // find player's position

        // move number DELETE PRINT MESSAGE
        // System.out.println("MOVE " + curPlayer.movesMade);

        // roll die, examine space
        int move = rollDice();
        // System.out.println("roll: " + move); //DELETE PRINT MESSAGE

        int bsIndex = move + currentSpace;

        // check if valid move/space
        if (bsIndex <= board.size()) { // a valid space
            BoardSpace bs = board.moveNode(bsIndex);
            // System.out.println("Move to position " + bsIndex); //DELETE PRINT MESSAGE

            // CHECK IF OCCUPIED
            if (bs.occupied) {
                char previousPlayer = bs.getPreviousPlayer(); // get previous player occupying space

                // System.out.println( //DELETE PRINT MESSAGE
                // "Player " + previousPlayer + " previously occupied the space. They get booted
                // to start!");
                bootPlayerToStart(previousPlayer);
            }

            boolean gameOver = false;
            boolean continueSpace = true;
            if (bsIndex == 26) { //
                bs.playerTakesSpace(player); // take the space
                gameOver = isGameOver();

                if (!gameOver) { // haven't achieved min points to win, gets booted
                    continueSpace = false;
                    bs.playerLeavesSpace(); // bad, leave the space
                }
            }

            if (continueSpace) {
                // update board space (player new spot)
                bs.playerTakesSpace(player);

                // update player's points + info DELETE PRINT MESSAGES
                // System.out.println("Player " + player + " gets " + bs.value + " points.");
                curPlayer.points += bs.value;

                // System.out.println("Player " + player + " now has " + curPlayer.points + "
                // points.");
                curPlayer.position = bsIndex;
            }
            // make previous board space unoccupied
            if (currentSpace != 0) {
                BoardSpace previousBS = board.moveNode(currentSpace);
                previousBS.playerLeavesSpace();
            }

        } else { // DELETE PRINT MESSAGES
            // System.out.println("Overshot it! Player " + player + " loses a turn.");
            // System.out.println("current position: " + currentSpace + " | Points: " +
            // curPlayer.points);
        }
        curPlayer.movesMade++;
        turn++; // next turn!

        // System.out.println(); DELETE PRINT MESSAGE
    }

    public static void runOneGame(BoardGame game, int numPlayers, int gameNum) {
        game.createBoard();

        game.setPlayers(numPlayers);

        do { // DELETE PRINT MESSAGE
             // System.out.println("Player " + game.getWhoseTurn() + "'s
             // turn:\n=====================================");
            game.makeMove();
        } while (!game.isGameOver());

    }

    public static void findAvgMovesToWin(int numPlayers) {
        int sumA = 0;
        int gamesWonA = 0;
        int sumB = 0;
        int gamesWonB = 0;
        int sumC = 0;
        int gamesWonC = 0;
        int sumD = 0;
        int gamesWonD = 0;
        char letterW = ' ';
        char[] players = new char[numPlayers];
        String plysStr = "";

        for (int i = 1; i <= 1000; i++) {
            BoardGame game = new BoardGame();
            runOneGame(game, numPlayers, i);
            int movesToWin = game.winner().movesMade; // moves for winner to win

            letterW = game.winner().player; // the winner type (ABCD)

            switch (letterW) {
                case 'A':
                    sumA += movesToWin;
                    gamesWonA++;
                    break;
                case 'B':
                    sumB += movesToWin;
                    gamesWonB++;
                    break;
                case 'C':
                    sumC += movesToWin;
                    gamesWonC++;
                    break;
                default:
                    sumD += movesToWin;
                    gamesWonD++;
                    break;
            }

            if (i % 100 == 1) {
                System.out.println("Game " + i);
                game.printBoard();
                System.out.println();
            }

            // list of people, collect info just once
            if (i == 1) {
                for (int p = 0; p < numPlayers; p++) {
                    players[p] = game.players[p].player;
                    plysStr += players[p];
                    if (p < game.players.length - 1)
                        plysStr += ", ";
                }
            }

        }

        int avgA = 0;
        int avgB = 0;
        int avgC = 0;
        int avgD = 0;
        // calculate any averages
        if (gamesWonA > 0) {
            avgA = sumA / gamesWonA; // careful of 0 denominator
        }
        if (gamesWonB > 0) {
            avgB = sumB / gamesWonB;
        }
        if (gamesWonC > 0) {
            avgC = sumC / gamesWonC;
        }
        if (gamesWonD > 0) {
            avgD = sumD / gamesWonD;
        }

        // Results
        System.out.println("RESULTS");
        System.out.println("Players in game: " + plysStr);
        for (int p = 1; p <= players.length; p++) {
            int moves = 0;
            int gamesWon = 0;
            switch (p) {
                case 1:
                    moves = avgA;
                    gamesWon = gamesWonA;
                    break;
                case 2:
                    moves = avgB;
                    gamesWon = gamesWonB;
                    break;
                case 3:
                    moves = avgC;
                    gamesWon = gamesWonC;
                    break;
                case 4:
                    moves = avgD;
                    gamesWon = gamesWonD;
                    break;
            }
            String percentage = String.format("%.2f", (gamesWon / 1000.0) * 100);
            System.out
                    .println("Player " + players[p - 1] + " moves / avg % winning:   " + moves + " /  "
                            + percentage
                            + "%");
        }

        System.out.println();
    }

    public static void main(String[] args) {

        findAvgMovesToWin(1);
        findAvgMovesToWin(2);
        findAvgMovesToWin(3);
        findAvgMovesToWin(4);
    }

}
