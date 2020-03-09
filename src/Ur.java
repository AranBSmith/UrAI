import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class Ur implements Cloneable {
    public static final int
            WHITETURN = 1,
            BLACKTURN = -1,
            PIECENUM = 6;

    private int[] diceRolls = new int[]{0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4};
    private int[] whitePiecePositions, blackPiecePositions;
    private ArrayList<Integer> rosetteSquarePositions;
    private int turn;
    private int turnsPassed;
    private double score;
    boolean diceRoll;

    Ur() {
        whitePiecePositions = new int[PIECENUM];
        blackPiecePositions = new int[PIECENUM];
        rosetteSquarePositions = new ArrayList<>();
        rosetteSquarePositions.add(4);
        rosetteSquarePositions.add(8);
        rosetteSquarePositions.add(14);
        turn = WHITETURN;
        turnsPassed = 0;
        score = 0;
        diceRoll = true;
    }

    Ur(int[] whitePiecePositions, int[] blackPiecePositions) {
        this.whitePiecePositions = whitePiecePositions;
        this.blackPiecePositions = blackPiecePositions;
        rosetteSquarePositions = new ArrayList<>();
        rosetteSquarePositions.add(4);
        rosetteSquarePositions.add(8);
        rosetteSquarePositions.add(14);
        turn = WHITETURN;
        turnsPassed = 0;
        score = 0;
        diceRoll = true;

        for (int pos : whitePiecePositions) score += pos;
        for (int pos : blackPiecePositions) score -= pos;
    }

    double getScore(){
        return score;
    }

    // return available pieces for a particular move and colour
    ArrayList<Integer> availablePiecesForMove(int roll, int turn) {
        ArrayList<Integer> availablePiecesForMove = new ArrayList<>();
        // 1. not an exact roll from the end
        // 2. not about to land on an occupied rosetta square
        // 3. not about to land on a friendly piece
        if (turn == WHITETURN) {
            for(int i = 0; i < PIECENUM; i++){
                int target = whitePiecePositions[i] + roll;
                if(target <= 15 && !friendlyCollision(turn, i, roll) && !occupiedRosettaSquare(target)){
                    availablePiecesForMove.add(i);
                }
            }
        } else if(turn == BLACKTURN) {
            for(int i = 0; i < PIECENUM; i++){
                int target = blackPiecePositions[i] + roll;
                if(target <= 15 && !friendlyCollision(turn, i, roll) && !occupiedRosettaSquare(target)){
                    availablePiecesForMove.add(i);
                }
            }
        }

        return availablePiecesForMove;
    }

    int rollDice() {
        Random rand = new Random();
        diceRoll = false;
        return diceRolls[rand.nextInt(16)];
    }

    public void setRoll(boolean status) {
        diceRoll = status;
    }

    private boolean occupiedRosettaSquare(int pos) {
        if(rosetteSquarePositions.contains(pos) &&
                (arrayContains(whitePiecePositions, pos) || arrayContains(blackPiecePositions, pos)))
            return true;

        return false;
    }

    public void changeTurn(){
        turn*=-1;
    }

    public int getTurn(){
        return turn;
    }

    void movePiece(int turn, int piece, int steps){
        try {
            if (this.turn == WHITETURN && turn == WHITETURN) {
                whitePiecePositions[piece] += steps;
                score += steps;
                if (!rosetteSquarePositions.contains(whitePiecePositions[piece])) {
                    changeTurn();
                }
            } else if (this.turn == BLACKTURN && turn == BLACKTURN) {
                blackPiecePositions[piece] += steps;
                score -= steps;
                if (!rosetteSquarePositions.contains(blackPiecePositions[piece])) {
                    changeTurn();
                }
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("It is not that player's turn!");
        }

        pieceCollision(turn, piece);
        turnsPassed++;
        diceRoll = true;
    }

    private void pieceCollision(int turn, int piece){
        for(int i = 0; i < PIECENUM; i++) {
            if (turn == WHITETURN) {
                if (blackPiecePositions[i] == whitePiecePositions[piece]
                        && whitePiecePositions[piece] > 4 && whitePiecePositions[piece] < 13) {
                    score+=blackPiecePositions[i];
                    blackPiecePositions[i] = 0;
                    return;
                }

            } else if(turn == BLACKTURN){
                if(whitePiecePositions[i] == blackPiecePositions[piece]
                        && blackPiecePositions[piece] > 4 && blackPiecePositions[piece] < 13) {
                    score-=whitePiecePositions[i];
                    whitePiecePositions[i] = 0;
                    return;
                }
            }
        }
    }

    private boolean friendlyCollision(int turn, int piece, int roll){
        if(turn == WHITETURN) {
            int target = whitePiecePositions[piece] + roll;
            return arrayContains(whitePiecePositions, target) && target!= 15;
        }

        int target = blackPiecePositions[piece] + roll;
        return arrayContains(blackPiecePositions, target) && target!=15;
    }

    private boolean arrayContains(int[] array, int member) {
        for (int sample : array) {
            if (sample == member) {
                return true;
            }
        }

        return false;
    }

    public boolean checkState() {
        boolean wCheck = true, bCheck = true;
        for(int i = 0; i < PIECENUM; i++){
            if(whitePiecePositions[i] < 15) wCheck = false;
            if(blackPiecePositions[i] < 15) bCheck = false;
        }

        return wCheck || bCheck;
    }

    public double randomGame() {
        // while the game hasn't finished
        Random random = new Random();

        // 500 as we know that the average game length under with random moves never exceeds 200
        for(int i = 0; i < 500; i++){
            if(checkState()) {
                // System.out.println(i);
                break;
            }

            // roll the dice
            int roll = rollDice();
            int turn = getTurn();

            // get possible moves for state
            ArrayList<Integer> pieces = availablePiecesForMove(roll, turn);

            // pick random valid piece for move
            if(pieces.size() != 0){
                int selectedPiece = pieces.get(random.nextInt(pieces.size()));
                //System.out.println("selected piece: " + selectedPiece + " Turn: " + turn);
                movePiece(turn, selectedPiece, roll);
            } else {
                // we rolled zero and the move goes on to the next player, so we just move a piece zero positions forward
                movePiece(turn, 0, 0);
            }
        }

        return getScore();
    }

    @Override
    public String toString() {
        String turnStatus;
        if (turn == 1) turnStatus = "It is white's turn.";
        else turnStatus = "It is black's turn.";

        return "Player White: " + Arrays.toString(whitePiecePositions) + "\n" +
                "Player Black: " + Arrays.toString(blackPiecePositions) + "\n" +
                turnStatus + "\n" +
                "Turns passed: " + turnsPassed + "\n" +
                "Score: " + score + "\n";
    }

    @Override
    protected Ur clone() throws CloneNotSupportedException {
        Ur clone = (Ur) super.clone();

        clone.whitePiecePositions = this.whitePiecePositions.clone();
        clone.blackPiecePositions = this.blackPiecePositions.clone();

        return clone;
    }
}
