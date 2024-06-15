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
            switch (i) {
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

    public void printBoard() { // 9, 7, 9 (squares) (+1 for end index)
        char[] space = { '|', 'P', ' ', '|' };
        char[] start = { '|', 'S', 'T', 'A', 'R', 'T', ' ', 'P', ' ', '|' };
        char[] end = { '|', 'E', 'N', 'D', ' ', 'P', ' ', '|' };

        printSpace(start);

        for (int i = 1; i <= board.size(); i++) {
            BoardSpace bs = board.moveNode(i);
            // check if occupied, print player if so
            if (bs.occupied) {
                char player = bs.player;
                space[2] = player;
            }
            printSpace(space);

            if (i == 9) {
                System.out.println();
            } // next line
            if (i == 16) {
                System.out.println();
            } // next line

        }

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

    public boolean isGameOver() {
        if (board.last().occupied) { // BUT CHECK POINTS!!! TO DO
            return true;
        }
        return false;
    }

    public void winner() {
        char pl = board.last().getPreviousPlayer();
        Player player = getPlayer(pl);
        System.out.println("Player " + pl + " won the game with " + player.points + " points!");
    }

    public void makeMove() {
        // whose Turn?
        char player = getWhoseTurn();
        Player curPlayer = getPlayer(player);
        int currentSpace = getPlayerPosition(player); // find player's position

        // roll die, examine space
        int move = rollDice();
        System.out.println("roll: " + move);

        int bsIndex = move + currentSpace;

        // check if valid move/space
        if (bsIndex <= board.size()) { // a valid space
            BoardSpace bs = board.moveNode(bsIndex);
            System.out.println("Move to position " + bsIndex);

            // CHECK IF OCCUPIED
            if (bs.occupied) {
                char previousPlayer = bs.getPreviousPlayer(); // get previous player occupying space

                System.out.println(
                        "Player " + previousPlayer + " previously occupied the space. They get booted to start!");
                bootPlayerToStart(previousPlayer);
            }
            // update board space (player new spot)
            bs.playerTakesSpace(player);
            // make previous board space unoccupied
            if (currentSpace != 0) {
                BoardSpace previousBS = board.moveNode(currentSpace);
                previousBS.playerLeavesSpace();
            }

            // update player's points + info
            System.out.println("Player " + player + " gets " + bs.value + " points.");
            curPlayer.points += bs.value;

            System.out.println("Player " + player + " now has " + curPlayer.points + " points.");
            curPlayer.position = bsIndex; // this could be a wrong line, double check index stuff

        } else {
            System.out.println("Overshot it! Player " + player + " loses a turn.");
        }
        curPlayer.movesMade++;
        turn++; // next turn!

        System.out.println();
    }

    public static void main(String[] args) {
        BoardGame game = new BoardGame();
        game.createBoard();

        game.printBoard();

        game.setPlayers(3);

        do {
            System.out.println("Player " + game.getWhoseTurn() + "'s turn:\n=====================================");
            game.makeMove();
        } while (!game.isGameOver());

        // final game message
        game.winner();
    }

}
