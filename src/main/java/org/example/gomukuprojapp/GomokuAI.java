/**
 *
 * @author [Kirk Douglas] [101401017]
 * [Andrei Gania][101478350]
 * [Denrick Viera][101426295]
 */


package org.example.gomukuprojapp;


public class GomokuAI {

    private final int MAX_DEPTH = 2; // The maximum depth for the Minimax search
    private final int SIZE = 9;


    public int[] getBestMove(int[][] board, int aiPlayer) {
        int bestScore = Integer.MIN_VALUE; // Start with the worst possible score
        int[] bestMove = {-1, -1}; // Initialize best move as invalid
        int humanPlayer = (aiPlayer == 1) ? 2 : 1; // Get the opponent's player number

        // Loop through all positions on the board
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // If the position is empty, evaluate it
                if (board[row][col] == 0) {
                    board[row][col] = aiPlayer; // Simulate AI's move
                    int score = minimax(board, 0, false, aiPlayer, humanPlayer); // Get the score for this move
                    board[row][col] = 0; // Undo the move

                    // If this score is better than the previous best, update the best score and move
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{row, col};
                    }
                }
            }
        }
        return bestMove; // Return the best move found
    }


    private int minimax(int[][] board, int depth, boolean isMax, int aiPlayer, int humanPlayer) {
        // If we've reached the max depth or the game is over, evaluate the board
        if (depth == MAX_DEPTH || isGameOver(board)) {
            return evaluateBoard(board, aiPlayer, humanPlayer);
        }

        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Best score for max or min turn
        int currentPlayer = isMax ? aiPlayer : humanPlayer; // Determine which player's turn it is

        // Loop through all possible moves
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    board[row][col] = currentPlayer; // Simulate the move
                    int score = minimax(board, depth + 1, !isMax, aiPlayer, humanPlayer); // Recursively evaluate the move
                    board[row][col] = 0; // Undo the move

                    // Update the best score based on whether it's a max or min turn
                    bestScore = isMax ? Math.max(bestScore, score) : Math.min(bestScore, score);
                }
            }
        }
        return bestScore; // Return the best score found
    }


    private boolean isGameOver(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] != 0 && checkWin(board, row, col, board[row][col])) {
                    return true;
                }
            }
        }
        return isBoardFull(board);
    }


    private boolean isBoardFull(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) return false; // If there's an empty spot, the board is not full
            }
        }
        return true; // If no empty spots, the board is full
    }


    private int evaluateBoard(int[][] board, int aiPlayer, int humanPlayer) {
        return scoreLines(board, aiPlayer) - scoreLines(board, humanPlayer); // The AI's score minus the human's score
    }


    private int scoreLines(int[][] board, int player) {
        int score = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == player) {
                    // Evaluate the horizontal, vertical, and diagonal lines
                    score += countSequence(board, row, col, player, 0, 1);  // →
                    score += countSequence(board, row, col, player, 1, 0);  // ↓
                    score += countSequence(board, row, col, player, 1, 1);  // ↘
                    score += countSequence(board, row, col, player, 1, -1); // ↙
                }
            }
        }
        return score; // Return the total score
    }


    private int countSequence(int[][] board, int row, int col, int player, int dRow, int dCol) {
        int count = 0;
        // Count pieces in the given direction
        for (int i = 1; i <= 4; i++) {
            int r = row + i * dRow;
            int c = col + i * dCol;
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || board[r][c] != player) break;
            count++;
        }

        // Assign scores based on the length of the sequence
        return switch (count) {
            case 4 -> 1000; // A sequence of 4 is very valuable
            case 3 -> 100;  // A sequence of 3 is moderately valuable
            case 2 -> 10;   // A sequence of 2 is somewhat valuable
            case 1 -> 1;    // A single piece is less valuable
            default -> 0;   // No sequence found
        };
    }


    private boolean checkWin(int[][] board, int row, int col, int player) {
        return checkDir(board, row, col, player, 0, 1) || // →
                checkDir(board, row, col, player, 1, 0) || // ↓
                checkDir(board, row, col, player, 1, 1) || // ↘
                checkDir(board, row, col, player, 1, -1);  // ↙
    }


    private boolean checkDir(int[][] board, int row, int col, int player, int dRow, int dCol) {
        int count = 1;
        count += countDir(board, row, col, player, dRow, dCol); // Count pieces in one direction
        count += countDir(board, row, col, player, -dRow, -dCol); // Count pieces in the opposite direction
        return count >= 5;
    }


    private int countDir(int[][] board, int row, int col, int player, int dRow, int dCol) {
        int count = 0;
        int r = row + dRow;
        int c = col + dCol;
        // Count consecutive pieces in the given direction
        while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] == player) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }
}