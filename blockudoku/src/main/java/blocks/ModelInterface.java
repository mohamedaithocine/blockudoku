package blocks;

import blocks.BlockShapes.Cell;
import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;

import java.util.List;
import java.util.Set;

public interface ModelInterface {
    int width = 9;  // these are constants
    int height = 9;
    int subSize = 3;

    boolean canPlace(Piece piece);

    void place(Piece piece);

    void remove(Shape region);

    boolean isComplete(Shape region);

    boolean isGameOver(List<Shape> palettePieces);

    List<Shape> getPoppableRegions(Piece piece);

    Set<Cell> getOccupiedCells();

    int getScore();
}
