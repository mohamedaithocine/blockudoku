package blocks;
/*
This is a container class for everything related to the block shapes.
We use inner classes of the container to represent all the required
shapes-related concepts.  We use the container to avoid having a separate
file for each class, which is inconvenient when some of the classes are so small.
One pot
 */

import java.util.ArrayList;
import java.util.List;

public class BlockShapes {
    // a grid location or cell in a shape
    public record Cell(int x, int y) {
    }

    // a shape is a list of cells
    public static class Shape extends ArrayList<Cell> {
        public Shape(List<Cell> cells) {
            super(cells);
        }

        public Shape() {
            super();
        }
    }

    // a piece is a shape located at a grid location
    public record Piece(Shape shape, Cell loc) {
        public List<Cell> cells() {
            return shape.stream()
                    .map(cell -> new Cell(cell.x() + loc.x(), cell.y() + loc.y()))
                    .toList();
        }
    }


    // a pixel location - just a pair of ints but semantically different
    // to a grid cell to avoid confusion
    public record PixelLoc(int x, int y) {
    }

    public enum SpriteState {
        IN_PLAY, IN_PALETTE, PLACED,
    }

    public static class Sprite {
        // models a Shape located at a place in pixel coords with
        Shape shape;
        // top left of piece
        int px, py; // pixel location
        SpriteState state = SpriteState.IN_PALETTE;

        public Sprite(Shape shape, int px, int py) {
            this.shape = shape;
            this.px = px;
            this.py = py;
        }

        // check if a point is within the sprite
        public boolean contains(PixelLoc point, int cellSize) {
            for (Cell cell : shape) {
                int cx = px + cell.x() * cellSize;
                int cy = py + cell.y() * cellSize;
                if (cx <= point.x() &&
                        cy <= point.y() &&
                        cx + cellSize > point.x() &&
                        cy + cellSize > point.y()) {
                    return true;
                }
            }
            return false;
        }

        // snap the piece to the grid and return as a Piece
        // since it will now be in grid coordinates
        public Piece snapToGrid(int margin, int cellSize) {
            int gx = (px - margin + cellSize / 2) / cellSize;
            int gy = (py - margin + cellSize / 2) / cellSize;
            return new Piece(shape, new Cell(gx, gy));
        }

        public String toString() {
            return "Sprite: " + state + " " + shape + " at " + px + ", " + py;
        }
    }


    public static class ShapeSet {
        // a better good-enough set of 'Blockonimo' shapes
        ArrayList<Shape> shapeTypes = new ArrayList<>(List.of(
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(2, 0))),
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(0, 1))),
                new Shape(List.of(new Cell(0, 0), new Cell(0, 1), new Cell(1, 1))),
                new Shape(List.of(new Cell(0, 1), new Cell(1, 0), new Cell(1, 1))),
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(1, 1))),
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(1, 1), new Cell(2, 1))),
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(1, 0), new Cell(1, 1))),
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(1, 1), new Cell(2, 1))),
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(1, 1), new Cell(2, 0))),
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(2, 0), new Cell(3, 0))),
                new Shape(List.of(new Cell(0, 0), new Cell(0, 1), new Cell(0, 2), new Cell(0, 3))),
                new Shape(List.of(new Cell(0, 0), new Cell(0, 1), new Cell(0, 2))),

                // Extra shapes
                new Shape(List.of(new Cell(0, 0), new Cell(1, 0), new Cell(2, 0), new Cell(2, 1))),
                new Shape(List.of(new Cell(0, 0), new Cell(0, 1), new Cell(0, 2), new Cell(1, 2))),
                new Shape(List.of(new Cell(0, 0), new Cell(0, 1), new Cell(1, 0), new Cell(1, 1)))
        ));

        public ArrayList<Shape> getShapes() {
            return shapeTypes;
        }
    }

}
