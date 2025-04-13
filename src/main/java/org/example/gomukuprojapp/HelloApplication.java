/**
 *
 * @author [Kirk Douglas] [101401017]
 * [Andrei Gania][101478350]
 * [Denrick Viera][101426295]
 */

package org.example.gomukuprojapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HelloApplication extends Application{


    private Canvas canvas;
    private GraphicsContext gc;
    private final double height = 900;
    private final double width = 900;
    private double cellWidth;
    private double cellHeight;
    private Boolean playerTurn = true; // Track whose turn it is (true for player 1, false for player 2)
    private final int[][] board = new int[9][9];
    private Stage primaryStage;
    private String playerOneName = "Player 1";
    private String playerTwoName = "Player 2";
    private int blackPlayer = 1; // Player 1 plays as black by default
    private VBox inputBox;
    private VBox colorChoiceBox;
    private Label turnLabel = new Label();
    private boolean isPvAIMode = false; // Tracks if the game mode is Player vs AI

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Canvas setup
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        cellWidth = width / 8;
        cellHeight = height / 8;

        // Set up root and scene for the game window
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #cc6600;");
        Scene scene = new Scene(root, width, height);

        // Game mode selection UI elements
        Label modeLabel = new Label("Select Game Mode:");
        modeLabel.setFont(new Font(40));
        Button pvpButton = new Button("Player vs Player");
        Button pvAIButton = new Button("Player vs AI");
        pvpButton.setFont(new Font(30));
        pvAIButton.setFont(new Font(30));

        // Layout for game mode selection
        VBox modeBox = new VBox(20, modeLabel, pvpButton, pvAIButton);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.setPadding(new Insets(20));
        root.getChildren().addAll(canvas, modeBox);

        // Player name input fields
        Label playerOneLabel = new Label("Player 1:");
        playerOneLabel.setFont(new Font(25));
        TextField playerOneField = new TextField();
        playerOneField.setPrefHeight(40);

        Label playerTwoLabel = new Label("Player 2:");
        playerTwoLabel.setFont(new Font(25));
        TextField playerTwoField = new TextField();
        playerTwoField.setPrefHeight(40);

        // Button to choose color
        Button chooseColorButton = new Button("Choose Color");
        chooseColorButton.setFont(new Font(25));

        // Layout for player name and color choice input
        inputBox = new VBox(10, playerOneLabel, playerOneField,
                playerTwoLabel, playerTwoField,
                chooseColorButton);
        inputBox.setPadding(new Insets(10));
        inputBox.setLayoutX(20);
        inputBox.setLayoutY(20);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));

        // Buttons for color choice (black or white)
        Button blackButton = new Button("Play as Black");
        Button whiteButton = new Button("Play as White");
        blackButton.setFont(new Font(25));
        whiteButton.setFont(new Font(25));

        // Layout for color choice buttons
        colorChoiceBox = new VBox(10, blackButton, whiteButton);
        colorChoiceBox.setAlignment(Pos.CENTER);
        colorChoiceBox.setPadding(new Insets(10));

        // Actions for game mode buttons
        pvpButton.setOnAction(e -> {
            isPvAIMode = false;
            root.getChildren().remove(modeBox);
            root.getChildren().add(inputBox);
        });

        pvAIButton.setOnAction(e -> {
            isPvAIMode = true;
            playerTwoName = "AI";
            root.getChildren().remove(modeBox);
            root.getChildren().add(inputBox);
        });

        // Action for choosing color button
        chooseColorButton.setOnAction(e -> {
            if (!playerOneField.getText().isEmpty()) {
                playerOneName = playerOneField.getText();
            }
            if (!playerTwoField.getText().isEmpty()) {
                playerTwoName = playerTwoField.getText();
            }

            root.getChildren().remove(inputBox);
            root.getChildren().add(colorChoiceBox);
        });

        // Actions for color buttons
        blackButton.setOnAction(e -> {
            blackPlayer = 1;
            startGame();
        });

        whiteButton.setOnAction(e -> {
            blackPlayer = 2;
            startGame();
        });

        // Set up the stage and show the window
        stage.setTitle("GOMOKU");
        stage.setScene(scene);
        stage.show();
    }

    // Start the actual game (when color is chosen)
    public void startGame(){
        StackPane root = (StackPane) canvas.getParent();
        root.getChildren().remove(colorChoiceBox); // Remove color choice UI
        drawBoard();

        // Set up the turn label and display it on top of the board
        turnLabel.setFont(new Font(25));
        turnLabel.setText(playerOneName + "'s Turn");

        VBox gameTop = new VBox(turnLabel);
        gameTop.setAlignment(Pos.TOP_CENTER);
        gameTop.setPadding(new Insets(20));
        gameTop.setMouseTransparent(true); // Disable interaction with this label
        root.getChildren().add(gameTop);

        // Set up mouse click event to handle player moves
        canvas.setOnMouseClicked(click -> handleClick(click.getX(), click.getY()));
    }

    // Draw the game board grid
    private void drawBoard(){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        for (int i = 0; i <= 8; i++) {
            double x = i * cellWidth;
            gc.strokeLine(x, 0, x, height); // Draw vertical lines
        }
        for (int i = 0; i <= 8; i++) {
            double y = i * cellHeight;
            gc.strokeLine(0, y, width, y); // Draw horizontal lines
        }
    }

    // Handle the player's click (move on the board)
    private void handleClick(double clickX, double clickY){
        int col = (int) Math.round(clickX / cellWidth); // Determine column based on X position
        int row = (int) Math.round(clickY / cellHeight); // Determine row based on Y position

        // If the cell is already occupied, ignore the click
        if (board[row][col] != 0) {
            return;
        }

        // Calculate the center of the cell for drawing the piece
        double centerX = col * cellWidth;
        double centerY = row * cellHeight;
        double radius = 46;

        // Determine the current player (black or white)
        int currentPlayer = playerTurn ? 1 : 2;
        gc.setFill(currentPlayer == blackPlayer ? Color.BLACK : Color.WHITE);
        gc.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2); // Draw the piece

        // Update the board state
        board[row][col] = currentPlayer;

        // Check if the current player has won
        if (checkWin(row, col, currentPlayer)) {
            gc.setFill(Color.WHITE);
            gc.setFont(new Font(40));
            String winner = currentPlayer == 1 ? playerOneName : playerTwoName;
            gc.fillText(winner + " WINS!", 300, 50);
            canvas.setOnMouseClicked(null); // Disable further clicks
            Button resetButton = new Button("Play Again");
            resetButton.setFont(new Font(25));
            resetButton.setOnAction(e -> resetGame());

            Button returnToMenu = new Button("Main Menu");
            returnToMenu.setFont(new Font(25));
            returnToMenu.setOnAction(e -> returnToMainMenu());

            VBox buttonBox = new VBox(20, resetButton, returnToMenu);
            buttonBox.setAlignment(Pos.CENTER);

            StackPane root = (StackPane) canvas.getParent();
            root.getChildren().add(buttonBox);
            return;
        }

        // Check if the game ended in a draw
        if (checkDraw()) {
            turnLabel.setText("It's a Draw!");
            gc.setFill(Color.WHITE);
            gc.setFont(new Font(40));
            gc.fillText("IT'S A DRAW!", 300, 50);
            canvas.setOnMouseClicked(null);
            Button resetButton = new Button("Play Again");
            resetButton.setFont(new Font(25));
            resetButton.setOnAction(e -> resetGame());
            Button returnToMenu = new Button("Main Menu");
            returnToMenu.setFont(new Font(25));
            returnToMenu.setOnAction(e -> returnToMainMenu());

            VBox buttonBox = new VBox(20, resetButton, returnToMenu);
            buttonBox.setAlignment(Pos.CENTER);

            StackPane root = (StackPane) canvas.getParent();
            root.getChildren().add(buttonBox);
            return;
        }

        // Switch turn to the other player
        playerTurn = !playerTurn;
        turnLabel.setText(playerTurn ? playerOneName + "'s Turn" : playerTwoName + "'s Turn");

        // If in PvAI mode, let AI play automatically after the player's turn
        if (isPvAIMode && !playerTurn) {
            makeAIMove(); // AI makes its move
        }
        if (row < 0 || row >= 9 || col < 0 || col >= 9 || board[row][col] != 0) {
            return;
        }
    }

    // Method for the AI to make a move in PvAI mode
    private void makeAIMove() {
        GomokuAI ai = new GomokuAI();
        int aiPlayer = playerTurn ? 2 : 1;
        int[] move = ai.getBestMove(board, aiPlayer);

        if (move[0] != -1 && move[1] != -1) {
            double clickX = move[1] * cellWidth;
            double clickY = move[0] * cellHeight;

            // Delay slightly so AI doesn't move instantly (optional)
            javafx.application.Platform.runLater(() -> {
                handleClick(clickX, clickY);
            });
        }
    }

    // Check if a player has won (5 in a row)
    private boolean checkWin(int row, int col, int player){
        return checkDirection(row, col, player, 0, 1) || // Check horizontally
                checkDirection(row, col, player, 1, 0) || // Check vertically
                checkDirection(row, col, player, 1, 1) || // Check diagonal (top-left to bottom-right)
                checkDirection(row, col, player, 1, -1);   // Check diagonal (bottom-left to top-right)
    }

    // Check if the game ended in a draw
    private boolean checkDraw() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return false; // There is still an empty spot
                }
            }
        }
        return true; // All spots are filled
    }

    // Check a given direction (horizontal, vertical, diagonal)
    private boolean checkDirection(int row, int col, int player, int dRow, int dCol){
        int count = 1; // Count the current piece
        count += countInDirection(row, col, player, dRow, dCol); // Count in one direction
        count += countInDirection(row, col, player, -dRow, -dCol); // Count in the opposite direction
        return count >= 5;
    }

    // Count consecutive pieces in a given direction
    private int countInDirection(int row, int col, int player, int dRow, int dCol){
        int count = 0;
        int r = row + dRow;
        int c = col + dCol;

        while (inBounds(r,c) && board[r][c] == player){
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }

    // Check if the given position is within the bounds of the board
    private boolean inBounds(int row, int col){
        return row >= 0 && row < 9 && col >= 0 && col < 9;
    }

    // Reset the game state for a new game
    private void resetGame() {
        gc.clearRect(0, 0, width, height);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = 0; // Clear the board
            }
        }
        playerTurn = true; // Player 1's turn
        StackPane root = (StackPane) canvas.getParent();
        root.getChildren().removeIf(node -> node instanceof VBox); // Remove buttons

        drawBoard(); // Redraw the board

        turnLabel.setText(playerOneName + "'s Turn");
        turnLabel.setFont(new Font(25));
        VBox gameTop = new VBox(turnLabel);
        gameTop.setAlignment(Pos.TOP_CENTER);
        gameTop.setPadding(new Insets(20));
        gameTop.setMouseTransparent(true);
        root.getChildren().add(gameTop); // Add the turn label back

        canvas.setOnMouseClicked(click -> handleClick(click.getX(), click.getY()));
    }

    // Return to the main menu (reset game and show mode selection)
    private void returnToMainMenu() {
        gc.clearRect(0, 0, width, height);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = 0;
            }
        }
        playerTurn = true;
        start(primaryStage); // Restart the game from the beginning
    }


    public static void start(String[] args){
        launch(args);
    }
}
