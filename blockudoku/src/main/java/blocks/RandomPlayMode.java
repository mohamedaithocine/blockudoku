package blocks;

import blocks.BlockShapes.Shape;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlayMode {
    private final ModelInterface model;
    private final Palette palette;
    private final GameView gameView;
    private final ArrayList<BlockShapes.Shape> shapeSet;
    private final Random random = new Random();
    private Timer gameTimer;

    public RandomPlayMode(ModelInterface model, Palette palette, GameView gameView) {
        this.model = model;
        this.palette = palette;
        this.gameView = gameView;
        this.shapeSet = new BlockShapes.ShapeSet().getShapes(); // Use the enhanced ShapeSet
    }

    public void startRandomPlay() {
        System.out.println("Starting Random Play Mode with GUI...");

        gameTimer = new Timer(1000, new ActionListener() { // Timer for 1-second delay between moves
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!makeRandomMove()) {
                    gameTimer.stop(); // Stop the game when no valid moves are left
                    System.out.println("Game Over! Final Score: " + model.getScore());
                } else {
                    gameView.repaint(); // Refresh the GUI after each move
                }
            }
        });

        gameTimer.start();
    }

    private boolean makeRandomMove() {
        List<Shape> currentPalette = getRandomShapes(); // Get a random palette of 3 shapes
        palette.replenish(); // Update the palette display

        for (Shape shape : currentPalette) {
            if (attemptPlacement(shape)) {
                return true; // Successfully placed a piece
            }
        }

        return false; // No valid moves left
    }

    private List<Shape> getRandomShapes() {
        List<Shape> randomShapes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            randomShapes.add((Shape) shapeSet.get(random.nextInt(shapeSet.size())));
        }
        return randomShapes;
    }

    private boolean attemptPlacement(Shape shape) {
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = random.nextInt(ModelInterface.width);
            int y = random.nextInt(ModelInterface.height);
            BlockShapes.Piece piece = new BlockShapes.Piece((BlockShapes.Shape) shape, new BlockShapes.Cell(x, y));

            if (model.canPlace(piece)) {
                model.place(piece); // Place the piece on the board
                System.out.println("Placed piece at (" + x + ", " + y + ")");
                return true;
            }
        }

        return false; // Could not place the piece
    }

    public static void main(String[] args) {
        // Create the main frame and components
        JFrame frame = new JFrame("Random Play Mode with GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ModelInterface model = new ModelSet();
        Palette palette = new Palette();
        GameView gameView = new GameView(model, palette);

        frame.add(gameView);
        frame.pack();
        frame.setVisible(true);

        // Start the random play mode
        RandomPlayMode randomPlay = new RandomPlayMode(model, palette, gameView);
        randomPlay.startRandomPlay();
    }
}
