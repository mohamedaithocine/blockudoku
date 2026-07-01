package blocks;

import blocks.BlockShapes.Piece;
import blocks.BlockShapes.PixelLoc;
import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.SpriteState;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller extends MouseAdapter {
    GameView view;
    ModelInterface model;
    Palette palette;
    JFrame frame;
    Sprite selectedSprite = null;
    Piece ghostShape = null;
    String title = "Blocks Puzzle";
    boolean gameOver = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;


    public Controller(GameView view, ModelInterface model, Palette palette, JFrame frame) {
        this.view = view;
        this.model = model;
        this.palette = palette;
        this.frame = frame;
        frame.setTitle(title);
        // force palette to do a layout
        palette.doLayout(view.margin, view.margin + ModelInterface.height * view.cellSize, view.shrinkSize);
        System.out.println("Palette layout done: " + palette.sprites);
    }

    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            System.out.println("Mouse pressed: " + e);
            PixelLoc loc = new PixelLoc(e.getX(), e.getY());
            selectedSprite = palette.getSprite(loc, view.shrinkSize);
            if (selectedSprite != null) {
                System.out.println("Selected sprite: " + selectedSprite);

                // Calculate the offset between mouse and sprite's top-left corner
                dragOffsetX = e.getX() - selectedSprite.px;
                dragOffsetY = e.getY() - selectedSprite.py;

                view.repaint();
            }
        }
    }


    public void mouseDragged(MouseEvent e) {
        if (selectedSprite != null) {
            // Adjust sprite position based on the drag offset
            selectedSprite.px = e.getX() - dragOffsetX;
            selectedSprite.py = e.getY() - dragOffsetY;

            view.ghostShape = selectedSprite.snapToGrid(view.margin, view.cellSize);
            if (model.canPlace(view.ghostShape)) {
                selectedSprite.state = SpriteState.IN_PLAY;
                view.poppableRegions = model.getPoppableRegions(view.ghostShape);
            } else {
                view.ghostShape = null;
                view.poppableRegions = null;
                selectedSprite.state = SpriteState.IN_PALETTE;
            }

            view.repaint();
        }
    }


    public void mouseReleased(MouseEvent e) {
        if (selectedSprite == null) {
            return; // Nothing to place
        }

        // Snap the selected sprite to the grid as a Piece
        Piece snappedPiece = selectedSprite.snapToGrid(view.margin, view.cellSize);

        // Check if the piece can be placed on the grid
        if (model.canPlace(snappedPiece)) {
            model.place(snappedPiece);

            // Remove the sprite from the palette
            selectedSprite.state = SpriteState.PLACED;
            palette.sprites.remove(selectedSprite);

            // Check for game over after placement
            if (palette.getSprites().isEmpty()) {
                palette.replenish();
            }
            else if (model.isGameOver(palette.getShapesToPlace())) {
                gameOver = true;
            }
        } else {
            selectedSprite.state = SpriteState.IN_PALETTE;
        }

        selectedSprite = null;
        view.ghostShape = null;
        view.poppableRegions = null;

        frame.setTitle(getTitle());
        view.repaint();
    }

    private String getTitle() {
        // make the title from the base title, score, and add GameOver if the game is over
        String title = this.title + " Score: " + model.getScore();
        if (gameOver) {
            title += " Game Over!";
        }
        return title;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModelInterface model = new ModelSet();
//        ModelInterface model = new Model2dArray();
        Palette palette = new Palette();
        GameView view = new GameView(model, palette);
        Controller controller = new Controller(view, model, palette, frame);
        view.addMouseListener(controller);
        view.addMouseMotionListener(controller);
        frame.add(view);
        frame.pack();
        frame.setVisible(true);
    }
}
