/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.myplayer;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import java.util.List;

public class MyPlayer extends Player {

    private static boolean searchCutoff = false;
    private static boolean aiMove = true;

    @Override
    public String getName() {
        return "Damian Horna 132240 Artur Mierzwa 132283";
    }


    @Override
    public Move nextMove(Board b) {
        long timeLimit = getTime();
        List<Move> moves = b.getMovesFor(getColor());
        int maxScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (Move move : moves) {
            Board newBoard = b.clone();
            newBoard.doMove(move);

            long searchTimeLimit = ((timeLimit - 1000) / (moves.size()));
            int score = iterativeDeepeningSearch(newBoard, searchTimeLimit);

            if (newBoard.getMovesFor(getColor()).size() == score) {
                return move;
            }

            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }


        return bestMove;
    }


    private int iterativeDeepeningSearch(Board board, long timeLimit) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        int depth = 1;
        int score = 0;
        searchCutoff = false;

        while (true) {
            long currentTime = System.currentTimeMillis();

            if (currentTime >= endTime) {
                break;
            }

            int searchResult = search(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, currentTime, endTime - currentTime);

            //
            // If the search finds a winning move, stop searching
            //
            if (searchResult == board.getMovesFor(getColor()).size()) {
                return searchResult;
            }

            if (!searchCutoff) {
                score = searchResult;
            }

            depth++;
        }

        return score;
    }

    private int search(Board board, int depth, int alpha, int beta, long startTime, long timeLimit) {
        List<Move> moves = board.getMovesFor(getColor());
        int score = board.getMovesFor(getColor()).size() - board.getMovesFor(getOpponent(getColor())).size();
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime);

        if (elapsedTime >= timeLimit) {
            searchCutoff = true;
        }

        if (searchCutoff || (depth == 0) || (moves.size() == 0) || (score == board.getMovesFor(getColor()).size())) {
            return score;
        }

        if (aiMove) {
            for (Move move : moves) {
                Board childBoard = board.clone();
                childBoard.doMove(move);
                aiMove = false;

                alpha = Math.max(alpha, search(childBoard, depth - 1, alpha, beta, startTime, timeLimit));

                if (beta <= alpha) {
                    break;
                }
            }

            return alpha;
        } else {
            for (Move move : moves) {
                Board childBoard = board.clone();
                childBoard.doMove(move);
                aiMove = true;
                beta = Math.min(beta, search(childBoard, depth - 1, alpha, beta, startTime, timeLimit));

                if (beta <= alpha) {
                    break;
                }
            }

            return beta;
        }
    }
}
