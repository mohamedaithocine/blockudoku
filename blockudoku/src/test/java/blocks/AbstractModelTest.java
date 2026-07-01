package blocks;

import blocks.BlockShapes.Cell;
import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractModelTest {

    protected ModelInterface model;

    protected abstract ModelInterface createModel();

    @BeforeEach
    void setUp() {
        model = createModel();
    }

    @Test
    void testCanPlace() {
        Shape lineShape = new Shape(List.of(
                new Cell(0, 0), new Cell(1, 0), new Cell(2, 0)
        ));
        Piece piece = new Piece(lineShape, new Cell(0, 0));

        assertTrue(model.canPlace(piece), "Piece should be placeable on an empty board.");
        model.place(piece);
        assertFalse(model.canPlace(piece), "Piece should not be placeable on an already occupied space.");
    }

    @Test
    void testPlaceAndOccupiedCells() {
        Shape lineShape = new Shape(List.of(
                new Cell(0, 0), new Cell(1, 0), new Cell(2, 0)
        ));
        Piece piece = new Piece(lineShape, new Cell(0, 0));

        model.place(piece);
        Set<Cell> occupiedCells = model.getOccupiedCells();

        assertTrue(occupiedCells.contains(new Cell(0, 0)));
        assertTrue(occupiedCells.contains(new Cell(1, 0)));
        assertTrue(occupiedCells.contains(new Cell(2, 0)));
    }

    @Test
    void testRemove() {
        Shape squareShape = new Shape(List.of(
                new Cell(0, 0), new Cell(1, 0), new Cell(0, 1), new Cell(1, 1)
        ));
        model.place(new Piece(squareShape, new Cell(0, 0)));

        model.remove(squareShape);
        Set<Cell> occupiedCells = model.getOccupiedCells();
        assertTrue(occupiedCells.isEmpty(), "Occupied cells should be empty after removal.");
    }

    @Test
    void testIsComplete() {
        Shape squareShape = new Shape(List.of(
                new Cell(0, 0), new Cell(1, 0), new Cell(0, 1), new Cell(1, 1)
        ));
        model.place(new Piece(squareShape, new Cell(0, 0)));

        assertTrue(model.isComplete(squareShape), "Region should be complete after placing the corresponding piece.");
    }

    @Test
    void testIsGameOver() {
        Shape lineShape = new Shape(List.of(
                new Cell(0, 0), new Cell(1, 0), new Cell(2, 0)
        ));
        List<Shape> palettePieces = List.of(lineShape);

        // place the line shape in the top left corner so that game over returning false has to do some work
        model.place(new Piece(lineShape, new Cell(0, 0)));
        assertFalse(model.isGameOver(palettePieces), "Game should not be over if a piece can be placed.");

        // Simulate filling the board to prevent further placements
        for (int i = 0; i < ModelInterface.width; i++) {
            for (int j = 0; j < ModelInterface.height; j++) {
                // only place on odd coordinates to simulate filling the board with a checkerboard pattern
                if ((i + j) % 2 == 0) {
                    model.place(new Piece(
                            new Shape(
                                    List.of(new Cell(0, 0))), new Cell(i, j)));
                }
            }
        }

        System.out.println(model.getOccupiedCells());
        assertTrue(model.isGameOver(palettePieces), "Game should be over if no piece can be placed - occupied: " + model.getOccupiedCells());
    }

//    interesting
//    @Test
//    void brokenTestIsGameOver() {
//        Shape lineShape = new Shape(List.of(
//                new Cell(0, 0), new Cell(1, 0), new Cell(2, 0)
//        ));
//        List<Shape> palettePieces = List.of(lineShape);
//
//        assertFalse(model.isGameOver(palettePieces), "Game should not be over if a piece can be placed.");
//
//        // Simulate filling the board to prevent further placements
//        for (int i = 0; i < ModelInterface.width; i++) {
//            for (int j = 0; j < ModelInterface.height; j++) {
//                model.place(new Piece(
//                        new Shape(
//                                List.of(new Cell(0, 0))), new Cell(i, j)));
//            }
//        }
//
//        assertTrue(model.isGameOver(palettePieces),
//                "Game should be over if no piece can be placed: " +
//                        model.getOccupiedCells());
//    }
//

    @Test
    void testGetScore() {
        Shape lineShape = new Shape(List.of(
                new Cell(0, 0), new Cell(1, 0), new Cell(2, 0)
        ));
        Piece piece = new Piece(lineShape, new Cell(0, 0));
        model.place(piece);
        assertEquals(0, model.getScore(), "Score calculation depends on poppable regions and should start at 0.");
        // now place enough pieces to pop a region and check the score is greater than zero
        model.place(new Piece(lineShape, new Cell(0, 1)));
        model.place(new Piece(lineShape, new Cell(0, 2)));
        assertTrue(model.getScore() > 0, "Score should be > 0 after popping a region: " + model.getScore());

    }
}
