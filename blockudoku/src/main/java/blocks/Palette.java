package blocks;

import blocks.BlockShapes.*;

import java.util.ArrayList;
import java.util.List;

public class Palette {
    ArrayList<Shape> shapes = new ArrayList<>();
    List<Sprite> sprites;
    int nShapes = 3;


    public Palette() {
        shapes.addAll(new ShapeSet().getShapes());
        sprites = new ArrayList<>();
        replenish();
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public ArrayList<Shape> getShapesToPlace() {
        // return a list of shapes that are in the palette - could use streams to filter this
        ArrayList<Shape> shapesToPlace = new ArrayList<>();
        sprites.stream()
                .filter(sprite -> sprite.state == SpriteState.IN_PALETTE || sprite.state == SpriteState.IN_PLAY)
                .forEach(sprite -> shapesToPlace.add(sprite.shape));
        return shapesToPlace;
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    // if we have a sprite that contains the point (px, py), return it
    // and the size of the cells - the sprite location is already in pixel coordinates
    public Sprite getSprite(PixelLoc mousePoint, int cellSize) {
        for (Sprite sprite : sprites) {
            if (sprite.contains(mousePoint, cellSize)) {
                return sprite;
            }
        }
        return null; // Return null if no sprite contains the point
    }

    private int nReadyPieces() {
        int count = 0;
        for (Sprite sprite : sprites) {
            if (sprite.state == SpriteState.IN_PALETTE || sprite.state == SpriteState.IN_PLAY) {
                count++;
            }
        }
        System.out.println("nReadyPieces: " + count);
        return count;
    }

    public void doLayout(int x0, int y0, int cellSize) {
        // layout the sprites in the palette
        int gap = cellSize * 2; // Gap between sprites for better layout
        int x = x0;
        int y = y0;

        for (Sprite sprite : sprites) {
            sprite.px = x;
            sprite.py = y;
            x += sprite.shape.size() * cellSize + gap;

            // Wrap to the next row if running out of space (optional improvement)
            if (x > x0 + nShapes * cellSize * 3) { // Example condition for wrapping
                x = x0;
                y += cellSize * 4; // Adjust for next row
            }
        }
    }

    public void replenish() {
        if (nReadyPieces() > 0) {
            return; // Do nothing if there are still ready pieces
        }

        sprites.clear();

        // Add nShapes random shapes to the palette
        for (int i = 0; i < nShapes; i++) {
            int randomIndex = (int) (Math.random() * shapes.size());
            Shape randomShape = shapes.get(randomIndex);

            sprites.add(new Sprite(randomShape, GameView.margin, GameView.margin + ModelInterface.height * GameView.cellSize));
            doLayout(GameView.margin, GameView.margin + ModelInterface.height * GameView.cellSize, GameView.shrinkSize);
        }

        System.out.println("Replenished: " + sprites);
    }

    public static void main(String[] args) {
        Palette palette = new Palette();
        System.out.println(palette.shapes);
        System.out.println(palette.sprites);
        palette.doLayout(0, 0, 20);
        System.out.println(palette.sprites);
    }
}
