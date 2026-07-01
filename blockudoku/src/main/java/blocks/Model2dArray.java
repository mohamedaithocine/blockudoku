package blocks;

/**
 * Logical model for the Blocks Puzzle
 * This handles the game logic, such as the grid, the pieces, and the rules for
 * placing pieces and removing lines and subgrids.
 * <p>
 * Note this has no dependencies on the UI or the game view, and no
 * concept of pixel-space or screen coordinates.
 * <p>
 * The standard block puzzle is on a 9x9 grid, so all placeable shapes will have
 * cells in that range.
 */

import blocks.BlockShapes.Cell;
import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model2dArray extends State2dArray implements ModelInterface {
    List<Shape> regions = new RegionHelper().allRegions();

    public Model2dArray() {
        grid = new boolean[width][height];
        // initially all cells are empty (false) - they would be by default anyway
        // but this makes it explicit
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = false;
            }
        }
    }

    public int getScore() {
        return score;
    }


    // interestingly, for canPlace we could also use sets to store the occupied cells
    // and then check if the shape's cells intersect with the occupied cells

    public boolean canPlace(Piece piece) {
        // check if the shape can be placed at this loc
        for (Cell cell : piece.cells()) {
            // Check if the cell is within bounds
            if (cell.x() < 0 || cell.x() >= width || cell.y() < 0 || cell.y() >= height) {
                return false;
            }

            // Check if the cell is already occupied
            if (grid[cell.x()][cell.y()]) {
                return false;
            }
        }
        return true;
    }

    private int consecutiveStreak = 0;
    @Override
    public void place(Piece piece) {
        for (Cell cell : piece.cells()) {
            // Mark the grid cell as occupied
            grid[cell.x()][cell.y()] = true;
        }

        // Find all completed regions first
        List<Shape> completedRegions = new ArrayList<>();
        for (Shape region : regions) {
            if (isComplete(region)) {
                completedRegions.add(region);
            }
        }

        if (!completedRegions.isEmpty()) {
            int baseScore = 0;
            for (Shape region : completedRegions) {
                baseScore += region.size();
                remove(region);
            }

            int simultaneousBonus = completedRegions.size() > 1
                    ? (completedRegions.size() - 1) * 10 // Bonus for completing 2+ regions
                    : 0;

            score += baseScore + simultaneousBonus;

            // Apply consecutive streak bonus
            int streakBonus = consecutiveStreak * 5; // Bonus increases with each streak
            score += streakBonus;
            consecutiveStreak++;
        } else {
            // Reset streak if no regions are completed
            consecutiveStreak = 0;
        }
    }

    @Override
    public void remove(Shape region) {
        for (Cell cell : region) {
            grid[cell.x()][cell.y()] = false; // Mark cell as unoccupied
        }
    }

    public boolean isComplete(Shape region) {
        Set<Cell> occupied = getOccupiedCells();
        for (Cell cell : region) {
            if (!occupied.contains(cell)) {
                return false;
            }
        }
        return true;
    }

    private boolean wouldBeComplete(Shape region, List<Cell> toAdd) {
        // check if the shape is complete, i.e. all cells are occupied
        Set<Cell> occupied = getOccupiedCells();
        occupied.addAll(toAdd);

        for (Cell cell : region) {
            if (!occupied.contains(cell)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isGameOver(List<Shape> palettePieces) {
        // if any shape in the palette can be placed, the game is not over
        for (Shape shape : palettePieces) {
            if (canPlaceAnywhere(shape)) {
                return false; // Game is not over if at least one shape can be placed
            }
        }
        return true;
    }

    public boolean canPlaceAnywhere(Shape shape) {
        // check if the shape can be placed anywhere on the grid
        // by checking if it can be placed at any loc
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Piece piece = new Piece(shape, new Cell(x, y));
                if (canPlace(piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Shape> getPoppableRegions(Piece piece) {
        // iterate over the regions
        List<Shape> poppable = new ArrayList<>();

        for (Shape region : regions) {
            if (wouldBeComplete(region, piece.cells())) {
                poppable.add(region);
            }
        }

        return poppable;
    }

    @Override
    public Set<Cell> getOccupiedCells() {
        Set<Cell> occupiedCells = new HashSet<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y]) {
                    occupiedCells.add(new Cell(x, y));
                }
            }
        }
        return occupiedCells;
    }
}
