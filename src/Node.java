import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;

class Node {
    private static int nodesExpanded = 0;
    private static int nodesInMemory = 0;

    private Node parent;
    Ur problem;
    private int pathDepth;
    private int roll;
    private int piece;
    boolean chanceNode;

    Node() {
        nodesInMemory++;
        this.parent = null;
        this.problem = new Ur();
        this.pathDepth = 0;
        this.roll = 0;
        this.piece = 0;
        this.chanceNode = false;
    }

    Node(Node parent, Ur problem, int pathDepth, int roll, boolean chanceNode) {
        nodesInMemory++;
        this.parent = parent;
        this.problem = problem;
        this.pathDepth = pathDepth;
        this.roll = roll;
        this.piece = 0;
        this.chanceNode = chanceNode;
    }

    static void resetMeta() {
        nodesInMemory = 0;
        nodesExpanded = 0;
    }

    @Override
    public String toString() {
        return problem.toString() +
                "Roll: " + roll + "\n" +
                "Piece: " + piece + "\n" +
                "Path Depth: " + pathDepth + "\n" +
                "Chance Node: " + chanceNode;
    }

    static int getNodesInMemory() {
        return nodesInMemory;
    }

    Node getParent(){
        return this.parent;
    }

    int getRoll() {
        return this.roll;
    }

    double getHueristicScore(){return this.problem.getScore();}

    int getPathDepth() {return this.pathDepth;}

    static int getNodesExpanded() {
        return nodesExpanded;
    }

     Ur getUr() {
        return this.problem;
    }

    double getProbability(){
        try {
            switch (roll) {
                case 0:
                    return 0.06;
                case 4:
                    return 0.06;
                case 1:
                    return 0.25;
                case 3:
                    return 0.25;
                case 2:
                    return 0.38;
            }
            throw new InvalidParameterException();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    boolean isChanceNode (){
        return chanceNode;
    }

    LinkedList<Node> getChildNodes(int roll) {
        LinkedList<Node> children = new LinkedList<>();
        try {

            int turn = this.problem.getTurn();
            LinkedList<Node> childNodes = new LinkedList<>();
            ArrayList<Integer> legalMoves = this.problem.availablePiecesForMove(roll, turn);

            // for every legal move, create a new Node to return later!
            for(int legalPieceMove : legalMoves) {
                Ur childProblem = this.problem.clone();
                childProblem.movePiece(turn, legalPieceMove, roll);
                childNodes.add(new Node(this, childProblem, this.pathDepth + 1, roll, false));
            }

            nodesExpanded++;
            return childNodes;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return children;
    }

    LinkedList<Node> getChildNodes() {
        LinkedList<Node> children = new LinkedList<>();

        try {
            if(!chanceNode) {
                // get 5 chance nodes representing the dice rolls 0-4
                for (int i = 0; i < 5; i++) {
                    Node child = new Node(this, this.problem.clone(), this.pathDepth + 1, i, true);
                    children.add(child);
                }
            } else {
                // we are getting the children of a chance node and must return only the legal
                // moves for the particular roll held in the chance node
                int turn = this.problem.getTurn();
                // for (int i = 0; i < 5; i++) {
                    ArrayList<Integer> legalMoves = this.problem.availablePiecesForMove(roll, turn);

                    // for every legal move, create a new Node to return later!
                    for (int legalPieceMove : legalMoves) {
                        Ur childProblem = this.problem.clone();
                        childProblem.movePiece(turn, legalPieceMove, roll);
                        children.add(new Node(this, childProblem, this.pathDepth + 1, roll, false));
                    }
                //}
                nodesExpanded++;
            }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return children;
    }
}
