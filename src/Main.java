import java.util.LinkedList;
import java.util.Random;

public class Main {

    static boolean log = false;
    static boolean prune = true;
    static boolean testOptimalPlay = false,
            testSubOptimalPlay = false,
            monteCarloTreeSearchVsSuboptimalPlayer = false,
            monteCarloTreeSearchVsExpectiminimax = true,
            blackGoesFirst = true; // only applies to the monte carlo involved searches

    public static void main(String[] args) {

        Node exampleStart = new Node();
        Node resultNode = monteCarloPlayVsSubOptimalPlayer(exampleStart, 2);
        LinkedList<Node> parents = new LinkedList<>();

        parents.add(resultNode);

        while(resultNode.getParent() != null){
            resultNode = resultNode.getParent();
            parents.add(resultNode);
        }

        parents.add(resultNode);
        while(!parents.isEmpty()){
            System.out.println(parents.removeLast().toString() + "\n");
        }

        /*we pitch a monte carlo tree search vs a player who makes random moves, expectiminimax first occurs to a
        certain ply, and then monte carlo is performed. Player white will then assume that the suboptimal player
        is actually optimal. Collect and output stats.*/
        if(monteCarloTreeSearchVsSuboptimalPlayer) {
            System.out.println("Monte Carlo Tree Search VS Suboptimal Player");
            double[] scores = new double[]{0, 0};
            float sumNodesExpanded = 0;
            float sumNodesInMemory = 0;

            LinkedList<Integer> plyDepths = new LinkedList<>();
            plyDepths.add(0);
            plyDepths.add(2);
            plyDepths.add(4);
            plyDepths.add(6);

            int iterations = 1000;
            for (int ply : plyDepths) {
                for (int i = 0; i < iterations; i++) {
                    Node.resetMeta();

                    Node startNode;
                    if(blackGoesFirst) {
                        Ur ur = new Ur();
                        ur.changeTurn();
                        startNode = new Node(null, ur, 0, 0, false);
                    } else {startNode = new Node();}

                    Node solution = monteCarloPlayVsSubOptimalPlayer(startNode, ply);

                    sumNodesExpanded += Node.getNodesExpanded();
                    sumNodesInMemory += Node.getNodesInMemory();

                    if (solution.getHueristicScore() > 0) {
                        scores[0]++;
                    } else {
                        scores[1]++;
                    }
                }

                System.out.println("White ply depth: " + ply + " vs. Suboptimal Opponent");
                System.out.println("White win percentage: " + scores[0] / (scores[0] + scores[1]));
                System.out.println("Black win percentage: " + scores[1] / (scores[0] + scores[1]));
                System.out.println("Avg. nodes expanded: " + sumNodesExpanded / iterations);
                System.out.println("Avg. nodes in memory: " + sumNodesInMemory / iterations);
                sumNodesExpanded = 0;
                sumNodesInMemory = 0;
                scores = new double[]{0, 0};
            }
        }

        /*We pitch a montecarlo tree search vs an expectiminimax adversary. Collect and output stats.*/
        if(monteCarloTreeSearchVsExpectiminimax) {
            System.out.println("Monte Carlo Tree Search VS Expectiminimax");
            double[] scores = new double[]{0, 0};
            float sumNodesExpanded = 0;
            float sumNodesInMemory = 0;

            LinkedList<int[]> plyDepthPairs = new LinkedList<>();
            plyDepthPairs.add(new int[]{0, 0});
            plyDepthPairs.add(new int[]{2, 2});
            plyDepthPairs.add(new int[]{4, 4});
            plyDepthPairs.add(new int[]{2, 0});
            plyDepthPairs.add(new int[]{4, 2});
            plyDepthPairs.add(new int[]{4, 0});
            plyDepthPairs.add(new int[]{6, 0});
            plyDepthPairs.add(new int[]{0, 2});
            plyDepthPairs.add(new int[]{0, 4});
            plyDepthPairs.add(new int[]{0, 6});

            int iterations = 100;
            for (int[] plyPair : plyDepthPairs) {
                for (int i = 0; i < iterations; i++) {
                    Node.resetMeta();

                    Node startNode;
                    if(blackGoesFirst) {
                        Ur ur = new Ur();
                        ur.changeTurn();
                        startNode = new Node(null, ur, 0, 0, false);
                    } else {startNode = new Node();}

                    Node solution = monteCarloPlayVsExpectiMiniMax(startNode, plyPair[0], plyPair[1]);

                    sumNodesExpanded += Node.getNodesExpanded();
                    sumNodesInMemory += Node.getNodesInMemory();

                    if (solution.getHueristicScore() > 0) {
                        scores[0]++;
                    } else {
                        scores[1]++;
                    }
                }

                System.out.println("White ply depth: " + plyPair[0] + " Black ply depth: " + plyPair[1]);
                System.out.println("White win percentage: " + scores[0] / (scores[0] + scores[1]));
                System.out.println("Black win percentage: " + scores[1] / (scores[0] + scores[1]));
                System.out.println("Avg. nodes expanded: " + sumNodesExpanded / iterations);
                System.out.println("Avg. nodes in memory: " + sumNodesInMemory / iterations);
                sumNodesExpanded = 0;
                sumNodesInMemory = 0;
                scores = new double[]{0, 0};
            }
        }

        /*Both players here are expectiminimax adversarys! We test how well they do with different ply configurations
        * Collect and output stats.
        */
        if (testOptimalPlay) {
            System.out.println("Expectiminimax vs Expectiminimax");
            double[] scores = new double[]{0, 0};
            int iterations = 1000;
            float sumNodesExpanded = 0;
            float sumNodesInMemory = 0;

            LinkedList<int[]> plyDepthPairs = new LinkedList<>();
            plyDepthPairs.add(new int[]{0, 0});
            plyDepthPairs.add(new int[]{2, 2});
            plyDepthPairs.add(new int[]{4, 4});
            plyDepthPairs.add(new int[]{2, 0});
            plyDepthPairs.add(new int[]{4, 2});
            plyDepthPairs.add(new int[]{4, 0});
            plyDepthPairs.add(new int[]{6, 0});
            plyDepthPairs.add(new int[]{0, 2});
            plyDepthPairs.add(new int[]{0, 4});
            plyDepthPairs.add(new int[]{0, 6});


            for (int[] plyPair : plyDepthPairs) {
                for (int i = 0; i < iterations; i++) {
                    Node.resetMeta();
                    Node end = recursiveOptimalPlay(new Node(), plyPair[0], plyPair[1]);

                    sumNodesExpanded += Node.getNodesExpanded();
                    sumNodesInMemory += Node.getNodesInMemory();
                    // System.out.println(end.getHueristicScore());

                    if (end.getHueristicScore() > 0) {
                        scores[0]++;
                    } else {
                        scores[1]++;
                    }
                }

                System.out.println("White ply depth: " + plyPair[0] + " Black ply depth: " + plyPair[1]);
                System.out.println("White win percentage: " + scores[0] / (scores[0] + scores[1]));
                System.out.println("Black win percentage: " + scores[1] / (scores[0] + scores[1]));
                System.out.println("Avg. nodes expanded: " + sumNodesExpanded / iterations);
                System.out.println("Avg. nodes in memory: " + sumNodesInMemory / iterations);
                sumNodesExpanded = 0;
                sumNodesInMemory = 0;
                scores = new double[]{0, 0};
            }
        }

        /* We pitch an expectiminimax player against a random player, expectiminimax adversary assumes the opponent is
        * optimal. Collect and output stats.
        */
        if (testSubOptimalPlay) {
            System.out.println("Expectivminimax vs Suboptimal opponent");
            float sumNodesExpanded = 0;
            float sumNodesInMemory = 0;
            double[] wins = new double[]{0, 0};
            int subOptIterations = 1000;
            int[] subOptPlys = new int[]{0, 2, 4, 6, 8, 10};

            for (int ply : subOptPlys) {
                for (int i = 0; i < subOptIterations; i++) {
                    Node.resetMeta();
                    Node end = recursiveSubOptimalOpp(new Node(), ply);

                    sumNodesExpanded += Node.getNodesExpanded();
                    sumNodesInMemory += Node.getNodesInMemory();

                    if (end.getHueristicScore() > 0) {
                        wins[0]++;
                    } else {
                        wins[1]++;
                    }
                }

                System.out.println("Optimal player's ply depth: " + ply);
                System.out.println("   Optimal player win percentage: " + wins[0] / (wins[0] + wins[1]));
                System.out.println("Suboptimal player win percentage: " + wins[1] / (wins[0] + wins[1]));
                System.out.println("Avg. nodes expanded: " + sumNodesExpanded / subOptIterations);
                System.out.println("Avg. nodes in memory: " + sumNodesInMemory / subOptIterations);
                sumNodesExpanded = 0;
                sumNodesInMemory = 0;
                wins = new double[]{0, 0};
            }
        }
    }

    static Node recursiveSubOptimalOpp(Node start, int maxPly) {
        // we encounter an end state and return this node
        if (start.getUr().checkState())
            return start;

        // we roll the dice and generate the possible moves for this particular roll
        int roll = start.getUr().rollDice();
        LinkedList<Node> childNodes = start.getChildNodes(roll);
        Node maxminNode = null;
        double maxmin = 0;

        if (start.getUr().getTurn() == 1){
            maxmin = -10000;
        }

        for(Node node: childNodes) {
            if(start.getUr().getTurn() == 1) {
                double a;
                if(prune) {
                    // assumes the opponent is optimal!
                    a = expectiMiniMaxPruned(node, maxPly, -1000, 1000);
                } else {
                    a = expectiMiniMax(node, maxPly);
                }
                if (a > maxmin) maxminNode = node;

            } else {
                // perform a move by selecting random child from the list of child nodes
                Random rand = new Random();
                maxminNode = childNodes.get(rand.nextInt(childNodes.size()));
            }
        }

        // player can't move a piece, so carry on
        if(roll == 0 || maxminNode == null) {
            start.getUr().changeTurn();
            return recursiveSubOptimalOpp(start, maxPly);
        }

        return recursiveSubOptimalOpp(maxminNode, maxPly);
    }

    static Node recursiveOptimalPlay(Node start, int maxPly, int minPly) {

        if (start.getUr().checkState())
            return start;

        int roll = start.getUr().rollDice();
        LinkedList<Node> childNodes = start.getChildNodes(roll);

        double maxmin;
        if (start.getUr().getTurn() == 1){
            maxmin = -10000;
        } else {
            maxmin = 10000;
        }

        Node maxminNode = null;

        for(Node node: childNodes) {
            if(start.getUr().getTurn() == 1) {
                double a;
                if (prune) {
                    a = expectiMiniMaxPruned(node, maxPly, -1000, 1000);
                } else {
                    a = expectiMiniMax(node, maxPly);
                }

                if (a > maxmin){
                    maxminNode = node;
                    maxmin = a;
                }

            } else {
                double a;
                if (prune) {
                    a = expectiMiniMaxPruned(node, minPly, -1000, 1000);
                } else {
                    a = expectiMiniMax(node, minPly);
                }

                if(a < maxmin) {
                    maxminNode = node;
                    maxmin = a;
                }
            }
        }

        // player can't move a piece, so carry on
        if(roll == 0 || maxminNode == null) {
            start.getUr().changeTurn();
            return recursiveOptimalPlay(start, maxPly, minPly);
        }

        return recursiveOptimalPlay(maxminNode, maxPly, minPly);
    }

    // we play against a random player (or a min player)! with montecarlo
    static Node monteCarloPlayVsSubOptimalPlayer(Node start, int maxPly){
        // we encounter an end state and return this node
        if (start.getUr().checkState())
            return start;

        // we roll the dice and generate the possible moves for this particular roll
        int roll = start.getUr().rollDice();
        LinkedList<Node> childNodes = start.getChildNodes(roll);
        Node maxminNode = null;
        double maxmin = 0;

        if (start.getUr().getTurn() == 1){
            maxmin = -10000;
        }

        for(Node node: childNodes) {
            if(start.getUr().getTurn() == 1) {
                double a;
                // assumes opponent is optimal
                a = expectiMiniMaxMonteCarlo(node, maxPly);
                if (a > maxmin) {
                    maxminNode = node;
                    maxmin = a;
                }

            } else {
                // perform a move by selecting random child from the list of child nodes
                Random rand = new Random();
                maxminNode = childNodes.get(rand.nextInt(childNodes.size()));
            }
        }

        // player can't move a piece, so carry on
        if(roll == 0 || maxminNode == null) {
            start.getUr().changeTurn();
            return monteCarloPlayVsSubOptimalPlayer(start, maxPly);
        }

        return monteCarloPlayVsSubOptimalPlayer(maxminNode, maxPly);
    }

    static Node monteCarloPlayVsExpectiMiniMax(Node start, int maxPly, int minPly){
        // we encounter an end state and return this node
        if (start.getUr().checkState())
            return start;

        // we roll the dice and generate the possible moves for this particular roll
        int roll = start.getUr().rollDice();
        LinkedList<Node> childNodes = start.getChildNodes(roll);
        Node maxminNode = null;
        double maxmin = 0;

        if (start.getUr().getTurn() == 1){
            maxmin = -10000;
        } else {
            maxmin = 10000;
        }

        for(Node node: childNodes) {
            if(start.getUr().getTurn() == 1) {
                double a;
                // assumes opponent is optimal
                a = expectiMiniMaxMonteCarlo(node, maxPly);
                if (a > maxmin) {
                    maxminNode = node;
                    maxmin = a;
                }

            } else {
                // perform plain expectiminimax to minply depth
                double a;
                if (prune) {
                    a = expectiMiniMaxPruned(node, minPly, -1000, 1000);
                } else {
                    a = expectiMiniMax(node, minPly);
                }

                if(a < maxmin) {
                    maxminNode = node;
                    maxmin = a;
                }
            }
        }

        // player can't move a piece, so carry on
        if(roll == 0 || maxminNode == null) {
            start.getUr().changeTurn();
            return monteCarloPlayVsExpectiMiniMax(start, maxPly, minPly);
        }

        return monteCarloPlayVsExpectiMiniMax(maxminNode, maxPly, minPly);
    }

    private static void printState(Ur ur) {log(ur.toString());}

    private static void log(String message) {
        if(log) System.out.println(message);
    }

    static double expectiMiniMax(Node node, int ply) {

        double alpha = 0;

        if(ply == 0 || node.getUr().checkState())
            return node.getHueristicScore();

        // if node is random event, return the weighted averages of the children
        else if(node.isChanceNode()) {
            //log("dice's turn!");
            alpha = 0;
            // whatever we roll, get the expected score
            LinkedList<Node> nodes = node.getChildNodes();
            // log("" + node.size());
            for(Node child : nodes) {
                //log("Multiplying");
                alpha += (child.getProbability() * expectiMiniMax(child, ply - 1));
            }

            log("expected: " + alpha + " for " + node.getUr().getTurn() + " with roll: " + node.getRoll() + " at depth: " + node.getPathDepth());
        }

        // min
        else if(node.getUr().getTurn() == Ur.BLACKTURN) {
            log("min's turn!");
            alpha = 1000;
                for (Node child : node.getChildNodes()) {
                    double a = expectiMiniMax(child, ply - 1);
                    if (a < alpha) alpha = a;
                    //log(a + "");
                }
           // }
            log("min chooses: " + alpha);
        }

        // max
        else if(node.getUr().getTurn() == Ur.WHITETURN) {
            alpha = -1000;
            log("max's turn!");

            // children are the dice rolls which are our expected scores, pick the max
            // create nodes whose roles are set, for those nodes get children based off role
                LinkedList<Node> nodes = node.getChildNodes();
                for (Node child : nodes) {
                    double a = expectiMiniMax(child, ply - 1);
                    if (a > alpha) alpha = a;
            }
            log("max chooses: " + alpha);
        }

        return alpha;
    }

    static double expectiMiniMaxMonteCarlo(Node node, int ply) {

        double alpha = 0;

        if(node.getUr().checkState()) {
            return node.getHueristicScore();
        }

        // we we get to our ply limit we get the expected score by sampling many random games.
        if(ply == 0) {
            // we determine the expected score from this node onwards
            double expectedScore = 0;
            int iterations = 1000;

            try {
                Ur state = node.getUr().clone();

                for (int i = 0; i < iterations; i++) {
                    Ur temp = state.clone();
                    expectedScore += temp.randomGame();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return expectedScore/=iterations;
        }

            // if node is random event, return the weighted averages of the children
        else if(node.isChanceNode()) {
            //log("dice's turn!");
            alpha = 0;
            // whatever we roll, get the expected score
            LinkedList<Node> nodes = node.getChildNodes();
            // log("" + node.size());
            for(Node child : nodes) {
                //log("Multiplying");
                alpha += (child.getProbability() * expectiMiniMax(child, ply - 1));
            }

            log("expected: " + alpha + " for " + node.getUr().getTurn() + " with roll: " + node.getRoll() + " at depth: " + node.getPathDepth());
        }

        // min
        else if(node.getUr().getTurn() == Ur.BLACKTURN) {
            log("min's turn!");
            alpha = 1000;
            // for (int i = 0; i < 5; i++) {
            for (Node child : node.getChildNodes()) {
                double a = expectiMiniMax(child, ply - 1);
                if (a < alpha) alpha = a;
                //log(a + "");
            }
            // }
            log("min chooses: " + alpha);
        }

        // max
        else if(node.getUr().getTurn() == Ur.WHITETURN) {
            alpha = -1000;
            log("max's turn!");

            // children are the dice rolls which are our expected scores, pick the max
            //for (int i = 0; i < 5; i++) {
            // create nodes whose roles are set, for those nodes get children based off role
            LinkedList<Node> nodes = node.getChildNodes();
            for (Node child : nodes) {
                double a = expectiMiniMax(child, ply - 1);
                // log("a = " + a);
                if (a > alpha) alpha = a;
                // }
                // log("alpha for max is: " + alpha);
            }
            log("max chooses: " + alpha);
        }

        return alpha;
    }

    static double expectiMiniMaxPruned(Node node, int ply, double alpha, double beta) {

        double v = 0;

        if(ply == 0 || node.getUr().checkState())
            return node.getHueristicScore();

            // if node is random event, return the weighted averages of the children
        else if(node.isChanceNode()) {
            //log("dice's turn!");
            v = 0;
            // whatever we roll, get the expected score
            LinkedList<Node> nodes = node.getChildNodes();
            // log("" + node.size());
            for(Node child : nodes) {
                //log("Multiplying");
                v += (child.getProbability() * expectiMiniMaxPruned(child, ply - 1, alpha, beta));
            }

            log("expected: " + v + " for " + node.getUr().getTurn() + " with roll: " + node.getRoll() + " at depth: " + node.getPathDepth());
        }

        // min
        else if(node.getUr().getTurn() == Ur.BLACKTURN) {
            log("min's turn!");
            v = 1000;
            for (Node child : node.getChildNodes()) {
                double a = expectiMiniMaxPruned(child, ply - 1, alpha, beta);
                if (a < v) v = a;
                if (v < beta) beta = v;
                if (alpha >= beta) break;
            }
            log("min chooses: " + v);
        }

        // max
        else if(node.getUr().getTurn() == Ur.WHITETURN) {
            v = -1000;
            log("max's turn!");

            // children are the dice rolls which are our expected scores, pick the max
            // create nodes whose roles are set, for those nodes get children based off role
            LinkedList<Node> nodes = node.getChildNodes();
            for (Node child : nodes) {
                double a = expectiMiniMaxPruned(child, ply - 1, alpha, beta);
                // log("a = " + a);
                if (a > v) v = a;
                if(v > alpha) alpha = v;
                if (alpha >= beta) break;
                // }
                // log("alpha for max is: " + alpha);
            }
            log("max chooses: " + v);
        }

        return v;
    }

}


